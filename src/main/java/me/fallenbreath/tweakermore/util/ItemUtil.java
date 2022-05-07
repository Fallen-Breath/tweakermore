package me.fallenbreath.tweakermore.util;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;

import java.util.List;
import java.util.stream.Collectors;

public class ItemUtil
{
	public static final List<Item> SHULKER_BOX_ITEMS = ImmutableList.copyOf(
			Registry.ITEM.stream().
					filter(item -> item instanceof BlockItem && ((BlockItem)item).getBlock() instanceof ShulkerBoxBlock).
					collect(Collectors.toList())
	);
}
