/*
 * This file is part of the TweakerMore project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2023  Fallen_Breath and contributors
 *
 * TweakerMore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * TweakerMore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with TweakerMore.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.fallenbreath.tweakermore.impl.features.infoView.beacon;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import fi.dy.masa.malilib.util.StringUtils;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.impl.features.infoView.CommonScannerInfoViewer;
import me.fallenbreath.tweakermore.impl.features.infoView.cache.RenderVisitorWorldView;
import me.fallenbreath.tweakermore.mixins.tweaks.features.infoView.beacon.BeaconBlockEntityAccessor;
import me.fallenbreath.tweakermore.util.PositionUtil;
import me.fallenbreath.tweakermore.util.render.InWorldPositionTransformer;
import me.fallenbreath.tweakermore.util.render.context.RenderContext;
import me.fallenbreath.tweakermore.util.render.RenderUtil;
import me.fallenbreath.tweakermore.util.render.TextRenderer;
import me.fallenbreath.tweakermore.util.render.context.RenderGlobals;
import net.minecraft.block.BeaconBlock;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.Sprite;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.List;

//#if MC >= 12006
//$$ import net.minecraft.registry.Registries;
//$$ import net.minecraft.registry.entry.RegistryEntry;
//$$ import java.util.Optional;
//#endif

//#if MC >= 11700
//$$ import com.mojang.blaze3d.systems.RenderSystem;
//#endif

//#if MC >= 11600
//$$ import me.fallenbreath.tweakermore.util.render.matrix.McMatrixStack;
//$$ import net.minecraft.client.util.math.MatrixStack;
//#endif

//#if MC < 11500
//$$ import net.minecraft.client.texture.SpriteAtlasTexture;
//#endif

public class BeaconEffectRenderer extends CommonScannerInfoViewer
{
	private static final double FONT_SCALE = TextRenderer.DEFAULT_FONT_SCALE;
	private static final double MARGIN = 5;  // margin between icon and text
	private static final int ICON_SIZE = 18;  // the status effect icon texture is a 18x18 square
	private static final int ICON_RENDERED_SIZE = RenderUtil.TEXT_HEIGHT;

	public BeaconEffectRenderer()
	{
		super(
				TweakerMoreConfigs.INFO_VIEW_BEACON,
				TweakerMoreConfigs.INFO_VIEW_BEACON_RENDER_STRATEGY,
				TweakerMoreConfigs.INFO_VIEW_BEACON_TARGET_STRATEGY
		);
	}

	@Override
	public boolean shouldRenderFor(RenderVisitorWorldView world, BlockPos pos)
	{
		return world.getBlockState(pos).getBlock() instanceof BeaconBlock && world.getBlockEntity(pos) instanceof BeaconBlockEntity;
	}

	@Override
	public boolean requireBlockEntitySyncing(RenderVisitorWorldView world, BlockPos pos)
	{
		return true;
	}

	@Override
	protected void render(RenderContext context, RenderVisitorWorldView world, BlockPos pos, boolean isCrossHairPos)
	{
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity == null)
		{
			return;
		}

		BeaconBlockEntityAccessor accessor = (BeaconBlockEntityAccessor)blockEntity;
		int beaconLevel = accessor.getLevel();

		//#if MC >= 12006
		//$$ StatusEffect primary = Optional.ofNullable(accessor.getPrimary()).map(RegistryEntry::value).orElse(null);
		//$$ StatusEffect secondary = Optional.ofNullable(accessor.getSecondary()).map(RegistryEntry::value).orElse(null);
		//#else
		StatusEffect primary = accessor.getPrimary();
		StatusEffect secondary = accessor.getSecondary();
		//#endif

		if (primary != null)
		{
			// list of (effect, level)
			List<Pair<StatusEffect, Integer>> effects = Lists.newArrayList();

			effects.add(Pair.of(primary, beaconLevel >= 4 && primary == secondary ? 1 : 0));
			if (beaconLevel >= 4 && primary != secondary && secondary != null)
			{
				effects.add(Pair.of(secondary, 0));
			}

			Vec3d centerPos = PositionUtil.centerOf(pos);
			double maxWidth = effects.stream().
					mapToDouble(pair -> this.calculateRowWidth(pair.getFirst(), pair.getSecond())).
					max().orElse(0);
			for (int i = 0; i < effects.size(); i++)
			{
				Pair<StatusEffect, Integer> pair = effects.get(i);
				StatusEffect statusEffect = pair.getFirst();
				int amplifier = pair.getSecond();

				double deltaX = -maxWidth / 2;  // unit: pixel (in scale=FONT_SCALE context)
				double kDeltaY = i - (effects.size() - 1) / 2.0;  // unit: ratio

				this.renderStatusEffectIcon(centerPos, statusEffect, amplifier, deltaX, kDeltaY);
				this.renderStatusEffectText(centerPos, statusEffect, amplifier, deltaX, kDeltaY);
			}
		}
	}

	private double calculateRowWidth(StatusEffect statusEffect, int amplifier)
	{
		double textWidth = RenderUtil.getRenderWidth(getStatusEffectText(statusEffect, amplifier));
		return ICON_RENDERED_SIZE + MARGIN + textWidth;
	}

	@SuppressWarnings("AccessStaticViaInstance")
	private void renderStatusEffectIcon(Vec3d pos, StatusEffect statusEffect, int amplifier, double deltaX, double kDeltaY)
	{
		MinecraftClient mc = MinecraftClient.getInstance();
		Sprite sprite = mc.getStatusEffectSpriteManager().getSprite(
				//#if MC >= 12006
				//$$ Registries.STATUS_EFFECT.getEntry(statusEffect)
				//#else
				statusEffect
				//#endif
		);

		RenderContext renderContext = RenderContext.of(
				//#if MC >= 11600
				//$$ new MatrixStack()
				//#endif
		);

		InWorldPositionTransformer positionTransformer = new InWorldPositionTransformer(pos);
		positionTransformer.apply(renderContext);
		{
			RenderGlobals.disableDepthTest();
			RenderGlobals.enableBlend();  // maybe useful
			//#if MC < 11700
			RenderGlobals.disableLighting();
			//#endif

			// ref: net.minecraft.client.gui.hud.InGameHud.renderStatusEffectOverlay

			renderContext.scale(-FONT_SCALE, -FONT_SCALE, FONT_SCALE);
			renderContext.translate(deltaX, 0, 0);

			// scale 2: make the rendered texture height == expected height (line height)
			double k = 1.0 * ICON_RENDERED_SIZE / ICON_SIZE;
			renderContext.scale(k, k, k);
			renderContext.translate(0, ICON_SIZE * (-0.5 + kDeltaY), 0);

			//#if MC >= 11903
			//$$ RenderSystem.setShaderTexture(0, sprite.getAtlasId());
			//#elseif MC >= 11700
			//$$ RenderSystem.setShaderTexture(0, sprite.getAtlas().getId());
			//#elseif MC >= 11500
			mc.getTextureManager().bindTexture(sprite.getAtlas().getId());
			//#else
			//$$ mc.getTextureManager().bindTexture(SpriteAtlasTexture.STATUS_EFFECT_ATLAS_TEX);
			//#endif
			RenderGlobals.color4f(1.0F, 1.0F, 1.0F, 1.0F);

			//#if MC >= 12000
			//$$ renderContext.getGuiDrawer().drawSprite(
			//#elseif MC >= 11600
			//$$ renderContext.getGuiDrawer().drawSprite(
			//$$ 		renderContext.getMatrixStack().asMcRaw(),
			//#else
			renderContext.getGuiDrawer().blit(
			//#endif
					0, 0, 0, ICON_SIZE, ICON_SIZE, sprite
			);
			RenderGlobals.enableDepthTest();
		}
		positionTransformer.restore();
	}

	private void renderStatusEffectText(Vec3d pos, StatusEffect statusEffect, int amplifier, double deltaX, double kDeltaY)
	{
		String description = getStatusEffectText(statusEffect, amplifier);
		TextRenderer textRenderer = TextRenderer.create().
				at(pos).
				text(description).fontScale(FONT_SCALE).
				align(TextRenderer.HorizontalAlignment.LEFT).
				seeThrough().shadow();
		textRenderer.shift(deltaX + ICON_RENDERED_SIZE + MARGIN, kDeltaY * textRenderer.getLineHeight());
		textRenderer.render();
	}

	private static String getStatusEffectText(StatusEffect statusEffect, int amplifier)
	{
		return StringUtils.translate(statusEffect.getTranslationKey()) + ' ' + StringUtils.translate("enchantment.level." + (amplifier + 1));
	}
}
