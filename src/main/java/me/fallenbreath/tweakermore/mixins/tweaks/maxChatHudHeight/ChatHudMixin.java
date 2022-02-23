package me.fallenbreath.tweakermore.mixins.tweaks.maxChatHudHeight;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.client.gui.hud.ChatHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ChatHud.class)
public abstract class ChatHudMixin
{
	@ModifyConstant(
			method = "getHeight(D)I",
			constant = @Constant(doubleValue = 160.0D),
			require = 0
	)
	private static double maxChatHudHeight(double maxHeight)
	{
		if (TweakerMoreConfigs.MAX_CHAT_HUD_HEIGHT.isModified())
		{
			maxHeight = TweakerMoreConfigs.MAX_CHAT_HUD_HEIGHT.getIntegerValue();
		}
		return maxHeight;
	}
}
