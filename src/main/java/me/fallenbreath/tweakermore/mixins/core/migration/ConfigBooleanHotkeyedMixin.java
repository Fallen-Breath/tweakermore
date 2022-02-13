package me.fallenbreath.tweakermore.mixins.core.migration;

import com.google.gson.JsonElement;
import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.config.options.ConfigBoolean;
import fi.dy.masa.malilib.config.options.ConfigBooleanHotkeyed;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.util.ModIds;
import me.fallenbreath.tweakermore.util.dependency.Condition;
import me.fallenbreath.tweakermore.util.dependency.Restriction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * ConfigBooleanHotkeyed in malilib 1.18 does not accepts single json primitive (boolean)
 * which breaks compatibility of config files generated in previous mc versions
 * here comes a hacky fix, still for tweakermore's config only xd
 */
@Restriction(enableWhen = @Condition(value = ModIds.malilib, versionPredicates = ">=0.11.5"))
@Mixin(ConfigBooleanHotkeyed.class)
public abstract class ConfigBooleanHotkeyedMixin extends ConfigBoolean
{
	public ConfigBooleanHotkeyedMixin(String name, boolean defaultValue, String comment)
	{
		super(name, defaultValue, comment);
	}

	@Inject(
			method = "setValueFromJsonElement",
			at = @At(
					value = "INVOKE",
					target = "Lcom/google/gson/JsonElement;isJsonObject()Z",
					remap = false
			),
			cancellable = true,
			remap = false
	)
	private void youCanReadSingleBooleanToo(JsonElement obj, CallbackInfo ci)
	{
		if (TweakerMoreConfigs.hasConfig((ConfigBooleanHotkeyed)(Object)this))
		{
			if (obj.isJsonPrimitive())
			{
				try
				{
					super.setValueFromJsonElement(obj);
				}
				catch (Exception e)
				{
					MaLiLib.logger.warn("Failed to set config value for '{}' from the JSON element '{}'", this.getName(), obj, e);
				}
				ci.cancel();
			}
		}
	}
}
