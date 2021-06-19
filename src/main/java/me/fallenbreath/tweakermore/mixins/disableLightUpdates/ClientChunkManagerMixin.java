package me.fallenbreath.tweakermore.mixins.disableLightUpdates;

import me.fallenbreath.tweakermore.ILightingProvider;
import net.minecraft.client.world.ClientChunkManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.world.chunk.light.LightingProvider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientChunkManager.class)
public abstract class ClientChunkManagerMixin
{
	@Shadow @Final private LightingProvider lightingProvider;

	@Shadow @Final ClientWorld world;

	@Inject(method = "<init>", at = @At("TAIL"))
	private void setWorldForLightingProvider(CallbackInfo ci)
	{
		((ILightingProvider)this.lightingProvider).setWorld$tweakermore(this.world);
	}
}
