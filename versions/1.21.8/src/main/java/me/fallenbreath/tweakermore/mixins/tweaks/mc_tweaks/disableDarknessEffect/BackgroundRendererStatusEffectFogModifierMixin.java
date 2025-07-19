/*
 * This file is part of the TweakerMore project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2025  Fallen_Breath and contributors
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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.disableDarknessEffect;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.util.ModIds;
import net.minecraft.client.render.fog.DarknessEffectFogModifier;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * mc1.14.4 ~ mc1.19.4: subproject 1.15.2 (main project)
 * mc1.19.4 ~ mc1.21.5: subproject 1.19.4
 * mc1.21.6+          : subproject 1.21.8        <--------
 */
@Restriction(require = @Condition(value = ModIds.minecraft, versionPredicates = ">=1.19"))
@Mixin(DarknessEffectFogModifier.class)
public abstract class BackgroundRendererStatusEffectFogModifierMixin
{
	@ModifyExpressionValue(
			method = "applyDarknessModifier",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/render/fog/DarknessEffectFogModifier;getStatusEffect()Lnet/minecraft/registry/entry/RegistryEntry;"
			)
	)
	private RegistryEntry<StatusEffect> disableDarknessEffect_doNotApplyIfItIsDarknessEffect(RegistryEntry<StatusEffect> statusEffect)
	{
		if (TweakerMoreConfigs.DISABLE_DARKNESS_EFFECT.getBooleanValue())
		{
			statusEffect = null;
		}
		return statusEffect;
	}
}
