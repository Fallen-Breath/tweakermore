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
import com.llamalad7.mixinextras.sugar.Local;
import fi.dy.masa.malilib.util.InfoUtils;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FireworkRocketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class ClientPlayerInteractionManagerMixin
{
	@Unique
	private long lastFireworkRocketUsageMilli = 0;

	@Unique
	private final ThreadLocal<Boolean> nullPacketSkipping = ThreadLocal.withInitial(() -> false);

	// ========================== activate cooldown ==========================

	@Inject(
			method = "method_41933",  // lambda method in interactBlock()
			at = @At("HEAD"),
			cancellable = true
	)
	private void fireworkRocketThrottler_cancelIfCooldown_useOnBlock(
			CallbackInfoReturnable<Packet<?>> cir,
			@Local(argsOnly = true) ClientPlayerEntity player,
			@Local(argsOnly = true) Hand hand,
			@Local(argsOnly = true) MutableObject<ActionResult> actionResult
	)
	{
		if (checkCooldown(player, hand))
		{
			this.nullPacketSkipping.set(true);
			cir.setReturnValue(null);
			actionResult.setValue(ActionResult.FAIL);
		}
	}

	@Inject(
			method = "method_41929",  // lambda method in interactItem
			at = @At("HEAD"),
			cancellable = true
	)
	private void fireworkRocketThrottler_cancelIfCooldown_useAtAir(
			CallbackInfoReturnable<Packet<?>> cir,
			@Local(argsOnly = true) PlayerEntity player,
			@Local(argsOnly = true) Hand hand,
			@Local(argsOnly = true) MutableObject<ActionResult> actionResult
	)
	{
		if (checkCooldown(player, hand))
		{
			this.nullPacketSkipping.set(true);
			cir.setReturnValue(null);
			actionResult.setValue(ActionResult.FAIL);
		}
	}

	@Unique
	private boolean checkCooldown(PlayerEntity player, Hand hand)
	{
		if (TweakerMoreConfigs.FIREWORK_ROCKET_THROTTLER.getBooleanValue())
		{
			ItemStack itemStack = player.getStackInHand(hand);
			if (itemStack.getItem() instanceof FireworkRocketItem)
			{
				long now = System.currentTimeMillis();
				double cooldown = TweakerMoreConfigs.FIREWORK_ROCKET_THROTTLER_COOLDOWN.getDoubleValue();
				double remaining = cooldown - (now - this.lastFireworkRocketUsageMilli) / 1000.0;
				if (remaining > 0)
				{
					InfoUtils.printActionbarMessage("tweakermore.impl.fireworkRocketThrottler.throttled", String.format("%.1f", remaining));
					return true;
				}
			}
		}
		return false;
	}

	@Inject(
			method = "sendSequencedPacket",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/network/SequencedPacketCreator;predict(I)Lnet/minecraft/network/packet/Packet;",
					shift = At.Shift.AFTER
			),
			cancellable = true
	)
	private void fireworkRocketThrottler_cancelSendSequencedPacketIfNull(CallbackInfo ci)
	{
		if (this.nullPacketSkipping.get())
		{
			this.nullPacketSkipping.remove();
			ci.cancel();
		}
	}

	// ========================== update cooldown ==========================

	@ModifyExpressionValue(
			method = "interactBlockInternal",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/item/ItemStack;useOnBlock(Lnet/minecraft/item/ItemUsageContext;)Lnet/minecraft/util/ActionResult;"
			)
	)
	private ActionResult fireworkRocketThrottler_updateCooldown_useOnBlock(ActionResult actionResult)
	{
		updateCooldownOnUse(actionResult);
		return actionResult;
	}

	@ModifyExpressionValue(
			method = "method_41929",  // lambda method in interactItem
			at = @At(
					value = "INVOKE",
					//#if MC >= 12103
					//$$ target = "Lnet/minecraft/world/item/ItemStack;use(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;"
					//#else
					target = "Lnet/minecraft/util/TypedActionResult;getResult()Lnet/minecraft/util/ActionResult;"
					//#endif
			)
	)
	private ActionResult fireworkRocketThrottler_updateCooldown_useAtAir(ActionResult actionResult)
	{
		updateCooldownOnUse(actionResult);
		return actionResult;
	}

	@Unique
	private void updateCooldownOnUse(ActionResult actionResult)
	{
		if (TweakerMoreConfigs.FIREWORK_ROCKET_THROTTLER.getBooleanValue())
		{
			if (actionResult.isAccepted())
			{
				this.lastFireworkRocketUsageMilli = System.currentTimeMillis();
			}
		}
	}
}
