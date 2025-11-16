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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.disableResourcePackListResetOnFailure;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

// impl in mc1.15+
@Mixin(Minecraft.class)
public abstract class MinecraftClientMixin
{
	@WrapWithCondition(
			//#if MC >= 11600
			//$$ method = "clearResourcePacksOnError",
			//#else
			method = "rollbackResourcePacks",
			//#endif
			at = @At(
					value = "INVOKE",
					target = "Ljava/util/List;clear()V"
			),
			require = 2,
			allow = 2
	)
	private boolean disableResourcePackListResetOnFailure_doNotClearTheList(List<?> instance)
	{
		return !TweakerMoreConfigs.DISABLE_RESOURCE_PACK_LIST_RESET_ON_FAILURE.getBooleanValue();
	}
}
