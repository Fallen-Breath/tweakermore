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
import fi.dy.masa.malilib.util.InfoUtils;
import me.fallenbreath.tweakermore.util.EntityUtils;
import me.fallenbreath.tweakermore.util.compat.litematica.LitematicaUtils;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.stream.Collectors;

//#if MC >= 11600
//$$ import me.fallenbreath.tweakermore.mixins.tweaks.features.copySignTextToClipBoard.SignBlockEntityAccessor;
//#endif

public class SignTextCopier
{
	public static void copySignText()
	{
		Minecraft mc = Minecraft.getInstance();
		LocalPlayer player = EntityUtils.getCurrentPlayerOrFreeCameraEntity();
		if (player != null && mc.level != null)
		{
			BlockPos blockPos = null;
			SignBlockEntity blockEntity = null;
			String copiedTextKey = null;
			if (LitematicaUtils.isRenderingEnabled())
			{
				blockPos = LitematicaUtils.getSchematicWorldCrosshairTargetPos(player);
				blockEntity = tryGetSignBlockEntity(LitematicaUtils.getSchematicWorld(), blockPos);
				copiedTextKey = "sign_copied_schematic";
			}
			if (blockEntity == null && mc.hitResult != null && mc.hitResult.getType() == HitResult.Type.BLOCK)
			{
				blockPos = ((BlockHitResult)mc.hitResult).getBlockPos();
				blockEntity = tryGetSignBlockEntity(mc.level, blockPos);
				copiedTextKey = "sign_copied";
			}

			if (blockEntity != null)
			{
				BlockState blockState = blockEntity.getBlockState();
				Component[] texts = getSignTexts(blockEntity, player);

				String text = Joiner.on("\n").join(
						Arrays.stream(texts).
								map(Component::getString).
								collect(Collectors.toList())
				);
				if (!text.isEmpty())
				{
					mc.keyboardHandler.setClipboard(text);
					InfoUtils.printActionbarMessage("tweakermore.impl.copySignTextToClipBoard." + copiedTextKey, blockState.getBlock().getName());
				}
				else
				{
					InfoUtils.printActionbarMessage("tweakermore.impl.copySignTextToClipBoard.empty_sign", blockState.getBlock().getName());
				}
				return;
			}
		}
		InfoUtils.printActionbarMessage("tweakermore.impl.copySignTextToClipBoard.no_sign");
	}

	private static Component[] getSignTexts(SignBlockEntity blockEntity, LocalPlayer player)
	{
		//#if MC >= 12004
		//$$ return blockEntity.getText((blockEntity).isFacingFrontText(player)).getMessages(false);
		//#elseif MC >= 12000
		//$$ return blockEntity.getTextFacingPlayer(player).getMessages(false);
		//#elseif MC >= 11600
		//$$ return ((SignBlockEntityAccessor)blockEntity).getTexts();
		//#else
		return blockEntity.messages;
		//#endif
	}

	@Nullable
	private static SignBlockEntity tryGetSignBlockEntity(@Nullable Level world, @Nullable BlockPos pos)
	{
		if (world != null && pos != null)
		{
			BlockState blockState = world.getBlockState(pos);
			if (blockState.getBlock() instanceof SignBlock)
			{
				BlockEntity blockEntity = world.getBlockEntity(pos);
				if (blockEntity instanceof SignBlockEntity)
				{
					return (SignBlockEntity)blockEntity;
				}
			}
		}
		return null;
	}
}
