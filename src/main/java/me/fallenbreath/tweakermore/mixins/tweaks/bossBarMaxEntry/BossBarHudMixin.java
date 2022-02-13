package me.fallenbreath.tweakermore.mixins.tweaks.bossBarMaxEntry;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.client.gui.hud.BossBarHud;
import net.minecraft.client.util.Window;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BossBarHud.class)
public abstract class BossBarHudMixin
{
	private int renderedBossBarAmount;

	@Inject(method = "render", at = @At("HEAD"))
	private void recordRenderedBossBarAmount(CallbackInfo ci)
	{
		this.renderedBossBarAmount = 0;
	}

	@Inject(
			method = "render",
			at = @At(
					value = "INVOKE",
					target = "Ljava/util/Iterator;next()Ljava/lang/Object;",
					remap = false
			),
			cancellable = true
	)
	private void tweakerMore_bossBarMaxEntry(CallbackInfo ci)
	{
		this.renderedBossBarAmount++;
		int value = TweakerMoreConfigs.BOSS_BAR_MAX_ENTRY.getIntegerValue();
		if (value >= 0 && this.renderedBossBarAmount > value)
		{
			ci.cancel();
		}
	}

	@Redirect(
			method = "render",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/util/Window;getScaledHeight()I"
			),
			require = 0
	)
	private int tweakerMore_bossBarMaxEntry_skipVanillaCheck(Window window)
	{
		if (TweakerMoreConfigs.BOSS_BAR_MAX_ENTRY.getIntegerValue() >= 0)
		{
			return Integer.MAX_VALUE;
		}
		return window.getScaledHeight();
	}
}
