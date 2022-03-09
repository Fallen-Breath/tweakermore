package me.fallenbreath.tweakermore.mixins.tweaks.tweakmDaytimeOverride;

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
		if (TweakerMoreConfigs.TWEAKM_DAYTIME_OVERRIDE.getBooleanValue())
		{
			timeArg = -TweakerMoreConfigs.DAYTIME_OVERRIDE_VALUE.getIntegerValue();
		}
		return timeArg;
	}
}
