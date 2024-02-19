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

package me.fallenbreath.tweakermore.mixins.core.gui.element;

import fi.dy.masa.malilib.gui.widgets.WidgetBase;
import fi.dy.masa.malilib.gui.widgets.WidgetLabel;
import me.fallenbreath.tweakermore.gui.TweakerMoreOptionLabel;
import me.fallenbreath.tweakermore.util.render.context.RenderContext;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

//#if MC >= 12000
//$$ import net.minecraft.client.gui.DrawContext;
//#endif

//#if MC >= 11600
//$$ import net.minecraft.client.util.math.MatrixStack;
//#endif

//#if MC < 11500
//$$ import com.mojang.blaze3d.platform.GlStateManager;
//#endif

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

	@Unique
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
			double scale = TweakerMoreOptionLabel.getConfigOriginalNameScale();
			if (scale > 0)
			{
				double deltaK = -0.1 / 0.35 * scale + 0.1 / 0.35 + 0.5;
				yTextStart -= (int)(this.fontHeight * scale * deltaK);
			}
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
			//#if MC >= 12000
			//$$ DrawContext matrixStackOrDrawContext,
			//#elseif MC >= 11600
			//$$ MatrixStack matrixStackOrDrawContext,
			//#endif
			CallbackInfo ci,
			int fontHeight, int yCenter, int yTextStart, int i, String text
	)
	{
		double scale = TweakerMoreOptionLabel.getConfigOriginalNameScale();
		if (this.shouldUseTranslatedOptionLabelLogic() && scale > 0)
		{
			int color = darkerColor(this.textColor);
			String originText = ((TweakerMoreOptionLabel)(Object)this).getOriginalLines()[i];
			double x = this.x + (this.centered ? this.width / 2.0 : 0);
			double y = (int)(yTextStart + (this.labels.size() + i * scale + 0.2) * fontHeight);

			RenderContext renderContext = RenderContext.of(
					//#if MC >= 11600
					//$$ matrixStackOrDrawContext
					//#endif
			);

			renderContext.pushMatrix();
			renderContext.scale(scale, scale, 1);

			x /= scale;
			y /= scale;

			if (this.centered)
			{
				this.drawCenteredStringWithShadow(
						(int)x, (int)y, color, originText
						//#if MC >= 11600
						//$$ , matrixStackOrDrawContext
						//#endif
				);
			}
			else
			{
				this.drawStringWithShadow(
						(int)x, (int)y, color, originText
						//#if MC >= 11600
						//$$ , matrixStackOrDrawContext
						//#endif
				);
			}

			renderContext.popMatrix();
		}
	}

	@SuppressWarnings("PointlessBitwiseExpression")
	@Unique
	private static int darkerColor(int color)
	{
		int a = (color >> 24) & 0xFF;
		int r = (color >> 16) & 0xFF;
		int g = (color >> 8) & 0xFF;
		int b = (color >> 0) & 0xFF;
		r = (int)(r * 0.6);
		g = (int)(g * 0.6);
		b = (int)(b * 0.6);
		return (a << 24) | (r << 16) | (g << 8) | (b << 0);
	}
}
