package me.fallenbreath.tweakermore.impl.mod_tweaks.eprHideOnDebugHud;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class EprHideOnDebugHudImpl
{
	public static void applyHide(CallbackInfo ci)
	{
		if (TweakerMoreConfigs.EPR_HIDE_ON_DEBUG_HUD.getBooleanValue())
		{
			if (MinecraftClient.getInstance().options.debugEnabled)
			{
				ci.cancel();
			}
		}
	}
}
