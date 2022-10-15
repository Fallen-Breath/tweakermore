package me.fallenbreath.tweakermore.impl.features.infoView.redstoneDust;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.impl.features.infoView.AbstractInfoViewer;
import me.fallenbreath.tweakermore.util.render.RenderContext;
import me.fallenbreath.tweakermore.util.render.TextRenderer;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public class RedstoneDustUpdateOrderRenderer extends AbstractInfoViewer
{
	public RedstoneDustUpdateOrderRenderer()
	{
		super(TweakerMoreConfigs.INFO_VIEW_REDSTONE_DUST_UPDATE_ORDER, TweakerMoreConfigs.INFO_VIEW_REDSTONE_DUST_UPDATE_ORDER_STRATEGY);
	}

	@Override
	public boolean shouldRenderFor(World world, BlockPos blockPos, BlockState blockState, BlockEntity blockEntity)
	{
		return blockState.getBlock() instanceof RedstoneWireBlock;
	}

	@Override
	public void render(RenderContext context, World world, BlockPos blockPos, BlockState blockState, BlockEntity blockEntity)
	{
		int alphaBits = (int)Math.round(255 * TweakerMoreConfigs.INFO_VIEW_REDSTONE_DUST_UPDATE_ORDER_TEXT_ALPHA.getDoubleValue());
		if (alphaBits == 0)
		{
			return;
		}
		int color = Objects.requireNonNull(Formatting.RED.getColorValue()) | ((alphaBits & 0xFF) << 24);

		List<BlockPos> order = getDustUpdateOrderAt(blockPos);
		for (int i = 0; i < order.size(); i++)
		{
			this.renderTextAtPos(order.get(i), String.valueOf(i + 1), color);
		}
	}

	private void renderTextAtPos(BlockPos pos, String text, int color)
	{
		TextRenderer.create().
				text(text).atCenter(pos).
				color(color).
				shadow().seeThrough().
				render();
	}

	/**
	 * Yeah, that's how dust generate the update emitting order
	 */
	private static List<BlockPos> getDustUpdateOrderAt(BlockPos pos)
	{
		Set<BlockPos> set = Sets.newHashSet();
		set.add(pos);
		for (Direction direction : Direction.values())
		{
			set.add(pos.offset(direction));
		}
		return Lists.newArrayList(set);
	}
}
