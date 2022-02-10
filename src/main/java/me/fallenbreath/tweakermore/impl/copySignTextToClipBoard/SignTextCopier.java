package me.fallenbreath.tweakermore.impl.copySignTextToClipBoard;

import com.google.common.base.Joiner;
import fi.dy.masa.malilib.hotkeys.IKeybind;
import fi.dy.masa.malilib.hotkeys.KeyAction;
import fi.dy.masa.malilib.util.InfoUtils;
import net.minecraft.block.AbstractSignBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.stream.Collectors;

public class SignTextCopier
{
	public static boolean copySignText(KeyAction action, IKeybind key)
	{
		MinecraftClient mc = MinecraftClient.getInstance();
		if (mc.world != null && mc.crosshairTarget != null && mc.crosshairTarget.getType() == HitResult.Type.BLOCK)
		{
			BlockPos blockPos = ((BlockHitResult)mc.crosshairTarget).getBlockPos();
			BlockState blockState = mc.world.getBlockState(blockPos);
			if (blockState.getBlock() instanceof AbstractSignBlock)
			{
				BlockEntity blockEntity = mc.world.getBlockEntity(blockPos);
				if (blockEntity instanceof SignBlockEntity)
				{
					String text = Joiner.on("\n").join(
							Arrays.stream(((SignBlockEntity)blockEntity).text).
									map(Text::getString).
									collect(Collectors.toList())
					);
					text = StringUtils.strip(text);
					if (!text.isEmpty())
					{
						mc.keyboard.setClipboard(text);
						InfoUtils.printActionbarMessage("tweakermore.copySignTextToClipBoard.sign_copied", blockState.getBlock().getName());
					}
					else
					{
						InfoUtils.printActionbarMessage("tweakermore.copySignTextToClipBoard.empty_sign", blockState.getBlock().getName());
					}
					return true;
				}
			}
		}
		return false;
	}
}
