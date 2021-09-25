package me.fallenbreath.tweakermore.mixins.tweaks.tweakmAutoFillContainer;

import me.fallenbreath.tweakermore.impl.tweakmAutoFillContainer.TweakAutoFillContainer;
import net.minecraft.screen.ScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ScreenHandler.class)
public abstract class ContainerMixin
{
	@Inject(method = "updateSlotStacks", at = @At("TAIL"))
	private void tweakmAutoFillContainer(CallbackInfo ci)
	{
		TweakAutoFillContainer.process((ScreenHandler)(Object)this);
	}
}
