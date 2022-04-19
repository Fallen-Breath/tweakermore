package me.fallenbreath.tweakermore.mixins.tweaks.disableSignTextLengthLimit;

import net.minecraft.client.util.SelectionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SelectionManager.class)
public interface SelectionManagerAccessor
{
	@Accessor(
			//#if MC < 11500
			//$$ "field_16455"
			//#endif
	)
	int getMaxLength();
}
