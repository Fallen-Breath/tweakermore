package me.fallenbreath.tweakermore.impl.tweakmAutoFillContainer;

import fi.dy.masa.itemscroller.util.InventoryUtils;
import fi.dy.masa.malilib.util.InfoUtils;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.config.TweakerMoreToggles;
import me.fallenbreath.tweakermore.mixins.access.ItemScrollerInventoryUtilsAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.ContainerScreen;
import net.minecraft.client.gui.screen.ingame.CraftingTableScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.container.Container;
import net.minecraft.container.Slot;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;
import java.util.stream.Collectors;

public class TweakAutoFillContainer
{
	public static void process(Container container)
	{
		if (TweakerMoreToggles.TWEAKM_AUTO_FILL_CONTAINER.getBooleanValue())
		{
			Screen screen = MinecraftClient.getInstance().currentScreen;
			ClientPlayerEntity player = MinecraftClient.getInstance().player;
			// not inventory and not crafting table
			if (player != null && screen instanceof ContainerScreen<?> && !(screen instanceof AbstractInventoryScreen) && !(screen instanceof CraftingTableScreen))
			{
				ContainerScreen<?> containerScreen = (ContainerScreen<?>)screen;
				if (containerScreen.getContainer() != container || !((IScreen)screen).shouldProcess())
				{
					return;
				}
				((IScreen)screen).setShouldProcess(false);
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
				Slot bestSlot = null;
				long maxCount = TweakerMoreConfigs.AUTO_FILL_CONTAINER_THRESHOLD.getIntegerValue() - 1;
				for (Slot slot : playerInvSlots)
					if (slot.hasStack() && containerInvSlots.get(0).canInsert(slot.getStack()))
					{
						long cnt = playerInvSlots.stream().filter(slt -> InventoryUtils.areStacksEqual(slot.getStack(), slt.getStack())).count();
						if (cnt > maxCount)
						{
							maxCount = cnt;
							bestSlot = slot;
						}
						else if (cnt == maxCount && bestSlot != null && !InventoryUtils.areStacksEqual(slot.getStack(), bestSlot.getStack()))
						{
							bestSlot = null;
						}
					}
				if (bestSlot != null && !allSlots.isEmpty())
				{
					Text stackName = bestSlot.getStack().getName();
					InventoryUtils.tryMoveStacks(bestSlot, containerScreen, true, true, false);
					long amount = containerInvSlots.stream().filter(Slot::hasStack).count(), total = containerInvSlots.size();
					boolean isFull = Container.calculateComparatorOutput(containerInvSlots.get(0).inventory) >= 15;
					String percentage = String.format("%s%d/%d%s", isFull ? Formatting.GREEN : Formatting.GOLD, amount, total, Formatting.RESET);
					InfoUtils.printActionbarMessage("tweakmAutoFillContainer.container_filled", screen.getTitle(), stackName, percentage);
				}
				else
				{
					InfoUtils.printActionbarMessage("tweakmAutoFillContainer.best_slot_not_found");
				}
				player.closeContainer();
			}
		}
	}
}
