/*
 * This file is part of the TweakerMore project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2024  Fallen_Breath and contributors
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

package me.fallenbreath.tweakermore.impl.mc_tweaks.clientEntityTargetingSelectAll;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

public class EntityItemPickHelper
{
	@Nullable
	public static ItemStack pickItem(Entity entity)
	{
		if (entity instanceof ItemEntity)
		{
			return ((ItemEntity)entity).getItem().copy();
		}
		else if (entity instanceof ExperienceOrb)
		{
			return new ItemStack(Items.EXPERIENCE_BOTTLE);
		}
		else if (entity instanceof FallingBlockEntity)
		{
			Block block = ((FallingBlockEntity)entity).getBlockState().getBlock();
			ItemStack itemStack = new ItemStack(block);
			return itemStack.isEmpty() ? null : itemStack;
		}

		return null;
	}
}
