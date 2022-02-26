package me.fallenbreath.tweakermore.mixins.tweaks.shaderGameTimeAsWorldTime;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.util.ModIds;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("UnresolvedMixinReference")
@Restriction(require = @Condition(ModIds.iris))
@Pseudo
@Mixin(targets = "net.coderbot.iris.uniforms.WorldTimeUniforms")
public abstract class WorldTimeUniformsMixin
{
	@Shadow(remap = false)
	private static ClientWorld getWorld() { return null; }

	@Inject(method = "getWorldDayTime", at = @At("HEAD"), cancellable = true, remap = false)
	private static void irisShaderGameTimeAsWorldTime(CallbackInfoReturnable<Integer> cir)
	{
		if (TweakerMoreConfigs.SHADER_GAME_TIME_AS_WORLD_TIME.getBooleanValue())
		{
			ClientWorld clientWorld = getWorld();
			if (clientWorld != null)
			{
				cir.setReturnValue((int)(clientWorld.getTime() % 24000L));
			}
		}
	}
}
