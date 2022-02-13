package me.fallenbreath.tweakermore.mixins.tweaks.bossBarScale;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.util.RenderUtil;
import net.minecraft.client.gui.hud.BossBarHud;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BossBarHud.class)
public abstract class BossBarHudMixin
{
	@Nullable
	private RenderUtil.Scaler scaler = null;

	@ModifyVariable(
			method = "render",
			at = @At(
					value = "INVOKE",
					target = "Ljava/util/Map;values()Ljava/util/Collection;",
					remap = false,
					ordinal = 0
			),
			ordinal = 0
	)
	private int tweakerMore_bossBarScale_push(int windowsWidth)
	{
		this.scaler = null;
		if (TweakerMoreConfigs.BOSS_BAR_SCALE.isModified())
		{
			this.scaler = RenderUtil.createScaler(windowsWidth / 2, 0, TweakerMoreConfigs.BOSS_BAR_SCALE.getDoubleValue());
			this.scaler.apply();
		}
		return windowsWidth;
	}

	@Inject(
			method = "render",
			slice = @Slice(
					from = @At(
							value = "INVOKE",
							target = "Ljava/util/Map;values()Ljava/util/Collection;",
							remap = false,
							ordinal = 0
					)
			),
			at = @At("RETURN")
	)
	private void tweakerMore_bossBarScale_pop(CallbackInfo ci)
	{
		if (this.scaler != null)
		{
			this.scaler.restore();
		}
	}
}
