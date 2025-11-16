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

package me.fallenbreath.tweakermore.mixins.util.qol;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.util.mixin.testers.DevelopmentEnvironmentTester;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Redirect;

// impl in mc [1.16, 1.20.4)
@Restriction(require = @Condition(type = Condition.Type.TESTER, tester = DevelopmentEnvironmentTester.class))
@Mixin(Minecraft.class)
public abstract class MinecraftClientMixin
{
	@Redirect(
			//#if MC >= 11800
			//$$ method = "createUserApiService",
			//#elseif MC >= 11700
			//$$ method = "createSocialInteractionsService",
			//#else
			method = "method_31382",
			//#endif
			at = @At(
					value = "INVOKE",
					//#if MC >= 11800
					//$$ target = "Lorg/slf4j/Logger;error(Ljava/lang/String;Ljava/lang/Throwable;)V",
					//#else
					target = "Lorg/apache/logging/log4j/Logger;error(Ljava/lang/String;Ljava/lang/Throwable;)V",
					//#endif
					remap = false
			),
			remap = true,
			require = 0
	)
	private void stopLoggingAuthenticationVerifyFailureInDevelopmentEnvironment(@Coerce Object logger, String s, Throwable throwable)
	{
		// do nothing
	}
}
