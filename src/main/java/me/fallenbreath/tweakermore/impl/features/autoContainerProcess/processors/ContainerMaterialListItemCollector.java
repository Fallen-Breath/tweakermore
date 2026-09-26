/*
 * This file is part of the TweakerMore project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2023  Fallen_Breath and contributors
 *
 * TweakerMore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * TweakerMore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with TweakerMore.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.fallenbreath.tweakermore.impl.features.autoContainerProcess.processors;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import fi.dy.masa.itemscroller.util.InventoryUtils;
import fi.dy.masa.litematica.data.DataManager;
import fi.dy.masa.litematica.materials.MaterialListBase;
import fi.dy.masa.litematica.materials.MaterialListEntry;
import fi.dy.masa.litematica.materials.MaterialListUtils;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.Message;
import fi.dy.masa.malilib.util.InfoUtils;
import fi.dy.masa.malilib.util.StringUtils;
import me.fallenbreath.tweakermore.TweakerMoreMod;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.config.options.TweakerMoreConfigBooleanHotkeyed;
import me.fallenbreath.tweakermore.config.options.listentries.AutoCollectMaterialListItemLogType;
import me.fallenbreath.tweakermore.config.options.listentries.AutoCollectMaterialListItemSource;
import me.fallenbreath.tweakermore.mixins.tweaks.features.autoCollectMaterialListItem.MaterialListHudRendererAccessor;
import me.fallenbreath.tweakermore.util.ItemUtils;
import me.fallenbreath.tweakermore.util.RegistryUtils;
import me.fallenbreath.tweakermore.util.compat.syncmatica.ClaimedMaterialRequirement;
import me.fallenbreath.tweakermore.util.compat.syncmatica.SyncmaticaMaterialApiAccess;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.ChatFormatting;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ContainerMaterialListItemCollector implements IContainerProcessor
{
	private static final int SHULKER_BOX_SLOT_COUNT = 27;

	@Override
	public TweakerMoreConfigBooleanHotkeyed getConfig()
	{
		return TweakerMoreConfigs.AUTO_COLLECT_MATERIAL_LIST_ITEM;
	}

	private static void log(Message.MessageType type, String translationKey, Object... args)
	{
		if (TweakerMoreConfigs.AUTO_COLLECT_MATERIAL_LIST_ITEM_MESSAGE_TYPE.getOptionListValue() == AutoCollectMaterialListItemLogType.FULL)
		{
			InfoUtils.showGuiOrInGameMessage(type, translationKey, args);
		}
		else
		{
			String text = type.getFormatting() + StringUtils.translate(translationKey, args) + GuiBase.TXT_RST;
			InfoUtils.printActionbarMessage(text);
		}
	}

	/**
	 * Requires litematica mod
	 */
	@Override
	public ProcessResult process(LocalPlayer player, AbstractContainerScreen<?> containerScreen, List<Slot> allSlots, List<Slot> playerInvSlots, List<Slot> containerInvSlots)
	{
		List<MaterialRequirement> requirements;
		MaterialListHudRendererAccessor hudRendererAccessor = null;
		AutoCollectMaterialListItemSource source = (AutoCollectMaterialListItemSource)TweakerMoreConfigs.AUTO_COLLECT_MATERIAL_LIST_ITEM_SOURCE.getOptionListValue();
		if (source == AutoCollectMaterialListItemSource.SYNCMATICA_R)
		{
			Optional<List<ClaimedMaterialRequirement>> claimedRequirements = SyncmaticaMaterialApiAccess.getClaimedMaterialRequirements(player.getUUID());
			if (!claimedRequirements.isPresent())
			{
				log(Message.MessageType.WARNING, "tweakermore.impl.autoCollectMaterialListItem.syncmatica_r_unavailable");
				return new ProcessResult(true, TweakerMoreConfigs.AUTO_COLLECT_MATERIAL_LIST_ITEM_CLOSE_GUI.getBooleanValue());
			}
			requirements = this.createSyncmaticaRequirements(claimedRequirements.get(), playerInvSlots, containerInvSlots);
		}
		else
		{
			MaterialListBase materialList = DataManager.getMaterialList();
			if (materialList == null)
			{
				log(Message.MessageType.WARNING, "tweakermore.impl.autoCollectMaterialListItem.no_material_list");
				return new ProcessResult(true, TweakerMoreConfigs.AUTO_COLLECT_MATERIAL_LIST_ITEM_CLOSE_GUI.getBooleanValue());
			}

			hudRendererAccessor = (MaterialListHudRendererAccessor)materialList.getHudRenderer();
			// refresh before operation starts to make sure it's up-to-date
			MaterialListUtils.updateAvailableCounts(materialList.getMaterialsAll(), player);
			requirements = this.createLitematicaRequirements(materialList);
		}

		String guiTitle = containerScreen.getTitle().getString();

		boolean summaryOnly = TweakerMoreConfigs.AUTO_COLLECT_MATERIAL_LIST_ITEM_MESSAGE_TYPE.getOptionListValue() == AutoCollectMaterialListItemLogType.SUMMARY;
		List<String> summaries = Lists.newArrayList();
		boolean takenSomething = false;

		for (MaterialRequirement requirement : requirements)
		{
			int collectionTarget = this.calculateCollectionTarget(requirement.stack, requirement.requiredAmount);
			MaterialCollection collection = this.createMaterialCollection(requirement.stack, requirement.requiredAmount, collectionTarget, containerInvSlots);
			if (TweakerMoreConfigs.AUTO_COLLECT_MATERIAL_LIST_ITEM_REQUIRE_SUFFICIENT_SUPPLY.getBooleanValue() && !collection.hasSufficientSupply())
			{
				continue;
			}

			this.collectMaterial(containerScreen, playerInvSlots, containerInvSlots, collection);
			if (collection.totalTaken <= 0)
			{
				continue;
			}

			if (!takenSomething && !summaryOnly)
			{
				log(Message.MessageType.INFO, "tweakermore.impl.autoCollectMaterialListItem.info.title", guiTitle);
			}
			takenSomething = true;
			this.reportCollection(hudRendererAccessor, collection, summaryOnly, summaries);
		}

		if (!takenSomething)
		{
			log(Message.MessageType.INFO, "tweakermore.impl.autoCollectMaterialListItem.took_nothing", guiTitle);
		}
		else if (summaryOnly)
		{
			log(Message.MessageType.INFO, Joiner.on(", ").join(summaries));
		}

		if (hudRendererAccessor != null)
		{
			// refresh after operation ends
			hudRendererAccessor.setLastUpdateTime(-1);
		}
		return new ProcessResult(true, TweakerMoreConfigs.AUTO_COLLECT_MATERIAL_LIST_ITEM_CLOSE_GUI.getBooleanValue());
	}

	private List<MaterialRequirement> createLitematicaRequirements(MaterialListBase materialList)
	{
		List<MaterialRequirement> requirements = new ArrayList<>();
		for (MaterialListEntry entry : materialList.getMaterialsMissingOnly(true))
		{
			int requiredAmount = this.calculateMaterialListRequiredAmount(materialList, entry);
			if (requiredAmount > 0)
			{
				requirements.add(new MaterialRequirement(entry.getStack(), requiredAmount));
			}
		}
		return requirements;
	}

	private List<MaterialRequirement> createSyncmaticaRequirements(List<ClaimedMaterialRequirement> claimedRequirements, List<Slot> playerInvSlots, List<Slot> containerInvSlots)
	{
		List<MaterialRequirement> requirements = new ArrayList<>();
		for (ClaimedMaterialRequirement claimedRequirement : claimedRequirements)
		{
			if (!claimedRequirement.getVariant().isEmpty())
			{
				TweakerMoreMod.LOGGER.debug("Skipping unsupported Syncmatica_r material variant '{}' for {}", claimedRequirement.getVariant(), claimedRequirement.getItemId());
				continue;
			}

			int requiredAmount = this.subtractPlayerInventory(claimedRequirement, playerInvSlots);
			Optional<ItemStack> stack = this.findMatchingStack(claimedRequirement.getItemId(), containerInvSlots);
			if (requiredAmount > 0 && stack.isPresent())
			{
				requirements.add(new MaterialRequirement(stack.get(), requiredAmount));
			}
		}
		return requirements;
	}

	private int subtractPlayerInventory(ClaimedMaterialRequirement requirement, List<Slot> playerInvSlots)
	{
		long carriedAmount = 0;
		for (Slot slot : playerInvSlots)
		{
			if (this.matchesItemId(slot.getItem(), requirement.getItemId()))
			{
				carriedAmount += slot.getItem().getCount();
			}
		}
		return (int)Math.max(0, (long)requirement.getMissingAmount() - carriedAmount);
	}

	private Optional<ItemStack> findMatchingStack(String itemId, List<Slot> containerInvSlots)
	{
		for (Slot slot : containerInvSlots)
		{
			ItemStack stack = slot.getItem();
			if (this.matchesItemId(stack, itemId))
			{
				return Optional.of(stack.copy());
			}
		}

		if (TweakerMoreConfigs.AUTO_COLLECT_MATERIAL_LIST_ITEM_TAKE_SHULKER_BOXES.getBooleanValue())
		{
			for (Slot slot : containerInvSlots)
			{
				ItemStack containerStack = slot.getItem();
				if (!ItemUtils.isShulkerBox(containerStack.getItem()))
				{
					continue;
				}
				Optional<NonNullList<ItemStack>> storedItems = me.fallenbreath.tweakermore.util.InventoryUtils.getStoredItems(containerStack);
				if (storedItems.isPresent())
				{
					for (ItemStack storedStack : storedItems.get())
					{
						if (this.matchesItemId(storedStack, itemId))
						{
							return Optional.of(storedStack.copy());
						}
					}
				}
			}
		}
		return Optional.empty();
	}

	private boolean matchesItemId(ItemStack stack, String itemId)
	{
		return !stack.isEmpty() && RegistryUtils.getItemId(stack.getItem()).equals(itemId);
	}

	private int calculateMaterialListRequiredAmount(MaterialListBase materialList, MaterialListEntry entry)
	{
		// Match Litematica's missing-only filter: multipliers apply to the total material count
		long materialListRequiredAmount = materialList.getMultiplier() == 1 ? entry.getCountMissing() : (long)entry.getCountTotal() * materialList.getMultiplier();
		materialListRequiredAmount -= entry.getCountAvailable();
		if (materialListRequiredAmount <= 0)
		{
			return 0;
		}
		return (int)Math.min(materialListRequiredAmount, Integer.MAX_VALUE);
	}

	private int calculateCollectionTarget(ItemStack stack, int materialListRequiredAmount)
	{
		long collectionTarget = (long)materialListRequiredAmount + TweakerMoreConfigs.AUTO_COLLECT_MATERIAL_LIST_ITEM_EXTRA_AMOUNT.getIntegerValue();
		if (TweakerMoreConfigs.AUTO_COLLECT_MATERIAL_LIST_ITEM_ROUND_UP_TO_STACK.getBooleanValue())
		{
			int maxStackSize = stack.getMaxStackSize();
			long roundedTarget = ((long)materialListRequiredAmount + maxStackSize - 1) / maxStackSize * maxStackSize;
			collectionTarget = Math.max(collectionTarget, roundedTarget);
		}
		return (int)Math.min(collectionTarget, Integer.MAX_VALUE);
	}

	private MaterialCollection createMaterialCollection(ItemStack stack, int materialListRequiredAmount, int collectionTarget, List<Slot> containerInvSlots)
	{
		MaterialCollection collection = new MaterialCollection(stack, materialListRequiredAmount, collectionTarget);
		boolean takeShulkerBoxes = TweakerMoreConfigs.AUTO_COLLECT_MATERIAL_LIST_ITEM_TAKE_SHULKER_BOXES.getBooleanValue();
		for (Slot slot : containerInvSlots)
		{
			ItemStack slotStack = slot.getItem();
			if (InventoryUtils.areStacksEqual(stack, slotStack))
			{
				collection.looseItemAmount += this.getMovableLooseItemAmount(slotStack);
			}
			else if (takeShulkerBoxes && ItemUtils.isShulkerBox(slotStack.getItem()))
			{
				// Retaining items only applies to loose stacks; shulker boxes are unstackable
				int contentAmount = this.getShulkerBoxContentAmount(slotStack, stack);
				for (int i = 0; contentAmount > 0 && i < slotStack.getCount(); i++)
				{
					collection.shulkerBoxCandidates.add(new ShulkerBoxCandidate(slot, contentAmount));
				}
			}
		}
		collection.shulkerBoxCandidates.sort((left, right) -> Integer.compare(right.itemAmount, left.itemAmount));
		return collection;
	}

	private void collectMaterial(AbstractContainerScreen<?> containerScreen, List<Slot> playerInvSlots, List<Slot> containerInvSlots, MaterialCollection collection)
	{
		String itemName = this.getItemName(collection.stack);
		this.collectShulkerBoxes(containerScreen, playerInvSlots, collection, itemName);
		this.collectLooseItems(containerScreen, playerInvSlots, containerInvSlots, collection, itemName);
	}

	private void collectShulkerBoxes(AbstractContainerScreen<?> containerScreen, List<Slot> playerInvSlots, MaterialCollection collection, String itemName)
	{
		while (!collection.hasReachedTarget())
		{
			int targetAmountRemaining = collection.getTargetAmountRemaining();
			int candidateIndex = this.findShulkerBoxToMove(collection.shulkerBoxCandidates, targetAmountRemaining, collection.looseItemAmount);
			if (candidateIndex < 0)
			{
				break;
			}

			ShulkerBoxCandidate candidate = collection.shulkerBoxCandidates.remove(candidateIndex);
			if (this.moveShulkerBoxToPlayerInventory(containerScreen, playerInvSlots, candidate))
			{
				collection.recordTaken(candidate.itemAmount);
				TweakerMoreMod.LOGGER.debug("Moved 1x shulker box containing {}x {}, {} items short of collection target", candidate.itemAmount, itemName, collection.getTargetAmountRemaining());
			}
		}
	}

	private void collectLooseItems(AbstractContainerScreen<?> containerScreen, List<Slot> playerInvSlots, List<Slot> containerInvSlots, MaterialCollection collection, String itemName)
	{
		for (Slot slot : containerInvSlots)
		{
			int targetAmountRemaining = collection.getTargetAmountRemaining();
			if (targetAmountRemaining <= 0)
			{
				break;
			}
			if (!InventoryUtils.areStacksEqual(collection.stack, slot.getItem()))
			{
				continue;
			}

			int stackAmount = slot.getItem().getCount();
			int tryMoveAmount = Math.min(targetAmountRemaining, this.getMovableLooseItemAmount(slot.getItem()));
			if (tryMoveAmount <= 0)
			{
				continue;
			}

			this.moveToPlayerInventory(containerScreen, playerInvSlots, slot, tryMoveAmount);
			int moved = stackAmount - slot.getItem().getCount();
			collection.recordTaken(moved);
			TweakerMoreMod.LOGGER.debug("Moved {}x (attempt {}x) {} to player inventory, {} items short of collection target", moved, tryMoveAmount, itemName, collection.getTargetAmountRemaining());
			if (moved == 0)
			{
				TweakerMoreMod.LOGGER.debug("Player inventory is full for item {}", itemName);
				break;
			}
		}
	}

	private void reportCollection(MaterialListHudRendererAccessor hudRendererAccessor, MaterialCollection collection, boolean summaryOnly, List<String> summaries)
	{
		String amountColor = collection.hasReachedTarget() ? GuiBase.TXT_GREEN : GuiBase.TXT_GOLD;
		String overRequiredSuffix = this.getOverRequiredSuffix(collection);
		ChatFormatting formatting = collection.stack.getRarity().
				//#if MC >= 12006
				//$$ color();
				//#else
				color;
				//#endif
		String stackName = formatting + collection.stack.getHoverName().getString() + GuiBase.TXT_RST;
		if (summaryOnly)
		{
			summaries.add(String.format("%s +%s%s", stackName, amountColor + collection.totalTaken + GuiBase.TXT_RST, overRequiredSuffix));
		}
		else
		{
			String progressSuffix = this.getProgressSuffix(hudRendererAccessor, collection);
			log(Message.MessageType.INFO, "tweakermore.impl.autoCollectMaterialListItem.info.line", GuiBase.TXT_GOLD + collection.totalTaken + GuiBase.TXT_RST, stackName, overRequiredSuffix, progressSuffix);
		}
	}

	private String getOverRequiredSuffix(MaterialCollection collection)
	{
		long amountOverRequired = collection.getAmountOverMaterialListRequirement();
		return amountOverRequired > 0 ? StringUtils.translate("tweakermore.impl.autoCollectMaterialListItem.info.over_required", GuiBase.TXT_GOLD + amountOverRequired + GuiBase.TXT_RST) : "";
	}

	private String getProgressSuffix(MaterialListHudRendererAccessor hudRendererAccessor, MaterialCollection collection)
	{
		int materialListAmountRemaining = collection.getMaterialListAmountRemaining();
		int targetAmountRemaining = collection.getTargetAmountRemaining();
		int amountRemaining;
		String translationKey;
		if (materialListAmountRemaining > 0)
		{
			amountRemaining = materialListAmountRemaining;
			translationKey = "tweakermore.impl.autoCollectMaterialListItem.info.material_list_remaining";
		}
		else if (targetAmountRemaining > 0)
		{
			amountRemaining = targetAmountRemaining;
			translationKey = "tweakermore.impl.autoCollectMaterialListItem.info.target_remaining";
		}
		else
		{
			return "";
		}

		String formattedAmount = hudRendererAccessor != null ?
				hudRendererAccessor.invokeGetFormattedCountString(amountRemaining, collection.stack.getMaxStackSize()) :
				Integer.toString(amountRemaining);
		return StringUtils.translate(translationKey, GuiBase.TXT_GOLD + formattedAmount + GuiBase.TXT_RST);
	}

	private String getItemName(ItemStack stack)
	{
		//#if MC >= 26.1
		//$$ return stack.getItem().getName(stack).getString();
		//#elseif MC >= 12103
		//$$ return stack.getItem().getName().getString();
		//#else
		return stack.getItem().getDescription().getString();
		//#endif
	}

	private int getMovableLooseItemAmount(ItemStack stack)
	{
		int amount = stack.getCount();
		if (TweakerMoreConfigs.AUTO_COLLECT_MATERIAL_LIST_ITEM_RETAIN_ITEM.getBooleanValue())
		{
			amount -= TweakerMoreConfigs.AUTO_COLLECT_MATERIAL_LIST_ITEM_KEEP_RETAIN_AMOUNT.getIntegerValue();
		}
		return Math.max(0, amount);
	}

	private int getShulkerBoxContentAmount(ItemStack shulkerBox, ItemStack expectedStack)
	{
		if (!ItemUtils.isShulkerBox(shulkerBox.getItem()))
		{
			return 0;
		}

		Optional<NonNullList<ItemStack>> storedItems = me.fallenbreath.tweakermore.util.InventoryUtils.getStoredItems(shulkerBox);
		if (!storedItems.isPresent() || storedItems.get().size() != SHULKER_BOX_SLOT_COUNT)
		{
			return 0;
		}

		int itemAmount = 0;
		for (ItemStack storedStack : storedItems.get())
		{
			if (storedStack.isEmpty())
			{
				continue;
			}
			if (!InventoryUtils.areStacksEqual(expectedStack, storedStack))
			{
				return 0;
			}
			itemAmount += storedStack.getCount();
		}

		if (itemAmount <= 0)
		{
			return 0;
		}

		int fullAmount = SHULKER_BOX_SLOT_COUNT * expectedStack.getMaxStackSize();
		double fillRatio = (double)itemAmount / fullAmount;
		if (fillRatio < TweakerMoreConfigs.AUTO_COLLECT_MATERIAL_LIST_ITEM_SHULKER_BOX_FILL_THRESHOLD.getDoubleValue())
		{
			return 0;
		}
		return itemAmount;
	}

	private int findShulkerBoxToMove(List<ShulkerBoxCandidate> candidates, int targetAmountRemaining, long looseItemAmount)
	{
		// Candidates are sorted from fullest to emptiest. Prefer the fullest box that won't exceed the target
		for (int index = 0; index < candidates.size(); index++)
		{
			if (candidates.get(index).itemAmount <= targetAmountRemaining)
			{
				return index;
			}
		}

		// If loose items can't fill the gap, use the smallest box to minimize unavoidable over-collection
		return targetAmountRemaining > looseItemAmount && !candidates.isEmpty() ? candidates.size() - 1 : -1;
	}

	private boolean moveShulkerBoxToPlayerInventory(AbstractContainerScreen<?> containerScreen, List<Slot> playerInvSlots, ShulkerBoxCandidate candidate)
	{
		int stackAmount = candidate.slot.getItem().getCount();
		this.moveToPlayerInventory(containerScreen, playerInvSlots, candidate.slot, 1);
		int movedBoxes = stackAmount - candidate.slot.getItem().getCount();
		return movedBoxes > 0;
	}

	private void moveToPlayerInventory(AbstractContainerScreen<?> containerScreen, List<Slot> playerInvSlots, Slot fromSlot, int amount)
	{
		ItemStack stack = fromSlot.getItem().copy();
		if (amount == stack.getCount())
		{
			InventoryUtils.shiftClickSlot(containerScreen, fromSlot.index);
			return;
		}
		else if (amount > stack.getCount())
		{
			TweakerMoreMod.LOGGER.warn("Too many items to move to player inventory, the stack {} has {} items but {} items are required", stack.getItem(), stack.getCount(), amount);
			return;
		}
		// ensured amount <= stack.getCount()

		InventoryUtils.leftClickSlot(containerScreen, fromSlot.index);
		// reversed iterating to match vanilla shift-click item putting order
		for (int idx = playerInvSlots.size() - 1; idx >= 0; idx--)
		{
			Slot slot = playerInvSlots.get(idx);
			int clickAmount = 0;
			if (slot.hasItem() && InventoryUtils.areStacksEqual(slot.getItem(), stack))
			{
				ItemStack invStack = slot.getItem();
				clickAmount = Math.min(invStack.getMaxStackSize() - invStack.getCount(), amount);
			}
			else if (!slot.hasItem())
			{
				clickAmount = amount;
			}
			for (int i = 0; i < clickAmount; i++) InventoryUtils.rightClickSlot(containerScreen, slot.index);
			amount -= clickAmount;
			if (amount == 0)
			{
				break;
			}
		}
		InventoryUtils.leftClickSlot(containerScreen, fromSlot.index);
		if (amount != 0)
		{
			TweakerMoreMod.LOGGER.warn("Failed to move full item stack to player inventory, {} items remains", amount);
		}
	}

	private static class ShulkerBoxCandidate
	{
		private final Slot slot;
		private final int itemAmount;

		private ShulkerBoxCandidate(Slot slot, int itemAmount)
		{
			this.slot = slot;
			this.itemAmount = itemAmount;
		}
	}

	private static class MaterialRequirement
	{
		private final ItemStack stack;
		private final int requiredAmount;

		private MaterialRequirement(ItemStack stack, int requiredAmount)
		{
			this.stack = stack;
			this.requiredAmount = requiredAmount;
		}
	}

	private static class MaterialCollection
	{
		private final ItemStack stack;
		private final int materialListRequiredAmount;
		private final int collectionTarget;
		private long looseItemAmount;
		private final List<ShulkerBoxCandidate> shulkerBoxCandidates = Lists.newArrayList();
		private long totalTaken;

		private MaterialCollection(ItemStack stack, int materialListRequiredAmount, int collectionTarget)
		{
			this.stack = stack;
			this.materialListRequiredAmount = materialListRequiredAmount;
			this.collectionTarget = collectionTarget;
		}

		private boolean hasSufficientSupply()
		{
			return this.getAvailableAmount() >= this.collectionTarget;
		}

		private long getAvailableAmount()
		{
			long amount = this.looseItemAmount;
			for (ShulkerBoxCandidate candidate : this.shulkerBoxCandidates)
			{
				amount += candidate.itemAmount;
			}
			return amount;
		}

		private void recordTaken(int amount)
		{
			this.totalTaken += amount;
		}

		private boolean hasReachedTarget()
		{
			return this.totalTaken >= this.collectionTarget;
		}

		private int getTargetAmountRemaining()
		{
			return (int)Math.max(0, this.collectionTarget - this.totalTaken);
		}

		private int getMaterialListAmountRemaining()
		{
			return (int)Math.max(0, this.materialListRequiredAmount - this.totalTaken);
		}

		private long getAmountOverMaterialListRequirement()
		{
			return Math.max(0, this.totalTaken - this.materialListRequiredAmount);
		}
	}
}
