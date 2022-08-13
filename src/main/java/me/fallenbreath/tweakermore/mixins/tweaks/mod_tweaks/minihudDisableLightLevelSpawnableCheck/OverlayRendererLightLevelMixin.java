package me.fallenbreath.tweakermore.mixins.tweaks.mod_tweaks.minihudDisableLightLevelSpawnableCheck;

import fi.dy.masa.minihud.renderer.OverlayRendererLightLevel;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.util.ModIds;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Restriction(require = @Condition(ModIds.minihud))
@Mixin(OverlayRendererLightLevel.class)
public abstract class OverlayRendererLightLevelMixin
{
	@Inject(
			method = "canSpawnAt",
			at = @At("HEAD"),
			remap = false,
			cancellable = true
	)
	private
	//#if MC < 11500
	//$$ static
	//#endif
	void minihudDisableLightOverlaySpawnCheck(
			int x, int y, int z, Chunk chunk, World world,
			//#if MC >= 11800
			//$$ boolean skipBlockCheck,
			//#endif
			CallbackInfoReturnable<Boolean> cir
	)
	{
		if (TweakerMoreConfigs.MINIHUD_DISABLE_LIGHT_OVERLAY_SPAWN_CHECK.getBooleanValue())
		{
			// simple checks
			BlockPos pos = new BlockPos(x, y, z);
			BlockPos posDown = pos.down();
			BlockState state = chunk.getBlockState(pos);
			boolean inSolidBlock = state.
					//#if MC >= 11600
					//$$ isSolidBlock
					//#else
					isSimpleFullBlock
					//#endif
							(world, pos);
			BlockState stateDown = chunk.getBlockState(posDown);
			boolean aboveNonAirBlock = !stateDown.isAir() && !(stateDown.getBlock() instanceof FluidBlock);
			cir.setReturnValue(!inSolidBlock && aboveNonAirBlock);
		}
	}
}
