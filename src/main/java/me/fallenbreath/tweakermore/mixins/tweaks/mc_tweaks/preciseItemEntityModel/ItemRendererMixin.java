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
import net.minecraft.client.renderer.block.model.ItemTransform;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

//#if MC >= 12104
//$$ import net.minecraft.client.renderer.item.ItemStackRenderState;
//#else
import net.minecraft.client.renderer.entity.ItemRenderer;
//#endif

@Restriction(require = @Condition(value = ModIds.minecraft, versionPredicates = ">=1.15"))
@Mixin(
		//#if MC >= 12104
		//$$ ItemStackRenderState.LayerRenderState.class
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
			//$$ method = "renderItem(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/item/ModelTransformationMode;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;ZF)V",
			//#elseif MC >= 11904
			//$$ method = "renderItem(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V",
			//#elseif MC >= 11500
			method = "render(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/client/renderer/block/model/ItemTransforms$TransformType;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/client/resources/model/BakedModel;)V",
			//#else
			//$$ method = "renderItem(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/client/render/model/BakedModel;Lnet/minecraft/client/render/model/json/ModelTransformation$Type;Z)V",
			//#endif
			at = @At(
					//#if MC >= 12105
					//$$ value = "FIELD",
					//$$ target = "Lnet/minecraft/client/renderer/item/ItemStackRenderState$LayerRenderState;transform:Lnet/minecraft/client/renderer/block/model/ItemTransform;"
					//#else

					value = "INVOKE",
					//#if MC >= 12104
					//$$ target = "Lnet/minecraft/client/renderer/item/ItemStackRenderState$LayerRenderState;transform()Lnet/minecraft/client/renderer/block/model/ItemTransform;"
					//#elseif MC >= 12103
					//$$ target = "Lnet/minecraft/client/renderer/block/model/ItemTransforms;getTransform(Lnet/minecraft/world/item/ItemDisplayContext;)Lnet/minecraft/client/renderer/block/model/ItemTransform;"
					//#elseif MC >= 11904
					//$$ target = "Lnet/minecraft/client/renderer/block/model/ItemTransforms;getTransform(Lnet/minecraft/world/item/ItemDisplayContext;)Lnet/minecraft/client/renderer/block/model/ItemTransform;"
					//#else
					target = "Lnet/minecraft/client/renderer/block/model/ItemTransforms;getTransform(Lnet/minecraft/client/renderer/block/model/ItemTransforms$TransformType;)Lnet/minecraft/client/renderer/block/model/ItemTransform;"
					//#endif

					//#endif
			)
	)
	private ItemTransform preciseItemEntityModel_tweakTransformationScale(ItemTransform transformation)
	{
		return PreciseItemEntityModelUtils.modifyTransformation(transformation);
	}
}
