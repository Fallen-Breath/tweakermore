package me.fallenbreath.tweakermore.mixins.tweaks.overwriteDayTime;

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
			int dayTime = TweakerMoreConfigs.OVERWRITE_DAYTIME.getIntegerValue();
			if (dayTime > 0)
			{
				cir.setReturnValue((long)dayTime);
			}
		}
	}
}
