package me.fallenbreath.tweakermore.mixins.tweaks.mod_tweaks.ofPlayerExtraModelOverride;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.impl.mod_tweaks.ofPlayerExtraModelOverride.OptifinePlayerExtraModelOverrider;
import me.fallenbreath.tweakermore.impl.mod_tweaks.ofPlayerExtraModelOverride.OverrideImpl;
import me.fallenbreath.tweakermore.impl.mod_tweaks.ofPlayerExtraModelOverride.PlayerConfigurationParserWithOverride;
import me.fallenbreath.tweakermore.util.ModIds;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.Optional;

@SuppressWarnings("UnresolvedMixinReference")
@Restriction(require = @Condition(ModIds.optifine))
@Pseudo
@Mixin(targets = "net.optifine.player.PlayerConfigurationReceiver")
public abstract class PlayerConfigurationReceiverMixin
{
	@Shadow(remap = false)
	private String player;

	@Nullable
	private OverrideImpl currentOverrideImpl$TKM = null;

	@ModifyVariable(
			method = "fileDownloadFinished",
			at = @At("HEAD"),
			argsOnly = true,
			remap = false
	)
	private byte[] replacePlayerCfgContent$TKM(byte[] bytes)
	{
		Optional<OverrideImpl> override = OptifinePlayerExtraModelOverrider.overridePlayerConfig(this.player);
		this.currentOverrideImpl$TKM = null;

		if (override.isPresent())
		{
			this.currentOverrideImpl$TKM = override.get();
			bytes = override.get().cfg;
		}

		return bytes;
	}

	@Inject(
			method = "fileDownloadFinished",
			at = @At(
					value = "INVOKE",
					target = "Lnet/optifine/player/PlayerConfigurationParser;parsePlayerConfiguration(Lcom/google/gson/JsonElement;)Lnet/optifine/player/PlayerConfiguration;"
			),
			remap = false,
			locals = LocalCapture.CAPTURE_FAILSOFT
	)
	private void attachOverrideToPlayerConfigParser$TKM(
			String url, byte[] bytes, Throwable exception,
			CallbackInfo ci,
			String s, JsonParser jsonparser, JsonElement jsonelement,
			@Coerce Object playerconfigurationparser
	)
	{
		if (this.currentOverrideImpl$TKM != null)
		{
			((PlayerConfigurationParserWithOverride)playerconfigurationparser).setOverride$TKM(this.currentOverrideImpl$TKM);
		}
	}
}
