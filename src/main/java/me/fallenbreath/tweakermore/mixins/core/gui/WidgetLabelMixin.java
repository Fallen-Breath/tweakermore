package me.fallenbreath.tweakermore.mixins.core.gui;

//#if MC >= 11500
//#else
//$$ import com.mojang.blaze3d.platform.GlStateManager;
//#endif

//#if MC >= 11600
//$$ import net.minecraft.client.util.math.MatrixStack;
//#endif

import fi.dy.masa.malilib.gui.widgets.WidgetBase;
import fi.dy.masa.malilib.gui.widgets.WidgetLabel;
import me.fallenbreath.tweakermore.gui.TweakerMoreOptionLabel;
import me.fallenbreath.tweakermore.util.render.RenderContext;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(WidgetLabel.class)
public abstract class WidgetLabelMixin extends WidgetBase
{
	@Shadow(remap = false) protected boolean centered;

	@Shadow(remap = false) @Final protected int textColor;

	@Shadow(remap = false) @Final protected List<String> labels;

	public WidgetLabelMixin(int x, int y, int width, int height)
	{
		super(x, y, width, height);
	}

	private boolean shouldUseTranslatedOptionLabelLogic()
	{
		WidgetLabel self = (WidgetLabel)(Object)this;
		return self instanceof TweakerMoreOptionLabel && ((TweakerMoreOptionLabel)self).shouldShowOriginalLines();
	}

	@ModifyVariable(
			method = "render",
			at = @At(
					value = "CONSTANT",
					args = "intValue=0",
					remap = false
			),
			remap = false,
			ordinal = 4
	)
	private int translatedOptionLabelShiftyTextStart(int yTextStart)
	{
		if (this.shouldUseTranslatedOptionLabelLogic())
		{
			yTextStart -= this.fontHeight * TweakerMoreOptionLabel.TRANSLATION_SCALE * 0.6;
		}
		return yTextStart;
	}

	@SuppressWarnings({
			"ConstantConditions",
			//#if MC >= 11600
			//$$ "deprecation",
			//#endif
	})
	@Inject(
			method = "render",
			at = @At(
					value = "FIELD",
					target = "Lfi/dy/masa/malilib/gui/widgets/WidgetLabel;centered:Z",
					remap = false
			),
			remap = false,
			locals = LocalCapture.CAPTURE_FAILHARD
	)
	private void translatedOptionLabelRenderTranslation(
			int mouseX, int mouseY, boolean selected,
			//#if MC >= 11600
			//$$ MatrixStack matrixStackd,
			//#endif
			CallbackInfo ci,
			int fontHeight, int yCenter, int yTextStart, int i, String text
	)
	{
		if (this.shouldUseTranslatedOptionLabelLogic())
		{
			int color = darkerColor(this.textColor);
			double scale = TweakerMoreOptionLabel.TRANSLATION_SCALE;
			String originText = ((TweakerMoreOptionLabel)(Object)this).getOriginalLines()[i];
			int x = this.x + (this.centered ? this.width / 2 : 0);
			int y = (int)(yTextStart + (this.labels.size() + i * scale + 0.2) * fontHeight);

			RenderContext renderContext = new RenderContext(
					//#if MC >= 11600
					//$$ matrixStackd
					//#endif
			);

			renderContext.pushMatrix();
			renderContext.scale(scale, scale, 1);

			x /= scale;
			y /= scale;

			if (this.centered)
			{
				this.drawCenteredStringWithShadow(
						x, y, color, originText
						//#if MC >= 11600
						//$$ , matrixStackd
						//#endif
				);
			}
			else
			{
				this.drawStringWithShadow(
						x, y, color, originText
						//#if MC >= 11600
						//$$ , matrixStackd
						//#endif
				);
			}

			renderContext.popMatrix();
		}
	}

	@SuppressWarnings("PointlessBitwiseExpression")
	private static int darkerColor(int color)
	{
		int a = (color >> 24) & 0xFF;
		int r = (color >> 16) & 0xFF;
		int g = (color >> 8) & 0xFF;
		int b = (color >> 0) & 0xFF;
		r *= 0.6;
		g *= 0.6;
		b *= 0.6;
		return (a << 24) | (r << 16) | (g << 8) | (b << 0);
	}
}
