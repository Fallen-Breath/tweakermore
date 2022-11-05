package me.fallenbreath.tweakermore.mixins.tweaks.mod_tweaks.ofPlayerExtraModelOverride;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.impl.mod_tweaks.ofPlayerExtraModelOverride.OverrideImpl;
import me.fallenbreath.tweakermore.impl.mod_tweaks.ofPlayerExtraModelOverride.PlayerConfigurationParserWithOverride;
import me.fallenbreath.tweakermore.util.ModIds;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

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

	@ModifyExpressionValue(
			method = "downloadModel",
			at = @At(
					value = "INVOKE",
					target = "Lnet/optifine/http/HttpPipeline;get(Ljava/lang/String;Ljava/net/Proxy;)[B"
			),
			require = 0,
			remap = false
	)
	private byte[] downloadModelOverride$TKM(byte[] getResult)
	{
		if (this.override$TKM != null && this.override$TKM.model != null)
		{
			getResult = this.override$TKM.model;
		}
		return getResult;
	}

	@ModifyExpressionValue(
			method = "downloadTextureImage",
			at = @At(
					value = "INVOKE",
					target = "Lnet/optifine/http/HttpPipeline;get(Ljava/lang/String;Ljava/net/Proxy;)[B"
			),
			require = 0,
			remap = false
	)
	private byte[] downloadTextureOverride$TKM(byte[] getResult)
	{
		if (this.override$TKM != null && this.override$TKM.texture != null)
		{
			getResult = this.override$TKM.texture;
		}
		return getResult;
	}
}
