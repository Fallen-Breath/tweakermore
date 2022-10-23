package me.fallenbreath.tweakermore.mixins.tweaks.features.tweakmSchematicProPlace;

import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BlockItem.class)
public interface BlockItemAccessor
{
	@Invoker
	BlockState invokeGetPlacementState(ItemPlacementContext context);
}
