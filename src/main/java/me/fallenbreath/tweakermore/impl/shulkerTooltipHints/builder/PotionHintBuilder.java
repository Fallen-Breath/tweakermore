package me.fallenbreath.tweakermore.impl.shulkerTooltipHints.builder;

import com.google.common.collect.Lists;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.item.*;
import net.minecraft.potion.PotionUtil;
import net.minecraft.text.BaseText;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PotionHintBuilder extends AbstractHintBuilder
{
	@Override
	@Nullable
	public BaseText build(ItemStack itemStack)
	{
		if (TweakerMoreConfigs.SHULKER_TOOLTIP_POTION_INFO_HINT.getBooleanValue())
		{
			Item item = itemStack.getItem();
			float ratio = getPotionDurationRatio(item);
			if (ratio > 0 && PotionUtil.getPotionEffects(itemStack).size() > 0)
			{
				List<Text> potionTexts = Lists.newArrayList();
				PotionUtil.buildTooltip(itemStack, potionTexts, ratio);

				int i = 0;
				BaseText newLine = new LiteralText("");
				for (; i < potionTexts.size(); i++)
				{
					// we don't want the "potion.whenDrank" section
					if (potionTexts.get(i).equals(newLine))
					{
						break;
					}
				}

				return buildSegments(potionTexts.subList(0, i));
			}
		}
		return null;
	}

	/**
	 * Reference: Constants in
	 * - {@link PotionItem#appendTooltip}
	 * - {@link LingeringPotionItem#appendTooltip}
	 * - {@link TippedArrowItem#appendTooltip}
	 */
	private static float getPotionDurationRatio(Item item)
	{
		if (item instanceof LingeringPotionItem)
		{
			return 0.25F;
		}
		if (item instanceof TippedArrowItem)
		{
			return 0.125F;
		}
		if (item instanceof PotionItem)
		{
			return 1.0F;
		}
		// unknown
		return -1;
	}
}
