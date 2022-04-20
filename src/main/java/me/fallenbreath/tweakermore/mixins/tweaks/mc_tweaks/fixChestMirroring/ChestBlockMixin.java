package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.fixChestMirroring;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.enums.ChestType;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.BlockMirror;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChestBlock.class)
public abstract class ChestBlockMixin
{
	@Shadow @Final public static EnumProperty<ChestType> CHEST_TYPE;

	@Inject(method = "mirror", at = @At("RETURN"), cancellable = true)
	private void fixChestMirroring(BlockState state, BlockMirror mirror, CallbackInfoReturnable<BlockState> cir)
	{
		if (TweakerMoreConfigs.FIX_CHEST_MIRRORING.getBooleanValue())
		{
			ChestType chestType = state.get(CHEST_TYPE);
			if (mirror != BlockMirror.NONE && chestType != ChestType.SINGLE)
			{
				// the chest type is always reverted when mirrored
				cir.setReturnValue(cir.getReturnValue().with(CHEST_TYPE, chestType.getOpposite()));
			}
		}
	}
}
