package me.fallenbreath.tweakermore.impl.tweakmSafeAfk;

import me.fallenbreath.tweakermore.TweakerMoreMod;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.BaseText;
import net.minecraft.text.TranslatableText;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SafeAfkHelper
{
	private static long lastHurtMs = 0;
	private static final long MAX_TIME_WAIT = 15 * 1000;  // 15s

	public static void resetHurtTime()
	{
		lastHurtMs = -MAX_TIME_WAIT;
	}

	public static void recordHurtTime()
	{
		lastHurtMs = System.currentTimeMillis();
	}

	public static boolean hasRecord()
	{
		return System.currentTimeMillis() - lastHurtMs <= MAX_TIME_WAIT;
	}

	public static void onHealthUpdate(MinecraftClient mc)
	{
		if (TweakerMoreConfigs.TWEAKM_SAFE_AFK.getBooleanValue())
		{
			if (mc.player != null && mc.world != null && hasRecord())
			{
				float health = mc.player.getHealth();
				float maxHealth = mc.player.getMaximumHealth();
				if (maxHealth > 0 && health < TweakerMoreConfigs.SAFE_AFK_HEALTH_THRESHOLD.getDoubleValue())
				{
					String title = TweakerMoreMod.MOD_NAME + " " + TweakerMoreConfigs.TWEAKM_SAFE_AFK.getPrettyName();
					BaseText reason = new TranslatableText(
							"tweakermore.config.tweakmSafeAfk.received_damage",
							new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()),
							String.format("%.1f / %.1f (%.0f%%)", health, maxHealth, health / maxHealth * 100)
					);
					resetHurtTime();
					mc.execute(() -> {
						mc.world.disconnect();
						mc.disconnect();
						mc.openScreen(new DisconnectedScreen(new MultiplayerScreen(new TitleScreen()), title, reason));
					});
				}
			}
		}
	}

	public static void onEntityEnterDamageStatus(LivingEntity livingEntity)
	{
		if (TweakerMoreConfigs.TWEAKM_SAFE_AFK.getBooleanValue())
		{
			MinecraftClient mc = MinecraftClient.getInstance();
			if (livingEntity == mc.player && mc.world != null)
			{
				recordHurtTime();
			}
		}
	}
}
