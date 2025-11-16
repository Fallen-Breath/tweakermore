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
import com.llamalad7.mixinextras.sugar.Local;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.impl.mc_tweaks.preciseItemEntityModel.PreciseItemEntityModelUtils;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//#if MC >= 12103
//$$ import me.fallenbreath.tweakermore.impl.mc_tweaks.preciseItemEntityModel.ItemEntityRenderStateExtra;
//$$ import net.minecraft.client.renderer.entity.state.ItemEntityRenderState;
//$$ import net.minecraft.world.entity.item.ItemEntity;
//$$ import org.spongepowered.asm.mixin.injection.Inject;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//#endif

@Mixin(ItemEntityRenderer.class)
public abstract class ItemEntityRendererMixin
{
	@ModifyExpressionValue(
			//#if MC >= 12109
			//$$ method = "Lnet/minecraft/client/render/entity/ItemEntityRenderer;render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;ILnet/minecraft/client/render/entity/state/ItemStackEntityRenderState;Lnet/minecraft/util/math/random/Random;Lnet/minecraft/util/math/Box;)V",
			//#elseif MC >= 12104
			//$$ method = "renderStack",
			//#elseif MC >= 12006
			//$$ method = "renderMultipleFromCount(Lnet/minecraft/client/renderer/entity/ItemRenderer;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/item/ItemStack;Lnet/minecraft/client/resources/model/BakedModel;ZLnet/minecraft/util/RandomSource;)V",
			//#elseif MC >= 11500
			method = "render(Lnet/minecraft/world/entity/item/ItemEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
			//#else
			//$$ method = "setupBobbingItem(Lnet/minecraft/world/entity/item/ItemEntity;DDDFLnet/minecraft/client/resources/model/BakedModel;)I",
			//#endif
			at = @At(
					//#if MC >= 12104
					//$$ value = "FIELD",
					//$$ target = "Lnet/minecraft/client/renderer/entity/state/ItemClusterRenderState;count:I"
					//#else
					value = "INVOKE",
					//#if MC >= 12006
					//$$ target = "Lnet/minecraft/client/renderer/entity/ItemEntityRenderer;getRenderedAmount(I)I"
					//#else
					target = "Lnet/minecraft/client/renderer/entity/ItemEntityRenderer;getRenderAmount(Lnet/minecraft/world/item/ItemStack;)I"
					//#endif
					//#endif  // if MC >= 12104
			)
	)
	private
	//#if MC >= 12006
	//$$ static
	//#endif
	int preciseItemEntityModel_setRenderedAmountTo1(int amount)
	{
		if (TweakerMoreConfigs.PRECISE_ITEM_ENTITY_MODEL.getBooleanValue())
		{
			amount = 1;
		}
		return amount;
	}

	@ModifyArg(
			//#if MC >= 12109
			//$$ method = "submit(Lnet/minecraft/client/renderer/entity/state/ItemEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/CameraRenderState;)V",
			//#elseif MC >= 12103
			//$$ method = "render(Lnet/minecraft/client/renderer/entity/state/ItemEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
			//#elseif MC >= 11500
			method = "render(Lnet/minecraft/world/entity/item/ItemEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
			//#else
			//$$ method = "setupBobbingItem(Lnet/minecraft/world/entity/item/ItemEntity;DDDFLnet/minecraft/client/resources/model/BakedModel;)I",
			//#endif
			at = @At(
					value = "INVOKE",
					//#if MC >= 11903
					//$$ target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V",
					//#elseif MC >= 11500
					target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(DDD)V",
					//#else
					//$$ target = "Lcom/mojang/blaze3d/platform/GlStateManager;translatef(FFF)V",
					//#endif
					ordinal = 0
			),
			index = 1
	)
	//#if MC >= 11903
	//$$ private float preciseItemEntityModel_adjustHeightShiftTo0(float y)
	//#elseif MC >= 11500
	private double preciseItemEntityModel_adjustHeightShiftTo0(double y)
	//#else
	//$$ private float preciseItemEntityModel_adjustHeightShiftTo0(float y, @Local(argsOnly = true, ordinal = 1) double currentY)
	//#endif
	{
		if (TweakerMoreConfigs.PRECISE_ITEM_ENTITY_MODEL.getBooleanValue())
		{
			//#if MC >= 11500
			y = 0;
			//#else
			//$$ y = (float)currentY;
			//#endif
		}
		return y;
	}

	@ModifyArg(
			//#if MC >= 12109
			//$$ method = "submit(Lnet/minecraft/client/renderer/entity/state/ItemEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/CameraRenderState;)V",
			//#elseif MC >= 12103
			//$$ method = "render(Lnet/minecraft/client/renderer/entity/state/ItemEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
			//#elseif MC >= 11500
			method = "render(Lnet/minecraft/world/entity/item/ItemEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
			//#else
			//$$ method = "setupBobbingItem(Lnet/minecraft/world/entity/item/ItemEntity;DDDFLnet/minecraft/client/resources/model/BakedModel;)I",
			//#endif
			at = @At(
					value = "INVOKE",
					//#if MC >= 11903
					//$$ target = "Lcom/mojang/math/Axis;rotation(F)Lorg/joml/Quaternionf;",
					//#elseif MC >= 11500
					target = "Lcom/mojang/math/Vector3f;rotation(F)Lcom/mojang/math/Quaternion;",
					//#else
					//$$ target = "Lcom/mojang/blaze3d/platform/GlStateManager;rotatef(FFFF)V",
					//#endif
					ordinal = 0
			),
			index = 0
	)
	private float preciseItemEntityModel_tweakItemEntityRotation(
			float rotation,
			//#if MC >= 12103
			//$$ @Local(argsOnly = true) ItemEntityRenderState itemEntityRenderState
			//#else
			@Local(argsOnly = true) ItemEntity itemEntity,
			@Local(
					argsOnly = true,
					//#if MC >= 11500
					ordinal = 1
					//#else
					//$$ ordinal = 0
					//#endif
			)
			float tickDelta
			//#endif
	)
	{
		if (TweakerMoreConfigs.PRECISE_ITEM_ENTITY_MODEL.getBooleanValue())
		{
			rotation = 0;
			if (TweakerMoreConfigs.PRECISE_ITEM_ENTITY_MODEL_YAW_SNAP.getBooleanValue())
			{
				//#if MC >= 12103
				//$$ float yaw = 0;
				//$$ if (itemEntityRenderState instanceof ItemEntityRenderStateExtra itemEntityRenderStateExtra)
				//$$ {
				//$$ 	yaw = itemEntityRenderStateExtra.getEntityYaw$TKM();
				//$$ }
				//#else
				float yaw = itemEntity.getViewYRot(tickDelta);
				//#endif
				yaw = (yaw + 360) % 360;
				for (int r : new int[]{0, 90, 180, 270, 360})
				{
					if (r - 45 <= yaw && yaw < r + 45)
					{
						rotation = -r;
						//#if MC >= 11500
						rotation *= (float)(Math.PI / 180);
						//#endif
						break;
					}
				}
			}
		}
		return rotation;
	}

	@Inject(
			//#if MC >= 12109
			//$$ method = "submit(Lnet/minecraft/client/renderer/entity/state/ItemEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/CameraRenderState;)V",
			//#elseif MC >= 12103
			//$$ method = "render(Lnet/minecraft/client/renderer/entity/state/ItemEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
			//#elseif MC >= 11500
			method = "render(Lnet/minecraft/world/entity/item/ItemEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
			//#else
			//$$ method = "render(Lnet/minecraft/world/entity/item/ItemEntity;DDDFF)V",
			//#endif
			at = @At(
					value = "INVOKE",
					//#if MC >= 12109
					//$$ target = "Lnet/minecraft/client/renderer/entity/ItemEntityRenderer;submitMultipleFromCount(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/ItemClusterRenderState;Lnet/minecraft/util/RandomSource;Lnet/minecraft/world/phys/AABB;)V"
					//#elseif MC >= 12105
					//$$ target = "Lnet/minecraft/client/renderer/entity/ItemEntityRenderer;renderMultipleFromCount(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/state/ItemClusterRenderState;Lnet/minecraft/util/RandomSource;Lnet/minecraft/world/phys/AABB;)V"
					//#elseif MC >= 12104
					//$$ target = "Lnet/minecraft/client/renderer/entity/ItemEntityRenderer;renderMultipleFromCount(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/state/ItemClusterRenderState;Lnet/minecraft/util/RandomSource;)V"
					//#elseif MC >= 12006
					//$$ target = "Lnet/minecraft/client/renderer/entity/ItemEntityRenderer;renderMultipleFromCount(Lnet/minecraft/client/renderer/entity/ItemRenderer;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/item/ItemStack;Lnet/minecraft/client/resources/model/BakedModel;ZLnet/minecraft/util/RandomSource;)V"
					//#elseif MC >= 11904
					//$$ target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;render(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/client/resources/model/BakedModel;)V"
					//#elseif MC >= 11500
					target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;render(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/client/renderer/block/model/ItemTransforms$TransformType;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/client/resources/model/BakedModel;)V"
					//#else
					//$$ target = "Lnet/minecraft/client/renderer/block/model/ItemTransforms;apply(Lnet/minecraft/client/renderer/block/model/ItemTransforms$TransformType;)V"
					//#endif
			)
	)
	private void preciseItemEntityModel_transformOverrideOn(CallbackInfo ci)
	{
		if (TweakerMoreConfigs.PRECISE_ITEM_ENTITY_MODEL.getBooleanValue())
		{
			PreciseItemEntityModelUtils.transformOverrideFlag.set(true);
		}
	}

	@Inject(
			//#if MC >= 12109
			//$$ method = "submit(Lnet/minecraft/client/renderer/entity/state/ItemEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/CameraRenderState;)V",
			//#elseif MC >= 12103
			//$$ method = "render(Lnet/minecraft/client/renderer/entity/state/ItemEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
			//#elseif MC >= 11500
			method = "render(Lnet/minecraft/world/entity/item/ItemEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
			//#else
			//$$ method = "render(Lnet/minecraft/world/entity/item/ItemEntity;DDDFF)V",
			//#endif
			at = @At(
					value = "INVOKE",
					//#if MC >= 12109
					//$$ target = "Lnet/minecraft/client/renderer/entity/ItemEntityRenderer;submitMultipleFromCount(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/ItemClusterRenderState;Lnet/minecraft/util/RandomSource;Lnet/minecraft/world/phys/AABB;)V",
					//#elseif MC >= 12105
					//$$ target = "Lnet/minecraft/client/renderer/entity/ItemEntityRenderer;renderMultipleFromCount(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/state/ItemClusterRenderState;Lnet/minecraft/util/RandomSource;Lnet/minecraft/world/phys/AABB;)V",
					//#elseif MC >= 12104
					//$$ target = "Lnet/minecraft/client/renderer/entity/ItemEntityRenderer;renderMultipleFromCount(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/state/ItemClusterRenderState;Lnet/minecraft/util/RandomSource;)V",
					//#elseif MC >= 12006
					//$$ target = "Lnet/minecraft/client/renderer/entity/ItemEntityRenderer;renderMultipleFromCount(Lnet/minecraft/client/renderer/entity/ItemRenderer;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/item/ItemStack;Lnet/minecraft/client/resources/model/BakedModel;ZLnet/minecraft/util/RandomSource;)V",
					//#elseif MC >= 11904
					//$$ target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;render(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/client/resources/model/BakedModel;)V",
					//#elseif MC >= 11500
					target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;render(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/client/renderer/block/model/ItemTransforms$TransformType;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/client/resources/model/BakedModel;)V",
					//#else
					//$$ target = "Lnet/minecraft/client/renderer/block/model/ItemTransforms;apply(Lnet/minecraft/client/renderer/block/model/ItemTransforms$TransformType;)V",
					//#endif
					shift = At.Shift.AFTER
			)
	)
	private void preciseItemEntityModel_transformOverrideOff(CallbackInfo ci)
	{
		if (TweakerMoreConfigs.PRECISE_ITEM_ENTITY_MODEL.getBooleanValue())
		{
			PreciseItemEntityModelUtils.transformOverrideFlag.set(false);
		}
	}

	//#if MC >= 12103
	//$$ @Inject(
	//$$ 		method = "extractRenderState(Lnet/minecraft/world/entity/item/ItemEntity;Lnet/minecraft/client/renderer/entity/state/ItemEntityRenderState;F)V",
	//$$ 		at = @At("TAIL")
	//$$ )
	//$$ private void preciseItemEntityModel_storeTheYawInfo(ItemEntity itemEntity, ItemEntityRenderState itemEntityRenderState, float tickDelta, CallbackInfo ci)
	//$$ {
	//$$ 	if (TweakerMoreConfigs.PRECISE_ITEM_ENTITY_MODEL.getBooleanValue())
	//$$ 	{
	//$$ 		if (itemEntityRenderState instanceof ItemEntityRenderStateExtra itemEntityRenderStateExtra)
	//$$ 		{
	//$$ 			itemEntityRenderStateExtra.setEntityYaw$TKM(itemEntity.getYaw(tickDelta));
	//$$ 		}
	//$$ 	}
	//$$ }
	//#endif
}
