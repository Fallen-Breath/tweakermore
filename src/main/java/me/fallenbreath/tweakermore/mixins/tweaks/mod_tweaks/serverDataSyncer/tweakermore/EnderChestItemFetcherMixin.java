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

package me.fallenbreath.tweakermore.mixins.tweaks.mod_tweaks.serverDataSyncer.tweakermore;

import com.google.common.collect.Maps;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.impl.mod_tweaks.mlShulkerBoxPreviewSupportEnderChest.EnderChestItemFetcher;
import me.fallenbreath.tweakermore.impl.mod_tweaks.serverDataSyncer.ServerDataSyncer;
import me.fallenbreath.tweakermore.mixins.tweaks.mod_tweaks.mlShulkerBoxPreviewSupportEnderChest.BasicInventoryAccessor;
import me.fallenbreath.tweakermore.util.collection.ExpiringMap;
import me.fallenbreath.tweakermore.util.event.TweakerMoreEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.UUID;

@Mixin(EnderChestItemFetcher.class)
public abstract class EnderChestItemFetcherMixin
{
	@Unique
	private static final ExpiringMap<UUID, EnderChestInventory> CACHE = new ExpiringMap<>(Maps.newHashMap(), 5_000);
	@Unique
	private static final long COOLDOWN_MS = 250;  // 0.25s (5gt)
	@Unique
	private static long prevMilli = 0;

	static
	{
		TweakerMoreEvents.registerDimensionChangedCallback(() -> {
			CACHE.clear();
			prevMilli = 0;
		});
	}

	@SuppressWarnings("UnresolvedMixinReference")
	@Inject(method = "getEntityData", at = @At("HEAD"), remap = false, cancellable = true)
	private static void serverDataSyncer4mlShulkerBoxPreviewSupportEnderChest(@Coerce Entity entity, CallbackInfoReturnable<Optional<DefaultedList<ItemStack>>> cir)
	{
		if (TweakerMoreConfigs.SERVER_DATA_SYNCER.getBooleanValue() && ServerDataSyncer.hasEnoughPermission() && entity instanceof PlayerEntity)
		{
			PlayerEntity player = (PlayerEntity)entity;
			UUID uuid = player.getUuid();

			// syncing the complete data of a player is a bit costly,
			// so we slow it down and only do it every 5 gt
			long now = System.currentTimeMillis();
			if (now - prevMilli >= COOLDOWN_MS)
			{
				prevMilli = now;

				ServerDataSyncer.getInstance().fetchEntity(player).
						ifPresent(future -> {
							EnderChestInventory enderChestInventory = CACHE.computeIfAbsent(uuid, k -> new EnderChestInventory());
							CACHE.keepAlive(uuid);
							future.thenAccept(nbt -> {
								// ref: net.minecraft.entity.player.PlayerEntity.readCustomDataFromTag
								if (nbt != null && nbt.contains("EnderItems", 9))
								{
									enderChestInventory.readTags(
											nbt.getList("EnderItems", 10)
											//#if MC >= 12006
											//$$ , player.getRegistryManager()
											//#endif
									);
								}
							});
						});
			}

			EnderChestInventory inventory = CACHE.get(uuid);
			if (inventory != null)
			{
				cir.setReturnValue(Optional.ofNullable(((BasicInventoryAccessor)inventory).getStackList()));
			}
		}
	}
}
