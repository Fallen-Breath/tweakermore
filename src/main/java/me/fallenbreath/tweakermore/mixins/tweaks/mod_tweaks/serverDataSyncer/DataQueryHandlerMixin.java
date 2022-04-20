package me.fallenbreath.tweakermore.mixins.tweaks.mod_tweaks.serverDataSyncer;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.impl.serverDataSyncer.ServerDataSyncer;
import net.minecraft.client.network.DataQueryHandler;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DataQueryHandler.class)
public abstract class DataQueryHandlerMixin
{
	@Inject(method = "handleQueryResponse", at = @At("HEAD"), cancellable = true)
	private void handle(int transactionId, CompoundTag tag, CallbackInfoReturnable<Boolean> cir)
	{
		if (TweakerMoreConfigs.SERVER_DATA_SYNCER.getBooleanValue())
		{
			if (ServerDataSyncer.getInstance().getQueryHandler().handleQueryResponse(transactionId, tag))
			{
				cir.setReturnValue(true);
			}
		}
	}
}
