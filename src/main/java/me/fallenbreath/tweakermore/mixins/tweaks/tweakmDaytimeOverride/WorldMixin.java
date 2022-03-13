package me.fallenbreath.tweakermore.mixins.tweaks.tweakmDaytimeOverride;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(World.class)
public abstract class WorldMixin
{
	@Inject(method = "getTimeOfDay", at = @At("HEAD"), cancellable = true)
	private void overwriteDayTime(CallbackInfoReturnable<Long> cir)
	{
		World self = (World)(Object)this;
		if (self instanceof ClientWorld)
		{
			if (TweakerMoreConfigs.TWEAKM_DAYTIME_OVERRIDE.getBooleanValue())
			{
				cir.setReturnValue((long)TweakerMoreConfigs.DAYTIME_OVERRIDE_VALUE.getIntegerValue());
			}
		}
	}
}
