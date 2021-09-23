package me.fallenbreath.tweakermore.impl.tweakmAutoFillContainer;

import fi.dy.masa.itemscroller.util.InventoryUtils;
import me.fallenbreath.tweakermore.config.TweakerMoreToggles;
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
import net.minecraft.text.TranslatableText;

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
				Slot bestSlot = null;
				long maxCount = 0;
				for (Slot slot : playerInvSlots)
					if (slot.hasStack() && allSlots.get(0).canInsert(slot.getStack()))
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
					player.addChatMessage(new TranslatableText("tweakermore.tweakm_auto_clean_container.container_filled", screen.getTitle(), stackName), true);
					player.closeContainer();
				}
			}
		}
	}
}
