package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.disableEntityModelRendering;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

//#if MC >= 11500
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
//#endif

@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin
{
	@WrapWithCondition(
			//#if MC >= 11500
			method = "render",
			//#else
			//$$ method = "render(Lnet/minecraft/entity/Entity;DDDFFZ)V",
			//#endif
			at = @At(
					value = "INVOKE",
					//#if MC >= 11500
					target = "Lnet/minecraft/client/render/entity/EntityRenderer;render(Lnet/minecraft/entity/Entity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"
					//#else
					//$$ target = "Lnet/minecraft/client/render/entity/EntityRenderer;render(Lnet/minecraft/entity/Entity;DDDFF)V"
					//#endif
			)
	)
	private boolean disableEntityModelRendering$TKM
			//#if MC >= 11500
			(EntityRenderer<Entity> instance, Entity entity, float yaw, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light)
			//#else
			//$$ (EntityRenderer<Entity> instance, Entity entity, double x, double y, double z, float yaw, float tickDelta)
			//#endif

	{
		return !TweakerMoreConfigs.DISABLE_ENTITY_MODEL_RENDERING.getBooleanValue();
	}
}
