package me.fallenbreath.tweakermore.mixins.core.gui;

import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.widgets.WidgetListBase;
import fi.dy.masa.malilib.gui.widgets.WidgetListConfigOptions;
import me.fallenbreath.tweakermore.gui.TweakerMoreConfigGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WidgetListBase.class)
public abstract class WidgetListBaseMixin
{
	private boolean shouldRenderTweakerMoreConfigGuiDropDownList = false;

	@Inject(method = "drawContents", at = @At("HEAD"), remap = false)
	private void drawTweakerMoreConfigGuiDropDownListSetFlag(CallbackInfo ci)
	{
		shouldRenderTweakerMoreConfigGuiDropDownList = true;
	}

	@Inject(
			method = "drawContents",
			at = @At(
					value = "INVOKE",
					target = "Lfi/dy/masa/malilib/gui/widgets/WidgetBase;postRenderHovered(IIZ)V",
					remap = false
			),
			remap = false
	)
	private void drawTweakerMoreConfigGuiDropDownListAgainBeforeHover(int mouseX, int mouseY, float partialTicks, CallbackInfo ci)
	{
		this.drawTweakerMoreConfigGuiDropDownListAgain(mouseX, mouseY);
	}

	@Inject(method = "drawContents", at = @At("TAIL"), remap = false)
	private void drawTweakerMoreConfigGuiDropDownListAgainAfterHover(int mouseX, int mouseY, float partialTicks, CallbackInfo ci)
	{
		this.drawTweakerMoreConfigGuiDropDownListAgain(mouseX, mouseY);
	}

	@SuppressWarnings("ConstantConditions")
	private void drawTweakerMoreConfigGuiDropDownListAgain(int mouseX, int mouseY)
	{
		if ((WidgetListBase<?, ?>)(Object)this instanceof WidgetListConfigOptions && shouldRenderTweakerMoreConfigGuiDropDownList)
		{
			GuiConfigsBase guiConfig = ((WidgetListConfigOptionsAccessor) this).getParent();
			if (guiConfig instanceof TweakerMoreConfigGui)
			{
				// render it again to make sure it's on the top but below hovering widgets
				((TweakerMoreConfigGui)guiConfig).renderDropDownList(mouseX, mouseY);
				shouldRenderTweakerMoreConfigGuiDropDownList = false;
			}
		}
	}
}
