package me.fallenbreath.tweakermore.mixins.core.gui;

import fi.dy.masa.malilib.gui.widgets.WidgetConfigOptionBase;
import fi.dy.masa.malilib.gui.widgets.WidgetListConfigOptions;
import fi.dy.masa.malilib.gui.widgets.WidgetListConfigOptionsBase;
import me.fallenbreath.tweakermore.gui.TweakerMoreConfigGui;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(WidgetConfigOptionBase.class)
public abstract class WidgetConfigOptionBaseMixin
{
	@Shadow @Final protected WidgetListConfigOptionsBase<?, ?> parent;

	private boolean isTweakerMoreConfigGui()
	{
		return this.parent instanceof WidgetListConfigOptions && ((WidgetListConfigOptionsAccessor)this.parent).getParent() instanceof TweakerMoreConfigGui;
	}

	@Inject(
			method = "onMouseClickedImpl",
			at = @At("HEAD"),
			remap = false
	)
	private void onEntryClickedTkmHook(int mouseX, int mouseY, int mouseButton, CallbackInfoReturnable<Boolean> cir)
	{
		if (this.isTweakerMoreConfigGui())
		{
			// TODO
		}
	}
}
