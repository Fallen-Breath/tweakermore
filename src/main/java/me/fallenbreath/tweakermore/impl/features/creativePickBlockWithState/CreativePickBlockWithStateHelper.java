/*
 * This file is part of the TweakerMore project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026  Fallen_Breath and contributors
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

package me.fallenbreath.tweakermore.impl.features.creativePickBlockWithState;

import fi.dy.masa.malilib.util.InfoUtils;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

//#if MC >= 12006
//$$ import com.google.common.collect.Maps;
//$$ import net.minecraft.core.component.DataComponents;
//$$ import net.minecraft.world.item.component.BlockItemStateProperties;
//$$ import java.util.Map;
//#else
import net.minecraft.nbt.CompoundTag;
//#endif

public class CreativePickBlockWithStateHelper
{
	public static void storeBlockState(boolean isCreative, ItemStack itemStack, BlockState blockState, Block block)
	{
		if (!isCreative || itemStack.isEmpty())
		{
			return;
		}

		if (!TweakerMoreConfigs.CREATIVE_PICK_BLOCK_WITH_STATE.isKeybindHeld())
		{
			return;
		}

		Item item = itemStack.getItem();

		// make sure the picked item is exactly what the selected block indicates
		// to avoid things like storing piston head's states into piston item which is not good
		if (item instanceof BlockItem && ((BlockItem)item).getBlock() != blockState.getBlock())
		{
			return;
		}

		//#if MC >= 12006
		//$$ Map<String, String> properties = Maps.newLinkedHashMap();
		//#if MC >= 26.1
		//$$ blockState.getValues().forEach(value -> properties.put(value.property().getName(), value.value().toString()));
		//#else
		//$$ blockState.getValues().forEach((property, value) -> {
		//$$ 	properties.put(property.getName(), value.toString());
		//$$ });
		//#endif
		//$$ itemStack.set(DataComponents.BLOCK_STATE, new BlockItemStateProperties(properties));
		//#else
		CompoundTag nbt = new CompoundTag();
		blockState.getValues().forEach((property, value) -> {
			nbt.putString(property.getName(), value.toString());
		});
		itemStack.getOrCreateTag().put("BlockStateTag", nbt);
		//#endif

		InfoUtils.printActionbarMessage("tweakermore.impl.creativePickBlockWithState.message", block.getName());
	}
}
