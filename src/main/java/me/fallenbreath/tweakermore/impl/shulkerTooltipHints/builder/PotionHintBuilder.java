package me.fallenbreath.tweakermore.impl.shulkerTooltipHints.builder;

import com.google.common.collect.Lists;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.LingeringPotionItem;
import net.minecraft.item.PotionItem;
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
			if (item instanceof PotionItem && PotionUtil.getPotionEffects(itemStack).size() > 0)
			{
				List<Text> potionTexts = Lists.newArrayList();
				PotionUtil.buildTooltip(itemStack, potionTexts, getPotionDurationRatio((PotionItem)item));

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
	 * Reference: Constants in {@link PotionItem#appendTooltip} and {@link LingeringPotionItem#appendTooltip}
	 */
	private static float getPotionDurationRatio(PotionItem item)
	{
		return item instanceof LingeringPotionItem ? 0.25F : 1.0F;
	}
}
