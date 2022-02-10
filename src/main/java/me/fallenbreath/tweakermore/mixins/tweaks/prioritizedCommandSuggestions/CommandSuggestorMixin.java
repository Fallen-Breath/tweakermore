package me.fallenbreath.tweakermore.mixins.tweaks.prioritizedCommandSuggestions;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.client.gui.screen.CommandSuggestor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Mixin(CommandSuggestor.class)
public abstract class CommandSuggestorMixin
{
	@ModifyVariable(
			method = "showSuggestions",
			at = @At(
					value = "INVOKE",
					target = "Lcom/mojang/brigadier/suggestion/Suggestions;isEmpty()Z",
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
