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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.disableLightUpdates;

import me.fallenbreath.tweakermore.impl.mc_tweaks.disableLightUpdates.ILightingProvider;
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

	@Shadow @Final
	//#if MC >= 11700
	//$$ ClientWorld world;
	//#else
	private ClientWorld world;
	//#endif

	@Inject(method = "<init>", at = @At("TAIL"))
	private void setWorldForLightingProvider(CallbackInfo ci)
	{
		((ILightingProvider)this.lightingProvider).setWorld$tweakermore(this.world);
	}
}
