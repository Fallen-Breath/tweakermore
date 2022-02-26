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
@Mixin(targets = "com.replaymod.replay.camera.ClassicCameraController")
public abstract class ClassicCameraControllerMixin
{
	@ModifyConstant(
			method = "setCameraMaximumSpeed",
			constant = @Constant(doubleValue = 20.0D, ordinal = 0),
			require = 0,
			remap = false
	)
	private double replayFlySpeedLimit(double limit)
	{
		return limit * TweakerMoreConfigs.REPLAY_FLY_SPEED_LIMIT_MULTIPLIER.getIntegerValue();
	}
}
