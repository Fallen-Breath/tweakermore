package me.fallenbreath.tweakermore.mixins.tweaks.tweakmAutoContainerProcess;

import me.fallenbreath.tweakermore.impl.tweakmAutoContainerProcess.ContainerProcessor;
import me.fallenbreath.tweakermore.util.mixin.ModIds;
import me.fallenbreath.tweakermore.util.mixin.ModRequire;
import me.fallenbreath.tweakermore.util.mixin.Requirement;
import net.minecraft.container.Container;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@ModRequire(enableWhen = @Requirement(ModIds.itemscroller))
@Mixin(Container.class)
public abstract class ContainerMixin
{
	@Inject(method = "updateSlotStacks", at = @At("TAIL"))
	private void tweakmAutoFillContainer(CallbackInfo ci)
	{
		ContainerProcessor.process((Container)(Object)this);
	}
}
