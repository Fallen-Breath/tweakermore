package me.fallenbreath.tweakermore.mixins.core.gui;

import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.widgets.*;
import me.fallenbreath.tweakermore.gui.TranslatedOptionLabel;
import me.fallenbreath.tweakermore.gui.TweakerMoreConfigGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(WidgetConfigOption.class)
public abstract class WidgetListConfigOptionMixin extends WidgetConfigOptionBase<GuiConfigsBase.ConfigOptionWrapper>
{
	public WidgetListConfigOptionMixin(int x, int y, int width, int height, WidgetListConfigOptionsBase<?, ?> parent, GuiConfigsBase.ConfigOptionWrapper entry, int listIndex)
	{
		super(x, y, width, height, parent, entry, listIndex);
	}

	private boolean isTweakerMoreConfigGui()
	{
		return this.parent instanceof WidgetListConfigOptions && ((WidgetListConfigOptionsAccessor)this.parent).getParent() instanceof TweakerMoreConfigGui;
	}

	@ModifyArgs(
			method = "addConfigOption",
			at = @At(
					value = "INVOKE",
					target = "Lfi/dy/masa/malilib/gui/widgets/WidgetConfigOption;addLabel(IIIII[Ljava/lang/String;)V",
					remap = false
			),
			remap = false
	)
	private void useMyBetterOptionLabelForTweakerMore(Args args)
	{
		if (isTweakerMoreConfigGui())
		{
			int x = args.get(0);
			int y = args.get(1);
			int width = args.get(2);
			int height = args.get(3);
			int textColor = args.get(4);
			String[] lines = args.get(5);

			args.set(5, null);  // cancel original call

			WidgetLabel label = new TranslatedOptionLabel(x, y, width, height, textColor, lines);
			this.addWidget(label);
		}
	}
}
