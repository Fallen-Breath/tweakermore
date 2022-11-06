package me.fallenbreath.tweakermore.mixins.tweaks.features.tweakmSchematicProPlace;

import net.minecraft.block.Block;
import net.minecraft.block.CoralFanBlock;
import net.minecraft.block.CoralWallFanBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CoralWallFanBlock.class)
public interface CoralWallFanBlockAccessor
{
	@Accessor
	Block getDeadCoralBlock();
}
