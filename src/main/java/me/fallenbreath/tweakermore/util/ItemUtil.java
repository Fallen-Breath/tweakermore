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

package me.fallenbreath.tweakermore.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ItemUtil
{
	public static final List<Item> SHULKER_BOX_ITEMS = ImmutableList.copyOf(
			Registry.ITEM.stream().
					filter(item -> item instanceof BlockItem && ((BlockItem)item).getBlock() instanceof ShulkerBoxBlock).
					collect(Collectors.toList())
	);
	private static final Set<Item> SHULKER_BOX_ITEMS_SET = ImmutableSet.copyOf(SHULKER_BOX_ITEMS);

	public static boolean isShulkerBox(Item item)
	{
		return SHULKER_BOX_ITEMS_SET.contains(item);
	}
}
