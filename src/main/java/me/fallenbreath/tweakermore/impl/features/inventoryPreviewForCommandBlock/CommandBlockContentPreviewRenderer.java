package me.fallenbreath.tweakermore.impl.features.inventoryPreviewForCommandBlock;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.mixins.tweaks.features.inventoryPreviewForCommandBlock.CommandSuggestorAccessor;
import me.fallenbreath.tweakermore.util.WorldUtil;
import me.fallenbreath.tweakermore.util.render.RenderContext;
import me.fallenbreath.tweakermore.util.render.TextRenderer;
import me.fallenbreath.tweakermore.util.render.TextRenderingUtil;
import me.fallenbreath.tweakermore.util.render.TweakerMoreIRenderer;
import net.minecraft.block.BlockState;
import net.minecraft.block.CommandBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.server.command.CommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.CommandBlockExecutor;
import net.minecraft.world.World;

//#if MC >= 11600
//$$ import me.fallenbreath.tweakermore.util.render.TextRenderingUtil;
//$$ import net.minecraft.text.OrderedText;
//#endif

public class CommandBlockContentPreviewRenderer implements TweakerMoreIRenderer
{
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
			CommandBlockExecutor executor = ((CommandBlockBlockEntity) blockEntity).getCommandExecutor();
			String command = executor.getCommand();
			Text lastOutput = executor.getLastOutput();
			final int MAX_WIDTH = TweakerMoreConfigs.INVENTORY_PREVIEW_FOR_COMMAND_BLOCK_MAX_WIDTH.getIntegerValue();

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
			String trimmedCommand = TextRenderingUtil.trim(command, MAX_WIDTH);
			//#if MC >= 11600
			//$$ OrderedText
			//#else
			String
			//#endif
					displayText = CommandSuggestorAccessor.invokeHighlight(parse, trimmedCommand, 0);
			if (trimmedCommand.length() < command.length())
			{
				//#if MC >= 11600
				//$$ displayText = OrderedText.concat(displayText, TextRenderingUtil.string2orderedText(Formatting.DARK_GRAY + "..."));
				//#else
				displayText += Formatting.DARK_GRAY + "...";
				//#endif
			}

			// render
			TextRenderer textRenderer = TextRenderer.create().
					text(displayText).atCenter(blockPos).
					fontSize(0.025 * TweakerMoreConfigs.INVENTORY_PREVIEW_FOR_COMMAND_BLOCK_TEXT_SCALE.getDoubleValue()).
					bgColor(0x3F000000).
					shadow().seeThrough();
			if (!lastOutput.getString().isEmpty())
			{
				//#if MC >= 11600
				//$$ OrderedText trimmedLastOutput = TextRenderingUtil.trim(
				//$$ 		lastOutput.asOrderedText(),
				//$$ 		MAX_WIDTH,
				//$$ 		trimmedText -> OrderedText.concat(trimmedText, TextRenderingUtil.string2orderedText(Formatting.DARK_GRAY + "..."))
				//$$ );
				//#else
				String trimmedLastOutput = TextRenderingUtil.trim(
						lastOutput.asFormattedString(),
						MAX_WIDTH,
						trimmedText -> trimmedText + Formatting.DARK_GRAY + "..."
				);
				//#endif
				textRenderer.addLine(trimmedLastOutput);
			}
			renderTask = textRenderer::render;
		}
	}
}
