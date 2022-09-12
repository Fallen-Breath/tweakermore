package me.fallenbreath.tweakermore.mixins.tweaks.features.inventoryPreviewForCommandBlock;

import fi.dy.masa.tweakeroo.renderer.RenderUtils;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.impl.features.inventoryPreviewForCommandBlock.CommandBlockContentPreviewRenderer;
import me.fallenbreath.tweakermore.util.ModIds;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Restriction(require = @Condition(ModIds.tweakeroo))
@Mixin(RenderUtils.class)
public abstract class RenderUtilsMixin
{
	@ModifyArgs(
			method = "renderInventoryOverlay",
			at = @At(
					value = "INVOKE",
					target = "Lfi/dy/masa/malilib/util/InventoryUtils;getInventory(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/inventory/Inventory;",
					ordinal = 0
			)
	)
	private static void inventoryPreviewForCommandBlock(Args args)
	{
		if (TweakerMoreConfigs.INVENTORY_PREVIEW_FOR_COMMAND_BLOCK.getBooleanValue())
		{
			CommandBlockContentPreviewRenderer.showPreview(args.get(0), args.get(1));
		}
	}
}
