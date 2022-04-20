package me.fallenbreath.tweakermore.mixins.tweaks.features.copySignTextToClipBoard;

import net.minecraft.block.entity.SignBlockEntity;
import org.spongepowered.asm.mixin.Mixin;

//#if MC >= 11600
//$$ import net.minecraft.text.Text;
//$$ import org.spongepowered.asm.mixin.gen.Accessor;
//#endif

@Mixin(SignBlockEntity.class)
public interface SignBlockEntityAccessor
{
	//#if MC >= 11600
	//$$ @Accessor
	//$$ Text[] getTexts();
	//#endif
}