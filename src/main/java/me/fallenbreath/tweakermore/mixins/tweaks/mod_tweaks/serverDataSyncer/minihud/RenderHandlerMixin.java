package me.fallenbreath.tweakermore.mixins.tweaks.mod_tweaks.serverDataSyncer.minihud;

import fi.dy.masa.minihud.event.RenderHandler;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.util.ModIds;
import org.spongepowered.asm.mixin.Mixin;

//#if MC >= 11500
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.impl.serverDataSyncer.ServerDataSyncer;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;
//#endif

@Restriction(require = @Condition(ModIds.minihud))
@Mixin(RenderHandler.class)
public abstract class RenderHandlerMixin
{
	// no bee hive in 1.14
	//#if MC >= 11500
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
			if (blockEntity instanceof BeehiveBlockEntity && !MinecraftClient.getInstance().isIntegratedServerRunning())
			{
				ServerDataSyncer.getInstance().syncBlockEntity(blockEntity.getPos());
			}
		}
		return blockEntity;
	}
	//#endif
}
