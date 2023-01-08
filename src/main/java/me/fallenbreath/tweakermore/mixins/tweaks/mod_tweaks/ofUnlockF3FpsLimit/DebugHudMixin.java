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

package me.fallenbreath.tweakermore.mixins.tweaks.mod_tweaks.ofUnlockF3FpsLimit;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.util.ModIds;
import net.minecraft.client.gui.hud.DebugHud;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("target")
@Restriction(require = {@Condition(ModIds.optifine), @Condition(value = ModIds.minecraft, versionPredicates = ">=1.15")})
@Mixin(DebugHud.class)
public abstract class DebugHudMixin
{
	@Dynamic("Added by optifine")
	@Shadow(remap = false)
	private long updateInfoLeftTimeMs;

	@Dynamic("Added by optifine")
	@Shadow(remap = false)
	private long updateInfoRightTimeMs;

	@Inject(method = "render", at = @At("TAIL"))
	private void cancelOptFpsLimit(CallbackInfo ci)
	{
		if (TweakerMoreConfigs.OF_UNLOCK_F3_FPS_LIMIT.getBooleanValue())
		{
			this.updateInfoLeftTimeMs = 0;
			this.updateInfoRightTimeMs = 0;
		}
	}
}