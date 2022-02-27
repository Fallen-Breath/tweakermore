package me.fallenbreath.tweakermore.mixins.tweaks.chatMessageLimit;

import com.google.common.collect.Lists;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.util.ModIds;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.List;

@Restriction(conflict = {
		@Condition(ModIds.compact_chat),
		@Condition(ModIds.more_chat_history),
		@Condition(ModIds.parachute),
		@Condition(ModIds.raise_chat_limit),
		@Condition(ModIds.wheres_my_chat_history)
})
@Mixin(ChatHud.class)
public abstract class ChatHudMixin
{
	@Mutable
	@Shadow @Final private List<ChatHudLine> messages;

	@Mutable
	@Shadow @Final private List<ChatHudLine> visibleMessages;

	@ModifyConstant(
			method = "addMessage(Lnet/minecraft/text/Text;IIZ)V",
			constant = @Constant(intValue = 100),
			// there are so many mod that modifies the chat limit
			// in case it's not in the conflict list, here comes a fail-soft solution
			require = 0
	)
	private int raiseChatLimitTo(int value)
	{
		return TweakerMoreConfigs.CHAT_MESSAGE_LIMIT.isModified() ? TweakerMoreConfigs.CHAT_MESSAGE_LIMIT.getIntegerValue() : value;
	}

	@ModifyArgs(
			method = "render",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/hud/ChatHud;fill(IIIII)V"
			)
	)
	private void makeSureTheScrollBarIsVisible(Args args)
	{
		int y1 = args.get(1);
		int y2 = args.get(3);
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
			args.set(1, y1);
			args.set(3, y2);
		}
	}

	@Inject(method = "<init>", at = @At("TAIL"))
	private void replaceMessageContainerWithLinkedList(CallbackInfo ci)
	{
		this.messages = Lists.newLinkedList(this.messages);
		this.visibleMessages = Lists.newLinkedList(this.visibleMessages);
	}
}
