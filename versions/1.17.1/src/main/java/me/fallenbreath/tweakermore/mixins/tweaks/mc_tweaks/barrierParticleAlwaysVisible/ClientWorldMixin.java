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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.barrierParticleAlwaysVisible;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * mc1.14         : subproject 1.14.4
 * mc1.15 ~ mc1.16: subproject 1.15.2 (main project)
 * mc1.17+        : subproject 1.17.1        <--------
 */
@Mixin(ClientWorld.class)
public abstract class ClientWorldMixin
{
	@ModifyExpressionValue(
			method = "getBlockParticle",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/item/ItemStack;getItem()Lnet/minecraft/item/Item;",
					ordinal = 0
			)
	)
	private Item barrierParticleAlwaysVisible_pretendToHoldingABarrierItem(Item item)
	{
		if (TweakerMoreConfigs.BARRIER_PARTICLE_ALWAYS_VISIBLE.getBooleanValue())
		{
			return Items.BARRIER;
		}
		return item;
	}
}
