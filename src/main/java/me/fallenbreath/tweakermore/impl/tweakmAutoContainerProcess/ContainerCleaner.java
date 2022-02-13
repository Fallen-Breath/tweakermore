package me.fallenbreath.tweakermore.impl.tweakmAutoContainerProcess;

import fi.dy.masa.itemscroller.util.InventoryUtils;
import fi.dy.masa.malilib.util.InfoUtils;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.client.gui.screen.ingame.ContainerScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.container.Slot;

import java.util.List;

public class ContainerCleaner implements Processor
{
	@Override
	public boolean isEnabled()
	{
		return TweakerMoreConfigs.TWEAKM_AUTO_CLEAN_CONTAINER.getBooleanValue();
	}

	@Override
	public boolean process(ClientPlayerEntity player, ContainerScreen<?> containerScreen, List<Slot> allSlots, List<Slot> playerInvSlots, List<Slot> containerInvSlots)
	{
		int counter = 0;
		for (Slot slot : containerInvSlots)
		{
			if (slot.hasStack())
			{
				InventoryUtils.dropStack(containerScreen, slot.id);
				counter++;
			}
		}
		InfoUtils.printActionbarMessage("tweakermore.config.tweakmAutoCleanContainer.container_cleaned", counter, containerScreen.getTitle());
		return true;
	}
}
