package me.fallenbreath.tweakermore.impl.features.infoView;

import com.google.common.collect.Lists;
import fi.dy.masa.malilib.util.WorldUtils;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.impl.features.infoView.commandBlock.CommandBlockContentRenderer;
import me.fallenbreath.tweakermore.impl.features.infoView.redstoneDust.RedstoneDustUpdateOrderRenderer;
import me.fallenbreath.tweakermore.util.WorldUtil;
import me.fallenbreath.tweakermore.util.render.RenderContext;
import me.fallenbreath.tweakermore.util.render.TweakerMoreIRenderer;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class InfoViewRenderer implements TweakerMoreIRenderer
{
	private static final List<AbstractInfoViewer> CONTENT_PREVIEWERS = Lists.newArrayList(
			new RedstoneDustUpdateOrderRenderer(),
			new CommandBlockContentRenderer()
	);

	@Override
	public void onRenderWorldLast(RenderContext context)
	{
		if (TweakerMoreConfigs.TWEAKM_INFO_VIEW.getBooleanValue())
		{
			MinecraftClient mc = MinecraftClient.getInstance();
			World world = WorldUtils.getBestWorld(mc);
			HitResult target = mc.crosshairTarget;
			if (world != null && target instanceof BlockHitResult)
			{
				BlockPos blockPos = ((BlockHitResult) target).getBlockPos();
				BlockState blockState = world.getBlockState(blockPos);
				BlockEntity blockEntity = WorldUtil.getBlockEntity(world, blockPos);

				boolean[] synced = new boolean[]{false};
				CONTENT_PREVIEWERS.stream().
						filter(AbstractInfoViewer::isEnabled).
						filter(previewer -> previewer.shouldRenderFor(blockState, blockEntity)).
						forEach(previewer -> {
							if (!synced[0])
							{
								synced[0] = true;
								this.syncBlockEntity(world, blockPos);
							}
							previewer.render(context, world, blockPos, blockState, blockEntity);
						});
			}
		}
	}

	@SuppressWarnings("unused")
	private void syncBlockEntity(World world, BlockPos blockPos)
	{
		// serverDataSyncer do your job here
	}
}
