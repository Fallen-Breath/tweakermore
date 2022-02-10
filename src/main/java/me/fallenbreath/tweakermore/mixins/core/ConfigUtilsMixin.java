package me.fallenbreath.tweakermore.mixins.core;

import fi.dy.masa.malilib.config.ConfigUtils;
import me.fallenbreath.tweakermore.util.StringUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ConfigUtils.class)
public abstract class ConfigUtilsMixin
{
	@ModifyArg(
			method = "readConfigBase",
			at = @At(
					value = "INVOKE",
					target = "Lcom/google/gson/JsonObject;has(Ljava/lang/String;)Z"
			),
			index = 0,
			remap = false
	)
	private static String removeTweakerMorePrefix_readConfigBase_has(String name)
	{
		return StringUtil.removeTweakerMoreNameSpacePrefix(name);
	}

	@ModifyArg(
			method = "readConfigBase",
			at = @At(
					value = "INVOKE",
					target = "Lcom/google/gson/JsonObject;get(Ljava/lang/String;)Lcom/google/gson/JsonElement;"
			),
			index = 0,
			remap = false
	)
	private static String removeTweakerMorePrefix_readConfigBase_get(String name)
	{
		return StringUtil.removeTweakerMoreNameSpacePrefix(name);
	}

	@ModifyArg(
			method = "readHotkeys",
			at = @At(
					value = "INVOKE",
					target = "Lfi/dy/masa/malilib/util/JsonUtils;getNestedObject(Lcom/google/gson/JsonObject;Ljava/lang/String;Z)Lcom/google/gson/JsonObject;",
					ordinal = 1
			),
			index = 1,
			remap = false
	)
	private static String removeTweakerMorePrefix_readHotkeys_getNestedObject(String name)
	{
		return StringUtil.removeTweakerMoreNameSpacePrefix(name);
	}

	@ModifyArg(
			method = "readHotkeys",
			at = @At(
					value = "INVOKE",
					target = "Lfi/dy/masa/malilib/util/JsonUtils;hasString(Lcom/google/gson/JsonObject;Ljava/lang/String;)Z"
			),
			index = 1,
			remap = false
	)
	private static String removeTweakerMorePrefix_readHotkeys_hasString(String name)
	{
		return StringUtil.removeTweakerMoreNameSpacePrefix(name);
	}

	@ModifyArg(
			method = "readHotkeys",
			at = @At(
					value = "INVOKE",
					target = "Lfi/dy/masa/malilib/util/JsonUtils;getString(Lcom/google/gson/JsonObject;Ljava/lang/String;)Ljava/lang/String;"
			),
			index = 1,
			remap = false
	)
	private static String removeTweakerMorePrefix_readHotkeys_getString(String name)
	{
		return StringUtil.removeTweakerMoreNameSpacePrefix(name);
	}

	@ModifyArg(
			method = "writeConfigBase",
			at = @At(
					value = "INVOKE",
					target = "Lcom/google/gson/JsonObject;add(Ljava/lang/String;Lcom/google/gson/JsonElement;)V"
			),
			index = 0,
			remap = false
	)
	private static String removeTweakerMorePrefix_writeConfigBase(String name)
	{
		return StringUtil.removeTweakerMoreNameSpacePrefix(name);
	}

	@ModifyArg(
			method = "writeHotkeys",
			at = @At(
					value = "INVOKE",
					target = "Lcom/google/gson/JsonObject;add(Ljava/lang/String;Lcom/google/gson/JsonElement;)V",
					ordinal = 2
			),
			index = 0,
			remap = false
	)
	private static String removeTweakerMorePrefix_writeHotkeys(String name)
	{
		return StringUtil.removeTweakerMoreNameSpacePrefix(name);
	}
}
