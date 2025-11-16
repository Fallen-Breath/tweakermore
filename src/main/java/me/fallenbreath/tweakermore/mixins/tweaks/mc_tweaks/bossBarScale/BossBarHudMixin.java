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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.bossBarScale;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.util.render.RenderUtils;
import me.fallenbreath.tweakermore.util.render.context.RenderContext;
import net.minecraft.client.gui.components.BossHealthOverlay;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//#if MC >= 12000
//$$ import net.minecraft.client.gui.GuiGraphics;
//#elseif MC >= 11600
//$$ import com.mojang.blaze3d.vertex.PoseStack;
//#endif

@Mixin(BossHealthOverlay.class)
public abstract class BossBarHudMixin
{
	@Unique
	@Nullable
	private RenderUtils.Scaler scaler = null;

	@ModifyVariable(
			method = "render",
			at = @At(
					value = "INVOKE",
					target = "Ljava/util/Map;values()Ljava/util/Collection;",
					remap = false,
					ordinal = 0
			),
			ordinal = 0
	)
	private int tweakerMore_bossBarScale_push(
			int windowsWidth
			//#if MC >= 12000
			//$$ , GuiGraphics matrixStackOrDrawContext
			//#elseif MC >= 11600
			//$$ , PoseStack matrixStackOrDrawContext
			//#endif
	)
	{
		this.scaler = null;
		if (TweakerMoreConfigs.BOSS_BAR_SCALE.isModified())
		{
			this.scaler = RenderUtils.createScaler(windowsWidth / 2.0, 0, TweakerMoreConfigs.BOSS_BAR_SCALE.getDoubleValue());
			this.scaler.apply(RenderContext.gui(
					//#if MC >= 11600
					//$$ matrixStackOrDrawContext
					//#endif
			));
		}
		return windowsWidth;
	}

	@Inject(
			method = "render",
			slice = @Slice(
					from = @At(
							value = "INVOKE",
							target = "Ljava/util/Map;values()Ljava/util/Collection;",
							remap = false,
							ordinal = 0
					)
			),
			at = @At("RETURN")
	)
	private void tweakerMore_bossBarScale_pop(CallbackInfo ci)
	{
		if (this.scaler != null)
		{
			this.scaler.restore();
		}
	}
}
