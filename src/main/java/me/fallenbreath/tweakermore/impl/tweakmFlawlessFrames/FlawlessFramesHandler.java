package me.fallenbreath.tweakermore.impl.tweakmFlawlessFrames;

import com.google.common.collect.ImmutableList;
import me.fallenbreath.tweakermore.TweakerMoreMod;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.WorldRenderer;

import java.util.List;

public class FlawlessFramesHandler
{
	private static final List<String> HOOK_CLASSES = ImmutableList.of(
			"com.replaymod.render.hooks.ChunkLoadingRenderGlobal",  // in ReplayMod <=2.5.2
			"com.replaymod.render.hooks.ForceChunkLoadingHook"  // in ReplayMod >=2.6.0
	);
	private static Object hook = null;

	private static void installHook()
	{
		hook = null;
		Class<?> clazz = null;
		WorldRenderer worldRenderer = MinecraftClient.getInstance().worldRenderer;
		for (String hookClass : HOOK_CLASSES)
		{
			try
			{
				clazz = Class.forName(hookClass);
				break;
			}
			catch (ClassNotFoundException ignored)
			{
			}
		}
		if (clazz == null)
		{
			TweakerMoreMod.LOGGER.error("Failed to located replay flawless rendering hook, searched {}", HOOK_CLASSES);
			return;
		}
		try
		{
			hook = clazz.getConstructor(WorldRenderer.class).newInstance(worldRenderer);
		}
		catch (Exception e)
		{
			TweakerMoreMod.LOGGER.error("Failed to construct replay flawless rendering hook", e);
		}
	}

	private static void uninstallHook()
	{
		try
		{
			hook.getClass().getMethod("uninstall").invoke(hook);
		}
		catch (Exception e)
		{
			TweakerMoreMod.LOGGER.error("Failed to invoke method uninstall of replay flawless rendering hook {}", hook, e);
		}
	}

	public static void setEnabled(boolean enabled)
	{
		if (!TweakerMoreConfigs.TWEAKM_FLAWLESS_FRAMES.getTweakerMoreOption().isEnabled())
		{
			return;
		}
		if (hook == null && enabled)
		{
			installHook();
		}
		else if (hook != null && !enabled)
		{
			uninstallHook();
		}
	}

	/**
	 * Re-hook after replay uninstall its hook for rendering or taking snapshot
	 */
	public static void refreshHook()
	{
		if (hook != null)
		{
			uninstallHook();
			installHook();
		}
	}
}
