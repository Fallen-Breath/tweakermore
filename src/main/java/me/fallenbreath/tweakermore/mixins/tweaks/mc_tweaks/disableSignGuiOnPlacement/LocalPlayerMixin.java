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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.disableSignGuiOnPlacement;

import com.mojang.blaze3d.platform.InputConstants;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.hotkeys.KeybindMulti;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.impl.mc_tweaks.disableSignGuiOnPlacement.SignPlacementTracker;
import me.fallenbreath.tweakermore.util.GameUtils;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//#if MC >= 26.3
//$$ import net.minecraft.world.level.block.entity.SignTextSlot;
//#endif
//#if MC >= 1.19.3
//$$ import net.minecraft.client.gui.screens.inventory.AbstractSignEditScreen;
//#else
import net.minecraft.client.gui.screens.inventory.SignEditScreen;
//#endif

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin
{
	@Inject(method = "openTextEdit", at = @At("RETURN"))
	private void disableSignGuiOnPlacement_closeEditor(
			SignBlockEntity sign,
			//#if MC >= 26.3
			//$$ SignTextSlot slot,
			//#elseif MC >= 1.20
			//$$ boolean isFrontText,
			//#endif
			CallbackInfo ci
	)
	{
		if (
				!TweakerMoreConfigs.DISABLE_SIGN_GUI_ON_PLACEMENT.getBooleanValue() ||
				//#if MC >= 26.3
				//$$ slot != SignTextSlot.FRONT ||
				//#elseif MC >= 1.20
				//$$ !isFrontText ||
				//#endif
				!SignPlacementTracker.INSTANCE.consume(sign.getBlockPos())
		)
		{
			return;
		}

		Minecraft mc = Minecraft.getInstance();
		//#if MC >= 1.19.3
		//$$ if (GameUtils.getCurrentMinecraftScreen(mc) instanceof AbstractSignEditScreen)
		//#else
		if (GameUtils.getCurrentMinecraftScreen(mc) instanceof SignEditScreen)
		//#endif
		{
			// Opening the screen releases the use key even if the button is still held.
			KeyMapping keyUse = mc.options.keyUse;
			InputConstants.Key key = InputConstants.getKey(keyUse.saveString());
			if (key != null)
			{
				KeyMapping.set(key, KeybindMulti.isKeyDown(KeybindMulti.getKeyCode(keyUse)));
			}
			GuiBase.openGui(null);
		}
	}
}
