/*
 * This file is part of the TweakerMore project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026  Fallen_Breath and contributors
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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.clientSidePickBlock;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.impl.mc_tweaks.clientSidePickBlock.ClientSidePickBlockHelper;
import me.fallenbreath.tweakermore.util.ModIds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * mc >= 1.21.4: subproject 1.21.4                  <--------
 * mc <  1.21.4: subproject 1.15.2 (main project)
 */
@Restriction(require = @Condition(value = ModIds.minecraft, versionPredicates = ">=1.21.4"))
@Mixin(MultiPlayerGameMode.class)
public abstract class ClientPlayerInteractionManagerMixin
{
	@Shadow @Final private Minecraft minecraft;

	@Shadow
	private void ensureHasSentCarriedItem()
	{
		throw new AssertionError();
	}

	@Inject(method = "handlePickItemFromBlock", at = @At("HEAD"), cancellable = true)
	private void clientSidePickBlock_pickBlock(BlockPos pos, boolean includeData, CallbackInfo ci)
	{
		if (TweakerMoreConfigs.CLIENT_SIDE_PICK_BLOCK.getBooleanValue())
		{
			ClientSidePickBlockHelper.pickBlock(this.minecraft, pos, includeData);
			if (this.minecraft.player != null)
			{
				this.ensureHasSentCarriedItem();
			}
			ci.cancel();
		}
	}

	@Inject(method = "handlePickItemFromEntity", at = @At("HEAD"), cancellable = true)
	private void clientSidePickBlock_pickEntity(Entity entity, boolean includeData, CallbackInfo ci)
	{
		if (TweakerMoreConfigs.CLIENT_SIDE_PICK_BLOCK.getBooleanValue())
		{
			ClientSidePickBlockHelper.pickEntity(this.minecraft, entity);
			if (this.minecraft.player != null)
			{
				this.ensureHasSentCarriedItem();
			}
			ci.cancel();
		}
	}
}
