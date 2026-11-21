/*
 * This file is part of the TweakerMore project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026  Fallen_Breath and contributors
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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.signEditingRequireHotkey;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.impl.mc_tweaks.signEditingRequireHotkey.SignEditRejectionTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ServerboundSignUpdatePacket;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

//#if MC >= 26.3
//$$ import net.minecraft.network.chat.Component;
//$$ import net.minecraft.world.level.block.entity.SignTextSlot;
//$$
//$$ import java.util.List;
//#else
import net.minecraft.world.level.block.entity.SignText;
//#endif

/**
 * mc >= 1.20.1: subproject 1.20.1  <--------
 * mc <  1.20.1: subproject 1.15.2 (main project)
 */
@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin
{
	@WrapWithCondition(
			method = "handleOpenSignEditor",
			at = @At(
					value = "INVOKE",
					//#if MC >= 26.3
					//$$ target = "Lnet/minecraft/client/player/LocalPlayer;openTextEdit(Lnet/minecraft/world/level/block/entity/SignBlockEntity;Lnet/minecraft/world/level/block/entity/SignTextSlot;)V",
					//#else
					target = "Lnet/minecraft/client/player/LocalPlayer;openTextEdit(Lnet/minecraft/world/level/block/entity/SignBlockEntity;Z)V",
					//#endif
					ordinal = 0  // Older versions use a second call with an empty sign when the block entity is missing
			)
	)
	private boolean signEditingRequireHotkey_allowEditing(
			LocalPlayer player, SignBlockEntity sign,
			//#if MC >= 26.3
			//$$ SignTextSlot slot
			//#else
			boolean front
			//#endif
	)
	{
		//#if MC >= 26.3
		//$$ boolean front = slot == SignTextSlot.FRONT;
		//#endif
		if (
				!TweakerMoreConfigs.SIGN_EDITING_REQUIRE_HOTKEY.getEnableState() ||
				!SignEditRejectionTracker.INSTANCE.consume(sign.getBlockPos(), front)
		)
		{
			return true;
		}

		// Submit the same lines as an untouched editor so the server releases the editing player.
		boolean filtered = Minecraft.getInstance().isTextFilteringEnabled();
		String[] lines = new String[4];
		//#if MC >= 26.3
		//$$ List<Component> messages = sign.getText(slot).getMessages(filtered);
		//#else
		SignText text = sign.getText(front);
		//#endif
		for (int i = 0; i < lines.length; i++)
		{
			//#if MC >= 26.3
			//$$ lines[i] = i < messages.size() ? messages.get(i).getString() : "";
			//#else
			lines[i] = text.getMessage(i, filtered).getString();
			//#endif
		}
		//#if MC >= 26.3
		//$$ player.connection.send(new ServerboundSignUpdatePacket(sign.getBlockPos(), List.of(lines), slot));
		//#else
		player.connection.send(new ServerboundSignUpdatePacket(sign.getBlockPos(), front, lines[0], lines[1], lines[2], lines[3]));
		//#endif
		return false;
	}
}
