package me.fallenbreath.tweakermore.mixins.core.gui;

import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.widgets.*;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.config.TweakerMoreOption;
import me.fallenbreath.tweakermore.gui.TranslatedOptionLabel;
import me.fallenbreath.tweakermore.gui.TweakerMoreConfigGui;
import me.fallenbreath.tweakermore.util.StringUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.function.Function;
import java.util.stream.IntStream;

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

	@ModifyVariable(
			method = "addConfigOption",
			at = @At("HEAD"),
			argsOnly = true,
			index = 4,
			remap = false
	)
	private int rightAlignedConfigPanel(int labelWidth, int x, int y, float zLevel, int labelWidth_, int configWidth, IConfigBase config)
	{
		if (isTweakerMoreConfigGui())
		{
			labelWidth = this.width - configWidth - 59;
		}
		return labelWidth;
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
	private void useMyBetterOptionLabelForTweakerMore(Args args, int x_, int y_, float zLevel, int labelWidth, int configWidth, IConfigBase config)
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

			IntStream.range(0, lines.length).forEach(i -> lines[i] = StringUtil.TWEAKERMORE_NAMESPACE_PREFIX + lines[i]);
			Function<String, String> modifier = s -> s;
			if (!TweakerMoreConfigs.getOptionFromConfig(config).map(TweakerMoreOption::isEnabled).orElse(true))
			{
				modifier = s -> GuiBase.TXT_DARK_RED + s + GuiBase.TXT_RST;
			}
			WidgetLabel label = new TranslatedOptionLabel(x, y, width, height, textColor, lines, modifier);
			this.addWidget(label);
		}
	}
}
