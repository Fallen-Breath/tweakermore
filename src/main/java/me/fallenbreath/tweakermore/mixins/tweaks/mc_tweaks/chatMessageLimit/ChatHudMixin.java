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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.chatMessageLimit;

import com.google.common.collect.Lists;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.util.ModIds;
import net.minecraft.client.gui.components.ChatComponent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.List;

@Restriction(conflict = {
		@Condition(ModIds.cheat_utils),
		@Condition(ModIds.compact_chat),
		@Condition(ModIds.more_chat_history),
		@Condition(ModIds.parachute),
		@Condition(ModIds.raise_chat_limit),
		@Condition(ModIds.wheres_my_chat_history)
})
@Mixin(ChatComponent.class)
public abstract class ChatHudMixin
{
	@Mutable
	@Shadow @Final private List<?> allMessages;

	@Mutable
	@Shadow @Final private List<?> trimmedMessages;

	@ModifyExpressionValue(
			//#if MC >= 12006
			//$$ method = {
			//$$ 		"addMessageToDisplayQueue",
			//$$ 		"addMessageToQueue(Lnet/minecraft/client/GuiMessage;)V"
			//$$ },
			//#elseif MC >= 11901
			//$$ method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;ILnet/minecraft/client/GuiMessageTag;Z)V",
			//#else
			method = "addMessage(Lnet/minecraft/network/chat/Component;IIZ)V",
			//#endif
			at = @At(
					value = "CONSTANT",
					args = "intValue=100"
			),
			// there are so many mod that modifies the chat limit
			// in case it's not in the conflict list, here comes a fail-soft solution
			require = 0
	)
	private int chatMessageLimit_modifyLimit(int value)
	{
		return TweakerMoreConfigs.CHAT_MESSAGE_LIMIT.isModified() ? TweakerMoreConfigs.CHAT_MESSAGE_LIMIT.getIntegerValue() : value;
	}

	@ModifyArgs(
			//#if MC >= 12111
			//$$ method = "render(Lnet/minecraft/client/gui/components/ChatComponent$ChatGraphicsAccess;IIZ)V",
			//#else
			method = "render",
			//#endif
			at = @At(
					value = "INVOKE",
					//#if MC >= 12111
					//$$ target = "Lnet/minecraft/client/gui/components/ChatComponent$ChatGraphicsAccess;fill(IIIII)V",
					//$$ ordinal = 0
					//#elseif MC >= 12000
					//$$ target = "Lnet/minecraft/client/gui/GuiGraphics;fill(IIIII)V"
					//#elseif MC >= 11600
					//$$ target = "Lnet/minecraft/client/gui/components/ChatComponent;fill(Lcom/mojang/blaze3d/vertex/PoseStack;IIIII)V"
					//#else
					target = "Lnet/minecraft/client/gui/components/ChatComponent;fill(IIIII)V"
					//#endif
			)
	)
	private void chatMessageLimit_makeSureTheScrollBarIsVisible(Args args)
	{
		//#if 11600 <= MC && MC < 12000
		//$$ int y1Idx = 2;
		//$$ int y2Idx = 4;
		//#else
		int y1Idx = 1;
		int y2Idx = 3;
		//#endif

		int y1 = args.get(y1Idx);
		int y2 = args.get(y2Idx);
		// it's too short (length = 0)
		if (y1 == y2)
		{
			if (y1 < 0)
			{
				y1++;
			}
			else
			{
				y2--;
			}
			args.set(y1Idx, y1);
			args.set(y2Idx, y2);
		}
	}

	// XXX: switch for this?
	@Inject(method = "<init>", at = @At("TAIL"))
	private void chatMessageLimit_replaceMessageContainerWithLinkedList(CallbackInfo ci)
	{
		this.allMessages = Lists.newLinkedList(this.allMessages);
		this.trimmedMessages = Lists.newLinkedList(this.trimmedMessages);
	}
}
