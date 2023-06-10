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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.steveAlexOnlyDefaultSkins;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.TweakerMoreMod;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.util.ModIds;
import net.minecraft.client.util.DefaultSkinHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Restriction(require = @Condition(value = ModIds.minecraft, versionPredicates = ">=1.19.3"))
@Mixin(DefaultSkinHelper.class)
public abstract class DefaultSkinHelperMixin
{
	@Shadow @Final
	private static DefaultSkinHelper.Skin[] SKINS;

	@Inject(method = "getSkin", at = @At("HEAD"), cancellable = true)
	private static void steveAlexOnlyDefaultSkins_overrideAlgorithm(UUID uuid, CallbackInfoReturnable<DefaultSkinHelper.Skin> cir)
	{
		if (TweakerMoreConfigs.STEVE_ALEX_ONLY_DEFAULT_SKINS.getBooleanValue())
		{
			boolean shouldUseSlimModel = (uuid.hashCode() & 1) == 1;
			String keyword = shouldUseSlimModel ? "/alex.png" : "/steve.png";
			for (var skin : SKINS)
			{
				if (skin.texture().getPath().endsWith(keyword))
				{
					cir.setReturnValue(skin);
					return;
				}
			}
			TweakerMoreMod.LOGGER.warn("Cannot locate default skin with suffix {}", keyword);
		}
	}
}
