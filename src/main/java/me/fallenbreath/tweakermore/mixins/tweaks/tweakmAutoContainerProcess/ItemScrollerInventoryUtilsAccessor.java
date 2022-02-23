package me.fallenbreath.tweakermore.mixins.tweaks.tweakmAutoContainerProcess;

import fi.dy.masa.itemscroller.util.InventoryUtils;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.util.ModIds;
import net.minecraft.container.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Restriction(require = @Condition(ModIds.itemscroller))
@Mixin(InventoryUtils.class)
public interface ItemScrollerInventoryUtilsAccessor
{
	@Invoker(value = "areSlotsInSameInventory", remap = false)
	static boolean areSlotsInSameInventory(Slot slot1, Slot slot2)
	{
		return false;
	}
}
