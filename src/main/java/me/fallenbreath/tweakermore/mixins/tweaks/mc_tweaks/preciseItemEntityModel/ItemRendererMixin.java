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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.preciseItemEntityModel;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.impl.mc_tweaks.preciseItemEntityModel.PreciseItemEntityModelUtils;
import me.fallenbreath.tweakermore.util.ModIds;
import net.minecraft.client.render.model.json.Transformation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

//#if MC >= 12104
//$$ import net.minecraft.client.render.item.ItemRenderState;
//#else
import net.minecraft.client.render.item.ItemRenderer;
//#endif

@Restriction(require = @Condition(value = ModIds.minecraft, versionPredicates = ">=1.15"))
@Mixin(
		//#if MC >= 12104
		//$$ ItemRenderState.LayerRenderState.class
		//#else
		ItemRenderer.class
		//#endif
)
public abstract class ItemRendererMixin
{
	@ModifyExpressionValue(
			//#if MC >= 12104
			//$$ method = "render",
			//#elseif MC >= 12103
			//$$ method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;ZF)V",
			//#elseif MC >= 11904
			//$$ method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V",
			//#elseif MC >= 11500
			method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformation$Mode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V",
			//#else
			//$$ method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/BakedModel;Lnet/minecraft/client/render/model/json/ModelTransformation$Type;Z)V",
			//#endif
			at = @At(
					value = "INVOKE",
					//#if MC >= 12104
					//$$ target = "Lnet/minecraft/client/render/item/ItemRenderState$LayerRenderState;getTransformation()Lnet/minecraft/client/render/model/json/Transformation;"
					//#elseif MC >= 12103
					//$$ target = "Lnet/minecraft/client/render/model/json/ModelTransformation;getTransformation(Lnet/minecraft/item/ModelTransformationMode;)Lnet/minecraft/client/render/model/json/Transformation;"
					//#elseif MC >= 11904
					//$$ target = "Lnet/minecraft/client/render/model/json/ModelTransformation;getTransformation(Lnet/minecraft/client/render/model/json/ModelTransformationMode;)Lnet/minecraft/client/render/model/json/Transformation;"
					//#else
					target = "Lnet/minecraft/client/render/model/json/ModelTransformation;getTransformation(Lnet/minecraft/client/render/model/json/ModelTransformation$Mode;)Lnet/minecraft/client/render/model/json/Transformation;"
					//#endif
			)
	)
	private Transformation preciseItemEntityModel_tweakTransformationScale(Transformation transformation)
	{
		return PreciseItemEntityModelUtils.modifyTransformation(transformation);
	}
}
