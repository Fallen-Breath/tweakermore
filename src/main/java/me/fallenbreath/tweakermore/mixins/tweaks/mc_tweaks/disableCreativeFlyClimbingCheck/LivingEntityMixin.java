package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.disableCreativeFlyClimbingCheck;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.util.EntityUtil;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin
{
	@Inject(
			method = "isClimbing",
			at = @At("HEAD"),
			cancellable = true
	)
	private void createFlyNoClimb(CallbackInfoReturnable<Boolean> cir)
	{
		if (TweakerMoreConfigs.DISABLE_CREATIVE_FLY_CLIMBING_CHECK.getBooleanValue())
		{
			if (EntityUtil.isFlyingCreativePlayer((LivingEntity)(Object)this))
			{
				cir.setReturnValue(false);
			}
		}
	}
}
