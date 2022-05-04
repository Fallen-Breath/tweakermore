package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.disableCameraSubmersionFog;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.client.render.BackgroundRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

//#if MC >= 11700
//$$ import net.minecraft.client.render.CameraSubmersionType;
//#else
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
//#endif

@Mixin(BackgroundRenderer.class)
public abstract class BackgroundRendererMixin
{
	@ModifyVariable(
			method = "applyFog",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/render/Camera;getFocusedEntity()Lnet/minecraft/entity/Entity;"
			)
	)
	//#if MC >= 11700
	//$$ private static CameraSubmersionType disableWaterFog(CameraSubmersionType cameraSubmersionType)
	//#else
	private static FluidState disableWaterFog(FluidState fluidState)
	//#endif
	{
		if (TweakerMoreConfigs.DISABLE_CAMERA_SUBMERSION_FOG.getBooleanValue())
		{
			//#if MC >= 11700
			//$$ cameraSubmersionType = CameraSubmersionType.NONE;
			//#else
			fluidState = Fluids.EMPTY.getDefaultState();
			//#endif
		}
		//#if MC >= 11700
		//$$ return cameraSubmersionType;
		//#else
		return fluidState;
		//#endif
	}
}
