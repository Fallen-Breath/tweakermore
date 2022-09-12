package me.fallenbreath.tweakermore.mixins.tweaks.mod_tweaks.serverDataSyncer.tweakermore;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.impl.features.inventoryPreviewForCommandBlock.CommandBlockContentPreviewRenderer;
import me.fallenbreath.tweakermore.impl.mod_tweaks.serverDataSyncer.serverDataSyncer.ServerDataSyncer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CommandBlockContentPreviewRenderer.class)
public abstract class CommandBlockContentPreviewRendererMixin
{
	@SuppressWarnings("UnresolvedMixinReference")
	@Inject(
			method = "showPreview",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/block/entity/CommandBlockBlockEntity;getCommandExecutor()Lnet/minecraft/world/CommandBlockExecutor;",
					remap = true
			),
			remap = false
	)
	private static void serverDataSyncer4CommandBlockContentPreview(World world, BlockPos blockPos, CallbackInfo ci)
	{
		if (TweakerMoreConfigs.SERVER_DATA_SYNCER.getBooleanValue())
		{
			if (!MinecraftClient.getInstance().isIntegratedServerRunning())
			{
				ServerDataSyncer.getInstance().syncBlockEntity(blockPos);
			}
		}
	}
}
