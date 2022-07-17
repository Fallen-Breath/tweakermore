package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.disableEntityRenderInterpolation;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

//#if MC >= 11500
import org.spongepowered.asm.mixin.injection.ModifyArg;
//#else
//$$ import org.spongepowered.asm.mixin.injection.ModifyVariable;
//#endif

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin
{
	//#if MC >= 11500
	@ModifyArg(
			method = "render",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/render/WorldRenderer;renderEntity(Lnet/minecraft/entity/Entity;DDDFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;)V"
			)
	)
	//#else
	//$$ @ModifyVariable(method = "renderEntities", at = @At("HEAD"), argsOnly = true)
	//#endif
	private float disableEntityRenderInterpolation(float tickDelta)
	{
		if (TweakerMoreConfigs.DISABLE_ENTITY_RENDER_INTERPOLATION.getBooleanValue())
		{
			tickDelta = 1.0F;
		}
		return tickDelta;
	}
}
