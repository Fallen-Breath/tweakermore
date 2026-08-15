/*
 * This file is part of the TweakerMore project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026  Fallen_Breath and contributors
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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.playerListScale;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.util.render.RenderUtils;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//#if MC < 26.1
import me.fallenbreath.tweakermore.util.render.context.RenderContext;
//#endif

//#if MC >= 26.1
//$$ import net.minecraft.client.gui.GuiGraphicsExtractor;
//#elseif MC >= 1.20
//$$ import net.minecraft.client.gui.GuiGraphics;
//#elseif MC >= 1.16
//$$ import com.mojang.blaze3d.vertex.PoseStack;
//#endif

@Mixin(PlayerTabOverlay.class)
public abstract class PlayerTabOverlayMixin
{
	// Vanilla starts the player list 10 pixels below the top edge
	@Unique
	private static final int VANILLA_TOP_OFFSET = 10;

	@Unique
	@Nullable
	private RenderUtils.Scaler playerListScaler$TKM = null;

	@Inject(
			//#if MC >= 26.1
			//$$ method = "extractRenderState",
			//#else
			method = "render",
			//#endif
			at = @At("HEAD")
	)
	private void playerListScale_push(
			//#if MC >= 26.1
			//$$ GuiGraphicsExtractor graphics,
			//#elseif MC >= 1.20
			//$$ GuiGraphics graphics,
			//#elseif MC >= 1.16
			//$$ PoseStack graphics,
			//#endif
			int screenWidth,
			Scoreboard scoreboard,
			@Nullable Objective objective,
			CallbackInfo ci
	)
	{
		this.playerListScaler$TKM = null;
		if (TweakerMoreConfigs.PLAYER_LIST_SCALE.isModified())
		{
			this.playerListScaler$TKM = RenderUtils.createScaler(
					screenWidth / 2.0,
					VANILLA_TOP_OFFSET,
					TweakerMoreConfigs.PLAYER_LIST_SCALE.getDoubleValue()
			);
			//#if MC >= 26.1
			//$$ this.playerListScaler$TKM.apply(graphics.pose());
			//#else
			this.playerListScaler$TKM.apply(RenderContext.gui(
					//#if MC >= 1.16
					//$$ graphics
					//#endif
			));
			//#endif
		}
	}

	@Inject(
			//#if MC >= 26.1
			//$$ method = "extractRenderState",
			//#else
			method = "render",
			//#endif
			at = @At("RETURN")
	)
	private void playerListScale_pop(CallbackInfo ci)
	{
		if (this.playerListScaler$TKM != null)
		{
			this.playerListScaler$TKM.restore();
			this.playerListScaler$TKM = null;
		}
	}
}
