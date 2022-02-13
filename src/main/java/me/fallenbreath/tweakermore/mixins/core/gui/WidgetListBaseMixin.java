package me.fallenbreath.tweakermore.mixins.core.gui;

import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.widgets.WidgetListBase;
import fi.dy.masa.malilib.gui.widgets.WidgetListConfigOptions;
import fi.dy.masa.malilib.gui.widgets.WidgetListEntryBase;
import me.fallenbreath.tweakermore.gui.TweakerMoreConfigGui;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WidgetListBase.class)
public abstract class WidgetListBaseMixin<TYPE, WIDGET extends WidgetListEntryBase<TYPE>>
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
					target = "Lfi/dy/masa/malilib/gui/widgets/WidgetBase;postRenderHovered(IIZLnet/minecraft/client/util/math/MatrixStack;)V",
					remap = true
			),
			remap = false
	)
	private void drawTweakerMoreConfigGuiDropDownListAgainBeforeHover(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks, CallbackInfo ci)
	{
		this.drawTweakerMoreConfigGuiDropDownListAgain(matrixStack, mouseX, mouseY);
	}

	@Inject(method = "drawContents", at = @At("TAIL"), remap = false)
	private void drawTweakerMoreConfigGuiDropDownListAgainAfterHover(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks, CallbackInfo ci)
	{
		this.drawTweakerMoreConfigGuiDropDownListAgain(matrixStack, mouseX, mouseY);
	}

	@SuppressWarnings("ConstantConditions")
	private boolean isTweakerMoreConfigGui()
	{
		if ((WidgetListBase<?, ?>)(Object)this instanceof WidgetListConfigOptions)
		{
			GuiConfigsBase guiConfig = ((WidgetListConfigOptionsAccessor) this).getParent();
			return guiConfig instanceof TweakerMoreConfigGui;
		}
		return false;
	}

	private void drawTweakerMoreConfigGuiDropDownListAgain(MatrixStack matrixStack, int mouseX, int mouseY)
	{
		if (this.isTweakerMoreConfigGui() && this.shouldRenderTweakerMoreConfigGuiDropDownList)
		{
			GuiConfigsBase guiConfig = ((WidgetListConfigOptionsAccessor) this).getParent();

			// render it again to make sure it's on the top but below hovering widgets
			((TweakerMoreConfigGui)guiConfig).renderDropDownList(matrixStack, mouseX, mouseY);
			this.shouldRenderTweakerMoreConfigGuiDropDownList = false;
		}
	}
}
