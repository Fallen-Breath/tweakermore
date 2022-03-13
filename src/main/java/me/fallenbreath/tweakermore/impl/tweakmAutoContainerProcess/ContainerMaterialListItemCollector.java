package me.fallenbreath.tweakermore.impl.tweakmAutoContainerProcess;

import fi.dy.masa.itemscroller.util.InventoryUtils;
import fi.dy.masa.litematica.data.DataManager;
import fi.dy.masa.litematica.materials.MaterialListBase;
import fi.dy.masa.litematica.materials.MaterialListEntry;
import fi.dy.masa.litematica.materials.MaterialListUtils;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.Message;
import fi.dy.masa.malilib.util.InfoUtils;
import me.fallenbreath.tweakermore.TweakerMoreMod;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.mixins.tweaks.tweakmAutoCollectMaterialListItem.MaterialListHudRendererAccessor;
import net.minecraft.client.gui.screen.ingame.ContainerScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.container.Slot;
import net.minecraft.item.ItemStack;

import java.util.List;

public class ContainerMaterialListItemCollector implements Processor
{
	@Override
	public boolean isEnabled()
	{
		return TweakerMoreConfigs.TWEAKM_AUTO_TAKE_MATERIAL_LIST_ITEM.getBooleanValue();
	}

	/**
	 * Requires litematica mod
	 */
	@Override
	public boolean process(ClientPlayerEntity player, ContainerScreen<?> containerScreen, List<Slot> allSlots, List<Slot> playerInvSlots, List<Slot> containerInvSlots)
	{
		// make sure required mods are loaded
		if (!TweakerMoreConfigs.TWEAKM_AUTO_TAKE_MATERIAL_LIST_ITEM.getTweakerMoreOption().isEnabled())
		{
			return false;
		}

		MaterialListBase materialList = DataManager.getMaterialList();
		if (materialList != null)
		{
			MaterialListHudRendererAccessor hudRendererAccessor = (MaterialListHudRendererAccessor)materialList.getHudRenderer();
			String guiTitle = containerScreen.getTitle().getString();

			// refresh before operation starts to make sure it's up-to-date
			MaterialListUtils.updateAvailableCounts(materialList.getMaterialsAll(), player);
			List<MaterialListEntry> missingOnly = materialList.getMaterialsMissingOnly(true);

			boolean takenSomething = false;
			for (MaterialListEntry entry : missingOnly)
			{
				int missing = entry.getCountMissing() * materialList.getMultiplier() - entry.getCountAvailable();
				ItemStack stack = entry.getStack();
				if (missing <= 0)
				{
					continue;
				}
				int totalTaken = 0;
				for (Slot slot : containerInvSlots)
				{
					if (InventoryUtils.areStacksEqual(stack, slot.getStack()))
					{
						int stackAmount = slot.getStack().getCount();
						this.moveToPlayerInventory(containerScreen, playerInvSlots, slot, Math.min(missing, stackAmount));
						int moved = stackAmount - slot.getStack().getCount();
						missing -= moved;
						totalTaken += moved;
						TweakerMoreMod.LOGGER.debug("Moved {}x {} to player inventory, still miss {} items", moved, stack.getItem().getName().getString(), missing);
						if (moved == 0)
						{
							TweakerMoreMod.LOGGER.debug("Player inventory is full for item {}", stack.getItem().getName().getString());
							break;
						}
					}
				}
				if (totalTaken > 0)
				{
					if (!takenSomething)
					{
						InfoUtils.showGuiOrInGameMessage(Message.MessageType.INFO, "tweakermore.config.tweakmAutoCollectMaterialListItem.info.title", guiTitle);
					}
					takenSomething = true;
					String missingColor = missing == 0 ? GuiBase.TXT_GREEN : GuiBase.TXT_GOLD;
					InfoUtils.showGuiOrInGameMessage(
							Message.MessageType.INFO,
							"tweakermore.config.tweakmAutoCollectMaterialListItem.info.line",
							GuiBase.TXT_GOLD + totalTaken + GuiBase.TXT_RST,
							stack.getRarity().formatting + stack.getName().getString() + GuiBase.TXT_RST,
							missingColor + hudRendererAccessor.invokeGetFormattedCountString(missing, stack.getMaxCount()) + GuiBase.TXT_RST
					);
				}
			}

			if (!takenSomething)
			{
				InfoUtils.showGuiOrInGameMessage(Message.MessageType.INFO, "tweakermore.config.tweakmAutoCollectMaterialListItem.took_nothing", guiTitle);
			}

			// refresh after operation ends
			hudRendererAccessor.setLastUpdateTime(-1);
		}
		else
		{
			InfoUtils.showGuiOrInGameMessage(Message.MessageType.WARNING, "tweakermore.config.tweakmAutoCollectMaterialListItem.no_material_list");
		}
		return true;
	}

	private void moveToPlayerInventory(ContainerScreen<?> containerScreen, List<Slot> playerInvSlots, Slot fromSlot, int amount)
	{
		ItemStack stack = fromSlot.getStack().copy();
		if (amount == stack.getCount())
		{
			InventoryUtils.shiftClickSlot(containerScreen, fromSlot.id);
			return;
		}
		else if (amount > stack.getCount())
		{
			TweakerMoreMod.LOGGER.warn("Too many items to move to player inventory, the stack {} has {} items but {} items are required", stack.getItem(), stack.getCount(), amount);
			return;
		}
		// ensure amount <= stack.getCount()

		InventoryUtils.leftClickSlot(containerScreen, fromSlot.id);
		// reversed iterating to match vanilla shift-click item putting order
		for (int idx = playerInvSlots.size() - 1; idx >= 0; idx--)
		{
			Slot slot = playerInvSlots.get(idx);
			int clickAmount = 0;
			if (slot.hasStack() && InventoryUtils.areStacksEqual(slot.getStack(), stack))
			{
				ItemStack invStack = slot.getStack();
				clickAmount = Math.min(invStack.getMaxCount() - invStack.getCount(), amount);
			}
			else if (!slot.hasStack())
			{
				clickAmount = amount;
			}
			for (int i = 0; i < clickAmount; i++) InventoryUtils.rightClickSlot(containerScreen, slot.id);
			amount -= clickAmount;
			if (amount == 0)
			{
				break;
			}
		}
		InventoryUtils.leftClickSlot(containerScreen, fromSlot.id);
		if (amount != 0)
		{
			TweakerMoreMod.LOGGER.warn("Failed to move full item stack to player inventory, {} items remains", amount);
		}
	}
}
