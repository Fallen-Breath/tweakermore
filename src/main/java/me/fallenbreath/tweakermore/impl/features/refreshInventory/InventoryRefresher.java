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

package me.fallenbreath.tweakermore.impl.features.refreshInventory;

import fi.dy.masa.malilib.util.InfoUtils;
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

			InfoUtils.printActionbarMessage("tweakermore.impl.refreshInventory.refreshed");
		}
	}
}
