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

package me.fallenbreath.tweakermore.impl.features.autoVillagerTradeFavorites;

import fi.dy.masa.itemscroller.util.InventoryUtils;
import fi.dy.masa.malilib.util.GuiUtils;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.config.options.TweakerMoreConfigBooleanHotkeyed;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.container.MerchantContainer;

import java.util.Objects;

//#if MC >= 11600
//$$ import fi.dy.masa.itemscroller.villager.VillagerDataStorage;
//$$ import fi.dy.masa.malilib.util.InfoUtils;
//#endif

public class MerchantAutoFavoritesTrader
{
	public static void doAutoTrade(MinecraftClient mc)
	{
		TweakerMoreConfigBooleanHotkeyed config = TweakerMoreConfigs.AUTO_VILLAGER_TRADE_FAVORITES;
		if (config.getTweakerMoreOption().isEnabled() && config.getBooleanValue())
		{
			Screen screen = GuiUtils.getCurrentScreen();
			if (screen instanceof MerchantScreen)
			{
				MerchantContainer container = ((MerchantScreen) screen).getContainer();

				//#if MC >= 11600
				//$$ if (!VillagerDataStorage.getInstance().getFavoritesForCurrentVillager(container).favorites.isEmpty())
				//$$ {
				//$$ 	InventoryUtils.villagerTradeEverythingPossibleWithAllFavoritedTrades();
				//$$ 	InfoUtils.printActionbarMessage("tweakermore.impl.autoVillagerTradeFavorites.triggered", config.getPrettyName(), screen.getTitle());
				//$$ }
				//$$ else
				//$$ {
				//$$ 	InfoUtils.printActionbarMessage("tweakermore.impl.autoVillagerTradeFavorites.no_favorite", screen.getTitle());
				//$$ }
				//#else
				Object dummy = InventoryUtils.class;
				//#endif

				Objects.requireNonNull(mc.player).closeContainer();
			}
		}
	}
}
