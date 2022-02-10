package me.fallenbreath.tweakermore.mixins.tweaks.tweakmAutoContainerProcess;

import fi.dy.masa.itemscroller.util.InventoryUtils;
import me.fallenbreath.tweakermore.util.mixin.ModIds;
import me.fallenbreath.tweakermore.util.mixin.ModRequire;
import me.fallenbreath.tweakermore.util.mixin.Requirement;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@ModRequire(enableWhen = @Requirement(ModIds.itemscroller))
@Mixin(InventoryUtils.class)
public interface ItemScrollerInventoryUtilsAccessor
{
	@Invoker(value = "areSlotsInSameInventory", remap = false)
	static boolean areSlotsInSameInventory(Slot slot1, Slot slot2)
	{
		return false;
	}
}
