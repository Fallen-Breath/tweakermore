package me.fallenbreath.tweakermore.impl.features.tweakmSchematicProPlace.restrict;

import fi.dy.masa.malilib.util.LayerRange;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.mixins.tweaks.features.tweakmSchematicProPlace.BlockItemAccessor;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

import java.util.Optional;

class RestrictionUtils
{
	public static boolean isItemInRestrictorWhitelist(ItemStack itemStack)
	{
		return TweakerMoreConfigs.TWEAKM_SCHEMATIC_BLOCK_PLACEMENT_RESTRICTION_ITEM_WHITELIST.getStrings().
				stream().
				map(itemId -> Registry.ITEM.get(new Identifier(itemId))).
				anyMatch(item -> item == itemStack.getItem());
	}

	public static boolean isWithinLayerRange(LayerRange layerRange, BlockPos pos, int margin)
	{
		return layerRange.intersectsBox(
				pos.add(-margin, -margin, -margin),
				pos.add(margin, margin, margin)
		);
	}

	/**
	 * Just a simple check. Not perfect, but it's safe enough
	 */
	public static ItemStack getPlayerUsingStack(PlayerEntity player)
	{
		ItemStack stackToUse = player.getMainHandStack();
		if (stackToUse.isEmpty())
		{
			stackToUse = player.getOffHandStack();
		}
		return stackToUse;
	}

	/**
	 * What will the block state be like if the player does the block placement
	 */
	public static Optional<BlockState> getStateToPlace(ItemPlacementContext context, ItemStack stackToUse)
	{
		if (stackToUse.getItem() instanceof BlockItem)
		{
			// ref: net.minecraft.item.BlockItem.place(net.minecraft.item.ItemPlacementContext)
			BlockItem blockItem = (BlockItem) stackToUse.getItem();
			ItemPlacementContext ctx = blockItem.getPlacementContext(context);
			if (ctx != null)
			{
				return Optional.ofNullable(((BlockItemAccessor) blockItem).invokeGetPlacementState(ctx));
			}
		}
		return Optional.empty();
	}
}
