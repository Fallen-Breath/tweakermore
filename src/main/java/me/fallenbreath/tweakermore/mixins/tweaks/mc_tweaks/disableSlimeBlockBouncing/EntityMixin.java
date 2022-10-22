package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.disableSlimeBlockBouncing;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static me.fallenbreath.tweakermore.util.ModIds.minecraft;

@Restriction(require = @Condition(value = minecraft, versionPredicates = ">=1.15"))
@Mixin(Entity.class)
public abstract class EntityMixin
{
	@Inject(method = "bypassesLandingEffects", at = @At("HEAD"), cancellable = true)
	private void disableSlimeBlockBouncing(CallbackInfoReturnable<Boolean> cir)
	{
		if (TweakerMoreConfigs.DISABLE_SLIME_BLOCK_BOUNCING.getBooleanValue())
		{
			Entity self = (Entity)(Object)this;
			if (self == MinecraftClient.getInstance().player)
			{
				cir.setReturnValue(true);
			}
		}
	}
}
