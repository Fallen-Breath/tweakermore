package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.spectatorTeleportMenuIncludeSpectator;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.impl.mc_tweaks.spectatorTeleportMenuIncludeSpectator.CommandEntryWithSpectatorMark;
import net.minecraft.client.gui.hud.spectator.SpectatorMenuCommand;
import net.minecraft.client.gui.hud.spectator.TeleportSpectatorMenu;
import net.minecraft.client.gui.hud.spectator.TeleportToSpecificPlayerSpectatorCommand;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.List;

@Mixin(TeleportSpectatorMenu.class)
public abstract class TeleportSpectatorMenuMixin
{
	@Shadow @Final private List<SpectatorMenuCommand> elements;

	@Inject(method = "<init>(Ljava/util/Collection;)V", at = @At("TAIL"), require = 0)
	private void spectatorTeleportMenuIncludeSpectator_alwaysEnterIfStatement(Collection<PlayerListEntry> entries, CallbackInfo ci)
	{
		if (TweakerMoreConfigs.SPECTATOR_TELEPORT_MENU_INCLUDE_SPECTATOR.getBooleanValue())
		{
			for (PlayerListEntry entry : entries)
			{
				if (entry.getGameMode() == GameMode.SPECTATOR)
				{
					TeleportToSpecificPlayerSpectatorCommand command = new TeleportToSpecificPlayerSpectatorCommand(entry.getProfile());
					((CommandEntryWithSpectatorMark)command).setIsSpectator(true);
					this.elements.add(command);
				}
			}
		}
	}
}
