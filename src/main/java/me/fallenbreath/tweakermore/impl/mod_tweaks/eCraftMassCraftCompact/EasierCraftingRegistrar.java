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

package me.fallenbreath.tweakermore.impl.mod_tweaks.eCraftMassCraftCompact;

import fi.dy.masa.itemscroller.recipes.CraftingHandler;
import fi.dy.masa.malilib.config.options.ConfigBoolean;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.container.CraftingResultSlot;

public class EasierCraftingRegistrar
{
	private static final String EASIER_CRAFTING_INVENTORY_CLASS = "de.guntram.mcmod.easiercrafting.ExtendedGuiInventory";
	private static final String EASIER_CRAFTING_CRAFTING_TABLE_CLASS = "de.guntram.mcmod.easiercrafting.ExtendedGuiCrafting";

	private static final String CRAFTING_RESULT_SLOT_CLASS = CraftingResultSlot.class.getName();

	private static boolean canRegister = false;

	public static void register()
	{
		if (canRegister && TweakerMoreConfigs.ECRAFT_ITEM_SCROLLER_COMPACT.getBooleanValue() && TweakerMoreConfigs.ECRAFT_ITEM_SCROLLER_COMPACT.getTweakerMoreOption().isEnabled())
		{
			canRegister = false;
			CraftingHandler.addCraftingGridDefinition(EASIER_CRAFTING_INVENTORY_CLASS, CRAFTING_RESULT_SLOT_CLASS, 0, new CraftingHandler.SlotRange(1, 4));
			CraftingHandler.addCraftingGridDefinition(EASIER_CRAFTING_CRAFTING_TABLE_CLASS, CRAFTING_RESULT_SLOT_CLASS, 0, new CraftingHandler.SlotRange(1, 9));
		}
	}

	public static void markDefinitionCleared()
	{
		canRegister = true;
	}

	public static void onConfigValueChanged(ConfigBoolean configBoolean)
	{
		register();
	}
}
