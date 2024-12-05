/*
 * This file is part of the TweakerMore project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2024  Fallen_Breath and contributors
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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.clientEntityTargetingSelectAll;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.impl.mc_tweaks.clientEntityTargetingSelectAll.EntityItemPickHelper;
import me.fallenbreath.tweakermore.impl.mc_tweaks.clientEntityTargetingSelectAll.MinecraftClientWithExtendedTargetEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClient_pickEntityMixin
{
	@ModifyExpressionValue(
			method = "doItemPick",
			at = @At(
					value = "FIELD",
					target = "Lnet/minecraft/client/MinecraftClient;crosshairTarget:Lnet/minecraft/util/hit/HitResult;"
			)
	)
	private HitResult clientEntityTargetingSelectAll_hackPickItem_replaceHitResult(HitResult crosshairTarget)
	{
		if (TweakerMoreConfigs.CLIENT_ENTITY_TARGETING_SUPPORT_ALL.getBooleanValue())
		{
			EntityHitResult entityHitResult = ((MinecraftClientWithExtendedTargetEntity)this).getExtendedEntityHitResult$TKM();
			if (entityHitResult != null)
			{
				crosshairTarget = entityHitResult;
			}
		}
		return crosshairTarget;
	}

	//#if MC >= 12104
	//$$ // now it's done on the serverside, we can't perform any tweak on this anymore :(
	//#elseif MC >= 11700
	//$$ @ModifyExpressionValue(
	//$$ 		method = "doItemPick",
	//$$ 		at = @At(
	//$$ 				value = "INVOKE",
	//$$ 				target = "Lnet/minecraft/entity/Entity;getPickBlockStack()Lnet/minecraft/item/ItemStack;"
	//$$ 		)
	//$$ )
	//$$ private @Nullable ItemStack clientEntityTargetingSelectAll_hackPickItem_overrideVanillaResult(
	//$$ 		@Nullable ItemStack itemStack,
	//$$ 		@Local Entity entity
	//$$ )
	//$$ {
	//$$ 	if (TweakerMoreConfigs.CLIENT_ENTITY_TARGETING_SUPPORT_ALL.getBooleanValue())
	//$$ 	{
	//$$ 		if (itemStack == null)
	//$$ 		{
	//$$ 			itemStack = EntityItemPickHelper.pickItem(entity);
	//$$ 		}
	//$$ 	}
	//$$ 	return itemStack;
	//$$ }
	//#else
	@ModifyExpressionValue(
			method = "doItemPick",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/item/SpawnEggItem;forEntity(Lnet/minecraft/entity/EntityType;)Lnet/minecraft/item/SpawnEggItem;"
			)
	)
	private @Nullable SpawnEggItem clientEntityTargetingSelectAll_hackPickItem_overrideVanillaTempResult(
			@Nullable SpawnEggItem item,
			@Local Entity entity,
			@Share("overrideItemStack") LocalRef<ItemStack> overrideItemStack
	)
	{
		if (TweakerMoreConfigs.CLIENT_ENTITY_TARGETING_SUPPORT_ALL.getBooleanValue())
		{
			if (item == null)
			{
				ItemStack newPickedItem = EntityItemPickHelper.pickItem(entity);
				if (newPickedItem != null)
				{
					overrideItemStack.set(newPickedItem);
					item = SpawnEggItem.forEntity(EntityType.BAT);
				}
			}
		}
		return item;
	}

	@ModifyExpressionValue(
			method = "doItemPick",
			at = @At(
					value = "NEW",
					target = "(Lnet/minecraft/item/ItemConvertible;)Lnet/minecraft/item/ItemStack;"
			)
	)
	private ItemStack clientEntityTargetingSelectAll_hackPickItem_applyOurResult(
			ItemStack itemStack,
			@Share("overrideItemStack") LocalRef<ItemStack> overrideItemStack
	)
	{
		if (TweakerMoreConfigs.CLIENT_ENTITY_TARGETING_SUPPORT_ALL.getBooleanValue())
		{
			ItemStack newPickedItem = overrideItemStack.get();
			if (newPickedItem != null)  // it shouldn't be null
			{
				itemStack = newPickedItem;
			}
		}
		return itemStack;
	}
	//#endif
}
