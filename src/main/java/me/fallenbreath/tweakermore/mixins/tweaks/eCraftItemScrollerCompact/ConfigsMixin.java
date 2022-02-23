package me.fallenbreath.tweakermore.mixins.tweaks.eCraftItemScrollerCompact;

import fi.dy.masa.itemscroller.config.Configs;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.impl.eCraftMassCraftCompact.EasierCraftingRegistrar;
import me.fallenbreath.tweakermore.util.ModIds;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Restriction(require = {@Condition(ModIds.easier_crafting), @Condition(ModIds.itemscroller)})
@Mixin(Configs.class)
public abstract class ConfigsMixin
{
	@Inject(method = "loadFromFile", at = @At("TAIL"), remap = false)
	private static void easierCraftingItemScrollerCompact(CallbackInfo ci)
	{
		if (TweakerMoreConfigs.ECRAFT_ITEM_SCROLLER_COMPACT.getBooleanValue())
		{
			EasierCraftingRegistrar.register();
		}
	}
}
