package me.fallenbreath.tweakermore.util.mixin;

import me.fallenbreath.conditionalmixin.api.mixin.RestrictiveMixinConfigPlugin;
import me.fallenbreath.tweakermore.util.FabricUtil;
import me.fallenbreath.tweakermore.util.ModIds;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixins;

import java.util.List;
import java.util.Set;

public class TweakerMoreMixinConfigPlugin extends RestrictiveMixinConfigPlugin
{
	private final Logger LOGGER = LogManager.getLogger();

	@Override
	protected void onRestrictionCheckFailed(String mixinClassName, String reason)
	{
		LOGGER.info("[TweakerMore] Disabled mixin {} due to {}", mixinClassName, reason);
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
		// Fabric ASM (mm) direct MixinConfigPlugin getMixins() ---mm:early_risers--> optiFabric OptifabricSetup
		// We need to wait until optifine loading stuffs to be done to add our optifine mixins
		if (FabricUtil.isModLoaded(ModIds.optifine))  // a rough check
		{
			LOGGER.info("[TweakerMore] loading optifine mixin");
			Mixins.addConfiguration("tweakermore.optifine.mixins.json");
		}
		return null;
	}
}
