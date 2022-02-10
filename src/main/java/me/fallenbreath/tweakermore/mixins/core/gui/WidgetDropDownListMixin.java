package me.fallenbreath.tweakermore.mixins.core.gui;

import fi.dy.masa.malilib.gui.widgets.WidgetDropDownList;
import me.fallenbreath.tweakermore.gui.SelectorDropDownList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(WidgetDropDownList.class)
public abstract class WidgetDropDownListMixin
{
	@SuppressWarnings({"ConstantConditions", "PointlessBitwiseExpression"})
	@ModifyArgs(
			method = "render",
			at = @At(
					value = "INVOKE",
					target = "Lfi/dy/masa/malilib/render/RenderUtils;drawRect(IIIII)V",
					remap = false
			),
			remap = false
	)
	private void selectorDropDownListMakeOpaque(Args args)
	{
		if ((WidgetDropDownList<?>)(Object)this instanceof SelectorDropDownList<?>)
		{
			// ensure background is opaque
			int bgColor = args.get(4);
			int a = (bgColor >> 24) & 0xFF;
			bgColor = (0xFF << 24) | (a << 16) | (a << 8) | (a << 0);
			args.set(4, bgColor);

			// show left box border
			args.set(0, (int)args.get(0) + 1);
		}
	}

	@SuppressWarnings("ConstantConditions")
	@Inject(
			method = "onMouseScrolledImpl",
			at = @At(
					value = "INVOKE",
					target = "Lfi/dy/masa/malilib/gui/GuiScrollBar;offsetValue(I)V",
					shift = At.Shift.AFTER,
					remap = false
			),
			cancellable = true,
			remap = false
	)
	private void fixNoReturnValueHandlingForScroll(CallbackInfoReturnable<Boolean> cir)
	{
		if ((WidgetDropDownList<?>)(Object)this instanceof SelectorDropDownList<?>)
		{
			cir.setReturnValue(true);
		}
	}
}
