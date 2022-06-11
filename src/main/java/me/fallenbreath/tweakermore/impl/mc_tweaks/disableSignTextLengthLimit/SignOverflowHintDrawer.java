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