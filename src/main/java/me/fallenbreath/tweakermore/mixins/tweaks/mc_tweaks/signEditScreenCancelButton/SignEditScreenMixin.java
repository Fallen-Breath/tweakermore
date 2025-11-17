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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.signEditScreenCancelButton;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.impl.mc_tweaks.signEditScreenCancelCommon.ClientSignTextRollbacker;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//#if MC >= 12001
//$$ import org.spongepowered.asm.mixin.injection.ModifyArg;
//#endif

//#if MC >= 11903
//$$ import net.minecraft.client.gui.screens.inventory.AbstractSignEditScreen;
//#else
import net.minecraft.client.gui.screens.inventory.SignEditScreen;
//#endif

//#if MC >= 11600
//$$ import net.minecraft.network.chat.CommonComponents;
//#else
import fi.dy.masa.malilib.util.StringUtils;
//#endif

@Mixin(
		//#if MC >= 11903
		//$$ AbstractSignEditScreen.class
		//#else
		SignEditScreen.class
		//#endif
)
public abstract class SignEditScreenMixin extends Screen
{
	protected SignEditScreenMixin(Component title)
	{
		super(title);
	}

	@Shadow protected abstract void onDone();

	@Shadow @Final private SignBlockEntity sign;

	@Unique private boolean editingCancelled = false;
	@Unique private ClientSignTextRollbacker clientSignTextRollbacker = null;

	@Inject(
			//#if MC >= 12001
			//$$ method = "<init>(Lnet/minecraft/world/level/block/entity/SignBlockEntity;ZZLnet/minecraft/network/chat/Component;)V",
			//#elseif MC >= 11903
			//$$ method = "<init>(Lnet/minecraft/world/level/block/entity/SignBlockEntity;ZLnet/minecraft/network/chat/Component;)V",
			//#else
			method = "<init>",
			//#endif
			at = @At("TAIL")
	)
	private void signEditScreenCancelButton_memorizeSignTexts(CallbackInfo ci)
	{
		this.clientSignTextRollbacker = new ClientSignTextRollbacker(
				//#if MC >= 11903
				//$$ (AbstractSignEditScreen)(Object)this, this.sign
				//#else
				(SignEditScreen)(Object)this, this.sign
				//#endif
		);
	}

	//#if MC >= 12001
	//$$ // The done button is shifted down in mc1.20.1+
	//$$ // This mixin reverts the change to make enough space for cancel button
	//$$ @ModifyArg(
	//$$ 		method = "init",
	//$$ 		at = @At(
	//$$ 				value = "INVOKE",
	//$$ 				target = "Lnet/minecraft/client/gui/components/Button$Builder;bounds(IIII)Lnet/minecraft/client/gui/components/Button$Builder;"
	//$$ 		),
	//$$ 		index = 1
	//$$ )
	//$$ private int signEditScreenCancelButton_adjustDoneButtonHeight(int y)
	//$$ {
	//$$ 	if (TweakerMoreConfigs.SIGN_EDIT_SCREEN_CANCEL_BUTTON.getBooleanValue())
	//$$ 	{
	//$$ 		// the same in < mc1.20
	//$$ 		y = this.height / 4 + 120;
	//$$ 	}
	//$$ 	return y;
	//$$ }
	//#endif

	@Inject(method = "init", at = @At("TAIL"))
	private void signEditScreenCancelButton_addCancelButton(CallbackInfo ci)
	{
		if (TweakerMoreConfigs.SIGN_EDIT_SCREEN_CANCEL_BUTTON.getBooleanValue())
		{
			//#if MC >= 11903
			//$$ this.addRenderableWidget(
			//$$ 		Button.builder(CommonComponents.GUI_CANCEL, button -> {
			//$$ 			this.editingCancelled = true;
			//$$ 			this.onDone();
			//$$ 		}).bounds(this.width / 2 - 100, this.height / 4 + 120 + 20 + 5, 200, 20).build()
			//$$ );
			//#else

			//#if MC >= 11700
			//$$ this.addRenderableWidget
			//#else
			this.addButton
			//#endif
					(new Button(
					this.width / 2 - 100, this.height / 4 + 120 + 20 + 5, 200, 20,
					//#if MC >= 11600
					//$$ CommonComponents.GUI_CANCEL,
					//#else
					StringUtils.translate("gui.cancel"),
					//#endif
					buttonWidget -> {
						this.editingCancelled = true;
						this.onDone();
					}
			));

			//#endif  // if MC >= 11903
		}
	}

	@Inject(method = "removed", at = @At("HEAD"))
	private void signEditScreenEscDiscard_rollbackIfEditingCancelled(CallbackInfo ci)
	{
		if (this.editingCancelled && this.clientSignTextRollbacker != null)
		{
			this.clientSignTextRollbacker.rollback();
		}
	}
}
