package me.fallenbreath.tweakermore.impl.shulkerTooltipEnchantmentHint;

import com.google.common.collect.Lists;
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
}
