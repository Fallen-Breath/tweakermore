package me.fallenbreath.tweakermore.impl.tweakmAutoCleanContainer;

import fi.dy.masa.itemscroller.util.InventoryUtils;
import fi.dy.masa.malilib.util.InfoUtils;
import me.fallenbreath.tweakermore.config.TweakerMoreToggles;
import me.fallenbreath.tweakermore.mixins.access.ItemScrollerInventoryUtilsAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.CraftingScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.screen.slot.Slot;

public class TweakAutoCleanContainer
{
	public static void process(Screen screen)
	{
		// 1.15.2 -> 1.16.5
		// HandledScreen -> HandledScreen
		// Container -> ScreenHandler
		if (TweakerMoreToggles.TWEAKM_AUTO_CLEAN_CONTAINER.getBooleanValue())
		{
			ClientPlayerEntity player = MinecraftClient.getInstance().player;
			// not inventory and not crafting table
			if (player != null && screen instanceof HandledScreen<?> && !(screen instanceof AbstractInventoryScreen) && !(screen instanceof CraftingScreen))
			{
				HandledScreen<?> containerScreen = (HandledScreen<?>)screen;
				Slot refSlot = null;
				for (Slot slot : containerScreen.getScreenHandler().slots)
				{
					if (refSlot == null)
					{
						refSlot = slot;
					}
					if (ItemScrollerInventoryUtilsAccessor.areSlotsInSameInventory(slot, refSlot))
					{
						InventoryUtils.dropStack(containerScreen, slot.id);
					}
				}
				// close the container
				InfoUtils.printActionbarMessage("tweakmAutoCleanContainer.container_cleaned", screen.getTitle());
				player.closeHandledScreen();
			}
		}
	}
}
