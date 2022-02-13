package me.fallenbreath.tweakermore.mixins.tweaks.scoreboardSideBarScale;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.util.RenderUtil;
import net.minecraft.client.gui.hud.InGameHud;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin
{
	@Shadow private int scaledWidth;

	@Nullable
	private RenderUtil.Scaler scaler = null;

	@ModifyVariable(
			method = "renderScoreboardSidebar",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/options/GameOptions;getTextBackgroundColor(F)I",
					ordinal = 0
			),
			ordinal = 3
	)
	private int tweakerMore_scoreboardSideBarScale_push(int centerY)
	{
		this.scaler = null;
		if (TweakerMoreConfigs.SCOREBOARD_SIDE_BAR_SCALE.isModified())
		{
			this.scaler = RenderUtil.createScaler(this.scaledWidth, centerY, TweakerMoreConfigs.SCOREBOARD_SIDE_BAR_SCALE.getDoubleValue());
			this.scaler.apply();
		}
		return centerY;
	}

	@Inject(method = "renderScoreboardSidebar", at = @At("RETURN"))
	private void tweakerMore_scoreboardSideBarScale_pop(CallbackInfo ci)
	{
		if (this.scaler != null)
		{
			this.scaler.restore();
		}
	}
}
