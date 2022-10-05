package me.fallenbreath.tweakermore.mixins.tweaks.features.infoView.commandBlock;

import com.mojang.brigadier.ParseResults;
import net.minecraft.server.command.CommandSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

//#if MC >= 11600
//$$ import net.minecraft.text.OrderedText;
//#endif

//#if MC >= 11500
import net.minecraft.client.gui.screen.CommandSuggestor;
//#else
//$$ import net.minecraft.client.gui.screen.ChatScreen;
//#endif

/**
 * 1.14: public ChatScreen#getRenderText
 * 1.15: public CommandSuggestor#highlight
 * 1.16+: private CommandSuggestor#highlight
 */
@Mixin(
		//#if MC >= 11500
		CommandSuggestor.class
		//#else
		//$$ ChatScreen.class
		//#endif
)
public interface CommandSuggestorAccessor
{
	@Invoker(
			//#if MC < 11500
			//$$ "getRenderText"
			//#endif
	)
	static
	//#if MC >= 11600
	//$$ OrderedText
	//#else
	String
	//#endif
	invokeHighlight(ParseResults<CommandSource> parse, String original, int firstCharacterIndex)
	{
		throw new RuntimeException();
	}
}
