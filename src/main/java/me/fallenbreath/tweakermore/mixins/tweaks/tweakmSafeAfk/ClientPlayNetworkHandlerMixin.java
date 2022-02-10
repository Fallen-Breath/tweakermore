package me.fallenbreath.tweakermore.mixins.tweaks.tweakmSafeAfk;

import me.fallenbreath.tweakermore.TweakerMoreMod;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.impl.tweakmSafeAfk.DamageMemory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.text.BaseText;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.text.SimpleDateFormat;
import java.util.Date;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin
{
	@Shadow private MinecraftClient client;

	@Inject(method = "onHealthUpdate", at = @At("TAIL"))
	private void tweakerMoreSafeAfkHook(CallbackInfo ci)
	{
		if (TweakerMoreConfigs.TWEAKM_SAFE_AFK.getBooleanValue())
		{
			MinecraftClient mc = this.client;
			if (mc.player != null && mc.world != null && DamageMemory.hasRecord())
			{
				float health = mc.player.getHealth();
				float maxHealth = mc.player.getMaximumHealth();
				if (maxHealth > 0 && health < TweakerMoreConfigs.SAFE_AFK_HEALTH_THRESHOLD.getDoubleValue())
				{
					String title = TweakerMoreMod.MOD_NAME + " " + TweakerMoreConfigs.TWEAKM_SAFE_AFK.getPrettyName();
					BaseText reason = new TranslatableText(
							"tweakmSafeAfk.received_damage",
							new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()),
							String.format("%.1f / %.1f (%.0f%%)", health, maxHealth, health / maxHealth * 100)
					);
					DamageMemory.resetTime();
					mc.execute(() -> {
						mc.world.disconnect();
						mc.disconnect();
						mc.openScreen(new DisconnectedScreen(new MultiplayerScreen(new TitleScreen()), title, reason));
					});
				}
			}
		}
	}

	@Inject(method = {"clearWorld", "onPlayerRespawn"}, at = @At("TAIL"))
	private void resetLastHurtGameTime(CallbackInfo ci)
	{
		DamageMemory.resetTime();
	}
}
