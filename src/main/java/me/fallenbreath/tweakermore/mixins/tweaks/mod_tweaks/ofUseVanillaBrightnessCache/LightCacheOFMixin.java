package me.fallenbreath.tweakermore.mixins.tweaks.mod_tweaks.ofUseVanillaBrightnessCache;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.util.ModIds;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@SuppressWarnings("UnresolvedMixinReference")
@Restriction(require = @Condition(ModIds.optifine))
@Pseudo
@Mixin(targets = "net.optifine.render.LightCacheOF")
public class LightCacheOFMixin
{
	@Redirect(
			method = "getBrightness",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/block/BlockState;getAmbientOcclusionLightLevel(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)F",
					remap = true
			),
			remap = false,
			require = 0
	)
	private static float ofUseVanillaBrightnessCache_ambientOcclusionLightLevel(BlockState state, BlockView view, BlockPos pos)
	{
		if (TweakerMoreConfigs.OF_USE_VANILLA_BRIGHTNESS_CACHE.getBooleanValue())
		{
			return BlockModelRendererAccessor.getBrightnessCache().get().getFloat(state, (BlockRenderView)view, pos);
		}
		return state.getAmbientOcclusionLightLevel(view, pos);
	}

	@Redirect(
			method = "getPackedLight",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/render/WorldRenderer;getLightmapCoordinates(Lnet/minecraft/world/BlockRenderView;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;)I",
					remap = true
			),
			remap = false,
			require = 0
	)
	private static int ofUseVanillaBrightnessCache_lightmapCoordinates(BlockRenderView view, BlockState state, BlockPos pos)
	{
		if (TweakerMoreConfigs.OF_USE_VANILLA_BRIGHTNESS_CACHE.getBooleanValue())
		{
			return BlockModelRendererAccessor.getBrightnessCache().get().getInt(state, view, pos);
		}
		//#if MC >= 11500
		return WorldRenderer.getLightmapCoordinates(view, state, pos);
		//#else
		//$$ return state.getBlockBrightness(view, pos);
		//#endif
	}
}
