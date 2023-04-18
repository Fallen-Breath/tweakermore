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
import me.fallenbreath.tweakermore.impl.features.infoView.AbstractInfoViewer;
import me.fallenbreath.tweakermore.mixins.tweaks.features.infoView.beacon.BeaconBlockEntityAccessor;
import me.fallenbreath.tweakermore.util.render.InWorldPositionTransformer;
import me.fallenbreath.tweakermore.util.render.RenderContext;
import me.fallenbreath.tweakermore.util.render.RenderUtil;
import me.fallenbreath.tweakermore.util.render.TextRenderer;
import net.minecraft.block.BeaconBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.Sprite;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

//#if MC >= 11700
//$$ import com.mojang.blaze3d.systems.RenderSystem;
//#endif

//#if MC >= 11600
//$$ import net.minecraft.client.util.math.MatrixStack;
//#endif

//#if MC < 11500
//$$ import net.minecraft.client.texture.SpriteAtlasTexture;
//#endif

public class BeaconEffectRenderer extends AbstractInfoViewer
{
	private static final double FONT_SCALE = TextRenderer.DEFAULT_FONT_SCALE;

	public BeaconEffectRenderer()
	{
		super(
				TweakerMoreConfigs.INFO_VIEW_BEACON,
				TweakerMoreConfigs.INFO_VIEW_BEACON_RENDER_STRATEGY,
				TweakerMoreConfigs.INFO_VIEW_BEACON_TARGET_STRATEGY
		);
	}

	@Override
	public boolean shouldRenderFor(World world, BlockPos blockPos, BlockState blockState, @Nullable BlockEntity blockEntity)
	{
		return blockState.getBlock() instanceof BeaconBlock && blockEntity instanceof BeaconBlockEntity;
	}

	@Override
	public boolean requireBlockEntitySyncing()
	{
		return true;
	}

	@Override
	public void render(RenderContext context, World world, BlockPos blockPos, BlockState blockState, BlockEntity blockEntity)
	{
		BeaconBlockEntityAccessor accessor = (BeaconBlockEntityAccessor)blockEntity;
		int beaconLevel = accessor.getLevel();
		StatusEffect primary = accessor.getPrimary();
		StatusEffect secondary = accessor.getSecondary();

		if (primary != null)
		{
			// list of (effect, level)
			List<Pair<StatusEffect, Integer>> effects = Lists.newArrayList();

			effects.add(Pair.of(primary, beaconLevel >= 4 && primary == secondary ? 1 : 0));
			if (beaconLevel >= 4 && primary != secondary && secondary != null)
			{
				effects.add(Pair.of(secondary, 0));
			}

			for (int i = 0; i < effects.size(); i++)
			{
				Pair<StatusEffect, Integer> pair = effects.get(i);
				Vec3d vec3d = new Vec3d(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5);
				this.renderStatusEffect(vec3d, pair.getFirst(), pair.getSecond(), i, effects.size());
			}
		}
	}

	private void renderStatusEffect(Vec3d pos, StatusEffect statusEffect, int amplifier, int index, int lineNum)
	{
		double margin = 2.5;
		double kDeltaY = index - (lineNum - 1) / 2.0;

		this.renderStatusEffectIcon(pos, statusEffect, amplifier, margin, kDeltaY);
		this.renderStatusEffectText(pos, statusEffect, amplifier, margin, kDeltaY);
	}

	@SuppressWarnings("AccessStaticViaInstance")
	private void renderStatusEffectIcon(Vec3d pos, StatusEffect statusEffect, int amplifier, double margin, double kDeltaY)
	{
		MinecraftClient mc = MinecraftClient.getInstance();
		Sprite sprite = mc.getStatusEffectSpriteManager().getSprite(statusEffect);

		RenderContext renderContext = new RenderContext(
				//#if MC >= 11600
				//$$ new MatrixStack()
				//#endif
		);

		InWorldPositionTransformer positionTransformer = new InWorldPositionTransformer(pos);
		positionTransformer.apply(renderContext);
		{
			renderContext.disableDepthTest();
			renderContext.enableBlend();  // maybe useful
			//#if MC < 11700
			renderContext.disableLighting();
			//#endif

			// ref: net.minecraft.client.gui.hud.InGameHud.renderStatusEffectOverlay
			final int TEXTURE_SIZE = 18;
			double scale = FONT_SCALE * RenderUtil.TEXT_HEIGHT / TEXTURE_SIZE;

			renderContext.scale(-scale, -scale, scale);
			renderContext.translate(-TEXTURE_SIZE - margin, TEXTURE_SIZE * (-0.5 + kDeltaY), 0);

			//#if MC >= 11900
			//$$ RenderSystem.setShaderTexture(0, sprite.getAtlasId());
			//#elseif MC >= 11700
			//$$ RenderSystem.setShaderTexture(0, sprite.getAtlas().getId());
			//#elseif MC >= 11500
			mc.getTextureManager().bindTexture(sprite.getAtlas().getId());
			//#else
			//$$ mc.getTextureManager().bindTexture(SpriteAtlasTexture.STATUS_EFFECT_ATLAS_TEX);
			//#endif
			renderContext.color4f(1.0F, 1.0F, 1.0F, 1.0F);

			//#if MC >= 11600
			//$$ renderContext.drawSprite(
			//$$ 		renderContext.getMatrixStack(),
			//#else
			renderContext.blit(
			//#endif
					0, 0, 0, TEXTURE_SIZE, TEXTURE_SIZE, sprite
			);
			renderContext.enableDepthTest();
		}
		positionTransformer.restore();
	}

	private void renderStatusEffectText(Vec3d pos, StatusEffect statusEffect, int amplifier, double margin, double kDeltaY)
	{
		String description = StringUtils.translate(statusEffect.getTranslationKey()) + ' ' + StringUtils.translate("enchantment.level." + (amplifier + 1));
		TextRenderer textRenderer = TextRenderer.create().
				at(pos).
				text(description).fontScale(FONT_SCALE).
				align(TextRenderer.HorizontalAlignment.LEFT).
				seeThrough().shadow();
		textRenderer.shift(margin, kDeltaY * textRenderer.getLineHeight());
		textRenderer.render();
	}
}
