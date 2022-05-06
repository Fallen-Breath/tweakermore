package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.disableCameraSubmersionFog;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.util.ModIds;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

//#if MC >= 11700
//$$ import net.minecraft.client.render.CameraSubmersionType;
//#else
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
//#endif

@Restriction(require = @Condition(ModIds.optifine))
@Mixin(BackgroundRenderer.class)
public abstract class BackgroundRenderer_optifineMixin
{
	// @ModifyVariable doesn't work, since mixin will not able to locate method metadata for the "setupFog" method
	@Dynamic("Added by optifine")
	@Redirect(
			method = "setupFog",
			at = @At(
					value = "INVOKE",
					//#if MC >= 11700
					//$$ target = "Lnet/minecraft/client/render/Camera;getSubmersionType()Lnet/minecraft/client/render/CameraSubmersionType;",
					//#else
					target = "Lnet/minecraft/client/render/Camera;getSubmergedFluidState()Lnet/minecraft/fluid/FluidState;",
					//#endif
					remap = true
			),
			require = 0,
			remap = false
	)
	//#if MC >= 11700
	//$$ private static CameraSubmersionType disableSubmergedFog(Camera camera)
	//#elseif MC >= 11500
	private static FluidState disableSubmergedFog(Camera camera)
	//#else
	//$$ private FluidState disableSubmergedFog(Camera camera)
	//#endif
	{
		if (TweakerMoreConfigs.DISABLE_CAMERA_SUBMERSION_FOG.getBooleanValue())
		{
			//#if MC >= 11700
			//$$ return CameraSubmersionType.NONE;
			//#else
			return Fluids.EMPTY.getDefaultState();
			//#endif
		}
		//#if MC >= 11700
		//$$ return camera.getSubmersionType();
		//#else
		return camera.getSubmergedFluidState();
		//#endif
	}
}
