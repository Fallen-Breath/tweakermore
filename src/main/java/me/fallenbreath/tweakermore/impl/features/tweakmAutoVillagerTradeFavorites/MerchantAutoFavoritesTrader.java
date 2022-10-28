package me.fallenbreath.tweakermore.impl.features.tweakmAutoVillagerTradeFavorites;

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
		TweakerMoreConfigBooleanHotkeyed config = TweakerMoreConfigs.TWEAKM_AUTO_VILLAGER_TRADE_FAVORITES;
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
				//$$ 	InfoUtils.printActionbarMessage("tweakermore.impl.tweakmAutoVillagerTradeFavorites.triggered", config.getPrettyName(), screen.getTitle());
				//$$ }
				//$$ else
				//$$ {
				//$$ 	InfoUtils.printActionbarMessage("tweakermore.impl.tweakmAutoVillagerTradeFavorites.no_favorite", screen.getTitle());
				//$$ }
				//#else
				Object dummy = InventoryUtils.class;
				//#endif

				Objects.requireNonNull(mc.player).closeContainer();
			}
		}
	}
}
