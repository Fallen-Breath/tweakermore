package me.fallenbreath.tweakermore.impl.features.tweakmAutoContainerProcess;

import com.google.common.collect.ImmutableList;
import me.fallenbreath.tweakermore.impl.features.tweakmAutoContainerProcess.processors.*;
import me.fallenbreath.tweakermore.mixins.tweaks.features.tweakmAutoContainerProcess.ItemScrollerInventoryUtilsAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.ContainerScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.container.Container;
import net.minecraft.container.Slot;
import net.minecraft.entity.player.PlayerInventory;

import java.util.List;
import java.util.stream.Collectors;

public class ContainerProcessorManager
{
	private static final List<IProcessor> CONTAINER_PROCESSORS = ImmutableList.of(
			new ContainerCleaner(),
			new ContainerFiller(),
			new ContainerItemPutBackProcessor(),
			new ContainerMaterialListItemCollector()
	);

	private static boolean hasTweakEnabled()
	{
		return CONTAINER_PROCESSORS.stream().anyMatch(IProcessor::isEnabled);
	}

	public static List<IProcessor> getProcessors()
	{
		return CONTAINER_PROCESSORS;
	}

	public static void process(Container container)
	{
		if (hasTweakEnabled())
		{
			Screen screen = MinecraftClient.getInstance().currentScreen;
			ClientPlayerEntity player = MinecraftClient.getInstance().player;
			// not inventory and not crafting table
			if (player != null && screen instanceof ContainerScreen<?>)
			{
				if (player.isSpectator())
				{
					return;
				}
				ContainerScreen<?> containerScreen = (ContainerScreen<?>)screen;
				if (containerScreen.getContainer() != container || !((AutoProcessableScreen)screen).shouldProcess())
				{
					return;
				}
				((AutoProcessableScreen)screen).setShouldProcess(false);
				List<Slot> allSlots = container.slots;
				List<Slot> playerInvSlots = allSlots.stream().filter(slot -> slot.inventory instanceof PlayerInventory).collect(Collectors.toList());
				if (allSlots.isEmpty() || playerInvSlots.isEmpty())
				{
					return;
				}
				List<Slot> containerInvSlots = allSlots.stream().filter(slot -> ItemScrollerInventoryUtilsAccessor.areSlotsInSameInventory(slot, allSlots.get(0))).collect(Collectors.toList());
				if (containerInvSlots.isEmpty())
				{
					return;
				}

				boolean hasProcessed = false;
				for (IProcessor processor : CONTAINER_PROCESSORS)
				{
					if (processor.isEnabled() && processor.shouldProcess(containerScreen))
					{
						hasProcessed = true;
						boolean cancelProcessing = processor.process(player, containerScreen, allSlots, playerInvSlots, containerInvSlots);
						if (cancelProcessing)
						{
							break;
						}
					}
				}
				if (hasProcessed)
				{
					player.closeContainer();
				}
			}
		}
	}
}
