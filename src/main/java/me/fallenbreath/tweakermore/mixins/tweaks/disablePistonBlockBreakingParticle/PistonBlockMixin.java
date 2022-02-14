package me.fallenbreath.tweakermore.mixins.tweaks.disablePistonBlockBreakingParticle;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PistonBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(PistonBlock.class)
public abstract class PistonBlockMixin
{
	/**
	 * There's an {@link net.minecraft.block.AbstractBlock.AbstractBlockState#isAir()} check in {@link net.minecraft.client.particle.ParticleManager#addBlockBreakParticles}
	 * so replacing the block state argument with an air can disable the particle adding
	 */
	@ModifyArg(
			method = "move",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/World;addBlockBreakParticles(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V"
			)
	)
	private BlockState disablePistonBlockBreakingParticle(BlockState blockState)
	{
		if (TweakerMoreConfigs.DISABLE_PISTON_BLOCK_BREAKING_PARTICLE.getBooleanValue())
		{
			blockState = Blocks.AIR.getDefaultState();
		}
		return blockState;
	}
}
