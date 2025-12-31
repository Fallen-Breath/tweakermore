package me.fallenbreath.tweakermore.impl.mod_tweaks.lmInventoryCheck;

import fi.dy.masa.litematica.render.schematic.ChunkCacheSchematic;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public final class LitematicaInventoryChecker {

    public static boolean isInventoryMismatched(
            BlockState schematicState,
            BlockState clientState,
            ChunkCacheSchematic schematicWorldView,
            ChunkCacheSchematic clientWorldView,
            BlockPos pos
    ) {
        return false;
    }

}
