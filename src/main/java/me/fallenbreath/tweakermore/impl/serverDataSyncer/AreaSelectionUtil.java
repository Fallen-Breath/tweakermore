package me.fallenbreath.tweakermore.impl.serverDataSyncer;

import com.google.common.collect.Sets;
import fi.dy.masa.litematica.selection.AreaSelection;
import fi.dy.masa.litematica.util.PositionUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AreaSelectionUtil
{
	/**
	 * Requires mod litematica
	 */
	public static TargetPair extractBlockEntitiesAndEntities(fi.dy.masa.litematica.selection.Box box, boolean saveableOnly)
	{
		MinecraftClient mc = MinecraftClient.getInstance();
		ClientPlayNetworkHandler networkHandler = mc.getNetworkHandler();
		if (networkHandler == null || mc.player == null)
		{
			return TargetPair.none();
		}

		BlockPos pos1 = box.getPos1();
		BlockPos pos2 = box.getPos2();
		if (pos1 == null || pos2 == null)
		{
			return TargetPair.none();
		}

		int minX = Math.min(pos1.getX(), pos2.getX());
		int minY = Math.min(pos1.getY(), pos2.getY());
		int minZ = Math.min(pos1.getZ(), pos2.getZ());
		int maxX = Math.max(pos1.getX(), pos2.getX());
		int maxY = Math.max(pos1.getY(), pos2.getY());
		int maxZ = Math.max(pos1.getZ(), pos2.getZ());
		World world = mc.player.clientWorld;

		List<BlockPos> bePositions = BlockPos.stream(minX, minY, minZ, maxX, maxY, maxZ).
				map(BlockPos::toImmutable).
				// same check in fi.dy.masa.litematica.schematic.LitematicaSchematic.takeBlocksFromWorldWithinChunk
						filter(blockPos -> world.getBlockState(blockPos).getBlock().hasBlockEntity()).
				collect(Collectors.toList());

		Box aabb = new Box(minX, minY, minZ, maxX + 1, maxY + 1, maxZ + 1);
		List<Entity> entities = world.getEntities((Entity)null, aabb, entity -> entity.getType().isSaveable());

		return TargetPair.of(bePositions, entities);
	}

	/**
	 * Requires mod litematica
	 */
	public static TargetPair extractBlockEntitiesAndEntities(AreaSelection area, boolean saveableOnly)
	{
		Set<BlockPos> bePositions = Sets.newLinkedHashSet();
		Set<Entity> entities = Sets.newLinkedHashSet();
		PositionUtils.getValidBoxes(area).forEach(box -> {
			TargetPair pair = extractBlockEntitiesAndEntities(box, saveableOnly);
			bePositions.addAll(pair.getBlockEntityPositions());
			entities.addAll(pair.getEntities());
		});
		return TargetPair.of(bePositions, entities);
	}
}
