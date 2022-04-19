package me.fallenbreath.tweakermore.mixins.tweaks.disableSignTextLengthLimit;

import net.minecraft.client.util.SelectionManager;
import org.spongepowered.asm.mixin.Mixin;

//#if MC < 11600
import org.spongepowered.asm.mixin.gen.Accessor;
//#endif

@Mixin(SelectionManager.class)
public interface SelectionManagerAccessor
{
	// un-used since mc 1.16
	//#if MC < 11600
	@Accessor(
			//#if MC < 11500
			//$$ "field_16455"
			//#endif
	)
	int getMaxLength();
	//#endif
}
