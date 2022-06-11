package me.fallenbreath.tweakermore.impl.features.tweakmAutoContainerProcess;

import com.google.common.collect.Lists;
import fi.dy.masa.malilib.config.HudAlignment;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.StringUtils;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.config.options.TweakerMoreConfigBooleanHotkeyed;
import me.fallenbreath.tweakermore.util.render.RenderContext;
import me.fallenbreath.tweakermore.util.render.TweakerMoreIRenderer;

import java.util.List;

public class AutoContainerProcessorHintRenderer implements TweakerMoreIRenderer
{
	@Override
	public void onRenderGameOverlayPost(RenderContext context)
	{
		if (!TweakerMoreConfigs.TWEAKM_CONTAINER_PROCESSOR_HINT.getBooleanValue())
		{
			return;
		}

		List<String> lines = Lists.newArrayList();
		ContainerProcessor.getProcessors().forEach(processor -> {
			if (processor.isEnabled())
			{
				lines.add(GuiBase.TXT_GRAY + processor.getConfig().getConfigGuiDisplayName());
			}
		});

		if (lines.isEmpty())
		{
			return;
		}
		lines.add(0, GuiBase.TXT_UNDERLINE + StringUtils.translate("tweakermore.config.tweakmContainerProcessorHint.title"));

		RenderUtils.renderText(
				0, 0,
				TweakerMoreConfigs.TWEAKM_CONTAINER_PROCESSOR_HINT_SCALE.getDoubleValue(),
				0xFFFFFFFF, 0x80000000,
				(HudAlignment)TweakerMoreConfigs.TWEAKM_CONTAINER_PROCESSOR_HINT_POS.getOptionListValue(),
				false, true,
				lines
				//#if MC >= 11600
				//$$ , context.getMatrixStack()
				//#endif
		);
	}

	public static String modifyComment(String comment)
	{
		StringBuilder builder = new StringBuilder(comment);
		for (Processor processor : ContainerProcessor.getProcessors())
		{
			TweakerMoreConfigBooleanHotkeyed config = processor.getConfig();
			String id = config.getName();
			String name = config.getConfigGuiDisplayName();
			builder.append("\n");
			builder.append(GuiBase.TXT_GRAY).append("- ");
			builder.append(GuiBase.TXT_RST).append(name);
			if (!id.equals(name))
			{
				builder.append(GuiBase.TXT_GRAY).append(" (").append(id).append(")").append(GuiBase.TXT_RST);
			}
		}
		return builder.toString();
	}
}
