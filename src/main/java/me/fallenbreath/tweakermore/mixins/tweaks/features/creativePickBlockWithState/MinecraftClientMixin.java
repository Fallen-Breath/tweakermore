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

package me.fallenbreath.tweakermore.mixins.tweaks.features.creativePickBlockWithState;

import fi.dy.masa.malilib.util.InfoUtils;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.util.ModIds;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

//#if MC >= 12006
//$$ import com.google.common.collect.Maps;
//$$ import net.minecraft.component.DataComponentTypes;
//$$ import net.minecraft.component.type.BlockStateComponent;
//$$ import java.util.Map;
//#endif

@Restriction(require = @Condition(value = ModIds.minecraft, versionPredicates = "<1.21.4"))
@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin
{
	@Inject(
			method = "doItemPick",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/item/ItemStack;isEmpty()Z",
					ordinal = 0
			),
			locals = LocalCapture.CAPTURE_FAILHARD
	)
	private void creativePickBlockWithState_storeStateInTag(CallbackInfo ci, boolean isCreative, ItemStack itemStack, HitResult.Type type, BlockPos blockPos, BlockState blockState, Block block)
	{
		if (isCreative && !itemStack.isEmpty())
		{
			if (TweakerMoreConfigs.CREATIVE_PICK_BLOCK_WITH_STATE.isKeybindHeld())
			{
				Item item = itemStack.getItem();
				// make sure the picked item is exactly what the selected block indicates
				// to avoid things like storing piston head's states into piston item which is not good
				if (item instanceof BlockItem && ((BlockItem)item).getBlock() != blockState.getBlock())
				{
					return;
				}

				//#if MC >= 12006
				//$$ Map<String, String> properties = Maps.newLinkedHashMap();
				//$$ blockState.getEntries().forEach((property, value) -> {
				//$$ 	properties.put(property.getName(), value.toString());
				//$$ });
				//$$ itemStack.set(DataComponentTypes.BLOCK_STATE, new BlockStateComponent(properties));
				//#else
				CompoundTag nbt = new CompoundTag();
				blockState.getEntries().forEach((property, value) -> {
					nbt.putString(property.getName(), value.toString());
				});
				itemStack.getOrCreateTag().put("BlockStateTag", nbt);
				//#endif

				InfoUtils.printActionbarMessage("tweakermore.impl.creativePickBlockWithState.message", block.getName());
			}
		}
	}
}
