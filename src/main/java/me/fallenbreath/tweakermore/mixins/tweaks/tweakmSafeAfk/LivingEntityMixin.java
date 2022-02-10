package me.fallenbreath.tweakermore.mixins.tweaks.tweakmSafeAfk;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.impl.tweakmSafeAfk.DamageMemory;
import net.minecraft.client.MinecraftClient;
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
		if (TweakerMoreConfigs.TWEAKM_SAFE_AFK.getBooleanValue())
		{
			MinecraftClient mc = MinecraftClient.getInstance();
			LivingEntity livingEntity = (LivingEntity)(Object)this;
			if (livingEntity == mc.player && mc.world != null)
			{
				DamageMemory.recordTime();
			}
		}
	}
}
