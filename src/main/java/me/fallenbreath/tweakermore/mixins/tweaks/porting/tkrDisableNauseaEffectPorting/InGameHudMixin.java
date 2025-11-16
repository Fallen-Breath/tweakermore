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

package me.fallenbreath.tweakermore.mixins.tweaks.porting.tkrDisableNauseaEffectPorting;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.impl.porting.tkrDisableNauseaEffectPorting.ClientPlayerEntityWithRealNauseaStrength;
import me.fallenbreath.tweakermore.util.ModIds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

//#if MC >= 11600
//$$ import net.minecraft.client.util.math.MatrixStack;
//#endif

@Restriction(require = @Condition(value = ModIds.minecraft, versionPredicates = "<1.17"))
@Mixin(Gui.class)
public abstract class InGameHudMixin
{
	@Shadow @Final private Minecraft minecraft;

	@ModifyVariable(
			method = "render",
			at = @At(
					value = "INVOKE_ASSIGN",
					target = "Lnet/minecraft/util/Mth;lerp(FFF)F",
					ordinal = 0
			),
			ordinal = 1
	)
	private float tkrDisableNauseaEffectPorting_makeSureNetherPortalOverlayIsNotAffected(
			float value,
			//#if MC >= 11600
			//$$ MatrixStack matrixStack,
			//#endif
			float tickDelta
	)
	{
		if (TweakerMoreConfigs.TKR_DISABLE_NAUSEA_EFFECT_PORTING.getBooleanValue())
		{
			LocalPlayer player = this.minecraft.player;

			// necessary instanceof check, since that mixin might not be applied
			if (player instanceof ClientPlayerEntityWithRealNauseaStrength)
			{
				float lastNauseaStrength = ((ClientPlayerEntityWithRealNauseaStrength)player).getRealLastNauseaStrength$TKM();
				float nextNauseaStrength = ((ClientPlayerEntityWithRealNauseaStrength)player).getRealNextNauseaStrength$TKM();
				value = Mth.lerp(tickDelta, lastNauseaStrength, nextNauseaStrength);
			}
		}
		return value;
	}
}
