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

package me.fallenbreath.tweakermore.mixins.tweaks.features.autoRespawn;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DeathScreen.class)
public abstract class DeathScreenMixin extends Screen
{
	@Shadow
	private int ticksSinceDeath;

	protected DeathScreenMixin(Text title)
	{
		super(title);
	}

	@Inject(method = "tick", at = @At("TAIL"))
	private void autoRespawn$TKM$tick(CallbackInfo ci)
	{
		if (!TweakerMoreConfigs.AUTO_RESPAWN.getBooleanValue())
		{
			return;
		}

		final int DELAY = 20;  // the amount of tick before the spectate / respawn button is enabled
		if (this.minecraft != null && this.ticksSinceDeath == DELAY)
		{
			// delay the operation a bit, cuz currently Minecraft is ticking the DeathScreen itself,
			// which doesn't seem to be a nice moment to close the screen
			this.minecraft.send(this::autoRespawn$TKM$impl);
		}
	}

	@Unique
	private void autoRespawn$TKM$impl()
	{
		if (this.minecraft != null && this.minecraft.player != null && this.minecraft.currentScreen == this)
		{
			// ref: the onPress callback of the spectate / respawn button created in net.minecraft.client.gui.screen.DeathScreen#init
			this.minecraft.player.requestRespawn();
			this.minecraft.openScreen(null);
		}
	}
}
