package me.fallenbreath.tweakermore.mixins.tweaks.mod_tweaks.lmInventoryCheck;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import fi.dy.masa.litematica.render.schematic.ChunkCacheSchematic;
import fi.dy.masa.litematica.render.schematic.ChunkRendererSchematicVbo;
import fi.dy.masa.litematica.util.OverlayType;
import fi.dy.masa.malilib.util.Color4f;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.impl.mod_tweaks.lmInventoryCheck.LitematicaInventoryChecker;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ChunkRendererSchematicVbo.class)
public class ChunkRendererSchematicVboMixin {

    @Unique
    private final ThreadLocal<Boolean> inventoryMismatched = ThreadLocal.withInitial(() -> false);

    @Shadow
    protected ChunkCacheSchematic schematicWorldView;

    @Shadow
    protected ChunkCacheSchematic clientWorldView;

    @WrapOperation(
            method = "renderBlocksAndOverlay",
            at = @At(
                    value = "INVOKE",
                    target = "Lfi/dy/masa/litematica/render/schematic/ChunkRendererSchematicVbo;getOverlayType(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;)Lfi/dy/masa/litematica/util/OverlayType;"
            )
    )
    private OverlayType lmInventoryCheck_check(
            ChunkRendererSchematicVbo instance,
            BlockState schematicState,
            BlockState clientState,
            Operation<OverlayType> original,
            @Local(argsOnly = true) BlockPos pos
            ) {
        OverlayType type = original.call(instance, schematicState, clientState);
        if (TweakerMoreConfigs.LM_INVENTORY_CHECK.getBooleanValue()
                && type == OverlayType.NONE
                && LitematicaInventoryChecker.isInventoryMismatched(schematicState, clientState, this.schematicWorldView, this.clientWorldView, pos)
        ) {
            inventoryMismatched.set(true);
        }
        return type;
    }

    @WrapOperation(
            method = "renderBlocksAndOverlay",
            at = @At(
                    value = "INVOKE",
                    target = "Lfi/dy/masa/litematica/render/schematic/ChunkRendererSchematicVbo;getOverlayColor(Lfi/dy/masa/litematica/util/OverlayType;)Lfi/dy/masa/malilib/util/Color4f;"
            )
    )
    private Color4f lmInventoryCheck_render(ChunkRendererSchematicVbo instance, OverlayType overlayType, Operation<Color4f> original) {
        if (TweakerMoreConfigs.LM_INVENTORY_CHECK.getBooleanValue()
                && overlayType == OverlayType.NONE
                && inventoryMismatched.get()
        ) {
            return TweakerMoreConfigs.LM_INVENTORY_CHECK_COLOR.getColor();
        }
        return original.call(instance, overlayType);
    }

}
