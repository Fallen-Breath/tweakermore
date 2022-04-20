package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.disablePistonBlockBreakingParticle;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.util.ModIds;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PistonBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Restriction(require = @Condition(value = ModIds.minecraft, versionPredicates = ">=1.17"))
@Mixin(PistonBlock.class)
public abstract class PistonBlockMixin
{
	/**
	 * There's an isAir check in {@link net.minecraft.client.particle.ParticleManager#addBlockBreakParticles}
	 * so replacing the block state argument with an air can disable the particle adding
	 */
	@ModifyArg(
			method = "move",
			at = @At(
					//#if MC >= 11700
					//$$ value = "INVOKE",
					//$$ target = "Lnet/minecraft/world/World;addBlockBreakParticles(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V"
					//#else
					// this mixin will not be applied due to @Restriction annotation
					// so just use a dummy target point here
					value = "HEAD"
					//#endif
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
