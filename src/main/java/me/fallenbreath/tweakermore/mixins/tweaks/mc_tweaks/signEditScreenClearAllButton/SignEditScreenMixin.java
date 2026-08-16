/*
 * This file is part of the TweakerMore project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026  Fallen_Breath and contributors
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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.signEditScreenClearAllButton;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.util.Messenger;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//#if MC >= 1.19.3
//$$ import net.minecraft.client.gui.components.Tooltip;
//$$ import net.minecraft.client.gui.screens.inventory.AbstractSignEditScreen;
//#else
import net.minecraft.client.gui.screens.inventory.SignEditScreen;
//#endif

//#if MC < 1.16
import fi.dy.masa.malilib.util.StringUtils;
//#endif

@Mixin(
		//#if MC >= 1.19.3
		//$$ AbstractSignEditScreen.class
		//#else
		SignEditScreen.class
		//#endif
)
public abstract class SignEditScreenMixin extends Screen
{
	@Unique private static final String CLEAR_ALL_BUTTON_TRANSLATION_KEY$TKM = "tweakermore.gui.sign_edit_screen.clear_all";
	@Unique private static final String CLEAR_ALL_BUTTON_HOVER_TRANSLATION_KEY$TKM = "tweakermore.gui.sign_edit_screen.clear_all_hover";
	@Unique private Button clearAllButton$TKM;

	//#if MC >= 1.19.4
	//$$ @Unique private boolean clearAllButtonNeedsUnfocus$TKM;
	//#endif

	protected SignEditScreenMixin(Component title)
	{
		super(title);
	}

	@Shadow @Final private SignBlockEntity sign;
	@Shadow private int line;
	@Shadow private TextFieldHelper signField;

	//#if MC >= 1.16
	//$$ @Shadow @Final private String[] messages;
	//#endif

	//#if MC >= 1.20
	//$$ @Shadow protected abstract void setMessage(String string);
	//#endif

	@Inject(method = "init", at = @At("TAIL"))
	private void signEditScreenClearAllButton_addClearAllButton(CallbackInfo ci)
	{
		if (TweakerMoreConfigs.SIGN_EDIT_SCREEN_CLEAR_ALL_BUTTON.getBooleanValue())
		{
			int y = this.height / 4 + 120;
			//#if MC >= 1.20
			//$$ if (!TweakerMoreConfigs.SIGN_EDIT_SCREEN_CANCEL_BUTTON.getBooleanValue())
			//$$ {
			//$$ 	y += 24;
			//$$ }
			//#endif

			//#if MC >= 1.19.3
			//$$ this.clearAllButton$TKM = Button.builder(Messenger.tr(CLEAR_ALL_BUTTON_TRANSLATION_KEY$TKM), button -> this.clearAllLines$TKM())
			//$$ 		.tooltip(Tooltip.create(Messenger.tr(CLEAR_ALL_BUTTON_HOVER_TRANSLATION_KEY$TKM)))
			//$$ 		.bounds(this.width / 2 + 105, y, 20, 20)
			//$$ 		.build();
			//$$ this.addRenderableWidget(this.clearAllButton$TKM);
			//#else

			this.clearAllButton$TKM = new Button(
					this.width / 2 + 105, y, 20, 20,
					//#if MC >= 1.16
					//$$ Messenger.tr(CLEAR_ALL_BUTTON_TRANSLATION_KEY$TKM),
					//#else
					StringUtils.translate(CLEAR_ALL_BUTTON_TRANSLATION_KEY$TKM),
					//#endif
					button -> this.clearAllLines$TKM()
					//#if MC >= 1.16
					//$$ , (button, poseStack, mouseX, mouseY) -> this.renderTooltip(
					//$$ 		poseStack, Messenger.tr(CLEAR_ALL_BUTTON_HOVER_TRANSLATION_KEY$TKM), mouseX, mouseY
					//$$ )
					//#endif
			);

			//#if MC >= 1.17
			//$$ this.addRenderableWidget(this.clearAllButton$TKM);
			//#else
			this.addButton(this.clearAllButton$TKM);
			//#endif

			//#endif  // if MC >= 1.19.3
		}
	}

	@Unique
	private void clearAllLines$TKM()
	{
		//#if MC >= 1.20
		//$$ for (this.line = 0; this.line < this.messages.length; this.line++)
		//$$ {
		//$$ 	this.setMessage("");
		//$$ }
		//#else
		for (int i = 0; i < 4; i++)
		{
			//#if MC >= 1.16
			//$$ this.messages[i] = "";
			//#endif
			this.sign.setMessage(i, Messenger.s(""));
		}
		//#endif
		this.line = 0;

		//#if MC >= 1.16
		//$$ this.signField.setCursorToEnd();
		//#else
		this.signField.setEnd();
		//#endif

		//#if MC >= 1.19.4
		//$$ this.clearAllButtonNeedsUnfocus$TKM = true;
		//#endif
	}

	//#if MC >= 1.19.4
	//$$ @Inject(method = "tick", at = @At("TAIL"))
	//$$ private void signEditScreenClearAllButton_unfocusAfterPress(CallbackInfo ci)
	//$$ {
	//$$ 	if (this.clearAllButtonNeedsUnfocus$TKM && this.getFocused() == this.clearAllButton$TKM)
	//$$ 	{
	//$$ 		this.setFocused(null);
	//$$ 	}
	//$$ 	this.clearAllButtonNeedsUnfocus$TKM = false;
	//$$ }
	//#endif

	//#if MC < 1.16
	@Inject(method = "render", at = @At("TAIL"))
	private void signEditScreenClearAllButton_renderHoverText(int mouseX, int mouseY, float delta, CallbackInfo ci)
	{
		if (this.clearAllButton$TKM != null && this.clearAllButton$TKM.isMouseOver(mouseX, mouseY))
		{
			this.renderTooltip(StringUtils.translate(CLEAR_ALL_BUTTON_HOVER_TRANSLATION_KEY$TKM), mouseX, mouseY);
		}
	}
	//#endif
}
