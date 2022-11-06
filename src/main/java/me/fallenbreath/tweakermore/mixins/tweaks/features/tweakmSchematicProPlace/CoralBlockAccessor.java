package me.fallenbreath.tweakermore.mixins.tweaks.features.tweakmSchematicProPlace;

import net.minecraft.block.Block;
import net.minecraft.block.CoralBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CoralBlock.class)
public interface CoralBlockAccessor
{
	@Accessor
	Block getDeadCoralBlock();
}
