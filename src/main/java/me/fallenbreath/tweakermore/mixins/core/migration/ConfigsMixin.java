package me.fallenbreath.tweakermore.mixins.core.migration;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fi.dy.masa.tweakeroo.config.Configs;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.util.ModIds;
import me.fallenbreath.tweakermore.util.mixin.Condition;
import me.fallenbreath.tweakermore.util.mixin.ModRequire;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.io.File;

@ModRequire(enableWhen = @Condition(ModIds.tweakeroo))
@Mixin(Configs.class)
public abstract class ConfigsMixin
{
	/**
	 * TweakerMore v1.x store its config in tweakeroo's config file
	 * So here's the solution for migration to v2.x
	 */
	@SuppressWarnings("InvalidInjectorMethodSignature")
	@Inject(
			method = "loadFromFile",
			at = @At(
					value = "INVOKE_ASSIGN",
					target = "Lcom/google/gson/JsonElement;getAsJsonObject()Lcom/google/gson/JsonObject;",
					shift = At.Shift.AFTER
			),
			locals = LocalCapture.CAPTURE_FAILHARD,
			remap = false
	)
	private static void loadLegacyTweakerMoreOptionsFromTweakeroo(CallbackInfo ci, File configFile, JsonElement element, JsonObject root)
	{
		TweakerMoreConfigs.loadFromJson(root);
	}
}
