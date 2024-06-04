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

import me.fallenbreath.tweakermore.impl.mc_tweaks.signMultilinePasteSupport.SelectionManagerInSignEditScreen;
import me.fallenbreath.tweakermore.impl.mc_tweaks.signMultilinePasteSupport.SignEditScreenRowIndexController;
import net.minecraft.client.util.SelectionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//#if MC >= 11903
//$$ import net.minecraft.client.gui.screen.ingame.AbstractSignEditScreen;
//#else
import net.minecraft.client.gui.screen.ingame.SignEditScreen;
//#endif

@Mixin(
		//#if MC >= 11903
		//$$ AbstractSignEditScreen.class
		//#else
		SignEditScreen.class
		//#endif
)
public abstract class SignEditScreenMixin implements SignEditScreenRowIndexController
{
	@Shadow private SelectionManager selectionManager;
	@Shadow private int currentRow;

	@Inject(method = "init", at = @At("TAIL"))
	private void signMultilinePasteSupport_storeSelfInSelectionManager(CallbackInfo ci)
	{
		((SelectionManagerInSignEditScreen)this.selectionManager).setSignEditScreen$TKM(this);
	}

	@Override
	public boolean canAddCurrentRowIndex$TKM(int delta)
	{
		int newIndex = this.currentRow + delta;
		return 0 <= newIndex && newIndex <= 3;
	}

	@Override
	public void addCurrentRowIndex$TKM(int delta)
	{
		if (this.canAddCurrentRowIndex$TKM(delta))
		{
			this.currentRow += delta;
		}
	}
}
