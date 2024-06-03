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

package me.fallenbreath.tweakermore.impl.mod_tweaks.mlShulkerBoxPreviewSupportEnderChest;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.mixins.tweaks.mod_tweaks.mlShulkerBoxPreviewSupportEnderChest.BasicInventoryAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.DefaultedList;

import java.util.Optional;

public class EnderChestItemFetcher
{
	public static boolean enableFor(ItemStack itemStack)
	{
		return TweakerMoreConfigs.ML_SHULKER_BOX_PREVIEW_SUPPORT_ENDER_CHEST.getBooleanValue() && itemStack.getItem() == Items.ENDER_CHEST;
	}

	// make a copy to ensure the original list is unchanged
	public static DefaultedList<ItemStack> makeCopy(DefaultedList<ItemStack> items)
	{
		DefaultedList<ItemStack> copied = DefaultedList.of();
		items.forEach(itemStack -> copied.add(itemStack.copy()));
		return copied;
	}

	public static Optional<DefaultedList<ItemStack>> fetch()
	{
		MinecraftClient mc = MinecraftClient.getInstance();
		PlayerEntity player = mc.player;
		if (player == null)
		{
			return Optional.empty();
		}

		if (mc.getServer() != null)
		{
			// single player
			PlayerEntity serverPlayer = mc.getServer().getPlayerManager().getPlayer(player.getUuid());
			return Optional.ofNullable(serverPlayer)
					.map(p -> ((BasicInventoryAccessor)p.getEnderChestInventory()).getStackList())
					.map(EnderChestItemFetcher::makeCopy);
		}

		// try fetch
		return getEntityData(player).map(EnderChestItemFetcher::makeCopy);
	}

	// do hacks here
	// return empty: nope; not-empty: fetching / fetched
	private static Optional<DefaultedList<ItemStack>> getEntityData(PlayerEntity player)
	{
		return Optional.empty();
	}
}
