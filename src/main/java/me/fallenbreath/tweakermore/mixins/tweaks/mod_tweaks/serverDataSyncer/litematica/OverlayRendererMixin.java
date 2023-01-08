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

package me.fallenbreath.tweakermore.mixins.tweaks.mod_tweaks.serverDataSyncer.litematica;

import fi.dy.masa.litematica.render.OverlayRenderer;
import fi.dy.masa.litematica.util.RayTraceUtils;
import fi.dy.masa.malilib.util.InventoryUtils;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.impl.mod_tweaks.serverDataSyncer.serverDataSyncer.ServerDataSyncer;
import me.fallenbreath.tweakermore.util.ModIds;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

//#if MC >= 11600
//$$ import net.minecraft.client.util.math.MatrixStack;
//#endif

@Restriction(require = @Condition(ModIds.litematica))
@Mixin(OverlayRenderer.class)
public abstract class OverlayRendererMixin
{
	@Inject(
			method = "renderBlockInfoOverlay",
			at = @At(
					value = "INVOKE_ASSIGN",
					target = "Lnet/minecraft/util/hit/BlockHitResult;getBlockPos()Lnet/minecraft/util/math/BlockPos;",
					remap = true
			),
			locals = LocalCapture.CAPTURE_FAILHARD,
			remap = false
	)
	private void serverDataSyncer4InfoOverlay(
			RayTraceUtils.RayTraceWrapper traceWrapper, MinecraftClient mc,
			//#if MC >= 11600
			//$$ MatrixStack matrixStack,
			//#endif
			CallbackInfo ci,
			BlockState air, World worldSchematic, World worldClient, BlockPos pos
	)
	{
		if (TweakerMoreConfigs.SERVER_DATA_SYNCER.getBooleanValue())
		{
			if (worldClient instanceof ClientWorld)
			{
				Inventory inventory = InventoryUtils.getInventory(worldClient, pos);
				if (inventory != null)
				{
					ServerDataSyncer.getInstance().syncBlockInventory(inventory);
				}
			}
		}
	}
}
