package me.fallenbreath.tweakermore.mixins.tweaks.ofRemoveSignTextRenderDistance;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.client.render.block.entity.SignBlockEntityRenderer;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static me.fallenbreath.tweakermore.util.ModIds.optifine;

@Restriction(require = @Condition(optifine))
@Mixin(SignBlockEntityRenderer.class)
public abstract class SignBlockEntityRendererMixin
{
	@SuppressWarnings("target")
	@Dynamic("Added by optifine")
	@Shadow(remap = false)
	private static double textRenderDistanceSq;

	@Dynamic("Added by optifine")
	@Redirect(
			method = "isRenderText",
			at = @At(
					value = "FIELD",
					target = "Lnet/minecraft/client/render/block/entity/SignBlockEntityRenderer;textRenderDistanceSq:D",
					remap = true
			),
			remap = false
	)
	private static double ofDisableSignTextRenderDistance()
	{
		if (TweakerMoreConfigs.OF_REMOVE_SIGN_TEXT_RENDER_DISTANCE.getBooleanValue())
		{
			return Double.MAX_VALUE;
		}
		// "vanilla" optifine behavior
		return textRenderDistanceSq;
	}
}
