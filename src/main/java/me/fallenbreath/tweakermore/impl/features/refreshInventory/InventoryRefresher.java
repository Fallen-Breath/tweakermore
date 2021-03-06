package me.fallenbreath.tweakermore.impl.features.refreshInventory;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.container.SlotActionType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClickWindowC2SPacket;

//#if MC >= 11700
//$$ import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
//#endif

public class InventoryRefresher
{
	/**
	 * Inspired by viaversion 1.14 -> 1.13 villager selection inventory forced resync
	 * https://github.com/ViaVersion/ViaVersion/blob/4074352a531cfb0de6fa81e043ee761737748a7a/common/src/main/java/com/viaversion/viaversion/protocols/protocol1_14to1_13_2/packets/InventoryPackets.java#L238
	 */
	public static void refresh()
	{
		MinecraftClient mc = MinecraftClient.getInstance();
		ClientPlayNetworkHandler networkHandler = mc.getNetworkHandler();
		if (networkHandler != null && mc.player != null)
		{
			ItemStack uniqueItem = new ItemStack(Items.STONE);
			uniqueItem.getOrCreateTag().putDouble("force_resync", Double.NaN);  // Tags with NaN are not equal
			networkHandler.sendPacket(new ClickWindowC2SPacket(
					mc.player.container.syncId,
					//#if MC >= 11700
					//$$ mc.player.currentScreenHandler.getRevision(),
					//#endif
					-999, 2,
					SlotActionType.QUICK_CRAFT,
					uniqueItem,

					//#if MC >= 11700
					//$$ new Int2ObjectOpenHashMap<>()
					//#else
					mc.player.container.getNextActionId(mc.player.inventory)
					//#endif
			));
		}
	}
}
