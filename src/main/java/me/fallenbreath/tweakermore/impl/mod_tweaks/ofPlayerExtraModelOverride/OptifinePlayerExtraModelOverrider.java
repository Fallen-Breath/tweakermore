package me.fallenbreath.tweakermore.impl.mod_tweaks.ofPlayerExtraModelOverride;

import fi.dy.masa.malilib.config.options.ConfigOptionList;
import me.fallenbreath.tweakermore.util.ReflectionUtil;
import net.minecraft.client.MinecraftClient;

import java.util.Map;
import java.util.Optional;

import static me.fallenbreath.tweakermore.impl.mod_tweaks.ofPlayerExtraModelOverride.OverrideDefinitions.OVERRIDE_DEFS;

public class OptifinePlayerExtraModelOverrider
{
	public static Optional<OverrideImpl> overridePlayerConfig(String playerName)
	{
		boolean isMe = Optional.ofNullable(MinecraftClient.getInstance().player).
				map(player -> player.getGameProfile().getName().equals(playerName)).
				orElse(false);

		for (OverrideDefinition overrideDefinition : OVERRIDE_DEFS)
		{
			boolean doOverride = false;
			switch (overrideDefinition.getStrategy())
			{
				case ME:
					doOverride = isMe;
					break;
				case ALL:
					doOverride = true;
					break;
				case UNTOUCHED:
				default:
					break;
			}

			if (doOverride)
			{
				return Optional.ofNullable(overrideDefinition.getImpl());
			}
		}
		return Optional.empty();
	}

	public static void onConfigValueChanged(ConfigOptionList config)
	{
		// clean optifine's player config cache map,
		// so optifine will try to fetch player config again
		// which means our PlayerConfigurationReceiverMixin mixin can do its job
		ReflectionUtil.getClass("net.optifine.player.PlayerConfigurations").ifPresent(clazz -> {
			ReflectionUtil.getField(clazz, "mapConfigurations").ifPresent(map -> {
				if (map instanceof Map)
				{
					((Map<?, ?>)map).clear();
				}
			});
		});
	}
}
