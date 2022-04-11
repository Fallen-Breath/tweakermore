package me.fallenbreath.tweakermore.mixins.tweaks.serverDataSyncer;

import net.minecraft.client.network.DataQueryHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DataQueryHandler.class)
public interface DataQueryHandlerAccessor
{
	@Accessor
	int getExpectedTransactionId();

	@Accessor
	void setExpectedTransactionId(int id);
}
