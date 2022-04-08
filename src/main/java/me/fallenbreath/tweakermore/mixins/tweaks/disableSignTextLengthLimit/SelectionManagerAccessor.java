package me.fallenbreath.tweakermore.mixins.tweaks.disableSignTextLengthLimit;

import net.minecraft.client.util.SelectionManager;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SelectionManager.class)
public interface SelectionManagerAccessor
{
// un used since mc 1.16
//	@Accessor
//	int getMaxLength();
}
