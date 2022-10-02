package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.disableHorizonShadingRendering;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.client.render.BackgroundRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(BackgroundRenderer.class)
public abstract class BackgroundRendererMixin
{
	@ModifyVariable(
			//#if MC >= 11500
			method = "render",
			//#else
			//$$ method = "renderBackground",
			//#endif
			slice = @Slice(
					from = @At(
							value = "INVOKE",
							//#if MC >= 11600
							//$$ target = "Lnet/minecraft/client/world/ClientWorld$Properties;getHorizonShadingRatio()F"
							//#else
							target = "Lnet/minecraft/world/dimension/Dimension;getHorizonShadingRatio()D"
							//#endif
					)
			),
			at = @At(
					value = "INVOKE",
					//#if MC >= 11900
					//$$ target = "Lnet/minecraft/client/render/BackgroundRenderer;getFogModifier(Lnet/minecraft/entity/Entity;F)Lnet/minecraft/client/render/BackgroundRenderer$StatusEffectFogModifier;"
					//#else

					target = "Lnet/minecraft/client/render/Camera;getFocusedEntity()Lnet/minecraft/entity/Entity;",
					//#if MC >= 11700
					//$$ ordinal = 1
					//#else
					ordinal = 0
					//#endif

					//#endif
			),
			//#if MC >= 11800
			//$$ ordinal = 2
			//#else
			ordinal = 0
			//#endif
	)
	//#if MC >= 11800
	//$$ private static float
	//#elseif MC >= 11500
	private static double
	//#else
	//$$ private double
	//#endif
	disableHorizonShadingRendering(
			//#if MC >= 11800
			//$$ float shaderRatio
			//#else
			double shaderRatio
			//#endif
	)
	{
		if (TweakerMoreConfigs.DISABLE_HORIZON_SHADING_RENDERING.getBooleanValue())
		{
			shaderRatio = 1.0F;
		}
		return shaderRatio;
	}
}
