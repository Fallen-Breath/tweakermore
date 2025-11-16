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

package me.fallenbreath.tweakermore.impl.mc_tweaks.signEditScreenCancelCommon;

import me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.signEditScreenCancelCommon.SignBlockEntityAccessor;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.network.chat.Component;

//#if MC >= 12001
//$$ import net.minecraft.world.level.block.entity.SignText;
//#endif

//#if MC >= 11903
//$$ import net.minecraft.client.gui.screens.inventory.AbstractSignEditScreen;
//#else
import net.minecraft.client.gui.screens.inventory.SignEditScreen;
//#endif

//#if MC >= 11600
//$$ import me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.signEditScreenCancelCommon.SignEditScreenAccessor;
//#endif

@SuppressWarnings("FieldCanBeLocal")
public class ClientSignTextRollbacker
{
	private final SignBlockEntity signBlockEntity;

	//#if MC >= 12001
	//$$ private final SignText blockEntityFrontText;
	//$$ private final SignText blockEntityBackText;
	//#else
	private final Component[] blockEntityTexts;
	private final Component[] blockEntityFilteredTexts;
	//#endif

	//#if MC >= 11903
	//$$ private final AbstractSignEditScreen signEditScreen;
	//#else
	private final SignEditScreen signEditScreen;
	//#endif
	private final String[] screenTexts;

	public ClientSignTextRollbacker(
			//#if MC >= 11903
			//$$ AbstractSignEditScreen signEditScreen,
			//#else
			SignEditScreen signEditScreen,
			//#endif
			SignBlockEntity signBlockEntity
	)
	{
		this.signEditScreen = signEditScreen;
		this.signBlockEntity = signBlockEntity;

		//#if MC >= 12001
		//$$ this.blockEntityFrontText = this.signBlockEntity.getText(true);
		//$$ this.blockEntityBackText = this.signBlockEntity.getText(false);
		//#else
		this.blockEntityTexts = ((SignBlockEntityAccessor)this.signBlockEntity).getTexts$TKM().clone();
		//#if MC >= 11700
		//$$ this.blockEntityFilteredTexts = ((SignBlockEntityAccessor)this.signBlockEntity).getFilteredTexts$TKM().clone();
		//#else
		this.blockEntityFilteredTexts = null;
		//#endif
		//#endif  // if MC >= 12001

		//#if MC >= 11600
		//$$ this.screenTexts = ((SignEditScreenAccessor)this.signEditScreen).getTexts$TKM().clone();
		//#else
		this.screenTexts = null;
		//#endif
	}

	public void rollback()
	{
		//#if MC >= 12001
		//$$ this.signBlockEntity.setText(this.blockEntityFrontText, true);
		//$$ this.signBlockEntity.setText(this.blockEntityBackText, false);
		//#endif

		final int lineNum = 4;
		for (int i = 0; i < lineNum; i++)
		{
			//#if MC < 12001
			this.signBlockEntity.setMessage(
					i, this.blockEntityTexts[i]
					//#if MC >= 11700
					//$$ , this.blockEntityFilteredTexts[i]
					//#endif
			);
			//#endif

			//#if MC >= 11600
			//$$ ((SignEditScreenAccessor)this.signEditScreen).getTexts$TKM()[i] = this.screenTexts[i];
			//#endif
		}
	}
}
