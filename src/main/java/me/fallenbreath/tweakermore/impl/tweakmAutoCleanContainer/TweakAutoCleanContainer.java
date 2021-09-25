package me.fallenbreath.tweakermore.impl.tweakmAutoCleanContainer;

import fi.dy.masa.itemscroller.util.InventoryUtils;
import fi.dy.masa.malilib.util.InfoUtils;
import me.fallenbreath.tweakermore.config.TweakerMoreToggles;
import me.fallenbreath.tweakermore.mixins.access.ItemScrollerInventoryUtilsAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.ContainerScreen;
import net.minecraft.client.gui.screen.ingame.CraftingTableScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.container.Slot;

public class TweakAutoCleanContainer
{
	public static void process(Screen screen)
	{
		if (TweakerMoreToggles.TWEAKM_AUTO_CLEAN_CONTAINER.getBooleanValue())
		{
			ClientPlayerEntity player = MinecraftClient.getInstance().player;
			// not inventory and not crafting table
			if (player != null && screen instanceof ContainerScreen<?> && !(screen instanceof AbstractInventoryScreen) && !(screen instanceof CraftingTableScreen))
			{
				ContainerScreen<?> containerScreen = (ContainerScreen<?>)screen;
				Slot refSlot = null;
				for (Slot slot : containerScreen.getContainer().slots)
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
				player.closeContainer();
			}
		}
	}
}
