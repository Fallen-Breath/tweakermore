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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.disableEntityModelRendering;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

//#if MC >= 12109
//$$ import net.minecraft.client.renderer.SubmitNodeCollector;
//$$ import net.minecraft.client.renderer.state.CameraRenderState;
//#endif

//#if MC >= 12103
//$$ import net.minecraft.client.renderer.entity.state.EntityRenderState;
//#endif

//#if MC >= 11500
import net.minecraft.client.renderer.MultiBufferSource;
import com.mojang.blaze3d.vertex.PoseStack;
//#endif

/**
 * mc1.21.9-: subproject 1.15.2 (main project)        <--------
 * mc1.21.9+: subproject 1.21.9
 */
@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin
{
	@WrapWithCondition(
			//#if MC >= 1.21.9
			//$$ method = "submit",
			//#elseif MC >= 1.21.3
			//$$ method = "render(Lnet/minecraft/world/entity/Entity;DDDFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/EntityRenderer;)V",
			//#elseif MC >= 1.15.0
			method = "render",
			//#else
			//$$ method = "render(Lnet/minecraft/world/entity/Entity;DDDFFZ)V",
			//#endif
			at = @At(
					value = "INVOKE",
					//#if MC >= 1.21.9
					//$$ target = "Lnet/minecraft/client/renderer/entity/EntityRenderer;submit(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/CameraRenderState;)V"
					//#elseif MC >= 1.21.5
					//$$ target = "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;render(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;DDDLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/EntityRenderer;)V"
					//#elseif MC >= 1.21.3
					//$$ target = "Lnet/minecraft/client/renderer/entity/EntityRenderer;render(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"
					//#elseif MC >= 1.15.0
					target = "Lnet/minecraft/client/renderer/entity/EntityRenderer;render(Lnet/minecraft/world/entity/Entity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"
					//#else
					//$$ target = "Lnet/minecraft/client/renderer/entity/EntityRenderer;render(Lnet/minecraft/world/entity/Entity;DDDFF)V"
					//#endif
			)
	)
	//#if MC >= 1.21.9
	//$$ private <S extends EntityRenderState> boolean disableEntityModelRendering_cancelRender(EntityRenderer instance, S entityRenderState, PoseStack matrixStack, SubmitNodeCollector orderedRenderCommandQueue, CameraRenderState cameraRenderState)
	//#elseif MC >= 1.21.5
	//$$ private <E extends Entity, S extends EntityRenderState> boolean disableEntityModelRendering_cancelRender(EntityRenderDispatcher instance, S entityRenderState, double x, double y, double z, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int light, EntityRenderer<? super E, S> entityRenderer)
	//#elseif MC >= 1.21.3
	//$$ private <E extends Entity, S extends EntityRenderState> boolean disableEntityModelRendering_cancelRender(EntityRenderer<? super E, S> instance, S entityRenderState, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int light)
	//#elseif MC >= 1.15.0
	boolean disableEntityModelRendering_cancelRender(EntityRenderer<Entity> instance, Entity entity, float yaw, float tickDelta, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int light)
	//#else
	//$$ boolean disableEntityModelRendering_cancelRender(EntityRenderer<Entity> instance, Entity entity, double x, double y, double z, float yaw, float tickDelta)
	//#endif
	{
		return !TweakerMoreConfigs.DISABLE_ENTITY_MODEL_RENDERING.getBooleanValue();
	}
}
