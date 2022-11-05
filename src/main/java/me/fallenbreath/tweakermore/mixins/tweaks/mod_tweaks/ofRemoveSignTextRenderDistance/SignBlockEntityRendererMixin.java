package me.fallenbreath.tweakermore.mixins.tweaks.mod_tweaks.ofRemoveSignTextRenderDistance;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.util.ModIds;
import net.minecraft.client.render.block.entity.SignBlockEntityRenderer;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Restriction(require = @Condition(ModIds.optifine))
@Mixin(SignBlockEntityRenderer.class)
public abstract class SignBlockEntityRendererMixin
{
	@Dynamic("Added by optifine")
	@ModifyExpressionValue(
			method = "isRenderText",
			at = @At(
					value = "FIELD",
					target = "Lnet/minecraft/client/render/block/entity/SignBlockEntityRenderer;textRenderDistanceSq:D",
					remap = true
			),
			remap = false
	)
	private static double ofDisableSignTextRenderDistance(double signTextRenderDistance)
	{
		if (TweakerMoreConfigs.OF_REMOVE_SIGN_TEXT_RENDER_DISTANCE.getBooleanValue())
		{
			signTextRenderDistance = Double.MAX_VALUE;
		}
		return signTextRenderDistance;
	}
}
