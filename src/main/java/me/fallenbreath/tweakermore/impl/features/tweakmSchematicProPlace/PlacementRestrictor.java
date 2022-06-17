package me.fallenbreath.tweakermore.impl.features.tweakmSchematicProPlace;

import fi.dy.masa.litematica.data.DataManager;
import fi.dy.masa.litematica.materials.MaterialCache;
import fi.dy.masa.litematica.util.EntityUtils;
import fi.dy.masa.litematica.util.WorldUtils;
import fi.dy.masa.litematica.world.SchematicWorldHandler;
import fi.dy.masa.malilib.util.InfoUtils;
import fi.dy.masa.malilib.util.LayerRange;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PlacementRestrictor
{
	private static void info(String key, Object... args)
	{
		if (TweakerMoreConfigs.TWEAKM_SCHEMATIC_BLOCK_PLACEMENT_RESTRICTION_HINT.getBooleanValue())
		{
			InfoUtils.printActionbarMessage("tweakermore.config.tweakmSchematicBlockPlacementRestriction." + key, args);
		}
	}

	public static boolean canDoBlockPlacement(MinecraftClient mc, ItemPlacementContext ctx)
	{
		final int MARGIN = TweakerMoreConfigs.TWEAKM_SCHEMATIC_BLOCK_PLACEMENT_RESTRICTION_MARGIN.getIntegerValue();

		BlockPos pos = ctx.getBlockPos();

		// Always permit if it's far from the schematic
		if (!WorldUtils.isPositionWithinRangeOfSchematicRegions(pos, MARGIN))
		{
			return true;
		}

		// Always permit if it's far from the render range
		LayerRange layerRange = DataManager.getRenderLayerRange();
		if (
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
		World clientWorld = mc.world;

		if (schematicWorld != null && mc.player != null && clientWorld != null && mc.interactionManager != null)
		{
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
				if (schematicState.isAir())
				{
					info("is_air");
				}
				else
				{
					info("no_block", schematicState.getBlock().getName());
				}
				return false;
			}

			// check if the player is using the right item stack for block placement
			if (EntityUtils.getUsedHandForItem(mc.player, schematicStack) == null)
			{
				info("wrong_item", schematicStack.getName());
				return false;
			}
		}

		return true;
	}
}
