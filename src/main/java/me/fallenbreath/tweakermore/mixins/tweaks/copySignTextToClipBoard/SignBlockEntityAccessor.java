package me.fallenbreath.tweakermore.mixins.tweaks.copySignTextToClipBoard;

import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SignBlockEntity.class)
public interface SignBlockEntityAccessor
{
	@Accessor
	Text[] getTexts();
}
