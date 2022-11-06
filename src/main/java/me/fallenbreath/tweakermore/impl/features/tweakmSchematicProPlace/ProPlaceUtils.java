package me.fallenbreath.tweakermore.impl.features.tweakmSchematicProPlace;

import fi.dy.masa.litematica.materials.MaterialCache;
import net.minecraft.block.BlockState;
import net.minecraft.block.BubbleColumnBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ProPlaceUtils
{
	/**
	 * BlockState -> ItemStack via litematica's MaterialCache
	 */
	public static ItemStack getItemForState(BlockState state, World world, BlockPos pos)
	{
		ItemStack result = MaterialCache.getInstance().
					//#if MC >= 11500
					getRequiredBuildItemForState
					//#else
					//$$ getItemForState
					//#endif
					(state, world, pos);

		// apply our more overrides if MaterialCache cannot provide a good result
		if (result.isEmpty())
		{
			if (state.getBlock() instanceof BubbleColumnBlock)
			{
				result = new ItemStack(Items.WATER_BUCKET);
			}
		}

		return result;
	}
}
