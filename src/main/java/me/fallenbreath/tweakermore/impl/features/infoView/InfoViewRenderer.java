package me.fallenbreath.tweakermore.impl.features.infoView;

import com.google.common.base.Suppliers;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import fi.dy.masa.malilib.event.TickHandler;
import fi.dy.masa.malilib.interfaces.IClientTickHandler;
import fi.dy.masa.malilib.util.WorldUtils;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.impl.features.infoView.commandBlock.CommandBlockContentRenderer;
import me.fallenbreath.tweakermore.impl.features.infoView.redstoneDust.RedstoneDustUpdateOrderRenderer;
import me.fallenbreath.tweakermore.impl.features.infoView.respawnBlock.RespawnBlockExplosionViewer;
import me.fallenbreath.tweakermore.util.PositionUtil;
import me.fallenbreath.tweakermore.util.ThrowawayRunnable;
import me.fallenbreath.tweakermore.util.render.RenderContext;
import me.fallenbreath.tweakermore.util.render.RenderUtil;
import me.fallenbreath.tweakermore.util.render.TweakerMoreIRenderer;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class InfoViewRenderer implements TweakerMoreIRenderer, IClientTickHandler
{
	private static final InfoViewRenderer INSTANCE = new InfoViewRenderer();
	private static final List<AbstractInfoViewer> CONTENT_PREVIEWERS = Lists.newArrayList(
			new RedstoneDustUpdateOrderRenderer(),
			new CommandBlockContentRenderer(),
			new RespawnBlockExplosionViewer()
	);

	private InfoViewRenderer()
	{
		TickHandler.getInstance().registerClientTickHandler(this);
	}

	public static InfoViewRenderer getInstance()
	{
		return INSTANCE;
	}

	@Override
	public void onRenderWorldLast(RenderContext context)
	{
		if (!TweakerMoreConfigs.TWEAKM_INFO_VIEW.getBooleanValue())
		{
			return;
		}

		List<AbstractInfoViewer> viewers = CONTENT_PREVIEWERS.stream().
				filter(AbstractInfoViewer::isRenderEnabled).
				collect(Collectors.toList());
		if (viewers.isEmpty())
		{
			return;
		}

		MinecraftClient mc = MinecraftClient.getInstance();
		World world = WorldUtils.getBestWorld(mc);
		if (world == null || mc.player == null)
		{
			return;
		}

		Vec3d camPos = mc.player.getCameraPosVec(RenderUtil.tickDelta);
		Vec3d camVec = mc.player.getRotationVec(RenderUtil.tickDelta);
		double reach = TweakerMoreConfigs.INFO_VIEW_TARGET_DISTANCE.getDoubleValue();
		double angle = Math.PI * TweakerMoreConfigs.INFO_VIEW_BEAM_ANGLE.getDoubleValue() / 2 / 180;
		HitResult target = mc.player.rayTrace(reach, RenderUtil.tickDelta, false);
		BlockPos crossHairPos = target instanceof BlockHitResult ? ((BlockHitResult) target).getBlockPos() : null;

		List<BlockPos> positions = Lists.newArrayList();
		positions.addAll(PositionUtil.beam(camPos, camPos.add(camVec.normalize().multiply(reach)), angle, PositionUtil.BeamMode.BEAM));
		if (crossHairPos != null && !positions.contains(crossHairPos))
		{
			positions.add(crossHairPos);
		}

		// sort by distance in descending order, so we render block info from far to near (simulating depth test)
		List<Pair<BlockPos, Double>> posPairs = Lists.newArrayList();
		positions.forEach(pos -> posPairs.add(Pair.of(pos, camPos.squaredDistanceTo(PositionUtil.centerOf(pos)))));
		posPairs.sort(Comparator.comparingDouble(Pair::getSecond));
		Collections.reverse(posPairs);

		ChunkCachedWorldAccess chunkCache = new ChunkCachedWorldAccess(world);
		viewers.forEach(AbstractInfoViewer::onInfoViewStart);
		for (Pair<BlockPos, Double> pair : posPairs)
		{
			BlockPos blockPos = pair.getFirst();
			boolean isCrossHairPos = blockPos.equals(crossHairPos);
			Supplier<BlockState> blockState = Suppliers.memoize(() -> chunkCache.getBlockState(blockPos));  // to avoid async block get --> faster
			Supplier<BlockEntity> blockEntity = Suppliers.memoize(() -> chunkCache.getBlockEntity(blockPos));
			ThrowawayRunnable sync = ThrowawayRunnable.of(() -> this.syncBlockEntity(world, blockPos));

			for (AbstractInfoViewer viewer : viewers)
			{
				boolean enabled = viewer.isValidTarget(isCrossHairPos);
				if (enabled && viewer.shouldRenderFor(world, blockPos, blockState.get()))
				{
					if (viewer.requireBlockEntitySyncing())
					{
						sync.run();
					}
					viewer.render(context, world, blockPos, blockState.get(), blockEntity.get());
				}
			}
		}
		viewers.forEach(AbstractInfoViewer::onInfoViewStop);
	}

	@SuppressWarnings("unused")
	private void syncBlockEntity(World world, BlockPos blockPos)
	{
		// serverDataSyncer do your job here
	}

	@Override
	public void onClientTick(MinecraftClient client)
	{
		CONTENT_PREVIEWERS.forEach(AbstractInfoViewer::onClientTick);
	}
}
