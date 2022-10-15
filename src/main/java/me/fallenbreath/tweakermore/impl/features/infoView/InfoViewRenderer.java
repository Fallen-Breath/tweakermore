package me.fallenbreath.tweakermore.impl.features.infoView;

import com.google.common.base.Suppliers;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import fi.dy.masa.malilib.util.WorldUtils;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.impl.features.infoView.commandBlock.CommandBlockContentRenderer;
import me.fallenbreath.tweakermore.impl.features.infoView.redstoneDust.RedstoneDustUpdateOrderRenderer;
import me.fallenbreath.tweakermore.impl.features.infoView.respawnBlock.RespawnBlockExplosionViewer;
import me.fallenbreath.tweakermore.util.PositionUtil;
import me.fallenbreath.tweakermore.util.ThrowawayRunnable;
import me.fallenbreath.tweakermore.util.WorldUtil;
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

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class InfoViewRenderer implements TweakerMoreIRenderer
{
	private static final List<AbstractInfoViewer> CONTENT_PREVIEWERS = Lists.newArrayList(
			new RedstoneDustUpdateOrderRenderer(),
			new CommandBlockContentRenderer(),
			new RespawnBlockExplosionViewer()
	);

	@Override
	public void onRenderWorldLast(RenderContext context)
	{
		if (!TweakerMoreConfigs.TWEAKM_INFO_VIEW.getBooleanValue())
		{
			return;
		}

		List<AbstractInfoViewer> viewers = CONTENT_PREVIEWERS.stream().
				filter(AbstractInfoViewer::isConfigEnabled).
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

		HitResult target = mc.crosshairTarget;
		BlockPos crossHairPos = target instanceof BlockHitResult ? ((BlockHitResult) target).getBlockPos() : null;
		Vec3d camPos = mc.player.getCameraPosVec(RenderUtil.tickDelta);
		Vec3d camVec = mc.player.getRotationVec(RenderUtil.tickDelta);
		double reach = TweakerMoreConfigs.INFO_VIEW_BEAM_DISTANCE.getDoubleValue();
		Set<BlockPos> positions = Sets.newHashSet();
		if (crossHairPos != null)
		{
			positions.add(crossHairPos);
		}
		double angle = Math.PI * TweakerMoreConfigs.INFO_VIEW_BEAM_CONE_ANGLE.getDoubleValue() / 2 / 180;
		PositionUtil.beam(camPos, camPos.add(camVec.normalize().multiply(reach)), angle).
				forEach(positions::add);

		positions.stream().
				sorted(Comparator.comparingDouble(pos -> camPos.squaredDistanceTo(PositionUtil.centerOf((BlockPos)pos))).reversed()).
				forEach(blockPos -> {
					boolean isCrossHairPos = blockPos.equals(crossHairPos);
					Supplier<BlockState> blockState = Suppliers.memoize(() -> world.getBlockState(blockPos));
					Supplier<BlockEntity> blockEntity = Suppliers.memoize(() -> WorldUtil.getBlockEntity(world, blockPos));
					ThrowawayRunnable sync = ThrowawayRunnable.of(() -> this.syncBlockEntity(world, blockPos));
					viewers.stream().
							filter(viewer -> {
								boolean enabled = viewer.isInBeamRangeViewEnabled() || (isCrossHairPos && viewer.isDirectViewEnabled());
								return enabled && viewer.shouldRenderFor(world, blockPos, blockState.get(), blockEntity.get());
							}).
							forEach(previewer -> {
								sync.run();
								previewer.render(context, world, blockPos, blockState.get(), blockEntity.get());
							});
				});
	}

	@SuppressWarnings("unused")
	private void syncBlockEntity(World world, BlockPos blockPos)
	{
		// serverDataSyncer do your job here
	}
}
