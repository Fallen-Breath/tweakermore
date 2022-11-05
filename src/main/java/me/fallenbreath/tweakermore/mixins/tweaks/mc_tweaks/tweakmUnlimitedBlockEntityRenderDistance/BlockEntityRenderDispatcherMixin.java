package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.tweakmUnlimitedBlockEntityRenderDistance;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockEntityRenderDispatcher.class)
public abstract class BlockEntityRenderDispatcherMixin
{
	@ModifyExpressionValue(
			method = "render(Lnet/minecraft/block/entity/BlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;)V",
			at = @At(
					value = "INVOKE",
					//#if MC>= 11700
					//$$ target = "Lnet/minecraft/client/render/block/entity/BlockEntityRenderer;isInRenderDistance(Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/util/math/Vec3d;)Z"
					//#else
					target = "Lnet/minecraft/block/entity/BlockEntity;getSquaredRenderDistance()D"
					//#endif
			)
	)
	//#if MC>= 11700
	//$$ private boolean tweakmUnlimitedBlockEntityRenderDistance(boolean inRenderDistance)
	//$$ {
	//$$ 	if (TweakerMoreConfigs.TWEAKM_UNLIMITED_BLOCK_ENTITY_RENDER_DISTANCE.getBooleanValue())
	//$$ 	{
	//$$ 		inRenderDistance = true;
	//$$ 	}
	//$$ 	return inRenderDistance;
	//$$ }
	//#else
	private double tweakmUnlimitedBlockEntityRenderDistance(double renderDistance)
	{
		if (TweakerMoreConfigs.TWEAKM_UNLIMITED_BLOCK_ENTITY_RENDER_DISTANCE.getBooleanValue())
		{
			renderDistance = Double.POSITIVE_INFINITY;
		}
		return renderDistance;
	}
	//#endif
}
