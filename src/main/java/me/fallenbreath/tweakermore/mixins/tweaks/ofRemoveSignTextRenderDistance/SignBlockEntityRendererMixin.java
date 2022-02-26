package me.fallenbreath.tweakermore.mixins.tweaks.ofRemoveSignTextRenderDistance;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.client.render.block.entity.SignBlockEntityRenderer;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.lang.reflect.Field;
import java.util.function.Supplier;

import static me.fallenbreath.tweakermore.util.ModIds.optifine;

@Restriction(require = @Condition(optifine))
@Mixin(SignBlockEntityRenderer.class)
public abstract class SignBlockEntityRendererMixin
{
// idk why @Shadow does not work
//	@SuppressWarnings("target")
//	@Dynamic("Added by optifine")
//	@Shadow(remap = false)
//	private static double textRenderDistanceSq;

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
		return textRenderDistanceSqGetter.get();
	}

	private static final Supplier<Double> textRenderDistanceSqGetter;

	static
	{
		try
		{
			//noinspection JavaReflectionMemberAccess
			Field field = SignBlockEntityRenderer.class.getDeclaredField("textRenderDistanceSq");
			field.setAccessible(true);
			textRenderDistanceSqGetter = () -> {
				try
				{
					return (double)field.get(null);
				}
				catch (Exception e)
				{
					throw new RuntimeException("cannot access textRenderDistanceSq");
				}
			};
		}
		catch (Exception e)
		{
			throw new RuntimeException("cannot access textRenderDistanceSq, it should be added by Optifine");
		}
	}
}
