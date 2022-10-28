package me.fallenbreath.tweakermore.impl.features.tweakmAutoContainerProcess.processors;

import com.google.common.collect.Sets;
import fi.dy.masa.itemscroller.util.InventoryUtils;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.util.InfoUtils;
import fi.dy.masa.malilib.util.ItemType;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.config.options.TweakerMoreConfigBooleanHotkeyed;
import net.minecraft.client.gui.screen.ingame.ContainerScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.container.Slot;
import net.minecraft.item.ItemStack;

import java.util.List;
import java.util.Set;

public class ContainerItemPutBackProcessor implements IContainerProcessor
{
	@Override
	public TweakerMoreConfigBooleanHotkeyed getConfig()
	{
		return TweakerMoreConfigs.TWEAKM_AUTO_PUT_BACK_EXISTED_ITEM;
	}

	@Override
	public boolean process(ClientPlayerEntity player, ContainerScreen<?> containerScreen, List<Slot> allSlots, List<Slot> playerInvSlots, List<Slot> containerInvSlots)
	{
		Set<ItemType> inventoryItems = Sets.newHashSet();
		for (Slot containerInvSlot : containerInvSlots)
		{
			inventoryItems.add(new ItemType(containerInvSlot.getStack(), true, true));
		}

		int matchedCount = 0, movedCount = 0;
		for (Slot playerInvSlot : playerInvSlots)
		{
			ItemStack stack = playerInvSlot.getStack();
			if (!stack.isEmpty() && inventoryItems.contains(new ItemType(stack, false, true)))
			{
				matchedCount++;
				InventoryUtils.shiftClickSlot(containerScreen, playerInvSlot.id);
				if (!playerInvSlot.hasStack())
				{
					movedCount++;
				}
			}
		}

		String color = matchedCount > 0 ? (movedCount == matchedCount ? GuiBase.TXT_GREEN : GuiBase.TXT_GOLD) : GuiBase.TXT_RED;
		String percentage = String.format("%s%d/%d%s", color, movedCount, matchedCount, GuiBase.TXT_RST);
		InfoUtils.printActionbarMessage(
				"tweakermore.impl.tweakmAutoPutBackExistedItem.result",
				percentage, containerScreen.getTitle()
		);
		return true;
	}
}
