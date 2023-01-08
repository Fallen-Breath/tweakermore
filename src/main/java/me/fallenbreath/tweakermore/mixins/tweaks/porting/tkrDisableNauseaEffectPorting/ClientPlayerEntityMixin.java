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
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static me.fallenbreath.tweakermore.util.ModIds.minecraft;

@Restriction(require = @Condition(value = minecraft, versionPredicates = "<1.17"))
@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin implements ClientPlayerEntityWithRealNauseaStrength
{
	@Shadow public float lastNauseaStrength;

	@Shadow public float nextNauseaStrength;

	private float realLastNauseaStrength$TKM = -1;
	private float realNextNauseaStrength$TKM = -1;

	@Override
	public float getRealLastNauseaStrength()
	{
		return this.realLastNauseaStrength$TKM;
	}

	@Override
	public float getRealNextNauseaStrength()
	{
		return this.realNextNauseaStrength$TKM;
	}

	@Inject(method = "updateNausea", at = @At("HEAD"))
	private void tkrDisableNauseaEffectPorting_restoreRealValues(CallbackInfo ci)
	{
		if (TweakerMoreConfigs.TKR_DISABLE_NAUSEA_EFFECT_PORTING.getBooleanValue())
		{
			if (this.realLastNauseaStrength$TKM >= 0)
			{
				this.lastNauseaStrength = this.realLastNauseaStrength$TKM;
			}
			if (this.realNextNauseaStrength$TKM >= 0)
			{
				this.nextNauseaStrength = this.realNextNauseaStrength$TKM;
			}
		}
	}

	@Inject(method = "updateNausea", at = @At("TAIL"))
	private void tkrDisableNauseaEffectPorting_storeRealValuesAndUseFakeValues(CallbackInfo ci)
	{
		if (TweakerMoreConfigs.TKR_DISABLE_NAUSEA_EFFECT_PORTING.getBooleanValue())
		{
			this.realLastNauseaStrength$TKM = this.lastNauseaStrength;
			this.realNextNauseaStrength$TKM = this.nextNauseaStrength;
			this.lastNauseaStrength = 0;
			this.nextNauseaStrength = 0;
		}
	}
}
