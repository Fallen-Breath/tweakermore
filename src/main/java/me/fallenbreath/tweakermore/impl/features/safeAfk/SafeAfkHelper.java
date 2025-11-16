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

package me.fallenbreath.tweakermore.impl.features.safeAfk;

import me.fallenbreath.tweakermore.TweakerMoreMod;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.util.Messenger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.DisconnectedScreen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.network.chat.BaseComponent;

import java.text.SimpleDateFormat;
import java.util.Date;

//#if MC >= 12106
//$$ import net.minecraft.text.Text;
//#endif

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

	public static void onHealthUpdate(Minecraft mc)
	{
		if (TweakerMoreConfigs.SAFE_AFK.getBooleanValue())
		{
			if (mc.player != null && mc.level != null && hasRecord())
			{
				float health = mc.player.getHealth();
				float maxHealth = mc.player.getMaxHealth();
				if (maxHealth > 0 && health < TweakerMoreConfigs.SAFE_AFK_HEALTH_THRESHOLD.getDoubleValue())
				{
					BaseComponent title = Messenger.s(TweakerMoreMod.MOD_NAME + " " + TweakerMoreConfigs.SAFE_AFK.getPrettyName());
					BaseComponent reason = Messenger.tr(
							"tweakermore.impl.safeAfk.received_damage",
							new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()),
							String.format("%.1f / %.1f (%.0f%%)", health, maxHealth, health / maxHealth * 100)
					);
					resetHurtTime();
					mc.execute(() -> {
						//#if MC >= 12106
						//$$ // ref: mc1.21.5 net.minecraft.client.world.ClientWorld#disconnect
						//$$ mc.world.disconnect(Text.translatable("multiplayer.status.quitting"));
						//$$ mc.disconnectWithProgressScreen();
						//#else
						mc.level.disconnect();
						mc.clearLevel();
						//#endif
						mc.setScreen(new DisconnectedScreen(
								new JoinMultiplayerScreen(new TitleScreen()),
								//#if MC >= 11600
								//$$ title,
								//#else
								title.getString(),
								//#endif
								reason
						));
					});
				}
			}
		}
	}

	public static void onEntityEnterDamageStatus(LivingEntity livingEntity)
	{
		if (TweakerMoreConfigs.SAFE_AFK.getBooleanValue())
		{
			Minecraft mc = Minecraft.getInstance();
			if (livingEntity == mc.player && mc.level != null)
			{
				recordHurtTime();
			}
		}
	}
}
