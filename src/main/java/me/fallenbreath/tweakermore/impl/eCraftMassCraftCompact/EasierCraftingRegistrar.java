package me.fallenbreath.tweakermore.impl.eCraftMassCraftCompact;

import fi.dy.masa.itemscroller.recipes.CraftingHandler;
import fi.dy.masa.malilib.config.options.ConfigBoolean;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.container.CraftingResultSlot;

public class EasierCraftingRegistrar
{
	private static final String EASIER_CRAFTING_INVENTORY_CLASS = "de.guntram.mcmod.easiercrafting.ExtendedGuiInventory";
	private static final String EASIER_CRAFTING_CRAFTING_TABLE_CLASS = "de.guntram.mcmod.easiercrafting.ExtendedGuiCrafting";

	private static final String CRAFTING_RESULT_SLOT_CLASS = CraftingResultSlot.class.getName();

	private static boolean canRegister = false;

	public static void register()
	{
		if (canRegister && TweakerMoreConfigs.ECRAFT_ITEM_SCROLLER_COMPACT.getBooleanValue() && TweakerMoreConfigs.ECRAFT_ITEM_SCROLLER_COMPACT.getTweakerMoreOption().isEnabled())
		{
			canRegister = false;
			CraftingHandler.addCraftingGridDefinition(EASIER_CRAFTING_INVENTORY_CLASS, CRAFTING_RESULT_SLOT_CLASS, 0, new CraftingHandler.SlotRange(1, 4));
			CraftingHandler.addCraftingGridDefinition(EASIER_CRAFTING_CRAFTING_TABLE_CLASS, CRAFTING_RESULT_SLOT_CLASS, 0, new CraftingHandler.SlotRange(1, 9));
		}
	}

	public static void markDefinitionCleared()
	{
		canRegister = true;
	}

	public static void onConfigValueChanged(ConfigBoolean configBoolean)
	{
		register();
	}
}
