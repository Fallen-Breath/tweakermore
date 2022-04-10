package me.fallenbreath.tweakermore.mixins.tweaks.serverDataSyncer.minihud;

import fi.dy.masa.minihud.event.RenderHandler;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.impl.serverDataSyncer.ServerDataSyncer;
import me.fallenbreath.tweakermore.util.ModIds;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;

@Restriction(require = @Condition(ModIds.minihud))
@Mixin(RenderHandler.class)
public abstract class RenderHandlerMixin
{
	@ModifyVariable(
			method = "addLine(Lfi/dy/masa/minihud/config/InfoToggle;)V",
			slice = @Slice(
					from = @At(
							value = "FIELD",
							target = "Lfi/dy/masa/minihud/config/InfoToggle;BEE_COUNT:Lfi/dy/masa/minihud/config/InfoToggle;",
							remap = false
					)
			),
			at = @At(
					value = "INVOKE_ASSIGN",
					target = "Lfi/dy/masa/minihud/event/RenderHandler;getTargetedBlockEntity(Lnet/minecraft/world/World;Lnet/minecraft/client/MinecraftClient;)Lnet/minecraft/block/entity/BlockEntity;",
					ordinal = 0,
					remap = true
			),
			remap = false
	)
	private BlockEntity serverDataSyncer4BeehiveBeeCount(BlockEntity blockEntity)
	{
		if (TweakerMoreConfigs.SERVER_DATA_SYNCER.getBooleanValue())
		{
			if (blockEntity instanceof BeehiveBlockEntity)
			{
				ServerDataSyncer.getInstance().syncBlockEntity(blockEntity.getPos());
			}
		}
		return blockEntity;
	}
}