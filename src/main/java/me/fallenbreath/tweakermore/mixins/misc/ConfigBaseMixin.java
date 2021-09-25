package me.fallenbreath.tweakermore.mixins.misc;

import fi.dy.masa.malilib.config.options.ConfigBase;
import me.fallenbreath.tweakermore.TweakerMoreMod;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ConfigBase.class)
public abstract class ConfigBaseMixin
{
	@Mutable
	@Shadow(remap = false) @Final private String name;

	@Inject(method = "<init>(Lfi/dy/masa/malilib/config/ConfigType;Ljava/lang/String;Ljava/lang/String;)V", at = @At("TAIL"), remap = false)
	private void changePrettyName(CallbackInfo ci)
	{
		if (this.name.startsWith(TweakerMoreMod.MOD_ID))
		{
			this.name = this.name.replaceFirst(TweakerMoreMod.MOD_ID + "\\.", "");
		}
	}
}
