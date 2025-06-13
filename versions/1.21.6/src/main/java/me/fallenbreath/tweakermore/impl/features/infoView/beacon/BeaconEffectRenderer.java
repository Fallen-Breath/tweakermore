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

import com.google.common.base.Suppliers;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import fi.dy.masa.malilib.render.MaLiLibPipelines;
import fi.dy.masa.malilib.util.StringUtils;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.impl.features.infoView.CommonScannerInfoViewer;
import me.fallenbreath.tweakermore.impl.features.infoView.cache.RenderVisitorWorldView;
import me.fallenbreath.tweakermore.mixins.tweaks.features.infoView.beacon.BeaconBlockEntityAccessor;
import me.fallenbreath.tweakermore.util.PositionUtils;
import me.fallenbreath.tweakermore.util.render.InWorldPositionTransformer;
import me.fallenbreath.tweakermore.util.render.RenderUtils;
import me.fallenbreath.tweakermore.util.render.TextRenderer;
import me.fallenbreath.tweakermore.util.render.context.MixedRenderContext;
import me.fallenbreath.tweakermore.util.render.context.RenderContext;
import me.fallenbreath.tweakermore.util.render.context.RenderGlobals;
import net.minecraft.block.BeaconBlock;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class BeaconEffectRenderer extends CommonScannerInfoViewer
{
	private static final double FONT_SCALE = TextRenderer.DEFAULT_FONT_SCALE;
	private static final double MARGIN = 5;  // margin between icon and text
	private static final int ICON_SIZE = 18;  // the status effect icon texture is a 18x18 square
	private static final int ICON_RENDERED_SIZE = RenderUtils.TEXT_HEIGHT;

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
		if (!(blockEntity instanceof BeaconBlockEntity))
		{
			return;
		}

		BeaconBlockEntityAccessor accessor = (BeaconBlockEntityAccessor)blockEntity;
		int beaconLevel = accessor.getLevel();

		//#if MC >= 12006
		StatusEffect primary = Optional.ofNullable(accessor.getPrimary()).map(RegistryEntry::value).orElse(null);
		StatusEffect secondary = Optional.ofNullable(accessor.getSecondary()).map(RegistryEntry::value).orElse(null);
		//#else
		//$$ StatusEffect primary = accessor.getPrimary();
		//$$ StatusEffect secondary = accessor.getSecondary();
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

			Vec3d centerPos = PositionUtils.centerOf(pos);
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

				this.renderStatusEffectIcon(context, centerPos, statusEffect, amplifier, deltaX, kDeltaY);
				this.renderStatusEffectText(centerPos, statusEffect, amplifier, deltaX, kDeltaY);
			}
		}
	}

	private double calculateRowWidth(StatusEffect statusEffect, int amplifier)
	{
		double textWidth = RenderUtils.getRenderWidth(getStatusEffectText(statusEffect, amplifier));
		return ICON_RENDERED_SIZE + MARGIN + textWidth;
	}

	// TODO: client close handler
	private final Supplier<MixedRenderContext> renderContext = Suppliers.memoize(MixedRenderContext::create);

	private void renderStatusEffectIcon(RenderContext context, Vec3d pos, StatusEffect statusEffect, int amplifier, double deltaX, double kDeltaY)
	{
		var sprite = InGameHud.getEffectTexture(Registries.STATUS_EFFECT.getEntry(statusEffect));
		MixedRenderContext mrc = this.renderContext.get();
		context = RenderContext.of(RenderSystem.getModelViewStack());

		InWorldPositionTransformer positionTransformer = new InWorldPositionTransformer(pos);
		positionTransformer.apply(context);
		{
			RenderGlobals.disableDepthTest();
			RenderGlobals.enableBlend();

			context.scale(FONT_SCALE * RenderUtils.getSizeScalingXSign(), -FONT_SCALE, FONT_SCALE);
			context.translate(deltaX, 0, 0);

			// scale 2: make the rendered texture height == expected height (line height)
			double k = 1.0 * ICON_RENDERED_SIZE / ICON_SIZE;
			context.scale(k, k, k);
			context.translate(0, ICON_SIZE * (-0.5 + kDeltaY), 0);

//			mrc.pushMatrixToGuiDrawer();
			mrc.getGuiDrawer().drawGuiTexture(MaLiLibPipelines.GUI_TEXTURED_OVERLAY, sprite, 10, 10, ICON_SIZE, ICON_SIZE);
			mrc.renderGuiElements();
//			mrc.popMatrixFromGuiDrawer();

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
