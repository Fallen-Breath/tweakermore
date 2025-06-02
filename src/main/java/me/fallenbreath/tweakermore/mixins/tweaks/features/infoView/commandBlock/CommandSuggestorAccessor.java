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

package me.fallenbreath.tweakermore.mixins.tweaks.features.infoView.commandBlock;

import com.mojang.brigadier.ParseResults;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

//#if MC >= 12106
//$$ import net.minecraft.client.network.ClientCommandSource;
//#else
import net.minecraft.server.command.CommandSource;
//#endif

//#if MC >= 11600
//$$ import net.minecraft.text.OrderedText;
//#endif

//#if MC >= 11500
import net.minecraft.client.gui.screen.CommandSuggestor;
//#else
//$$ import net.minecraft.client.gui.screen.ChatScreen;
//#endif

/**
 * 1.14: public ChatScreen#getRenderText
 * 1.15: public CommandSuggestor#highlight
 * 1.16+: private CommandSuggestor#highlight
 */
@Mixin(
		//#if MC >= 11500
		CommandSuggestor.class
		//#else
		//$$ ChatScreen.class
		//#endif
)
public interface CommandSuggestorAccessor
{
	@Invoker(
			//#if MC < 11500
			//$$ "getRenderText"
			//#endif
	)
	static
	//#if MC >= 11600
	//$$ OrderedText
	//#else
	String
	//#endif
	invokeHighlight(
			//#if MC >= 12106
			//$$ ParseResults<ClientCommandSource> parse,
			//#else
			ParseResults<CommandSource> parse,
			//#endif
			String original, int firstCharacterIndex
	)
	{
		throw new RuntimeException();
	}
}
