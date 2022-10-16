package me.fallenbreath.tweakermore.mixins.core.gui;

//#if MC >= 11800
//$$ import fi.dy.masa.malilib.config.IConfigBoolean;
//$$ import fi.dy.masa.malilib.config.IConfigResettable;
//#endif

import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.IConfigBoolean;
import fi.dy.masa.malilib.config.IHotkeyTogglable;
import fi.dy.masa.malilib.config.gui.ConfigOptionChangeListenerButton;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.ConfigButtonBoolean;
import fi.dy.masa.malilib.gui.button.ConfigButtonKeybind;
import fi.dy.masa.malilib.gui.interfaces.IKeybindConfigGui;
import fi.dy.masa.malilib.gui.widgets.*;
import fi.dy.masa.malilib.hotkeys.*;
import fi.dy.masa.malilib.util.StringUtils;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.config.options.IHotkeyWithSwitch;
import me.fallenbreath.tweakermore.config.options.TweakerMoreIConfigBase;
import me.fallenbreath.tweakermore.gui.ConfigButtonBooleanSwitch;
import me.fallenbreath.tweakermore.gui.HotkeyedBooleanResetListener;
import me.fallenbreath.tweakermore.gui.TweakerMoreConfigGui;
import me.fallenbreath.tweakermore.gui.TweakerMoreOptionLabel;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.Objects;
import java.util.function.Function;

@Mixin(WidgetConfigOption.class)
public abstract class WidgetListConfigOptionMixin extends WidgetConfigOptionBase<GuiConfigsBase.ConfigOptionWrapper>
{
	@Shadow(remap = false) @Final protected IKeybindConfigGui host;
	@Shadow(remap = false) @Final protected GuiConfigsBase.ConfigOptionWrapper wrapper;
	@Shadow(remap = false) @Final @Mutable protected KeybindSettings initialKeybindSettings;

	@Shadow(remap = false) protected abstract void addKeybindResetButton(int x, int y, IKeybind keybind, ConfigButtonKeybind buttonHotkey);

	@Unique
	private boolean initialBoolean$TKM;

	public WidgetListConfigOptionMixin(int x, int y, int width, int height, WidgetListConfigOptionsBase<?, ?> parent, GuiConfigsBase.ConfigOptionWrapper entry, int listIndex)
	{
		super(x, y, width, height, parent, entry, listIndex);
	}

	private boolean isTweakerMoreConfigGui()
	{
		return this.parent instanceof WidgetListConfigOptions && ((WidgetListConfigOptionsAccessor)this.parent).getParent() instanceof TweakerMoreConfigGui;
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
			if (config instanceof TweakerMoreIConfigBase)
			{
				modifier = ((TweakerMoreIConfigBase)config).getGuiDisplayLineModifier();
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

	//#if MC >= 11800
	//$$ @Inject(
	//$$ 		method = "addHotkeyConfigElements",
	//$$ 		at = @At(value = "HEAD"),
	//$$ 		remap = false,
	//$$ 		cancellable = true
	//$$ )
	//$$ private void tweakerMoreCustomConfigGui(int x, int y, int configWidth, String configName, IHotkey config, CallbackInfo ci)
	//$$ {
	//$$ 	if (this.isTweakerMoreConfigGui())
	//$$ 	{
	//$$ 		if (config instanceof IHotkeyWithSwitch)
	//$$ 		{
	//$$ 			this.addHotkeyWithSwitchButtons(x, y, configWidth, (IHotkeyWithSwitch)config);
	//$$ 			ci.cancel();
	//$$ 		}
	//$$ 		else if ((config).getKeybind() instanceof KeybindMulti)
	//$$ 		{
	//$$ 			this.addButtonAndHotkeyWidgets(x, y, configWidth, config);
	//$$ 			ci.cancel();
	//$$ 		}
	//$$ 	}
	//$$ }
	//#else
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
		if (this.isTweakerMoreConfigGui() && config instanceof IHotkey)
		{
			boolean modified = true;
			if (config instanceof IHotkeyTogglable)
			{
				this.addHotkeyTogglableButtons(x, y, configWidth, (IHotkeyTogglable)config);
			}
			else if (config instanceof IHotkeyWithSwitch)
			{
				this.addHotkeyWithSwitchButtons(x, y, configWidth, (IHotkeyWithSwitch)config);
			}
			else if (((IHotkey)config).getKeybind() instanceof KeybindMulti)
			{
				this.addButtonAndHotkeyWidgets(x, y, configWidth, (IHotkey)config);
			}
			else
			{
				modified = false;
			}
			if (modified)
			{
				ci.cancel();
			}
		}
	}
	//#endif

	private void addButtonAndHotkeyWidgets(int x, int y, int configWidth, IHotkey config)
	{
		IKeybind keybind = config.getKeybind();

		int triggerBtnWidth = (configWidth - 24) / 2;
		ButtonGeneric triggerButton = new ButtonGeneric(
				x, y, triggerBtnWidth, 20,
				StringUtils.translate("tweakermore.gui.trigger_button.text"),
				StringUtils.translate("tweakermore.gui.trigger_button.hover", config.getName())
		);
		this.addButton(triggerButton, (button, mouseButton) -> {
			IHotkeyCallback callback = ((KeybindMultiAccessor)keybind).getCallback();
			if (callback == null)
			{
				return;
			}
			KeyAction activateOn = keybind.getSettings().getActivateOn();
			if (activateOn == KeyAction.BOTH || activateOn == KeyAction.PRESS)
			{
				callback.onKeyAction(KeyAction.PRESS, keybind);
			}
			if (activateOn == KeyAction.BOTH || activateOn == KeyAction.RELEASE)
			{
				callback.onKeyAction(KeyAction.RELEASE, keybind);
			}
			if (config instanceof TweakerMoreIConfigBase)
			{
				((TweakerMoreIConfigBase)config).updateStatisticOnUse();
			}
		});

		x += triggerBtnWidth + 2;
		configWidth -= triggerBtnWidth + 2 + 22;

		ConfigButtonKeybind keybindButton = new ConfigButtonKeybind(x, y, configWidth, 20, keybind, this.host);
		x += configWidth + 2;

		this.addWidget(new WidgetKeybindSettings(x, y, 20, 20, keybind, config.getName(), this.parent, this.host.getDialogHandler()));

		//#if MC >= 11800
		//$$ x += 22;
		//#else
		x += 24;
		//#endif

		this.addButton(keybindButton, this.host.getButtonPressListener());
		this.addKeybindResetButton(x, y, keybind, keybindButton);
	}

	private <T extends IHotkey & IConfigBoolean> void addBooleanAndHotkeyWidgets(int x, int y, int configWidth, T config, ButtonGeneric booleanButton)
	{
		IKeybind keybind = config.getKeybind();

		int booleanBtnWidth = booleanButton.getWidth();
		x += booleanBtnWidth + 2;
		configWidth -= booleanBtnWidth + 2 + 22;

		ConfigButtonKeybind keybindButton = new ConfigButtonKeybind(x, y, configWidth, 20, keybind, this.host);
		x += configWidth + 2;

		this.addWidget(new WidgetKeybindSettings(x, y, 20, 20, keybind, config.getName(), this.parent, this.host.getDialogHandler()));
		x += 24;

		ButtonGeneric resetButton = this.createResetButton(x, y, config);

		ConfigOptionChangeListenerButton booleanChangeListener = new ConfigOptionChangeListenerButton(config, resetButton, null);
		HotkeyedBooleanResetListener resetListener = new HotkeyedBooleanResetListener(config, booleanButton, keybindButton, resetButton, this.host);

		this.host.addKeybindChangeListener(
				//#if MC >= 11800
				//$$ resetListener::updateButtons
				//#else
				resetListener
				//#endif
		);

		this.addButton(booleanButton, booleanChangeListener);
		this.addButton(keybindButton, this.host.getButtonPressListener());
		this.addButton(resetButton, resetListener);
	}

	private void addHotkeyTogglableButtons(int x, int y, int configWidth, IHotkeyTogglable config)
	{
		int booleanBtnWidth = (configWidth - 24) / 2;
		ButtonGeneric booleanButton = new ConfigButtonBoolean(x, y, booleanBtnWidth, 20, config);
		this.addBooleanAndHotkeyWidgets(x, y, configWidth, config, booleanButton);
	}

	private void addHotkeyWithSwitchButtons(int x, int y, int configWidth, IHotkeyWithSwitch config)
	{
		int booleanBtnWidth = (configWidth - 24) / 2;
		ButtonGeneric booleanButton = new ConfigButtonBooleanSwitch(x, y, booleanBtnWidth, 20, config);
		this.addBooleanAndHotkeyWidgets(x, y, configWidth, config, booleanButton);
	}

	/*
	 * Stolen from malilib 1.18 v0.11.4
	 * to make IHotkeyWithSwitch option panel works
	 * and also to make compact ConfigBooleanHotkeyed option panel works in <1.18
	 */

	@Inject(method = "<init>", at = @At("TAIL"), remap = false)
	private void initInitialState(CallbackInfo ci)
	{
		if (isTweakerMoreConfigGui() && this.wrapper.getType() == GuiConfigsBase.ConfigOptionWrapper.Type.CONFIG)
		{
			IConfigBase config = wrapper.getConfig();
			if (config instanceof IConfigBoolean && config instanceof IHotkey)
			{
				this.initialBoolean$TKM = ((IConfigBoolean)config).getBooleanValue();
				this.initialStringValue = ((IHotkey)config).getKeybind().getStringValue();
				this.initialKeybindSettings = ((IHotkey)config).getKeybind().getSettings();
			}
		}
	}

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
		if (config instanceof IConfigBoolean && config instanceof IHotkey && TweakerMoreConfigs.hasConfig(config))
		{
			IKeybind keybind = ((IHotkey)config).getKeybind();
			cir.setReturnValue(
					this.initialBoolean$TKM != ((IConfigBoolean)config).getBooleanValue() ||
					!Objects.equals(this.initialStringValue, keybind.getStringValue()) ||
					!Objects.equals(this.initialKeybindSettings, keybind.getSettings())
			);
		}
	}

	/*
	 * Compact panel thing ends
	 */

	// some ocd alignment things xd
	//#if MC < 11800
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
	//#endif

	//#if MC >= 11800
	//$$ @ModifyVariable(
	//$$ 		method = "addBooleanAndHotkeyWidgets",
	//$$ 		at = @At(
	//$$ 				value = "STORE",
	//$$ 				ordinal = 0
	//$$ 		),
	//$$ 		ordinal = 3,
	//$$ 		remap = false
	//$$ )
	//$$ private int tweakerMoreDynamicBooleanButtonWidth(int booleanBtnWidth, int x, int y, int configWidth, IConfigResettable resettableConfig, IConfigBoolean booleanConfig, IKeybind keybind)
	//$$ {
	//$$ 	if (this.isTweakerMoreConfigGui())
	//$$ 	{
	//$$ 		booleanBtnWidth = (configWidth - 24) / 2;
	//$$ 	}
	//$$ 	return booleanBtnWidth;
	//$$ }
	//#else
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
	//#endif
}
