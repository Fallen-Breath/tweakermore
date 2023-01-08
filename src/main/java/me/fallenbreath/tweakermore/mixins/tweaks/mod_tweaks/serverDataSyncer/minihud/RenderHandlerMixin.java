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

package me.fallenbreath.tweakermore.mixins.tweaks.mod_tweaks.serverDataSyncer.minihud;

import fi.dy.masa.minihud.event.RenderHandler;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.util.ModIds;
import org.spongepowered.asm.mixin.Mixin;

//#if MC >= 11500
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.impl.mod_tweaks.serverDataSyncer.serverDataSyncer.ServerDataSyncer;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;
//#endif

@Restriction(require = @Condition(ModIds.minihud))
@Mixin(RenderHandler.class)
public abstract class RenderHandlerMixin
{
	// no bee hive in 1.14
	//#if MC >= 11500
	@ModifyVariable(
			method = "addLine(Lfi/dy/masa/minihud/config/InfoToggle;)V",
			slice = @Slice(
					from = @At(
							value = "FIELD",
							target = "Lfi/dy/masa/minihud/config/InfoToggle;BEE_COUNT:Lfi/dy/masa/minihud/config/InfoToggle;",
							remap = false
					)
			),
			at = @At(
					value = "INVOKE_ASSIGN",
					target = "Lfi/dy/masa/minihud/event/RenderHandler;getTargetedBlockEntity(Lnet/minecraft/world/World;Lnet/minecraft/client/MinecraftClient;)Lnet/minecraft/block/entity/BlockEntity;",
					ordinal = 0,
					remap = true
			),
			remap = false
	)
	private BlockEntity serverDataSyncer4BeehiveBeeCount(BlockEntity blockEntity)
	{
		if (TweakerMoreConfigs.SERVER_DATA_SYNCER.getBooleanValue())
		{
			if (blockEntity instanceof BeehiveBlockEntity && !MinecraftClient.getInstance().isIntegratedServerRunning())
			{
				ServerDataSyncer.getInstance().syncBlockEntity(blockEntity.getPos());
			}
		}
		return blockEntity;
	}
	//#endif
}
