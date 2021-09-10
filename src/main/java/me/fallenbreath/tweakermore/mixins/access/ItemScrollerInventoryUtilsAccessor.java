package me.fallenbreath.tweakermore.mixins.access;

import fi.dy.masa.itemscroller.util.InventoryUtils;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(InventoryUtils.class)
public interface ItemScrollerInventoryUtilsAccessor
{
	@Invoker(value = "areSlotsInSameInventory", remap = false)
	static boolean areSlotsInSameInventory(Slot slot1, Slot slot2)
	{
		return false;
	}
}
