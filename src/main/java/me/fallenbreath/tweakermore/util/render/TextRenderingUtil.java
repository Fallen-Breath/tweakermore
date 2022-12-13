package me.fallenbreath.tweakermore.util.render;

import net.minecraft.client.MinecraftClient;

//#if MC >= 11600
//$$ import com.google.common.collect.Lists;
//$$ import me.fallenbreath.tweakermore.mixins.util.render.TextHandlerAccessor;
//$$ import net.minecraft.client.font.TextHandler;
//$$ import net.minecraft.client.font.TextRenderer;
//$$ import net.minecraft.client.font.TextVisitFactory;
//$$ import net.minecraft.text.OrderedText;
//$$ import net.minecraft.text.Style;
//$$ import org.apache.commons.lang3.mutable.MutableFloat;
//$$ import org.apache.commons.lang3.tuple.Triple;
//$$ import java.util.List;
//#endif

public class TextRenderingUtil
{
	//#if MC >= 11600
	//$$ public static OrderedText string2orderedText(String string)
	//$$ {
	//$$ 	return visitor -> TextVisitFactory.visitFormatted(string, Style.EMPTY, visitor);
	//$$ }
	//$$
	//$$ public static String orderedText2string(OrderedText text)
	//$$ {
	//$$ 	StringBuilder builder = new StringBuilder();
	//$$ 	text.accept((index, style, codePoint) -> {
	//$$ 		builder.append((char)codePoint);
	//$$ 		return true;
	//$$ 	});
	//$$ 	return builder.toString();
	//$$ }
	//$$
	//$$ public static OrderedText trim(OrderedText text, int maxWidth, PostTrimModifier<OrderedText> postTrimModifier)
	//$$ {
	//$$ 	TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
	//$$ 	TextHandler.WidthRetriever widthRetriever = ((TextHandlerAccessor)textRenderer.getTextHandler()).getWidthRetriever();
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
	//$$ 	OrderedText trimmedText = visitor ->
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
	//$$ public static OrderedText trim(OrderedText text, int maxWidth)
	//$$ {
	//$$ 	return trim(text, maxWidth, t -> t);
	//$$ }
	//#endif

	public static String trim(String text, int maxWidth, PostTrimModifier<String> postTrimModifier)
	{
		MinecraftClient mc = MinecraftClient.getInstance();
		String trimmedText = mc.textRenderer.trimToWidth(text, maxWidth);
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
