package me.fallenbreath.tweakermore.mixins.tweaks.serverDataSyncer;

import net.minecraft.inventory.DoubleInventory;
import net.minecraft.inventory.Inventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DoubleInventory.class)
public interface DoubleInventoryAccessor
{
	@Accessor
	Inventory getFirst();

	@Accessor
	Inventory getSecond();
}
