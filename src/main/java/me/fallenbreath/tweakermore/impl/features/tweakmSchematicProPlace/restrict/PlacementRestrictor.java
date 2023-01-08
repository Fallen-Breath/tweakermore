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

package me.fallenbreath.tweakermore.impl.features.tweakmSchematicProPlace.restrict;

import com.google.common.collect.Lists;
import fi.dy.masa.litematica.data.DataManager;
import fi.dy.masa.litematica.util.WorldUtils;
import fi.dy.masa.litematica.world.SchematicWorldHandler;
import fi.dy.masa.malilib.util.InfoUtils;
import fi.dy.masa.malilib.util.LayerRange;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.config.options.listentries.SchematicBlockPlacementRestrictionHintType;
import me.fallenbreath.tweakermore.impl.features.tweakmSchematicProPlace.ProPlaceUtils;
import net.minecraft.block.*;
import net.minecraft.block.enums.SlabType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;

public class PlacementRestrictor
{
	private static final List<Property<?>> FACING_PROPERTIES = Lists.newArrayList(
			Properties.FACING, Properties.HORIZONTAL_FACING, Properties.HOPPER_FACING,
			Properties.ROTATION,
			Properties.BLOCK_HALF,  // stairs, trapdoors
			Properties.AXIS, Properties.HORIZONTAL_AXIS  // logs, pillars
	);

	private static void info(boolean condition, String key, Object... args)
	{
		if (condition)
		{
			InfoUtils.printActionbarMessage("tweakermore.impl.tweakmSchematicBlockPlacementRestriction.info." + key, args);
		}
	}

	public static boolean canDoBlockPlacement(MinecraftClient mc, BlockHitResult hitResult, ItemPlacementContext context)
	{
		final int MARGIN = TweakerMoreConfigs.TWEAKM_SCHEMATIC_BLOCK_PLACEMENT_RESTRICTION_MARGIN.getIntegerValue();
		SchematicBlockPlacementRestrictionHintType hintType = (SchematicBlockPlacementRestrictionHintType)TweakerMoreConfigs.TWEAKM_SCHEMATIC_BLOCK_PLACEMENT_RESTRICTION_HINT.getOptionListValue();
		BlockPos pos = context.getBlockPos();

		// Always permit if it's far from the schematic
		if (!WorldUtils.isPositionWithinRangeOfSchematicRegions(pos, MARGIN))
		{
			return true;
		}

		// Always permit if it's far from the render range
		LayerRange layerRange = DataManager.getRenderLayerRange();
		if (!RestrictionUtils.isWithinLayerRange(layerRange, pos, MARGIN))
		{
			return true;
		}

		World schematicWorld = SchematicWorldHandler.getSchematicWorld();
		World realWorld = context.getWorld();
		PlayerEntity player = context.getPlayer();

		if (schematicWorld != null && player != null && mc.interactionManager != null)
		{
			// check if the player will interact with the block
			{
				BlockPos interactPos = hitResult.getBlockPos();
				BlockState interactWorldState = realWorld.getBlockState(interactPos);
				BlockState interactSchematicState = schematicWorld.getBlockState(interactPos);
				BlockInteractionRestrictor.Result result = BlockInteractionRestrictor.checkInteract(player, interactWorldState, interactSchematicState);
				switch (result.getType())
				{
					case GOOD_INTERACTION:
						// The right click action will be consumed, no block placement which means no need to check
						return true;
					case BAD_INTERACTION:
						info(hintType.showNotAllowed, "interaction_not_allowed", result.getMessage());
						return false;
					default:
						break;
				}
			}
			// now we are sure that the player will try to do the block placement

			BlockState schematicState = schematicWorld.getBlockState(pos);
			Block schematicBlock = schematicState.getBlock();
			ItemStack schematicStack = ProPlaceUtils.getItemForState(schematicState, schematicWorld, pos);
			ItemStack stackToUse = RestrictionUtils.getPlayerUsingStack(player);

			// whitelist logic for e.g. scaffolding blocks
			if (RestrictionUtils.isItemInRestrictorWhitelist(stackToUse))
			{
				return true;
			}

			// -----------------------------------------------------------------------------
			//       now cancel logics due to restriction not satisfied start to work
			// -----------------------------------------------------------------------------

			// If the target pos is outside the layer range, cancel
			if (!layerRange.isPositionWithinRange(pos))
			{
				info(hintType.showNotPossible, "outside_the_layer");
				return false;
			}

			if (!LooseCaseChecker.isLooseCheckSpecialCase(schematicState, schematicStack, stackToUse))
			{
				// there's no possible block for this schematic block, cancel
				if (schematicStack.isEmpty())
				{
					info(hintType.showNotPossible, schematicState.isAir() ? "is_air" : "no_block", schematicBlock.getName());
					return false;
				}

				// check if the player is using the correct item stack for block placement
				if (stackToUse.getItem() != schematicStack.getItem())
				{
					info(hintType.showWrongItem, "wrong_item", schematicStack.getName());
					return false;
				}
			}

			// check if the will-be-placed block state is acceptable
			Optional<BlockState> stateToPlaceOptional = RestrictionUtils.getStateToPlace(context, stackToUse);
			if (stateToPlaceOptional.isPresent())
			{
				// now we know that the player is able to place the block down
				BlockState stateToPlace = stateToPlaceOptional.get();
				Block blockToPlace = stateToPlace.getBlock();

				// block type check
				if (!isBlockToPlaceCorrect(schematicState, stateToPlace))
				{
					info(hintType.showNotAllowed, "wrong_block_type", Registry.BLOCK.getId(schematicBlock));
					return false;
				}

				// block state check
				// notes that blockToPlace might != schematicBlock
				{
					// slab check
					if (TweakerMoreConfigs.TWEAKM_SCHEMATIC_BLOCK_PLACEMENT_RESTRICTION_CHECK_SLAB.getBooleanValue())
					{
						if (blockToPlace instanceof SlabBlock && schematicBlock instanceof SlabBlock)
						{
							SlabType targetSlabType = schematicState.get(SlabBlock.TYPE);
							if (targetSlabType != SlabType.DOUBLE && stateToPlace.get(SlabBlock.TYPE) != targetSlabType)
							{
								info(hintType.showNotAllowed, "wrong_slab", targetSlabType);
								return false;
							}
						}
					}

					// facing properties check
					if (TweakerMoreConfigs.TWEAKM_SCHEMATIC_BLOCK_PLACEMENT_RESTRICTION_CHECK_FACING.getBooleanValue())
					{
						for (Property<?> property : FACING_PROPERTIES)
						{
							if (stateToPlace.contains(property) && schematicState.contains(property) && stateToPlace.get(property) != schematicState.get(property))
							{
								info(hintType.showNotAllowed, "wrong_facing", schematicState.get(property));
								return false;
							}
						}
					}
				}
			}
		}

		return true;
	}

	private static boolean isBlockToPlaceCorrect(BlockState schematicState, BlockState stateToPlace)
	{
		Block schematicBlock = schematicState.getBlock();
		Block blockToPlace = stateToPlace.getBlock();

		// special case
		// check again using the calculated blockToPlace instead of the block from BlockItem.getBlock()
		// just in case the real blockToPlace unequals to BlockItem.getBlock()
		if (LooseCaseChecker.isLooseCheckSpecialCase(schematicState, blockToPlace))
		{
			return true;
		}

		// flow pot
		if (schematicBlock instanceof FlowerPotBlock && blockToPlace == Blocks.FLOWER_POT)
		{
			return true;
		}

		// 1.17+ cauldron variants things
		// notes that preprocessor remapper will remap CauldronBlock into AbstractCauldronBlock in 1.17+
		if (schematicBlock instanceof CauldronBlock && blockToPlace == Blocks.CAULDRON)
		{
			return true;
		}

		// direct block check
		// the blockToPlace needs to be exactly equaled to schematicBlock
		return blockToPlace == schematicBlock;
	}
}
