package me.fallenbreath.tweakermore.impl.mc_tweaks.shulkerTooltipHints;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.util.InventoryUtils;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.impl.mc_tweaks.shulkerTooltipHints.builder.PotionHintBuilder;
import me.fallenbreath.tweakermore.impl.mc_tweaks.shulkerTooltipHints.builder.EnchantmentHintBuilder;
import me.fallenbreath.tweakermore.impl.mc_tweaks.shulkerTooltipHints.builder.AbstractHintBuilder;
import me.fallenbreath.tweakermore.util.InventoryUtil;
import me.fallenbreath.tweakermore.util.Messenger;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Formatting;

import java.util.List;
import java.util.Objects;
import java.util.function.ToIntFunction;

//#if MC >= 11600
//$$ import net.minecraft.text.MutableText;
//#endif

public class ShulkerToolTipEnhancer
{
	private static final List<AbstractHintBuilder> HINT_BUILDERS = ImmutableList.of(
			new EnchantmentHintBuilder(),
			new PotionHintBuilder()
	);

	/**
	 * Content hints are for modifying the item contents display text of shulker box
	 * It's injected during the shulker box tooltip building
	 */
	public static void appendContentHints(
			ItemStack itemStack,
			//#if MC >= 11600
			//$$ MutableText text
			//#else
			Text text
			//#endif
	)
	{
		HINT_BUILDERS.stream().
				map(builder -> builder.build(itemStack)).
				filter(Objects::nonNull).
				forEach(text::append);
	}

	/**
	 * Fill level hint is applied at the end of the tool tip building, since it requires the complete tool tip text list
	 * It's injected at the end of general item tooltip building
	 */
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
			Text fillLevelText = Messenger.s(String.format("%.2f%%", 100 * ratio), color);

			// let fillLevelText be rendered right-aligned
			String spacing = " ";
			Text firstLine = Messenger.c(tooltip.get(0), spacing, fillLevelText);
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
				siblings.set(1, Messenger.s(spacing));
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
