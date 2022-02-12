package me.fallenbreath.tweakermore.mixins.core.gui;

import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.widgets.WidgetConfigOption;
import fi.dy.masa.malilib.gui.widgets.WidgetConfigOptionBase;
import fi.dy.masa.malilib.gui.widgets.WidgetListConfigOptions;
import fi.dy.masa.malilib.gui.widgets.WidgetListConfigOptionsBase;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.config.TweakerMoreOption;
import me.fallenbreath.tweakermore.gui.TweakerMoreConfigGui;
import me.fallenbreath.tweakermore.gui.TweakerMoreOptionLabel;
import me.fallenbreath.tweakermore.util.StringUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.function.Function;

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

	private boolean showOriginalTextsThisTime;

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
		if (isTweakerMoreConfigGui() || TweakerMoreConfigs.APPLY_TWEAKERMORE_OPTION_LABEL_GLOBALLY.getBooleanValue())
		{
			int x = args.get(0);
			int y = args.get(1);
			int width = args.get(2);
			int height = args.get(3);
			int textColor = args.get(4);
			String[] lines = args.get(5);
			if (lines.length != 1)
			{
				return;
			}

			args.set(5, null);  // cancel original call

			Function<String, String> modifier = s -> s;
			if (isTweakerMoreConfigGui())
			{
				lines[0] = StringUtil.TWEAKERMORE_NAMESPACE_PREFIX + lines[0];
				if (!TweakerMoreConfigs.getOptionFromConfig(config).map(TweakerMoreOption::isEnabled).orElse(true))
				{
					modifier = s -> GuiBase.TXT_DARK_RED + s + GuiBase.TXT_RST;
				}
			}
			TweakerMoreOptionLabel label = new TweakerMoreOptionLabel(x, y, width, height, textColor, lines, new String[]{config.getName()}, modifier);
			this.addWidget(label);
			this.showOriginalTextsThisTime = label.shouldShowOriginalLines();
		}
		else
		{
			this.showOriginalTextsThisTime = false;
		}
	}

	@ModifyArg(
			method = "addConfigOption",
			at = @At(
					value = "INVOKE",
					target = "Lfi/dy/masa/malilib/gui/widgets/WidgetConfigOption;addConfigComment(IIIILjava/lang/String;)V",
					remap = false
			),
			index = 1,
			remap = false
	)
	private int tweaksCommentHeight_minY(int y)
	{
		if (this.showOriginalTextsThisTime)
		{
			y -= 4;
		}
		return y;
	}

	@ModifyArg(
			method = "addConfigOption",
			at = @At(
					value = "INVOKE",
					target = "Lfi/dy/masa/malilib/gui/widgets/WidgetConfigOption;addConfigComment(IIIILjava/lang/String;)V",
					remap = false
			),
			index = 3,
			remap = false
	)
	private int tweaksCommentHeight_height(int height)
	{
		if (this.showOriginalTextsThisTime)
		{
			height += 6;
		}
		return height;
	}
}
