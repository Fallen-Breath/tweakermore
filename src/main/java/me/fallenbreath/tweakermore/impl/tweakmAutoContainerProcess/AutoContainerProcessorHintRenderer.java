package me.fallenbreath.tweakermore.impl.tweakmAutoContainerProcess;

import com.google.common.collect.Lists;
import fi.dy.masa.malilib.config.HudAlignment;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.interfaces.IRenderer;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.StringUtils;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;

import java.util.List;

//#if MC >= 11600
//$$ import net.minecraft.client.util.math.MatrixStack;
//#endif

public class AutoContainerProcessorHintRenderer implements IRenderer
{
	@Override
	public void onRenderGameOverlayPost(
			//#if MC >= 11700
			//$$ MatrixStack matrixStack
			//#elseif MC >= 11600
			//$$ float partialTicks, MatrixStack matrixStack
			//#else
			float partialTicks
			//#endif
	)
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
				//$$ , matrixStack
				//#endif
		);
	}
}
