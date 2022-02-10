package me.fallenbreath.tweakermore.mixins.core.gui;

import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.IHotkeyTogglable;
import fi.dy.masa.malilib.config.gui.ConfigOptionChangeListenerButton;
import fi.dy.masa.malilib.config.options.ConfigBooleanHotkeyed;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.ConfigButtonBoolean;
import fi.dy.masa.malilib.gui.button.ConfigButtonKeybind;
import fi.dy.masa.malilib.gui.interfaces.IKeybindConfigGui;
import fi.dy.masa.malilib.gui.widgets.*;
import fi.dy.masa.malilib.hotkeys.IKeybind;
import fi.dy.masa.malilib.hotkeys.KeybindSettings;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.gui.HotkeyedBooleanResetListener;
import me.fallenbreath.tweakermore.gui.TranslatedOptionLabel;
import me.fallenbreath.tweakermore.gui.TweakerMoreConfigGui;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.Objects;

@Mixin(WidgetConfigOption.class)
public abstract class WidgetListConfigOptionMixin extends WidgetConfigOptionBase<GuiConfigsBase.ConfigOptionWrapper>
{
	@Shadow(remap = false) @Final protected IKeybindConfigGui host;

	@Shadow(remap = false) @Final protected GuiConfigsBase.ConfigOptionWrapper wrapper;

	@Mutable
	@Shadow(remap = false) @Final protected KeybindSettings initialKeybindSettings;

	@Unique
	private boolean initialBoolean;

	public WidgetListConfigOptionMixin(int x, int y, int width, int height, WidgetListConfigOptionsBase<?, ?> parent, GuiConfigsBase.ConfigOptionWrapper entry, int listIndex)
	{
		super(x, y, width, height, parent, entry, listIndex);
	}

	private boolean isTweakerMoreConfigGui()
	{
		return this.parent instanceof WidgetListConfigOptions && ((WidgetListConfigOptionsAccessor)this.parent).getParent() instanceof TweakerMoreConfigGui;
	}

	/**
	 * Stolen from malilib 1.18 v0.11.4
	 * to make compact ConfigBooleanHotkeyed option panel works
	 */
	@Inject(method = "<init>", at = @At("TAIL"), remap = false)
	private void initInitialState(CallbackInfo ci)
	{
		if (isTweakerMoreConfigGui() && this.wrapper.getType() == GuiConfigsBase.ConfigOptionWrapper.Type.CONFIG)
		{
			IConfigBase config = wrapper.getConfig();
			if (config instanceof ConfigBooleanHotkeyed)
			{
				this.initialBoolean = ((ConfigBooleanHotkeyed)config).getBooleanValue();
				this.initialStringValue = ((ConfigBooleanHotkeyed)config).getKeybind().getStringValue();
				this.initialKeybindSettings = ((ConfigBooleanHotkeyed)config).getKeybind().getSettings();
			}
		}
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
			labelWidth = this.width - configWidth - 60;
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

	@Inject(
			method = "addConfigOption",
			at = @At(
					value = "FIELD",
					target = "Lfi/dy/masa/malilib/config/ConfigType;BOOLEAN:Lfi/dy/masa/malilib/config/ConfigType;",
					remap = false
			),
			remap = false,
			cancellable = true
	)
	private void tweakerMoreCustomConfigGui(int x, int y, float zLevel, int labelWidth, int configWidth, IConfigBase config, CallbackInfo ci)
	{
		if (this.isTweakerMoreConfigGui() && config instanceof IHotkeyTogglable)
		{
			this.addBooleanAndHotkeyWidgets(x, y, configWidth, (IHotkeyTogglable)config);
			ci.cancel();
		}
	}

	/**
	 * Stolen from malilib 1.18 v0.11.4
	 */
	private void addBooleanAndHotkeyWidgets(int x, int y, int configWidth, IHotkeyTogglable config)
	{
		IKeybind keybind = config.getKeybind();

		int booleanBtnWidth = 60;
		ConfigButtonBoolean booleanButton = new ConfigButtonBoolean(x, y, booleanBtnWidth, 20, config);
		x += booleanBtnWidth + 2;
		configWidth -= booleanBtnWidth + 2 + 22;

		ConfigButtonKeybind keybindButton = new ConfigButtonKeybind(x, y, configWidth, 20, keybind, this.host);
		x += configWidth + 2;

		this.addWidget(new WidgetKeybindSettings(x, y, 20, 20, keybind, config.getName(), this.parent, this.host.getDialogHandler()));
		x += 24;

		ButtonGeneric resetButton = this.createResetButton(x, y, config);

		ConfigOptionChangeListenerButton booleanChangeListener = new ConfigOptionChangeListenerButton(config, resetButton, null);
		HotkeyedBooleanResetListener resetListener = new HotkeyedBooleanResetListener(config, booleanButton, keybindButton, resetButton, this.host);

		this.host.addKeybindChangeListener(resetListener);

		this.addButton(booleanButton, booleanChangeListener);
		this.addButton(keybindButton, this.host.getButtonPressListener());
		this.addButton(resetButton, resetListener);
	}

	/**
	 * Stolen from malilib 1.18 v0.11.4
	 * to make compact ConfigBooleanHotkeyed option panel works
	 */
	@Inject(
			method = "wasConfigModified",
			at = @At(
					value = "INVOKE",
					target = "Lfi/dy/masa/malilib/gui/GuiConfigsBase$ConfigOptionWrapper;getConfig()Lfi/dy/masa/malilib/config/IConfigBase;",
					ordinal = 0,
					remap = false
			),
			cancellable = true,
			remap = false
	)
	private void specialJudgeCustomConfigBooleanHotkeyed(CallbackInfoReturnable<Boolean> cir)
	{
		IConfigBase config = this.wrapper.getConfig();
		if (config instanceof ConfigBooleanHotkeyed && TweakerMoreConfigs.hasConfig(config))
		{
			ConfigBooleanHotkeyed booleanHotkey = (ConfigBooleanHotkeyed)config;
			IKeybind keybind = booleanHotkey.getKeybind();
			cir.setReturnValue(
					this.initialBoolean != booleanHotkey.getBooleanValue() ||
					!Objects.equals(this.initialStringValue, keybind.getStringValue()) ||
					!Objects.equals(this.initialKeybindSettings, keybind.getSettings())
			);
		}
	}

	// some ocd alignment things xd

	@ModifyArg(
			method = "addConfigOption",
			at = @At(
					value = "INVOKE",
					target = "Lfi/dy/masa/malilib/gui/widgets/WidgetKeybindSettings;<init>(IIIILfi/dy/masa/malilib/hotkeys/IKeybind;Ljava/lang/String;Lfi/dy/masa/malilib/gui/widgets/WidgetListBase;Lfi/dy/masa/malilib/gui/interfaces/IDialogHandler;)V",
					remap = false
			),
			index = 0,
			remap = false
	)
	private int whyNotAlignTheHotkeyConfigButtonWidthWithOthers(int x)
	{
		if (isTweakerMoreConfigGui())
		{
			x += 1;
		}
		return x;
	}

	@ModifyArg(
			method = "addConfigOption",
			at = @At(
					value = "INVOKE",
					target = "Lfi/dy/masa/malilib/gui/button/ConfigButtonKeybind;<init>(IIIILfi/dy/masa/malilib/hotkeys/IKeybind;Lfi/dy/masa/malilib/gui/interfaces/IKeybindConfigGui;)V",
					remap = false
			),
			index = 2,
			remap = false
	)
	private int whyNotAlignTheHotkeySetterButtonWidthWithOthers(int width)
	{
		if (isTweakerMoreConfigGui())
		{
			width += 3;
		}
		return width;
	}
}
