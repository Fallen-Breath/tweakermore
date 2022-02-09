package me.fallenbreath.tweakermore.util.mixin;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class TweakerMoreMixinConfigPlugin extends DependencyMixinConfigPlugin
{
	private final Logger LOGGER = LogManager.getLogger();

	@Override
	protected void onDependencyCheckFailed(String mixinClassName, String reason)
	{
		LOGGER.info("[TweakerMore] Disabled mixin {} due to {}", mixinClassName, reason);
	}

	@Override
	public void onLoad(String mixinPackage)
	{
	}

	@Override
	public String getRefMapperConfig()
	{
		return null;
	}

	@Override
	public void acceptTargets(Set<String> myTargets, Set<String> otherTargets)
	{
	}

	@Override
	public List<String> getMixins()
	{
		return null;
	}

	@Override
	public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo)
	{
	}

	@Override
	public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo)
	{
	}
}
