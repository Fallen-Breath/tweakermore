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

package me.fallenbreath.tweakermore.mixins.tweaks.features.shulkerTooltipHints;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReceiver;
import me.fallenbreath.tweakermore.impl.mc_tweaks.shulkerTooltipHints.ShulkerToolTipEnhancer;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Text;
import net.minecraft.util.DefaultedList;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.List;

//#if MC >= 11600
//$$ import net.minecraft.text.MutableText;
//#endif

@Mixin(ShulkerBoxBlock.class)
public abstract class ShulkerBoxBlockMixin
{
	@Unique private ItemStack currentItemStack = null;

	@ModifyReceiver(
			//#if MC >= 11600
			//$$ method = "appendTooltip",
			//#else
			method = "buildTooltip",
			//#endif
			slice = @Slice(
					from = @At(
							value = "CONSTANT",
							args = "stringValue=Items"
					)
			),
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/item/ItemStack;isEmpty()Z",
					ordinal = 0
			)
	)
	private ItemStack shulkerTooltipHints_storeCurrentItemStack(ItemStack itemStack)
	{
		this.currentItemStack = itemStack;
		return itemStack;
	}

	@ModifyArg(
			//#if MC >= 11600
			//$$ method = "appendTooltip",
			//#else
			method = "buildTooltip",
			//#endif
			slice = @Slice(
					from = @At(
							value = "CONSTANT",
							//#if MC >= 11600
							//$$ args = "stringValue=container.shulkerBox.itemCount"
							//#else
							args = "stringValue= x"
							//#endif
					)
			),
			at = @At(
					value = "INVOKE",
					target = "Ljava/util/List;add(Ljava/lang/Object;)Z",
					ordinal = 0
			)
	)
	private Object shulkerTooltipHints(Object textObj)
	{
		if (this.currentItemStack != null)
		{
			//#if MC >= 11600
			//$$ MutableText text = (MutableText)textObj;
			//#else
			Text text = (Text)textObj;
			//#endif
			ShulkerToolTipEnhancer.appendContentHints(this.currentItemStack, text);
		}
		return textObj;
	}
}
