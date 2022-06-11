package me.fallenbreath.tweakermore.impl.mc_tweaks.shulkerTooltipHints.builder;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.text.BaseText;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class AbstractHintBuilder
{
	@Nullable
	public abstract BaseText build(ItemStack itemStack);

	protected static BaseText getDivider()
	{
		return (BaseText)(new LiteralText(" | ").formatted(Formatting.DARK_GRAY));
	}

	@Nullable
	protected static BaseText buildSegments(List<Text> texts)
	{
		int amount = texts.size();
		if (amount == 0)
		{
			return null;
		}
		BaseText extraText = getDivider();

		int maxLength = TweakerMoreConfigs.SHULKER_TOOLTIP_HINT_LENGTH_LIMIT.getIntegerValue();
		TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
		int idx;
		for (idx = 0; idx < amount; idx++)
		{
			if (idx > 0 && textRenderer.getStringWidth(extraText.getString() + texts.get(idx).getString()) > maxLength)
			{
				break;
			}
			extraText.append(texts.get(idx));
			if (idx < amount - 1)
			{
				extraText.append(new LiteralText(", ").formatted(Formatting.GRAY));
			}
		}
		if (idx < amount)
		{
			extraText.append(new TranslatableText("tweakermore.impl.shulkerTooltipHintBuilder.more", amount - idx).formatted(Formatting.GRAY));
		}

		return extraText;
	}
}
