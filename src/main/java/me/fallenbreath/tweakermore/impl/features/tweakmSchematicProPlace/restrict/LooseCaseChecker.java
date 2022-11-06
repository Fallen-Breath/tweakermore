package me.fallenbreath.tweakermore.impl.features.tweakmSchematicProPlace.restrict;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.mixins.tweaks.features.tweakmSchematicProPlace.CoralBlockAccessor;
import me.fallenbreath.tweakermore.mixins.tweaks.features.tweakmSchematicProPlace.CoralFanBlockAccessor;
import me.fallenbreath.tweakermore.mixins.tweaks.features.tweakmSchematicProPlace.CoralWallFanBlockAccessor;
import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.FluidTags;

import java.util.Optional;

public class LooseCaseChecker
{
	private static boolean isStrict()
	{
		return TweakerMoreConfigs.TWEAKM_SCHEMATIC_BLOCK_PLACEMENT_RESTRICTION_STRICT.getBooleanValue();
	}

	/**
	 * Item using check
	 */
	public static boolean isLooseCheckSpecialCase(BlockState schematicState, ItemStack schematicStack, ItemStack stackToUse)
	{
		if (isStrict())
		{
			return false;
		}

		Item itemToUse = stackToUse.getItem();
		if (itemToUse instanceof BlockItem)
		{
			Block itemToUseBlock = ((BlockItem)itemToUse).getBlock();

			// normal coral is ok if dead coral is required
			if (checkDeadCoral(schematicState, schematicStack, stackToUse))
			{
				return true;
			}

			//noinspection RedundantIfStatement
			if (checkIce(schematicState, itemToUseBlock))
			{
				return true;
			}
		}

		return false;
	}

	/**
	 * Block placement check
	 */
	public static boolean isLooseCheckSpecialCase(BlockState schematicState, Block blockToPlace)
	{
		return !isStrict() && (
				checkDeadCoral(schematicState, blockToPlace) ||
				checkIce(schematicState, blockToPlace)
		);

	}

	/*
	 * -----------------------------------------------
	 *             Detailed Implementations
	 * -----------------------------------------------
	 */

	/**
	 * Normal coral is ok if dead coral is required
	 */
	private static boolean checkDeadCoral(BlockState schematicState, ItemStack schematicStack, ItemStack itemToUse)
	{
		if (isDeadCoral(schematicState.getBlock()))
		{
			Optional<Item> toUseAsDead = coralLiving2Dead(itemToUse.getItem());
			return toUseAsDead.isPresent() && toUseAsDead.get() == schematicStack.getItem();
		}
		return false;
	}
	private static boolean checkDeadCoral(BlockState schematicState, Block blockToPlace)
	{
		Block schematicBlock = schematicState.getBlock();
		if (isDeadCoral(schematicBlock))
		{
			Optional<Block> deadCoralToPlace = coralLiving2Dead(blockToPlace);
			return deadCoralToPlace.isPresent() && deadCoralToPlace.get() == schematicBlock;
		}
		return false;
	}

	/**
	 * Ice is ok if target block is bubble column or waterlogged
	 */
	private static boolean checkIce(BlockState schematicState, Block blockToPlace)
	{
		return blockToPlace instanceof IceBlock && schematicState.getFluidState().matches(FluidTags.WATER);
	}

	/*
	 * -----------------------------------------------
	 *                     Utils
	 * -----------------------------------------------
	 */

	// notes: check this stupid coral classes inheritance tree before reading these coral util methods below:
	// CoralParentBlock
	//     CoralBlock
	//     DeadCoralBlock
	//     DeadCoralFanBlock
	//         CoralFanBlock
	//         DeadCoralWallFanBlock
	//             CoralWallFanBlock

	private static Optional<Block> coralLiving2Dead(Block livingCoral)
	{
		Block deadCoral = null;
		if (livingCoral instanceof CoralBlock)
		{
			deadCoral = ((CoralBlockAccessor)livingCoral).getDeadCoralBlock();
		}
		else if (livingCoral instanceof CoralFanBlock)
		{
			deadCoral = ((CoralFanBlockAccessor)livingCoral).getDeadCoralBlock();
		}
		else if (livingCoral instanceof CoralWallFanBlock)
		{
			deadCoral = ((CoralWallFanBlockAccessor)livingCoral).getDeadCoralBlock();
		}
		return Optional.ofNullable(deadCoral);
	}

	private static Optional<Item> coralLiving2Dead(Item livingCoral)
	{
		if (livingCoral instanceof BlockItem)
		{
			return coralLiving2Dead(((BlockItem)livingCoral).getBlock()).
					map(Block::asItem);
		}
		return Optional.empty();
	}

	private static boolean isLivingCoral(Block block)
	{
		return coralLiving2Dead(block).isPresent();
	}

	private static boolean isDeadCoral(Block block)
	{
		return block instanceof DeadCoralBlock ||
				block.getClass() == DeadCoralFanBlock.class ||
				block.getClass() == DeadCoralWallFanBlock.class;
	}
}
