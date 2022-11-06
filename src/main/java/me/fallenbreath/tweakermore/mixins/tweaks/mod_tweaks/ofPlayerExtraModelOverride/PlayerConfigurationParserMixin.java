package me.fallenbreath.tweakermore.mixins.tweaks.mod_tweaks.ofPlayerExtraModelOverride;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.impl.mod_tweaks.ofPlayerExtraModelOverride.OverrideImpl;
import me.fallenbreath.tweakermore.impl.mod_tweaks.ofPlayerExtraModelOverride.PlayerConfigurationParserWithOverride;
import me.fallenbreath.tweakermore.util.ModIds;
import me.fallenbreath.tweakermore.util.ReflectionUtil;
import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.net.Proxy;

@SuppressWarnings("UnresolvedMixinReference")
@Restriction(require = @Condition(ModIds.optifine))
@Pseudo
@Mixin(targets = "net.optifine.player.PlayerConfigurationParser")
public abstract class PlayerConfigurationParserMixin implements PlayerConfigurationParserWithOverride
{
	private OverrideImpl override$TKM = null;

	@Override
	public void setOverride$TKM(OverrideImpl override)
	{
		this.override$TKM = override;
	}

	@Redirect(
			method = "downloadModel",
			at = @At(
					value = "INVOKE",
					target = "Lnet/optifine/http/HttpPipeline;get(Ljava/lang/String;Ljava/net/Proxy;)[B"
			),
			require = 0,
			remap = false
	)
	private byte[] downloadModelOverride$TKM(String urlStr, Proxy proxy)
	{
		if (this.override$TKM != null && this.override$TKM.model != null)
		{
			return this.override$TKM.model;
		}

		// original logic
		return invokeHttpPipelineGet$TKM(urlStr, proxy);
	}

	@Redirect(
			method = "downloadTextureImage",
			at = @At(
					value = "INVOKE",
					target = "Lnet/optifine/http/HttpPipeline;get(Ljava/lang/String;Ljava/net/Proxy;)[B"
			),
			require = 0,
			remap = false
	)
	private byte[] downloadTextureOverride$TKM(String urlStr, Proxy proxy)
	{
		if (this.override$TKM != null && this.override$TKM.texture != null)
		{
			return this.override$TKM.texture;
		}

		// original logic
		return invokeHttpPipelineGet$TKM(urlStr, proxy);
	}

	private static byte[] invokeHttpPipelineGet$TKM(String urlStr, Proxy proxy)
	{
		// HttpPipeline.get(urlStr, proxy)
		MutableObject<byte[]> ret = new MutableObject<>();
		ReflectionUtil.getClass("net.optifine.http.HttpPipeline").ifPresent(clazz -> {
			byte[] b = ReflectionUtil.<byte[]>invoke(clazz, "get", null, urlStr, proxy).get();
			ret.setValue(b);
		});
		return ret.getValue();
	}
}
