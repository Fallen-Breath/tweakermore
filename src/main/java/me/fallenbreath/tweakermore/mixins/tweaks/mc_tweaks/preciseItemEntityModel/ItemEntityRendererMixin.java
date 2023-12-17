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
import net.minecraft.client.render.entity.ItemEntityRenderer;
import net.minecraft.entity.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntityRenderer.class)
public abstract class ItemEntityRendererMixin
{
	@ModifyExpressionValue(
			//#if MC >= 11500
			method = "render(Lnet/minecraft/entity/ItemEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
			//#else
			//$$ method = "method_3997",
			//#endif
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/render/entity/ItemEntityRenderer;getRenderedAmount(Lnet/minecraft/item/ItemStack;)I"
			)
	)
	private int preciseItemEntityModel_setRenderedAmountTo1(int amount)
	{
		if (TweakerMoreConfigs.PRECISE_ITEM_ENTITY_MODEL.getBooleanValue())
		{
			amount = 1;
		}
		return amount;
	}

	@ModifyArg(
			//#if MC >= 11500
			method = "render(Lnet/minecraft/entity/ItemEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
			//#else
			//$$ method = "method_3997",
			//#endif
			at = @At(
					value = "INVOKE",
					//#if MC >= 11903
					//$$ target = "Lnet/minecraft/client/util/math/MatrixStack;translate(FFF)V",
					//#elseif MC >= 11500
					target = "Lnet/minecraft/client/util/math/MatrixStack;translate(DDD)V",
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
			//#if MC >= 11500
			method = "render(Lnet/minecraft/entity/ItemEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
			//#else
			//$$ method = "method_3997",
			//#endif
			at = @At(
					value = "INVOKE",
					//#if MC >= 11903
					//$$ target = "Lnet/minecraft/util/math/RotationAxis;rotation(F)Lorg/joml/Quaternionf;",
					//#elseif MC >= 11500
					target = "Lnet/minecraft/client/util/math/Vector3f;getRadialQuaternion(F)Lnet/minecraft/util/math/Quaternion;",
					//#else
					//$$ target = "Lcom/mojang/blaze3d/platform/GlStateManager;rotatef(FFFF)V",
					//#endif
					ordinal = 0
			),
			index = 0
	)
	private float preciseItemEntityModel_tweakItemEntityRotation(
			float rotation,
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
	)
	{
		if (TweakerMoreConfigs.PRECISE_ITEM_ENTITY_MODEL.getBooleanValue())
		{
			rotation = 0;
			if (TweakerMoreConfigs.PRECISE_ITEM_ENTITY_MODEL_YAW_SNAP.getBooleanValue())
			{
				float yaw = (itemEntity.getYaw(tickDelta) + 360) % 360;
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
			//#if MC >= 11500
			method = "render(Lnet/minecraft/entity/ItemEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
			//#else
			//$$ method = "render(Lnet/minecraft/entity/ItemEntity;DDDFF)V",
			//#endif
			at = @At(
					value = "INVOKE",
					//#if MC >= 11904
					//$$ target = "Lnet/minecraft/client/render/item/ItemRenderer;renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V"
					//#elseif MC >= 11500
					target = "Lnet/minecraft/client/render/item/ItemRenderer;renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformation$Mode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V"
					//#else
					//$$ target = "Lnet/minecraft/client/render/model/json/ModelTransformation;applyGl(Lnet/minecraft/client/render/model/json/ModelTransformation$Type;)V"
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
			//#if MC >= 11500
			method = "render(Lnet/minecraft/entity/ItemEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
			//#else
			//$$ method = "render(Lnet/minecraft/entity/ItemEntity;DDDFF)V",
			//#endif
			at = @At(
					value = "INVOKE",
					//#if MC >= 11904
					//$$ target = "Lnet/minecraft/client/render/item/ItemRenderer;renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V",
					//#elseif MC >= 11500
					target = "Lnet/minecraft/client/render/item/ItemRenderer;renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformation$Mode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V",
					//#else
					//$$ target = "Lnet/minecraft/client/render/model/json/ModelTransformation;applyGl(Lnet/minecraft/client/render/model/json/ModelTransformation$Type;)V",
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
}
