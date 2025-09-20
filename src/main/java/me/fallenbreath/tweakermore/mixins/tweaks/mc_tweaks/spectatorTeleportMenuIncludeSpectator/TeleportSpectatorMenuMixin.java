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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.spectatorTeleportMenuIncludeSpectator;

import com.google.common.collect.Lists;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.impl.mc_tweaks.spectatorTeleportMenuIncludeSpectator.CommandEntryWithSpectatorMark;
import net.minecraft.client.gui.hud.spectator.SpectatorMenuCommand;
import net.minecraft.client.gui.hud.spectator.TeleportSpectatorMenu;
import net.minecraft.client.gui.hud.spectator.TeleportToSpecificPlayerSpectatorCommand;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.List;

//#if MC >= 11903
//$$ import java.util.Collections;
//#endif

@Mixin(TeleportSpectatorMenu.class)
public abstract class TeleportSpectatorMenuMixin
{
	@Shadow @Final @Mutable
	private List<SpectatorMenuCommand> elements;

	@Inject(method = "<init>(Ljava/util/Collection;)V", at = @At("TAIL"), require = 0)
	private void spectatorTeleportMenuIncludeSpectator_alwaysEnterIfStatement(Collection<PlayerListEntry> entries, CallbackInfo ci)
	{
		if (TweakerMoreConfigs.SPECTATOR_TELEPORT_MENU_INCLUDE_SPECTATOR.getBooleanValue())
		{
			// mc1.19.3 make the elements list immutable, so we need to reassign the field
			List<SpectatorMenuCommand> extendedElements = Lists.newArrayList(this.elements);

			for (PlayerListEntry entry : entries)
			{
				if (entry.getGameMode() == GameMode.SPECTATOR)
				{
					TeleportToSpecificPlayerSpectatorCommand command = new TeleportToSpecificPlayerSpectatorCommand(
							//#if MC >= 1.21.9
							//$$ entry
							//#else
							entry.getProfile()
							//#endif
					);
					((CommandEntryWithSpectatorMark)command).setIsSpectator$TKM(true);
					extendedElements.add(command);
				}
			}

			this.elements = extendedElements;

			//#if MC >= 11903
			//$$ this.elements = Collections.unmodifiableList(this.elements);
			//#endif
		}
	}
}
