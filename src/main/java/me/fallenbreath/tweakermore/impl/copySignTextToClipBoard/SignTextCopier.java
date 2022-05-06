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

//#if MC >= 11600
//$$ import me.fallenbreath.tweakermore.mixins.tweaks.features.copySignTextToClipBoard.SignBlockEntityAccessor;
//#endif

import java.util.Arrays;
import java.util.stream.Collectors;

public class SignTextCopier
{
	public static void copySignText()
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
					//#if MC >= 11600
					//$$ Text[] texts = ((SignBlockEntityAccessor)blockEntity).getTexts();
					//#else
					Text[] texts = ((SignBlockEntity)blockEntity).text;
					//#endif

					String text = Joiner.on("\n").join(
							Arrays.stream(texts).
									map(Text::getString).
									collect(Collectors.toList())
					);
					text = StringUtils.strip(text);
					if (!text.isEmpty())
					{
						mc.keyboard.setClipboard(text);
						InfoUtils.printActionbarMessage("tweakermore.config.copySignTextToClipBoard.sign_copied", blockState.getBlock().getName());
					}
					else
					{
						InfoUtils.printActionbarMessage("tweakermore.config.copySignTextToClipBoard.empty_sign", blockState.getBlock().getName());
					}
					return;
				}
			}
		}
		InfoUtils.printActionbarMessage("tweakermore.config.copySignTextToClipBoard.no_sign");
	}
}
