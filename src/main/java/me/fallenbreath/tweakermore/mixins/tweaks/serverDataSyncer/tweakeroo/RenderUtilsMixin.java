package me.fallenbreath.tweakermore.mixins.tweaks.serverDataSyncer.tweakeroo;

import fi.dy.masa.tweakeroo.renderer.RenderUtils;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.impl.serverDataSyncer.ServerDataSyncer;
import me.fallenbreath.tweakermore.util.ModIds;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.Inventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Restriction(require = @Condition(ModIds.tweakeroo))
@Mixin(RenderUtils.class)
public abstract class RenderUtilsMixin
{
	@ModifyVariable(
			method = "renderInventoryOverlay",
			at = @At(
					value = "INVOKE_ASSIGN",
					target = "Lfi/dy/masa/malilib/util/InventoryUtils;getInventory(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/inventory/Inventory;",
					ordinal = 0,
					remap = true
			),
			remap = false
	)
	private static Inventory serverDataSyncer4InventoryOverlay(Inventory inventory)
	{
		if (TweakerMoreConfigs.SERVER_DATA_SYNCER.getBooleanValue())
		{
			if (!MinecraftClient.getInstance().isIntegratedServerRunning())
			{
				ServerDataSyncer.getInstance().syncBlockInventory(inventory);
			}
		}
		return inventory;
	}

	@ModifyVariable(
			method = "renderInventoryOverlay",
			at = @At(
					value = "INVOKE_ASSIGN",
					target = "Lnet/minecraft/util/hit/EntityHitResult;getEntity()Lnet/minecraft/entity/Entity;",
					remap = true
			),
			remap = false
	)
	private static Entity serverDataSyncer4InventoryOverlay(Entity entity)
	{
		if (TweakerMoreConfigs.SERVER_DATA_SYNCER.getBooleanValue())
		{
			if (!MinecraftClient.getInstance().isIntegratedServerRunning())
			{
				ServerDataSyncer.getInstance().syncEntity(entity);
			}
		}
		return entity;
	}
}
