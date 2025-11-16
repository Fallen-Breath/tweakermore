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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.bossBarMaxEntry;

import com.google.common.collect.Iterators;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.client.gui.components.LerpingBossEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Iterator;

@Mixin(BossHealthOverlay.class)
public abstract class BossBarHudMixin
{
	@ModifyVariable(
			method = "render",
			at = @At(
					value = "INVOKE_ASSIGN",
					target = "Ljava/util/Collection;iterator()Ljava/util/Iterator;",
					remap = false
			)
	)
	private Iterator<LerpingBossEvent> tweakerMore_bossBarMaxEntry_checkLimitation(Iterator<LerpingBossEvent> iterator)
	{
		int limitation = TweakerMoreConfigs.BOSS_BAR_MAX_ENTRY.getIntegerValue();
		if (limitation >= 0)
		{
			iterator = Iterators.limit(iterator, limitation);
		}
		return iterator;
	}

	@ModifyExpressionValue(
			method = "render",
			at = @At(
					value = "INVOKE",
					//#if MC >= 12001
					//$$ target = "Lnet/minecraft/client/gui/DrawContext;getScaledWindowHeight()I"
					//#else
					target = "Lcom/mojang/blaze3d/platform/Window;getGuiScaledHeight()I"
					//#endif
			),
			require = 0
	)
	private int tweakerMore_bossBarMaxEntry_skipVanillaCheck(int scaledHeight)
	{
		if (TweakerMoreConfigs.BOSS_BAR_MAX_ENTRY.getIntegerValue() >= 0)
		{
			scaledHeight = Integer.MAX_VALUE;
		}
		return scaledHeight;
	}
}
