package me.fallenbreath.tweakermore.impl.shulkerTooltipHints;

import com.google.common.collect.Lists;
import fi.dy.masa.malilib.util.InventoryUtils;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.util.InventoryUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.ListTag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Formatting;

//#if MC >= 11600
//$$ import net.minecraft.text.MutableText;
//#endif

import java.util.List;
import java.util.function.ToIntFunction;

public class ShulkerToolTipEnhancer
{
	private static final int MAX_TEXT_LENGTH = 120;

	public static void appendEnchantmentHints(
			ItemStack itemStack,
			//#if MC >= 11600
			//$$ MutableText text
			//#else
			Text text
			//#endif
	)
	{
		if (TweakerMoreConfigs.SHULKER_TOOLTIP_ENCHANTMENT_HINT.getBooleanValue())
		{
			List<Text> enchantmentTexts = Lists.newArrayList();
			ListTag enchantmentTag = itemStack.getItem() instanceof EnchantedBookItem ? EnchantedBookItem.getEnchantmentTag(itemStack) : itemStack.getEnchantments();
			ItemStack.appendEnchantments(enchantmentTexts, enchantmentTag);
			int amount = enchantmentTexts.size();
			if (amount > 0)
			{
				//#if MC >= 11600
				//$$ MutableText extraText
				//#else
				Text extraText
				//#endif
						= new LiteralText(" | ").formatted(Formatting.DARK_GRAY);

				TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
				int idx;
				for (idx = 0; idx < amount; idx++)
				{
					if (idx > 0 && textRenderer.getStringWidth(extraText.getString() + enchantmentTexts.get(idx).getString()) > MAX_TEXT_LENGTH)
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
			int slotAmount = InventoryUtil.getInventorySlotAmount(skulker);
			if (slotAmount == -1)
			{
				return;
			}

			DefaultedList<ItemStack> stackList = InventoryUtils.getStoredItems(skulker, slotAmount);
			if (stackList.isEmpty())
			{
				return;
			}

			double sum = 0;
			for (ItemStack stack : stackList)
			{
				sum += 1.0D * stack.getCount() / stack.getMaxCount();
			}

			double ratio = sum / slotAmount;
			Formatting color = ratio >= 1.0D ? Formatting.DARK_GREEN : Formatting.GRAY;
			Text fillLevelText = new LiteralText(String.format("%.2f%%", 100 * ratio)).formatted(color);

			// let fillLevelText be rendered right-aligned
			String spacing = " ";
			Text firstLine = new LiteralText("").append(tooltip.get(0)).append(spacing).append(fillLevelText);
			TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

			ToIntFunction<Text> textLenCalc = text ->
					//#if MC >= 11600
					//$$ textRenderer.getWidth(text);
					//#else
					textRenderer.getStringWidth(text.asFormattedString());
					//#endif

			int maxWidth = tooltip.stream().mapToInt(textLenCalc).max().orElse(0);

			while (true)
			{
				List<Text> siblings = firstLine.getSiblings();
				spacing += " ";
				Text prevSpacing = siblings.get(1);
				siblings.set(1, new LiteralText(spacing));
				if (textLenCalc.applyAsInt(firstLine) > maxWidth)
				{
					siblings.set(1, prevSpacing);  // rollback
					break;
				}
			}
			tooltip.set(0, firstLine);
		}
	}
}
