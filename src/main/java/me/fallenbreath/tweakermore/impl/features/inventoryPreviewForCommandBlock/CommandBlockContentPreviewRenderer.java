package me.fallenbreath.tweakermore.impl.features.inventoryPreviewForCommandBlock;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import me.fallenbreath.tweakermore.mixins.tweaks.features.inventoryPreviewForCommandBlock.CommandSuggestorAccessor;
import me.fallenbreath.tweakermore.util.WorldUtil;
import me.fallenbreath.tweakermore.util.render.RenderContext;
import me.fallenbreath.tweakermore.util.render.TextRenderer;
import me.fallenbreath.tweakermore.util.render.TweakerMoreIRenderer;
import net.minecraft.block.BlockState;
import net.minecraft.block.CommandBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.server.command.CommandSource;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

//#if MC >= 11600
//$$ import me.fallenbreath.tweakermore.util.render.TextUtil;
//$$ import net.minecraft.text.OrderedText;
//#endif

public class CommandBlockContentPreviewRenderer implements TweakerMoreIRenderer
{
	public static final int MAXIMUM_PREVIEW_WIDTH = 100;
	private static Runnable renderTask = null;

	@Override
	public void onRenderWorldLast(RenderContext context)
	{
		if (renderTask != null)
		{
			renderTask.run();
			renderTask = null;
		}
	}

	public static void showPreview(World world, BlockPos blockPos)
	{
		BlockState blockState = world.getBlockState(blockPos);
		BlockEntity blockEntity = WorldUtil.getBlockEntity(world, blockPos);
		if (blockState.getBlock() instanceof CommandBlock && blockEntity instanceof CommandBlockBlockEntity)
		{
			String command = ((CommandBlockBlockEntity)blockEntity).getCommandExecutor().getCommand();

			// parse command for highlight logic
			ClientPlayerEntity player = MinecraftClient.getInstance().player;
			if (player == null)
			{
				return;
			}
			StringReader stringReader = new StringReader(command);
			if (stringReader.canRead() && stringReader.peek() == '/')
			{
				stringReader.read();
			}
			ParseResults<CommandSource> parse = player.networkHandler.getCommandDispatcher().parse(
					stringReader, player.networkHandler.getCommandSource()
			);

			// trim command
			String trimmedCommand = trim(command);
			//#if MC >= 11600
			//$$ OrderedText
			//#else
			String
			//#endif
					displayText = CommandSuggestorAccessor.invokeHighlight(parse, trimmedCommand, 0);
			if (trimmedCommand.length() < command.length())
			{
				//#if MC >= 11600
				//$$ displayText = OrderedText.concat(displayText, TextUtil.orderedText(Formatting.DARK_GRAY + "..."));
				//#else
				displayText += Formatting.DARK_GRAY + "...";
				//#endif
			}

			// render
			TextRenderer textRenderer = TextRenderer.create().
					text(displayText).atCenter(blockPos).
					fontSize(0.025F).bgColor(0x3F000000).
					shadow().seeThrough();
			renderTask = textRenderer::render;
		}
	}

	private static String trim(String command)
	{
		MinecraftClient mc = MinecraftClient.getInstance();
		return mc.textRenderer.trimToWidth(command, MAXIMUM_PREVIEW_WIDTH);
	}
}
