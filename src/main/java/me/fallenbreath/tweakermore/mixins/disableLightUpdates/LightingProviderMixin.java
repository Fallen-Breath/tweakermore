package me.fallenbreath.tweakermore.mixins.disableLightUpdates;

import me.fallenbreath.tweakermore.ILightingProvider;
import net.minecraft.world.World;
import net.minecraft.world.chunk.light.LightingProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LightingProvider.class)
public abstract class LightingProviderMixin implements ILightingProvider
{
	private World world$tweakermore;

	public void setWorld$tweakermore(World world$tweakermore)
	{
		this.world$tweakermore = world$tweakermore;
	}

	@Inject(method = "checkBlock", at = @At("HEAD"), cancellable = true)
	private void noLightUpdate(CallbackInfo ci)
	{
		if (this.world$tweakermore.isClient() && me.fallenbreath.tweakermore.config.TweakerMoreConfigs.DISABLE_LIGHT_UPDATES.getBooleanValue())
		{
			ci.cancel();
		}
	}
}
