package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.tweakmFakeNightVision;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin
{
	@Inject(
			method = "hasStatusEffect",
			at = @At("HEAD"),
			cancellable = true
	)
	private void tweakmFakeNightVision(StatusEffect effect, CallbackInfoReturnable<Boolean> cir)
	{
		if (TweakerMoreConfigs.TWEAKM_FAKE_NIGHT_VISION.getBooleanValue())
		{
			LivingEntity self = (LivingEntity)(Object)this;
			MinecraftClient mc = MinecraftClient.getInstance();
			if (effect == StatusEffects.NIGHT_VISION && (self == mc.player || self == mc.gameRenderer.getCamera().getFocusedEntity()))
			{
				cir.setReturnValue(true);
			}
		}
	}
}
