package me.fallenbreath.tweakermore.mixins.tweaks.tweakmAutoContainerProcess;

import fi.dy.masa.itemscroller.util.InventoryUtils;
import me.fallenbreath.tweakermore.util.ModIds;
import me.fallenbreath.tweakermore.util.dependency.Condition;
import me.fallenbreath.tweakermore.util.dependency.Strategy;
import net.minecraft.container.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Strategy(enableWhen = @Condition(ModIds.itemscroller))
@Mixin(InventoryUtils.class)
public interface ItemScrollerInventoryUtilsAccessor
{
	@Invoker(value = "areSlotsInSameInventory", remap = false)
	static boolean areSlotsInSameInventory(Slot slot1, Slot slot2)
	{
		return false;
	}
}
