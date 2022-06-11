package me.fallenbreath.tweakermore.mixins.tweaks.porting.tkrDisableNauseaEffectPorting;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.impl.porting.tkrDisableNauseaEffectPorting.ClientPlayerEntityWithRealNauseaStrength;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

//#if MC >= 11600
//$$ import net.minecraft.client.util.math.MatrixStack;
//#endif

@Mixin(InGameHud.class)
public abstract class InGameHudMixin
{
	@Shadow @Final private MinecraftClient client;

	@ModifyVariable(
			method = "render",
			at = @At(
					value = "INVOKE_ASSIGN",
					target = "Lnet/minecraft/util/math/MathHelper;lerp(FFF)F",
					ordinal = 0
			),
			ordinal = 1
	)
	private float tkrDisableNauseaEffectPorting_makeSureNetherPortalOverlayIsNotAffected(
			float value,
			//#if MC >= 11600
			//$$ MatrixStack matrixStack,
			//#endif
			float tickDelta
	)
	{
		if (TweakerMoreConfigs.TKR_DISABLE_NAUSEA_EFFECT_PORTING.getBooleanValue())
		{
			ClientPlayerEntity player = this.client.player;

			// necessary instanceof check, since that mixin might not be applied
			if (player instanceof ClientPlayerEntityWithRealNauseaStrength)
			{
				float lastNauseaStrength = ((ClientPlayerEntityWithRealNauseaStrength)player).getRealLastNauseaStrength();
				float nextNauseaStrength = ((ClientPlayerEntityWithRealNauseaStrength)player).getRealNextNauseaStrength();
				value = MathHelper.lerp(tickDelta, lastNauseaStrength, nextNauseaStrength);
			}
		}
		return value;
	}
}
