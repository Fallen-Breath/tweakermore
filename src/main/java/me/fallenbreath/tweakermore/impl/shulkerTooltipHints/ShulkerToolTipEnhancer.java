package me.fallenbreath.tweakermore.impl.shulkerTooltipHints;

import com.google.common.collect.Lists;
import fi.dy.masa.malilib.util.InventoryUtils;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.collection.DefaultedList;

import java.util.List;

public class ShulkerToolTipEnhancer
{
	private static final int MAX_TEXT_LENGTH = 120;

	public static void appendEnchantmentHints(ItemStack itemStack, MutableText text)
	{
		if (TweakerMoreConfigs.SHULKER_TOOLTIP_ENCHANTMENT_HINT.getBooleanValue())
		{
			List<Text> enchantmentTexts = Lists.newArrayList();
			NbtList enchantmentTag = itemStack.getItem() instanceof EnchantedBookItem ? EnchantedBookItem.getEnchantmentNbt(itemStack) : itemStack.getEnchantments();
			ItemStack.appendEnchantments(enchantmentTexts, enchantmentTag);
			int amount = enchantmentTexts.size();
			if (amount > 0)
			{
				TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
				MutableText extraText = new LiteralText(" | ").formatted(Formatting.DARK_GRAY);
				int idx;
				for (idx = 0; idx < amount; idx++)
				{
					if (idx > 0 && textRenderer.getWidth(extraText.getString() + enchantmentTexts.get(idx).getString()) > MAX_TEXT_LENGTH)
					{
						break;
					}
					extraText.append(enchantmentTexts.get(idx));
					if (idx < amount - 1)
					{
						extraText.append(new LiteralText(", ").formatted(Formatting.GRAY));
					}
				}
				if (idx < amount)
				{
					extraText.append(new TranslatableText("tweakermore.config.shulkerTooltipEnchantmentHint.more", amount - idx).formatted(Formatting.GRAY));
				}
				text.append(extraText);
			}
		}
	}

	public static void applyFillLevelHint(ItemStack skulker, List<Text> tooltip)
	{
		if (TweakerMoreConfigs.SHULKER_TOOLTIP_FILL_LEVEL_HINT.getBooleanValue() && tooltip.size() > 0)
		{
			DefaultedList<ItemStack> stackList = InventoryUtils.getStoredItems(skulker, -1);
			if (stackList.isEmpty())
			{
				return;
			}

			int total = 0, maximum = 0;
			for (ItemStack stack : stackList)
			{
				total += stack.getCount();
				maximum += stack.getMaxCount();
			}

			double ratio = 1.0D * total / maximum;
			Formatting color = ratio >= 1.0D ? Formatting.DARK_GREEN : Formatting.GRAY;
			Text fillLevelText = new LiteralText(String.format("%.2f%%", 100 * ratio)).formatted(color);

			// let fillLevelText be rendered right-aligned
			String spacing = " ";
			Text firstLine = new LiteralText("").append(tooltip.get(0)).append(spacing).append(fillLevelText);
			TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
			int maxWidth = tooltip.stream().
					mapToInt(textRenderer::getWidth).
					max().
					orElse(0);

			while (true)
			{
				List<Text> siblings = firstLine.getSiblings();
				spacing += " ";
				Text prevSpacing = siblings.get(1);
				siblings.set(1, new LiteralText(spacing));
				if (textRenderer.getWidth(firstLine) > maxWidth)
				{
					siblings.set(1, prevSpacing);  // rollback
					break;
				}
			}
			tooltip.set(0, firstLine);
		}
	}
}
