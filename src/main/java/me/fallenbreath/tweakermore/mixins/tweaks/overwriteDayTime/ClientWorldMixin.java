package me.fallenbreath.tweakermore.mixins.tweaks.overwriteDayTime;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Modify daytime here too,
 * so the logic used when the client received a time update packet can be reused by us (gamerule changing etc.)
 */
@Mixin(ClientWorld.class)
public abstract class ClientWorldMixin
{
	@ModifyVariable(method = "setTimeOfDay", at = @At("HEAD"), argsOnly = true)
	private long overwriteDayTime(long timeArg)
	{
		int dayTime = TweakerMoreConfigs.OVERWRITE_DAYTIME.getIntegerValue();
		if (dayTime > 0)
		{
			timeArg = -dayTime;
		}
		return timeArg;
	}
}
