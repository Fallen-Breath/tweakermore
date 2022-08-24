package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.playerSkinBlockingLoading;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.impl.mc_tweaks.playerSkinBlockingLoading.TaskSynchronizer;
import net.minecraft.client.texture.PlayerSkinTexture;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerSkinTexture.class)
public abstract class PlayerSkinTextureMixin
{
	@Shadow private @Nullable Thread downloadThread;

	@Inject(method = "startTextureDownload", at = @At("TAIL"))
	private void playerSkinBlockingLoading_blockingTextureDownloading(CallbackInfo ci)
	{
		if (TweakerMoreConfigs.PLAYER_SKIN_BLOCKING_LOADING.getBooleanValue())
		{
			TaskSynchronizer.runOnClientThread(() -> {
				Thread thread = this.downloadThread;
				if (thread != null)
				{
					try
					{
						thread.join();
					}
					catch (InterruptedException ignored)
					{
					}
				}
			});
		}
	}
}
