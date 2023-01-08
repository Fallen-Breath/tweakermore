/*
 * This file is part of the TweakerMore project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2023  Fallen_Breath and contributors
 *
 * TweakerMore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * TweakerMore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with TweakerMore.  If not, see <https://www.gnu.org/licenses/>.
 */

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
