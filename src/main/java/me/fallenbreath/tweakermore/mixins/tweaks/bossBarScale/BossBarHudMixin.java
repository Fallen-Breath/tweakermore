package me.fallenbreath.tweakermore.mixins.tweaks.bossBarScale;

import com.mojang.blaze3d.systems.RenderSystem;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.client.gui.hud.BossBarHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BossBarHud.class, priority = 2000)
public abstract class BossBarHudMixin
{
	private boolean renderScaled;

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
		this.renderScaled = false;
		if (TweakerMoreConfigs.BOSS_BAR_SCALE.isModified())
		{
			this.renderScaled = true;
			double scale = TweakerMoreConfigs.BOSS_BAR_SCALE.getDoubleValue();
			int centerX = windowsWidth / 2;
			RenderSystem.pushMatrix();
			RenderSystem.translated(-centerX * scale, 0, 0);
			RenderSystem.scaled(scale, scale, 1);
			RenderSystem.translated(centerX / scale, 0, 0);
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
		if (this.renderScaled)
		{
			RenderSystem.popMatrix();
		}
	}
}
