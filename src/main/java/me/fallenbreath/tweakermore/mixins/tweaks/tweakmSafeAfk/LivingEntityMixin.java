package me.fallenbreath.tweakermore.mixins.tweaks.tweakmSafeAfk;

import me.fallenbreath.tweakermore.impl.tweakmSafeAfk.SafeAfkHelper;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin
{
	@Inject(
			method = "handleStatus",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/entity/LivingEntity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"
			)
	)
	private void playerGetsHurtHook(byte status, CallbackInfo ci)
	{
		SafeAfkHelper.onEntityEnterDamageStatus((LivingEntity)(Object)this);
	}
}
