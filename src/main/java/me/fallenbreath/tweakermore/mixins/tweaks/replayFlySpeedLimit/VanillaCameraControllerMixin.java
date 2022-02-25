package me.fallenbreath.tweakermore.mixins.tweaks.replayFlySpeedLimit;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.util.ModIds;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@SuppressWarnings("UnresolvedMixinReference")
@Restriction(require = @Condition(ModIds.replay_mod))
@Pseudo
@Mixin(targets = "com.replaymod.replay.camera.VanillaCameraController")
public abstract class VanillaCameraControllerMixin
{
	@ModifyConstant(
			method = "increaseSpeed",
			constant = @Constant(intValue = 1000),
			require = 0,
			remap = false
	)
	private int replayFlySpeedLimit(int limit)
	{
		return limit * TweakerMoreConfigs.REPLAY_FLY_SPEED_LIMIT_MULTIPLIER.getIntegerValue();
	}
}
