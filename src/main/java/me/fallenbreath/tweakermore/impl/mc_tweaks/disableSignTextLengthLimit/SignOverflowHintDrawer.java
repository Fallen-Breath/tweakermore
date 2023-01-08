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

package me.fallenbreath.tweakermore.impl.mc_tweaks.disableSignTextLengthLimit;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.Texts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class SignOverflowHintDrawer
{
	/**
	 * Only used in mc1.14.4
	 */
	public static void drawLineOverflowHint(SignBlockEntity signBlockEntity, TextRenderer textRenderer, int lineIdx, String lineContent)
	{
		//#if MC < 11500
		//$$ if (TweakerMoreConfigs.DISABLE_SIGN_TEXT_LENGTH_LIMIT.getBooleanValue())
		//$$ {
		//$$ 	// make sure it's rendered during SignEditScreen rendering
		//$$ 	if (signBlockEntity.getCurrentRow() == -1)
		//$$ 	{
		//$$ 		return;
		//$$ 	}
  //$$
		//$$ 	Text[] texts = signBlockEntity.text;
		//$$ 	if (0 <= lineIdx && lineIdx < texts.length)
		//$$ 	{
		//$$ 		boolean overflowed = Texts.wrapLines(texts[lineIdx], 90, textRenderer, false, true).size() > 1;
		//$$ 		if (overflowed)
		//$$ 		{
		//$$ 			assert Formatting.RED.getColorValue() != null;
		//$$ 			float xStart = (float)(-textRenderer.getStringWidth(lineContent) / 2);
		//$$ 			textRenderer.draw("!", xStart - 10, lineIdx * 10 - texts.length * 5, Formatting.RED.getColorValue());
		//$$ 	}
		//$$ 	}
		//$$ }
		//#endif
	}
}