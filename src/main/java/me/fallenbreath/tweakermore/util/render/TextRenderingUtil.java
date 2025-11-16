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

package me.fallenbreath.tweakermore.util.render;

import net.minecraft.client.Minecraft;

//#if MC >= 11600
//$$ import com.google.common.collect.Lists;
//$$ import me.fallenbreath.tweakermore.mixins.util.render.TextHandlerAccessor;
//$$ import net.minecraft.client.StringSplitter;
//$$ import net.minecraft.client.gui.Font;
//$$ import net.minecraft.util.StringDecomposer;
//$$ import net.minecraft.util.FormattedCharSequence;
//$$ import net.minecraft.network.chat.Style;
//$$ import org.apache.commons.lang3.mutable.MutableFloat;
//$$ import org.apache.commons.lang3.tuple.Triple;
//$$ import java.util.List;
//#endif

public class TextRenderingUtil
{
	//#if MC >= 11600
	//$$ public static FormattedCharSequence string2orderedText(String string)
	//$$ {
	//$$ 	return visitor -> StringDecomposer.visitFormatted(string, Style.EMPTY, visitor);
	//$$ }
	//$$
	//$$ public static String orderedText2string(FormattedCharSequence text)
	//$$ {
	//$$ 	StringBuilder builder = new StringBuilder();
	//$$ 	text.accept((index, style, codePoint) -> {
	//$$ 		builder.append((char)codePoint);
	//$$ 		return true;
	//$$ 	});
	//$$ 	return builder.toString();
	//$$ }
	//$$
	//$$ public static FormattedCharSequence trim(FormattedCharSequence text, int maxWidth, PostTrimModifier<FormattedCharSequence> postTrimModifier)
	//$$ {
	//$$ 	Font textRenderer = Minecraft.getInstance().textRenderer;
	//$$ 	StringSplitter.WidthRetriever widthRetriever = ((TextHandlerAccessor)textRenderer.getTextHandler()).getWidthRetriever();
	//$$
	//$$ 	List<Triple<Integer, Style, Integer>> elements = Lists.newArrayList();
	//$$ 	MutableFloat width = new MutableFloat(0);
	//$$ 	boolean hasTrimmed = text.accept((index, style, codePoint) -> {
	//$$ 		width.add(widthRetriever.getWidth(codePoint, style));
	//$$ 		boolean ok = width.getValue() <= maxWidth;
	//$$ 		if (ok)
	//$$ 		{
	//$$ 			elements.add(Triple.of(index, style, codePoint));
	//$$ 		}
	//$$ 		return ok;
	//$$ 	});
	//$$
	//$$ 	FormattedCharSequence trimmedText = visitor ->
	//$$ 	{
	//$$ 		for (Triple<Integer, Style, Integer> element : elements)
	//$$ 		{
	//$$ 			if (!visitor.accept(element.getLeft(), element.getMiddle(), element.getRight()))
	//$$ 			{
	//$$ 				return false;
	//$$ 			}
	//$$ 		}
	//$$ 		return true;
	//$$ 	};
	//$$ 	if (hasTrimmed)
	//$$ 	{
	//$$ 		trimmedText = postTrimModifier.modify(trimmedText);
	//$$ 	}
	//$$ 	return trimmedText;
	//$$ }
	//$$ public static FormattedCharSequence trim(FormattedCharSequence text, int maxWidth)
	//$$ {
	//$$ 	return trim(text, maxWidth, t -> t);
	//$$ }
	//#endif

	public static String trim(String text, int maxWidth, PostTrimModifier<String> postTrimModifier)
	{
		Minecraft mc = Minecraft.getInstance();
		String trimmedText = mc.font.substrByWidth(text, maxWidth);
		if (trimmedText.length() < text.length())
		{
			trimmedText = postTrimModifier.modify(trimmedText);
		}
		return trimmedText;
	}
	public static String trim(String text, int maxWidth)
	{
		return trim(text, maxWidth, t -> t);
	}

	@FunctionalInterface
	public interface PostTrimModifier<T>
	{
		T modify(T trimmedText);
	}
}
