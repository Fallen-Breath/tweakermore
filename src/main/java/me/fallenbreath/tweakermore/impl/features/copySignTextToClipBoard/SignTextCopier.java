/*
 * This file is part of the TweakerMore project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2023  Fallen_Breath and contributors
 *
 * TweakerMore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * TweakerMore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with TweakerMore.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.fallenbreath.tweakermore.impl.features.copySignTextToClipBoard;

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
		if (mc.player != null && mc.world != null && mc.crosshairTarget != null && mc.crosshairTarget.getType() == HitResult.Type.BLOCK)
		{
			BlockPos blockPos = ((BlockHitResult)mc.crosshairTarget).getBlockPos();
			BlockState blockState = mc.world.getBlockState(blockPos);
			if (blockState.getBlock() instanceof AbstractSignBlock)
			{
				BlockEntity blockEntity = mc.world.getBlockEntity(blockPos);
				if (blockEntity instanceof SignBlockEntity)
				{
					//#if MC >= 12000
					//$$ Text[] texts = ((SignBlockEntity)blockEntity).getTextFacing(mc.player).getMessages(false);
					//#elseif MC >= 11600
					//$$ Text[] texts = ((SignBlockEntityAccessor)blockEntity).getTexts();
					//#else
					Text[] texts = ((SignBlockEntity)blockEntity).text;
					//#endif

					String text = Joiner.on("\n").join(
							Arrays.stream(texts).
									map(Text::getString).
									collect(Collectors.toList())
					);
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
