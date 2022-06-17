package me.fallenbreath.tweakermore.impl.features.tweakmSchematicProPlace;

import fi.dy.masa.litematica.data.DataManager;
import fi.dy.masa.litematica.materials.MaterialCache;
import fi.dy.masa.litematica.util.InventoryUtils;
import fi.dy.masa.litematica.util.ItemUtils;
import fi.dy.masa.litematica.world.SchematicWorldHandler;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.util.LayerRange;
import fi.dy.masa.tweakeroo.tweaks.PlacementTweaks;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

//#if MC >= 11600
//$$ import fi.dy.masa.litematica.config.Configs;
//$$ import net.minecraft.screen.slot.Slot;
//#endif

public class SchematicBlockPicker
{
	/**
	 * Stolen from {@link fi.dy.masa.litematica.util.WorldUtils#doSchematicWorldPickBlock}
	 */
	public static void doSchematicWorldPickBlock(MinecraftClient mc, BlockPos pos, Hand hand)
	{
		World schematicWorld = SchematicWorldHandler.getSchematicWorld();
		World clientWorld = mc.world;
		if (schematicWorld != null && mc.player != null && clientWorld != null && mc.interactionManager != null)
		{
			LayerRange layerRange = DataManager.getRenderLayerRange();
			if (!layerRange.isPositionWithinRange(pos))
			{
				return;
			}
			BlockState state = schematicWorld.getBlockState(pos);

			ItemStack stack = MaterialCache.getInstance().
					//#if MC >= 11500
					getRequiredBuildItemForState
					//#else
					//$$ getItemForState
					//#endif
							(state, schematicWorld, pos);


			//#if MC >= 11700
			//$$ InventoryUtils.schematicWorldPickBlock(stack, pos, schematicWorld, mc);
			//#endif

			if (!stack.isEmpty())
			{
				//#if MC < 11700
				PlayerInventory inv = mc.player.inventory;
				stack = stack.copy();
				if (mc.player.abilities.creativeMode)
				{
					BlockEntity te = schematicWorld.getBlockEntity(pos);
					if (GuiBase.isCtrlDown() && te != null && clientWorld.isAir(pos))
					{
						ItemUtils.storeTEInStack(stack, te);
					}

					InventoryUtils.setPickedItemToHand(stack, mc);
					mc.interactionManager.clickCreativeStack(mc.player.getStackInHand(Hand.MAIN_HAND), 36 + inv.selectedSlot);
				}
				else
				{
					int slot = inv.getSlotWithStack(stack);
					boolean shouldPick = inv.selectedSlot != slot;
					if (shouldPick && slot != -1)
					{
						InventoryUtils.setPickedItemToHand(stack, mc);
					}
					//#if MC >= 11600
					//$$ else if (slot == -1 && Configs.Generic.PICK_BLOCK_SHULKERS.getBooleanValue())
					//$$ {
					//$$ 	slot = InventoryUtils.findSlotWithBoxWithItem(mc.player.playerScreenHandler, stack, false);
					//$$ 	if (slot != -1)
					//$$ 	{
					//$$ 		ItemStack boxStack = ((Slot) mc.player.playerScreenHandler.slots.get(slot)).getStack();
					//$$ 		InventoryUtils.setPickedItemToHand(boxStack, mc);
					//$$ 	}
					//$$ }
					//#endif
				}
				//#endif  // if MC < 11700

				// so hand restore works fine
				PlacementTweaks.cacheStackInHand(hand);
			}
		}
	}
}
