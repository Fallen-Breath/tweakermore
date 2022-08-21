package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.nameTagRenderStrategy;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.config.options.listentries.RestrictionType;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

//#if MC >= 11500
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
//#endif

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin
{
	@Inject(
			//#if MC >= 11500
			method = "renderLabelIfPresent",
			//#else
			//$$ method = "renderLabel(Lnet/minecraft/entity/Entity;Ljava/lang/String;DDDI)V",
			//#endif
			at = @At("HEAD"),
			cancellable = true
	)
	private void nameTagRenderStrategy(
			//#if MC >= 11500
			// the text arg is String in 15 and Text in 16+, so just @Coerce here for lazyness
			Entity entity, @Coerce Object text, MatrixStack matrices, VertexConsumerProvider vertexConsumerProvider, int i,
			//#else
			//$$ Entity entity, String text, double x, double y, double z, int maxDistance,
			//#endif
			CallbackInfo ci
	)
	{
		RestrictionType strategyType = (RestrictionType)TweakerMoreConfigs.PLAYER_NAME_TAG_RENDER_STRATEGY_TYPE.getOptionListValue();
		if (strategyType != RestrictionType.NONE && entity instanceof PlayerEntity)
		{
			String playerName = ((PlayerEntity)entity).getGameProfile().getName();
			List<String> list = TweakerMoreConfigs.PLAYER_NAME_TAG_RENDER_STRATEGY_LIST.getStrings();
			boolean shouldRender = strategyType.testEquality(playerName, list);
			if (!shouldRender)
			{
				ci.cancel();
			}
		}
	}
}
