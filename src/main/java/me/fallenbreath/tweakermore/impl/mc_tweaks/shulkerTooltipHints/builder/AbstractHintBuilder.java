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

package me.fallenbreath.tweakermore.impl.mc_tweaks.shulkerTooltipHints.builder;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.util.Messenger;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.text.BaseText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class AbstractHintBuilder
{
	@Nullable
	public abstract BaseText build(ItemStack itemStack);

	protected static BaseText getDivider()
	{
		return Messenger.s(" | ", Formatting.DARK_GRAY);
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
				extraText.append(Messenger.s(", ", Formatting.GRAY));
			}
		}
		if (idx < amount)
		{
			extraText.append(Messenger.formatting(Messenger.tr("tweakermore.impl.shulkerTooltipHintBuilder.more", amount - idx), Formatting.GRAY));
		}

		return extraText;
	}
}
