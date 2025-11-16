/*
 * This file is part of the TweakerMore project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2024  Fallen_Breath and contributors
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

package me.fallenbreath.tweakermore.mixins.tweaks.features.fireworkRocketThrottler;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import fi.dy.masa.malilib.util.InfoUtils;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FireworkRocketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiPlayerGameMode.class)
public abstract class ClientPlayerInteractionManagerMixin
{
	@Unique
	private long lastFireworkRocketUsageMilli = 0;

	// ========================== activate cooldown ==========================

	@Inject(
			method = "useItemOn",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/multiplayer/ClientPacketListener;send(Lnet/minecraft/network/protocol/Packet;)V",
					ordinal = 2
			),
			cancellable = true
	)
	private void fireworkRocketThrottler_cancelIfCooldown_useOnBlock(LocalPlayer player, ClientLevel world, InteractionHand hand, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir)
	{
		cancelIfCooldown(player, hand, cir);
	}

	@Inject(
			method = "useItem",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/multiplayer/ClientPacketListener;send(Lnet/minecraft/network/protocol/Packet;)V"
			),
			cancellable = true
	)
	private void fireworkRocketThrottler_cancelIfCooldown_useAtAir(Player player, Level world, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir)
	{
		cancelIfCooldown(player, hand, cir);
	}

	@Unique
	private void cancelIfCooldown(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir)
	{
		if (TweakerMoreConfigs.FIREWORK_ROCKET_THROTTLER.getBooleanValue())
		{
			ItemStack itemStack = player.getItemInHand(hand);
			if (itemStack.getItem() instanceof FireworkRocketItem)
			{
				long now = System.currentTimeMillis();
				double cooldown = TweakerMoreConfigs.FIREWORK_ROCKET_THROTTLER_COOLDOWN.getDoubleValue();
				double remaining = cooldown - (now - this.lastFireworkRocketUsageMilli) / 1000.0;
				if (remaining > 0)
				{
					InfoUtils.printActionbarMessage("tweakermore.impl.fireworkRocketThrottler.throttled", String.format("%.1f", remaining));
					cir.setReturnValue(InteractionResult.FAIL);
				}
			}
		}
	}

	// ========================== update cooldown ==========================

	@ModifyExpressionValue(
			method = "useItemOn",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/item/ItemStack;useOn(Lnet/minecraft/world/item/UseOnContext;)Lnet/minecraft/world/InteractionResult;"
			)
	)
	private InteractionResult fireworkRocketThrottler_updateCooldown_useOnBlock(InteractionResult actionResult)
	{
		updateCooldownOnUse(actionResult);
		return actionResult;
	}

	@ModifyExpressionValue(
			method = "useItem",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/InteractionResultHolder;getResult()Lnet/minecraft/world/InteractionResult;"
			)
	)
	private InteractionResult fireworkRocketThrottler_updateCooldown_useAtAir(InteractionResult actionResult)
	{
		updateCooldownOnUse(actionResult);
		return actionResult;
	}

	@Unique
	private void updateCooldownOnUse(InteractionResult actionResult)
	{
		if (TweakerMoreConfigs.FIREWORK_ROCKET_THROTTLER.getBooleanValue())
		{
			if (
					//#if MC >= 11500
					actionResult.consumesAction()
					//#else
					//$$ actionResult == InteractionResult.SUCCESS
					//#endif
			)
			{
				this.lastFireworkRocketUsageMilli = System.currentTimeMillis();
			}
		}
	}
}
