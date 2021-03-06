package me.fallenbreath.tweakermore.impl.features.showRedstoneDustUpdateOrder;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.util.render.RenderContext;
import me.fallenbreath.tweakermore.util.render.RenderUtil;
import me.fallenbreath.tweakermore.util.render.TweakerMoreIRenderer;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public class RedstoneDustUpdateOrderRenderer implements TweakerMoreIRenderer
{
	@Override
	public void onRenderWorldLast(RenderContext context)
	{
		if (!TweakerMoreConfigs.TWEAKM_SHOW_REDSTONE_DUST_UPDATE_ORDER.getBooleanValue())
		{
			return;
		}

		MinecraftClient mc = MinecraftClient.getInstance();
		if (mc.world!= null && mc.crosshairTarget instanceof BlockHitResult)
		{
			BlockPos pos = ((BlockHitResult)mc.crosshairTarget).getBlockPos();
			if (mc.world.getBlockState(pos).getBlock() instanceof RedstoneWireBlock)
			{
				int alphaBits = (int)Math.round(255 * TweakerMoreConfigs.REDSTONE_DUST_UPDATE_ORDER_TEXT_ALPHA.getDoubleValue());
				if (alphaBits == 0)
				{
					return;
				}
				int color = Objects.requireNonNull(Formatting.RED.getColorValue()) | ((alphaBits & 0xFF) << 24);

				List<BlockPos> order = getDustUpdateOrderAt(pos);
				for (int i = 0; i < order.size(); i++)
				{
					this.renderTextAtPos(order.get(i), String.valueOf(i + 1), color);
				}
			}
		}
	}

	private void renderTextAtPos(BlockPos pos, String text, int color)
	{
		double x = pos.getX() + 0.5;
		double y = pos.getY() + 0.5;
		double z = pos.getZ() + 0.5;

		RenderUtil.drawString(text, x, y, z, 0.02F, color, true, true);
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
