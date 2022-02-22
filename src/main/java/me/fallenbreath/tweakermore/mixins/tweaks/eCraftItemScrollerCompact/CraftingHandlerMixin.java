package me.fallenbreath.tweakermore.mixins.tweaks.eCraftItemScrollerCompact;

import fi.dy.masa.itemscroller.recipes.CraftingHandler;
import me.fallenbreath.tweakermore.impl.eCraftMassCraftCompact.EasierCraftingRegistrar;
import me.fallenbreath.tweakermore.util.dependency.Condition;
import me.fallenbreath.tweakermore.util.dependency.Restriction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Restriction(enableWhen = @Condition(type = Condition.Type.MIXIN, value = "me.fallenbreath.tweakermore.mixins.tweaks.eCraftItemScrollerCompact.ConfigsMixin"))
@Mixin(CraftingHandler.class)
public abstract class CraftingHandlerMixin
{
	@Inject(method = "clearDefinitions", at = @At("TAIL"), remap = false)
	private static void easierCraftingItemScrolledCompact_markDefinitionsCleared(CallbackInfo ci)
	{
		EasierCraftingRegistrar.markDefinitionCleared();
	}
}
