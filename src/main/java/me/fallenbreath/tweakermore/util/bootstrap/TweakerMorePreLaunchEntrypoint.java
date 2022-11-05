package me.fallenbreath.tweakermore.util.bootstrap;

import com.llamalad7.mixinextras.MixinExtrasBootstrap;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

public class TweakerMorePreLaunchEntrypoint implements PreLaunchEntrypoint
{
	@Override
	public void onPreLaunch()
	{
		MixinExtrasBootstrap.init();
	}
}
