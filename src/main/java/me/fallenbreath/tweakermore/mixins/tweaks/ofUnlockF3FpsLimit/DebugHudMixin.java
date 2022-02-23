package me.fallenbreath.tweakermore.mixins.tweaks.ofUnlockF3FpsLimit;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.util.ModIds;
import net.minecraft.client.gui.hud.DebugHud;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("target")
@Restriction(require = {@Condition(ModIds.optifine), @Condition(value = ModIds.minecraft, versionPredicates = ">=1.15")})
@Mixin(DebugHud.class)
public abstract class DebugHudMixin
{
	@Dynamic("Added by optifine")
	@Shadow(remap = false)
	private long updateInfoLeftTimeMs;

	@Dynamic("Added by optifine")
	@Shadow(remap = false)
	private long updateInfoRightTimeMs;

	@Inject(method = "render", at = @At("TAIL"))
	private void cancelOptFpsLimit(CallbackInfo ci)
	{
		if (TweakerMoreConfigs.OF_UNLOCK_F3_FPS_LIMIT.getBooleanValue())
		{
			this.updateInfoLeftTimeMs = 0;
			this.updateInfoRightTimeMs = 0;
		}
	}
}