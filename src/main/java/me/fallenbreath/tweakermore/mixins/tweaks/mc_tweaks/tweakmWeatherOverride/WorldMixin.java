package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.tweakmWeatherOverride;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.config.options.listentries.WeatherOverrideValue;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Function;

@Mixin(World.class)
public abstract class WorldMixin
{
	@Inject(method = "getRainGradient", at = @At("HEAD"), cancellable = true)
	private void tweakmWeatherOverride_overrideRain(CallbackInfoReturnable<Float> cir)
	{
		this.tweakmWeatherOverride_common(cir, WeatherOverrideValue::getRainGradient);
	}

	@Inject(method = "getThunderGradient", at = @At("HEAD"), cancellable = true)
	private void tweakmWeatherOverride_overrideThunder(CallbackInfoReturnable<Float> cir)
	{
		this.tweakmWeatherOverride_common(cir, WeatherOverrideValue::getThunderGradient);
	}

	private void tweakmWeatherOverride_common(CallbackInfoReturnable<Float> cir, Function<WeatherOverrideValue, Float> overrider)
	{
		if (TweakerMoreConfigs.TWEAKM_WEATHER_OVERRIDE.getBooleanValue())
		{
			World self = (World)(Object)this;
			if (self instanceof ClientWorld)
			{
				WeatherOverrideValue value = (WeatherOverrideValue) TweakerMoreConfigs.WEATHER_OVERRIDE_VALUE.getOptionListValue();
				cir.setReturnValue(overrider.apply(value));
			}
		}
	}
}
