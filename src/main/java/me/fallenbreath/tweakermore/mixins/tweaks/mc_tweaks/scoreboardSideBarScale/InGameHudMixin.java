package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.scoreboardSideBarScale;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.util.render.RenderUtil;
import net.minecraft.client.gui.hud.InGameHud;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//#if MC >= 11600
//$$ import net.minecraft.client.util.math.MatrixStack;
//$$ import net.minecraft.scoreboard.ScoreboardObjective;
//#endif

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
					//#if MC >= 11600
					//$$ target = "Lnet/minecraft/client/option/GameOptions;getTextBackgroundColor(F)I",
					//#else
					target = "Lnet/minecraft/client/options/GameOptions;getTextBackgroundColor(F)I",
					//#endif
					ordinal = 0
			),
			ordinal = 3
	)
	private int tweakerMore_scoreboardSideBarScale_push(
			int centerY
			//#if MC >= 11600
			//$$ , MatrixStack matrices, ScoreboardObjective objective
			//#endif
	)
	{
		this.scaler = null;
		if (TweakerMoreConfigs.SCOREBOARD_SIDE_BAR_SCALE.isModified())
		{
			this.scaler = RenderUtil.createScaler(this.scaledWidth, centerY, TweakerMoreConfigs.SCOREBOARD_SIDE_BAR_SCALE.getDoubleValue());
			this.scaler.apply(
					//#if MC >= 11600
					//$$ matrices
					//#endif
			);;
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
