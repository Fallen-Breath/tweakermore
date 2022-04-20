package me.fallenbreath.tweakermore.impl.showRedstoneDustUpdateOrder;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import fi.dy.masa.malilib.interfaces.IRenderer;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.util.render.RenderUtil;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

//#if MC >= 11700
//$$ import net.minecraft.util.math.Matrix4f;
//#endif

//#if MC >= 11500
import net.minecraft.client.util.math.MatrixStack;
//#endif

import java.util.List;
import java.util.Objects;
import java.util.Set;

public class RedstoneDustUpdateOrderRenderer implements IRenderer
{
	@Override
	public void onRenderWorldLast(
			//#if MC >= 11700
			//$$ MatrixStack matrixStack, Matrix4f positionMatrix
			//#elseif MC >= 11500
			float partialTicks, MatrixStack matrixStack
			//#else
			//$$ float partialTicks
			//#endif
	)
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
				List<BlockPos> order = getDustUpdateOrderAt(pos);
				for (int i = 0; i < order.size(); i++)
				{
					this.renderTextAtPos(order.get(i), String.valueOf(i + 1), Objects.requireNonNull(Formatting.RED.getColorValue()));
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
