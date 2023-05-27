/*
 * This file is part of the TweakerMore project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2023  Fallen_Breath and contributors
 *
 * TweakerMore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * TweakerMore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with TweakerMore.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.fallenbreath.tweakermore.mixins.core.gui.panel;

import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.IConfigBoolean;
import fi.dy.masa.malilib.config.IConfigResettable;
import fi.dy.masa.malilib.config.IHotkeyTogglable;
import fi.dy.masa.malilib.config.gui.ConfigOptionChangeListenerButton;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.button.*;
import fi.dy.masa.malilib.gui.interfaces.IKeybindConfigGui;
import fi.dy.masa.malilib.gui.widgets.*;
import fi.dy.masa.malilib.hotkeys.*;
import fi.dy.masa.malilib.util.StringUtils;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.config.options.IHotkeyWithSwitch;
import me.fallenbreath.tweakermore.config.options.IOptionListHotkeyed;
import me.fallenbreath.tweakermore.config.options.TweakerMoreIConfigBase;
import me.fallenbreath.tweakermore.gui.*;
import me.fallenbreath.tweakermore.mixins.core.gui.access.KeybindMultiAccessor;
import me.fallenbreath.tweakermore.mixins.core.gui.access.WidgetListConfigOptionsAccessor;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

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

	@Unique
	private boolean isTweakerMoreConfigGui()
	{
		return this.parent instanceof WidgetListConfigOptions && ((WidgetListConfigOptionsAccessor)this.parent).getParent() instanceof TweakerMoreConfigGui;
	}

	//#if MC >= 11800
	//$$ @Inject(
	//$$ 		method = "addConfigOption",
	//$$ 		at = @At(
	//$$ 				value = "INVOKE",
	//$$ 				target = "Lfi/dy/masa/malilib/gui/button/ConfigButtonOptionList;<init>(IIIILfi/dy/masa/malilib/config/IConfigOptionList;)V"
	//$$ 		),
	//$$ 		remap = false,
	//$$ 		cancellable = true
	//$$ )
	//$$ private void tweakerMoreCustomConfigGui_IOptionListHotkeyed(int x, int y, float zLevel, int labelWidth, int configWidth, IConfigBase config, CallbackInfo ci)
	//$$ {
	//$$ 	if (this.isTweakerMoreConfigGui())
	//$$ 	{
	//$$ 		if (config instanceof IOptionListHotkeyed)
	//$$ 		{
	//$$ 			this.addOptionListWithHotkey(x, y, configWidth, (IOptionListHotkeyed)config);
	//$$ 			ci.cancel();
	//$$ 		}
	//$$ 	}
	//$$ }
	//$$
	//$$ @Inject(
	//$$ 		method = "addHotkeyConfigElements",
	//$$ 		at = @At(value = "HEAD"),
	//$$ 		remap = false,
	//$$ 		cancellable = true
	//$$ )
	//$$ private void tweakerMoreCustomConfigGui_IHotkey(int x, int y, int configWidth, String configName, IHotkey config, CallbackInfo ci)
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
			else if (config instanceof IOptionListHotkeyed)
			{
				this.addOptionListWithHotkey(x, y, configWidth, (IOptionListHotkeyed)config);
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

	@Inject(method = "addConfigButtonEntry", at = @At("HEAD"), remap = false)
	private void enableEnableValueHoveringForConfigButtonOptionList(int xReset, int yReset, IConfigResettable config, ButtonBase optionButton, CallbackInfo ci)
	{
		if (optionButton instanceof ConfigButtonOptionListHovering)
		{
			((ConfigButtonOptionListHovering)optionButton).setEnableValueHovering();
		}
	}

	/**
	 * For regular IHotkey
	 */
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

	/*
	 * ------------------------------------------------
	 *             Widgets Builders start
	 * ------------------------------------------------
	 */

	/**
	 * Common logic used for IHotkeyTogglable, IHotkeyWithSwitch and IOptionListHotkeyed
	 * whose layouts all are [some button] [keybind button] [reset button]
	 */
	private void addValueWithKeybindWidgets(int x, int y, int configWidth, IHotkey config, ButtonGeneric valueButton)
	{
		IKeybind keybind = config.getKeybind();

		int booleanBtnWidth = valueButton.getWidth();
		x += booleanBtnWidth + 2;
		configWidth -= booleanBtnWidth + 2 + 22;

		ConfigButtonKeybind keybindButton = new ConfigButtonKeybind(x, y, configWidth, 20, keybind, this.host);
		x += configWidth + 2;

		this.addWidget(new WidgetKeybindSettings(x, y, 20, 20, keybind, config.getName(), this.parent, this.host.getDialogHandler()));

		//#if MC >= 11800
		//$$ x += 22;
		//#else
		x += 24;
		//#endif

		ButtonGeneric resetButton = this.createResetButton(x, y, config);

		ConfigOptionChangeListenerButton booleanChangeListener = new ConfigOptionChangeListenerButton(config, resetButton, null);
		HotkeyedResetListener resetListener = new HotkeyedResetListener(config, valueButton, keybindButton, resetButton, this.host);

		this.host.addKeybindChangeListener(
				//#if MC >= 11800
				//$$ resetListener::updateButtons
				//#else
				resetListener
				//#endif
		);

		this.addButton(valueButton, booleanChangeListener);
		this.addButton(keybindButton, this.host.getButtonPressListener());
		this.addButton(resetButton, resetListener);
	}

	private void addHotkeyTogglableButtons(int x, int y, int configWidth, IHotkeyTogglable config)
	{
		int booleanBtnWidth = (configWidth - 24) / 2;
		ButtonGeneric booleanButton = new ConfigButtonBoolean(x, y, booleanBtnWidth, 20, config);
		this.addValueWithKeybindWidgets(x, y, configWidth, config, booleanButton);
	}

	private void addHotkeyWithSwitchButtons(int x, int y, int configWidth, IHotkeyWithSwitch config)
	{
		int booleanBtnWidth = (configWidth - 24) / 2;
		ButtonGeneric booleanButton = new ConfigButtonBooleanSwitch(x, y, booleanBtnWidth, 20, config);
		this.addValueWithKeybindWidgets(x, y, configWidth, config, booleanButton);
	}

	private void addOptionListWithHotkey(int x, int y, int configWidth, IOptionListHotkeyed config)
	{
		int optionBtnWidth = (configWidth - 24) / 2;
		ConfigButtonOptionList optionButton = new ConfigButtonOptionList(x, y, optionBtnWidth, 20, config);
		((ConfigButtonOptionListHovering)optionButton).setEnableValueHovering();
		this.addValueWithKeybindWidgets(x, y, configWidth, config, optionButton);
	}

	/*
	 * ------------------------------------------------
	 *             Widgets Builders ends
	 * ------------------------------------------------
	 */

	/*
	 * Stolen from malilib 1.18 v0.11.4
	 * to make IHotkeyWithSwitch option panel works
	 * and also to make compact ConfigBooleanHotkeyed option panel works in <1.18
	 *
	 * Compact panel thing starts
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
}
