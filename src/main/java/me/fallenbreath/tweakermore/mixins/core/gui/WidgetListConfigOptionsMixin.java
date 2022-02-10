package me.fallenbreath.tweakermore.mixins.core.gui;

import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.widgets.WidgetConfigOption;
import fi.dy.masa.malilib.gui.widgets.WidgetListConfigOptions;
import fi.dy.masa.malilib.gui.widgets.WidgetListConfigOptionsBase;
import me.fallenbreath.tweakermore.gui.TweakerMoreConfigGui;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(WidgetListConfigOptions.class)
public abstract class WidgetListConfigOptionsMixin extends WidgetListConfigOptionsBase<GuiConfigsBase.ConfigOptionWrapper, WidgetConfigOption>
{
	@Shadow(remap = false) @Final protected GuiConfigsBase parent;

	public WidgetListConfigOptionsMixin(int x, int y, int width, int height, int configWidth)
	{
		super(x, y, width, height, configWidth);
	}

	@ModifyArg(
			method = "<init>",
			at = @At(
					value = "INVOKE",
					target = "Lfi/dy/masa/malilib/gui/widgets/WidgetSearchBar;<init>(IIIIILfi/dy/masa/malilib/gui/interfaces/IGuiIcon;Lfi/dy/masa/malilib/gui/LeftRight;)V"
			),
			index = 2,
			remap = false
	)
	private int tweakermoreSearchBarWidth(int width)
	{
		if (this.parent instanceof TweakerMoreConfigGui)
		{
			width -= 120;
		}
		return width;
	}
}
