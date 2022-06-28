package me.fallenbreath.tweakermore.impl.features.tweakmAutoContainerProcess.processors;

import fi.dy.masa.itemscroller.util.InventoryUtils;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.config.options.TweakerMoreConfigBooleanHotkeyed;
import net.minecraft.client.gui.screen.ingame.ContainerScreen;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.container.Slot;

import java.util.List;

public class MerchantAutoFavoritesTrader implements IProcessor
{
	private static final Class<?> dummy$dontOptimizeImport = InventoryUtils.class;

	@Override
	public TweakerMoreConfigBooleanHotkeyed getConfig()
	{
		return TweakerMoreConfigs.TWEAKM_AUTO_VILLAGER_TRADE_FAVORITES;
	}

	@Override
	public boolean shouldProcess(ContainerScreen<?> containerScreen)
	{
		return containerScreen instanceof MerchantScreen;
	}

	@Override
	public boolean process(ClientPlayerEntity player, ContainerScreen<?> containerScreen, List<Slot> allSlots, List<Slot> playerInvSlots, List<Slot> containerInvSlots)
	{
		//#if MC >= 11600
		//$$ InventoryUtils.villagerTradeEverythingPossibleWithAllFavoritedTrades();
		//$$ return true;
		//#else
		return false;
		//#endif
	}
}
