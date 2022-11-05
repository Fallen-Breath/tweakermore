package me.fallenbreath.tweakermore.impl.features.tweakmSchematicProPlace.restrict;

import com.google.common.collect.Lists;
import fi.dy.masa.litematica.data.DataManager;
import fi.dy.masa.litematica.materials.MaterialCache;
import fi.dy.masa.litematica.util.EntityUtils;
import fi.dy.masa.litematica.util.WorldUtils;
import fi.dy.masa.litematica.world.SchematicWorldHandler;
import fi.dy.masa.malilib.util.InfoUtils;
import fi.dy.masa.malilib.util.LayerRange;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.config.options.listentries.SchematicBlockPlacementRestrictionHintType;
import me.fallenbreath.tweakermore.mixins.tweaks.features.tweakmSchematicProPlace.BlockItemAccessor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.enums.SlabType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.List;

public class PlacementRestrictor
{
	private static void info(String key, Object... args)
	{
		InfoUtils.printActionbarMessage("tweakermore.impl.tweakmSchematicBlockPlacementRestriction.info." + key, args);
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
		if (
				// bruteforce iterating xd
				BlockPos.stream(
						pos.add(-MARGIN, -MARGIN, -MARGIN),
						pos.add(MARGIN, MARGIN, MARGIN)
				).
				noneMatch(layerRange::isPositionWithinRange)
		)
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
						if (hintType.showNotAllowed)
						{
							info("interaction_not_allowed", result.getMessage());
						}
						return false;
					case NO_INTERACTION:
					default:
						break;
				}
			}
			// now we are sure that the player will try to do the block placement

			// -----------------------------------------------------------------------------
			// now cancel logics due to restriction not satisfied start to work

			// If the target pos is outside the layer range, cancel
			if (!layerRange.isPositionWithinRange(pos))
			{
				if (hintType.showNotPossible)
				{
					info("outside_the_layer");
				}
				return false;
			}

			BlockState schematicState = schematicWorld.getBlockState(pos);
			ItemStack schematicStack = MaterialCache.getInstance().
					//#if MC >= 11500
					getRequiredBuildItemForState
					//#else
					//$$ getItemForState
					//#endif
					(schematicState, schematicWorld, pos);

			// there's no possible block for this schematic block, cancel
			if (schematicStack.isEmpty())
			{
				if (hintType.showNotPossible)
				{
					if (schematicState.isAir())
					{
						info("is_air");
					}
					else
					{
						info("no_block", schematicState.getBlock().getName());
					}
				}
				return false;
			}

			// check if the player is using the correct item stack for block placement
			Hand usedHandForItem = EntityUtils.getUsedHandForItem(player, schematicStack);
			if (usedHandForItem == null)
			{
				if (hintType.showWrongItem)
				{
					info("wrong_item", schematicStack.getName());
				}
				return false;
			}

			// check if the will-be-placed block state is acceptable
			ItemStack stackToUse = player.getStackInHand(usedHandForItem);
			//noinspection RedundantIfStatement
			if (!checkStateToPlace(context, player, stackToUse, schematicState, hintType.showNotAllowed))
			{
				return false;
			}
		}

		return true;
	}

	private static final List<Property<?>> FACING_PROPERTIES = Lists.newArrayList(
			Properties.FACING, Properties.HORIZONTAL_FACING, Properties.HOPPER_FACING,
			Properties.ROTATION,
			Properties.BLOCK_HALF  // stairs, trapdoors
	);

	public static boolean checkStateToPlace(ItemPlacementContext context, PlayerEntity player, ItemStack stackToUse, BlockState schematicState, boolean showNotAllowed)
	{
		if (!(stackToUse.getItem() instanceof BlockItem))
		{
			return true;
		}

		// ref: net.minecraft.item.BlockItem.place(net.minecraft.item.ItemPlacementContext)
		BlockItem blockItem = (BlockItem)stackToUse.getItem();
		ItemPlacementContext ctx = blockItem.getPlacementContext(context);
		if (ctx == null)
		{
			return true;
		}
		BlockState stateToPlace = ((BlockItemAccessor)blockItem).invokeGetPlacementState(ctx);
		if (stateToPlace == null)
		{
			return true;
		}

		// not we know that the player is able to place the block down

		// block type check
		Block blockToPlace = stateToPlace.getBlock();
		if (blockToPlace != schematicState.getBlock())
		{
			if (showNotAllowed)
			{
				info("wrong_block_type", Registry.BLOCK.getId(schematicState.getBlock()));
				return false;
			}
		}

		// slab check
		if (TweakerMoreConfigs.TWEAKM_SCHEMATIC_BLOCK_PLACEMENT_RESTRICTION_CHECK_SLAB.getBooleanValue())
		{
			if (blockToPlace instanceof SlabBlock)
			{
				SlabType targetSlabType = schematicState.get(SlabBlock.TYPE);
				if (targetSlabType != SlabType.DOUBLE && stateToPlace.get(SlabBlock.TYPE) != targetSlabType)
				{
					if (showNotAllowed)
					{
						info("wrong_slab", targetSlabType);
					}
					return false;
				}
			}
		}

		// check facing, optional since sometimes we don't care about block facing
		if (TweakerMoreConfigs.TWEAKM_SCHEMATIC_BLOCK_PLACEMENT_RESTRICTION_CHECK_FACING.getBooleanValue())
		{
			for (Property<?> property : FACING_PROPERTIES)
			{
				if (stateToPlace.contains(property) && stateToPlace.get(property) != schematicState.get(property))
				{
					if (showNotAllowed)
					{
						info("wrong_facing", schematicState.get(property));
					}
					return false;
				}
			}
		}

		return true;
	}
}
