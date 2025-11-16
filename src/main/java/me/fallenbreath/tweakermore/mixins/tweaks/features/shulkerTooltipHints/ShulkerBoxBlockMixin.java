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

import com.llamalad7.mixinextras.sugar.Local;
import me.fallenbreath.tweakermore.impl.mc_tweaks.shulkerBoxTooltipHints.ShulkerBoxToolTipEnhancer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;

//#if MC >= 12105
//$$ import net.minecraft.world.item.component.ItemContainerContents;
//#else
import net.minecraft.world.level.block.ShulkerBoxBlock;
//#endif

//#if MC >= 12006
//$$ import net.minecraft.world.item.Item;
//#endif

//#if MC >= 11600
//$$ import net.minecraft.network.chat.MutableComponent;
//#else
import net.minecraft.network.chat.Component;
//#endif

@Mixin(
		//#if MC >= 12105
		//$$ ItemContainerContents.class
		//#else
		ShulkerBoxBlock.class
		//#endif
)
public abstract class ShulkerBoxBlockMixin
{
	@ModifyArg(
			//#if MC >= 11600
			//$$ method = "appendTooltip",
			//#else
			method = "appendHoverText",
			//#endif
			slice = @Slice(
					from = @At(
							value = "CONSTANT",
							//#if MC >= 12105
							//$$ args = "stringValue=item.container.item_count"
							//#elseif MC >= 12002
							//$$ args = "stringValue=container.shulkerBox.itemCount"
							//#else
							args = "stringValue= x"
							//#endif
					)
			),
			at = @At(
					value = "INVOKE",
					//#if MC >= 12105
					//$$ target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V",
					//#else
					target = "Ljava/util/List;add(Ljava/lang/Object;)Z",
					//#endif
					ordinal = 0
			)
	)
	private Object shulkerTooltipHints_addHints(
			Object textObj,
			@Local(
					//#if MC < 12105
					ordinal = 1
					//#endif
			) ItemStack stackInTheContainer
			//#if MC >= 12006
			//$$ , @Local(argsOnly = true) Item.TooltipContext context
			//#endif
	)
	{
		//#if MC >= 11600
		//$$ MutableComponent text = (MutableComponent)textObj;
		//#else
		Component text = (Component)textObj;
		//#endif
		ShulkerBoxToolTipEnhancer.appendContentHints(
				//#if MC >= 12006
				//$$ context,
				//#endif
				stackInTheContainer, text
		);
		return textObj;
	}
}
