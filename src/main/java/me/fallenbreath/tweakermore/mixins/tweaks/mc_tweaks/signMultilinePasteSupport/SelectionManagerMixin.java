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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.signMultilinePasteSupport;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.impl.mc_tweaks.signMultilinePasteSupport.SelectionManagerInSignEditScreen;
import me.fallenbreath.tweakermore.impl.mc_tweaks.signMultilinePasteSupport.SignEditScreenRowIndexController;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.font.TextFieldHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(TextFieldHelper.class)
public abstract class SelectionManagerMixin implements SelectionManagerInSignEditScreen
{
	@Shadow
	//#if MC >= 11600
	//$$ public
	//#else
	protected
	//#endif
	abstract void insertText(String string);

	@Shadow public abstract void setEnd();

	@Unique
	private SignEditScreenRowIndexController signEditScreen;
	@Unique
	private String pastingString;

	@Override
	public void setSignEditScreen$TKM(SignEditScreenRowIndexController signEditScreen)
	{
		this.signEditScreen = signEditScreen;
	}

	@ModifyArg(
			//#if MC >= 11600
			//$$ method = "paste",
			//$$ at = @At(
			//$$ 		value = "INVOKE",
			//$$ 		target = "Lnet/minecraft/client/gui/font/TextFieldHelper;insertText(Ljava/lang/String;Ljava/lang/String;)V"
			//$$ ),
			//$$ index = 1
			//#else
			method = "keyPressed",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/ChatFormatting;stripFormatting(Ljava/lang/String;)Ljava/lang/String;"
			)
			//#endif
	)
	private String signMultilinePasteSupport_storeRawString(String string)
	{
		if (TweakerMoreConfigs.SIGN_MULTILINE_PASTE_SUPPORT.getBooleanValue())
		{
			this.pastingString = string;
		}
		return string;
	}

	@Inject(
			//#if MC >= 11600
			//$$ method = "insertText(Ljava/lang/String;Ljava/lang/String;)V",
			//#else
			method = "insertText(Ljava/lang/String;)V",
			//#endif
			at = @At("HEAD"),
			cancellable = true
	)
	private void signMultilinePasteSupport_impl(CallbackInfo ci)
	{
		if (TweakerMoreConfigs.SIGN_MULTILINE_PASTE_SUPPORT.getBooleanValue())
		{
			String string = this.pastingString;
			this.pastingString = null;

			if (string == null || string.indexOf('\n') == -1 || this.signEditScreen == null)
			{
				return;
			}

			Consumer<String> inserter = line -> this.insertText(
					//#if MC >= 11600
					//$$ line
					//#else
					SharedConstants.filterText(line)
					//#endif
			);
			String[] lines = string.split("\\n");
			for (int i = 0; i < lines.length; i++)
			{
				//#if MC >= 11600
				//$$ // MC 1.16+ doesn't do SharedConstants.stripInvalidChars for the pasting string
				//$$ // So we need to manually removed the \r character
				//$$ lines[i] = lines[i].replaceAll("\\r", "");
				//#endif

				if (this.signEditScreen.canAddCurrentRowIndex$TKM(1))
				{
					inserter.accept(lines[i]);
					this.signEditScreen.addCurrentRowIndex$TKM(1);
					this.setEnd();
				}
				else
				{
					StringBuilder rest = new StringBuilder();
					for (int j = i; j < lines.length; j++)
					{
						rest.append(lines[j]);
					}
					inserter.accept(rest.toString());
					break;
				}
			}
			ci.cancel();
		}
	}
}
