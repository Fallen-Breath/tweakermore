package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.disableEntityRenderInterpolation;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.util.compat.carpet.CarpetModAccess;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BoatEntity.class)
public abstract class BoatEntityMixin extends Entity
{
	public BoatEntityMixin(EntityType<?> type, World world)
	{
		super(type, world);
	}

	@Shadow private int field_7708;

	@Inject(method = "updateTrackedPositionAndAngles", at = @At("TAIL"))
	private void disableEntityRenderInterpolation_noExtraInterpolationSteps(double x, double y, double z, float yaw, float pitch, int interpolationSteps, boolean interpolate, CallbackInfo ci)
	{
		if (TweakerMoreConfigs.DISABLE_ENTITY_RENDER_INTERPOLATION.getBooleanValue())
		{
			this.field_7708 = 1;

			// carpet's tick freeze state also freezes client world entity ticking,
			// which cancels the client-side entity position interpolation logic,
			// so we need to manually setting the entity's position & rotation to the correct values
			if (CarpetModAccess.isTickFrozen())
			{
				super.updateTrackedPositionAndAngles(x, y, z, yaw, pitch, interpolationSteps, interpolate);
			}
		}
	}
}
