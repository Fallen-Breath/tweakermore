package me.fallenbreath.tweakermore.mixins.tweaks.ofUnlockF3FpsLimit;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.util.mixin.ModIds;
import me.fallenbreath.tweakermore.util.mixin.ModRequire;
import me.fallenbreath.tweakermore.util.mixin.Requirement;
import net.minecraft.client.gui.hud.DebugHud;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("ShadowTarget")
@ModRequire(enableWhen = @Requirement(ModIds.optifine))
@Mixin(DebugHud.class)
public abstract class DebugHudMixin
{
	@Dynamic
	@Shadow(remap = false)
	private long updateInfoLeftTimeMs;

	@Dynamic
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