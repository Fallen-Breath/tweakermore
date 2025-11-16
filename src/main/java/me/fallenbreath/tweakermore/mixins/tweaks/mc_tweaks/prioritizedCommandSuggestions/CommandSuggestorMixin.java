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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.prioritizedCommandSuggestions;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;

//#if MC >= 11500
import net.minecraft.client.gui.components.CommandSuggestions;
//#else
//$$ import net.minecraft.client.gui.screens.ChatScreen;
//#endif

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * there's no CommandSuggestor in 1.14, and our injection point is in ChatScreen
 * the class name is not changed to make git happier
 */
//#if MC >= 11500
@Mixin(CommandSuggestions.class)
//#else
//$$ @Mixin(ChatScreen.class)
//#endif
public abstract class CommandSuggestorMixin
{
	@ModifyVariable(
			method = "showSuggestions",
			at = @At(
					value = "INVOKE",
					target = "Lcom/mojang/brigadier/suggestion/Suggestions;getList()Ljava/util/List;",
					ordinal = 0,
					remap = false
			)
	)
	private Suggestions tweakSuggestions(Suggestions suggestions)
	{
		List<String> vips = TweakerMoreConfigs.PRIORITIZED_COMMAND_SUGGESTIONS.getStrings();
		if (!vips.isEmpty())
		{
			Map<String, Integer> rank = Maps.newHashMap();
			for (int i = 0; i < vips.size(); i++)
			{
				rank.put(vips.get(i), i);
			}
			List<Suggestion> items = Lists.newArrayList(suggestions.getList());
			items.sort(Comparator.comparing((a) -> rank.getOrDefault(a.getText(), Integer.MAX_VALUE)));
			suggestions = new Suggestions(suggestions.getRange(), items);
		}
		return suggestions;
	}
}
