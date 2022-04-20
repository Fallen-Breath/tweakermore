package me.fallenbreath.tweakermore.mixins.tweaks.porting.isScrollStacksFallbackFixPorting;

import fi.dy.masa.itemscroller.util.InventoryUtils;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.util.ModIds;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Restriction(require = {
		@Condition(ModIds.itemscroller),
		@Condition(value = ModIds.minecraft, versionPredicates = "<1.18")
})
@Mixin(InventoryUtils.class)
public abstract class InventoryUtilsMixin
{
	@Inject(
			//#if MC >= 11600
			//$$ method = "tryMoveStacks(Lnet/minecraft/item/ItemStack;Lnet/minecraft/screen/slot/Slot;Lnet/minecraft/client/gui/screen/ingame/HandledScreen;ZZZ)V",
			//#else
			method = "tryMoveStacks(Lnet/minecraft/item/ItemStack;Lnet/minecraft/container/Slot;Lnet/minecraft/client/gui/screen/ingame/ContainerScreen;ZZZ)V",
			//#endif
			at = @At(
					value = "INVOKE",
					//#if MC >= 11600
					//$$ target = "Lfi/dy/masa/itemscroller/util/InventoryUtils;clickSlotsToMoveItemsFromSlot(Lnet/minecraft/screen/slot/Slot;Lnet/minecraft/client/gui/screen/ingame/HandledScreen;Z)V",
					//#else
					target = "Lfi/dy/masa/itemscroller/util/InventoryUtils;clickSlotsToMoveItemsFromSlot(Lnet/minecraft/container/Slot;Lnet/minecraft/client/gui/screen/ingame/ContainerScreen;Z)V",
					//#endif
					ordinal = 1
			),
			cancellable = true
	)
	private static void itemScrollerScrollStacksFallbackFix(CallbackInfo ci)
	{
		if (TweakerMoreConfigs.IS_SCROLL_STACKS_FALLBACK_FIX_PORTING.getBooleanValue())
		{
			ci.cancel();
		}
	}
}
