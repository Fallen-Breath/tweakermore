package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.fixHoverTextScale;

import me.fallenbreath.tweakermore.impl.mc_tweaks.fixHoverTextScale.ScaleableHoverTextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

//#if MC >= 11903
//$$ import net.minecraft.client.gui.tooltip.TooltipComponent;
//$$ import net.minecraft.client.gui.tooltip.TooltipPositioner;
//$$ import net.minecraft.client.util.math.MatrixStack;
//$$ import org.joml.Vector2i;
//$$ import org.spongepowered.asm.mixin.injection.ModifyVariable;
//#else
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.fallenbreath.tweakermore.impl.mc_tweaks.fixHoverTextScale.RenderTooltipArgs;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
//#endif

//#if MC >= 11600
//$$ import net.minecraft.client.util.math.MatrixStack;
//#endif

@Mixin(Screen.class)
public abstract class ScreenMixin implements ScaleableHoverTextRenderer
{
	@Shadow public int width;
	@Shadow public int height;
	private Double hoverTextScale$TKM = null;

	@Override
	public void setHoverTextScale(@Nullable Double scale)
	{
		if (scale != null)
		{
			this.hoverTextScale$TKM = MathHelper.clamp(scale, 0.01, 1);
		}
		else
		{
			this.hoverTextScale$TKM = null;
		}
	}

	@Inject(
			//#if MC >= 11600
			//$$ method = "renderTextHoverEffect",
			//#else
			method = "renderComponentHoverEffect",
			//#endif
			at = @At("TAIL")
	)
	private void fixHoverTextScale_cleanup(CallbackInfo ci)
	{
		this.hoverTextScale$TKM = null;
	}

	@ModifyArg(
			//#if MC >= 11600
			//$$ method = "renderTextHoverEffect",
			//#else
			method = "renderComponentHoverEffect",
			//#endif
			at = @At(
					value = "INVOKE",
					target = "Ljava/lang/Math;max(II)I"
			),
			index = 0
	)
	private int fixHoverTextScale_modifyEquivalentMaxScreenWidth(int width)
	{
		if (this.hoverTextScale$TKM != null)
		{
			width /= this.hoverTextScale$TKM;
		}
		return width;
	}

	/*
		// vanilla
		if (x + maxWidth > this.width)
		 {
			x -= 28 + maxWidth;
		}
		if (y + totalHeight + 6 > this.height)
		{
			y = this.height - totalHeight - 6;
		}

		// what we want
		if (xBase + maxWidth * scale > this.width)
		{
			xBase -= 28 + maxWidth;
		}
		if (yBase + totalHeight * scale + 6 > this.height)
		{
			yBase += (this.height - yBase - 12 - 1) / scale - totalHeight + 6 + 1
		}
	 */

	//#if MC >= 11903
	//$$ @ModifyVariable(method = "renderTooltipFromComponents", at = @At("HEAD"), argsOnly = true)
	//$$ private TooltipPositioner fixHoverTextScale_modifyPositioner(
	//$$ 		TooltipPositioner positioner,
	//$$ 		/* parent method parameters vvv */
	//$$ 		MatrixStack matrices, List<TooltipComponent> components, int x, int y, TooltipPositioner positioner_
	//$$ )
	//$$ {
	//$$ 	if (this.hoverTextScale$TKM != null)
	//$$ 	{
	//$$ 		double scale = this.hoverTextScale$TKM;
	//$$ 		positioner = (screen, xBase, yBase, width, height) -> {
	//$$ 			if (xBase + width * scale > screen.width)
	//$$ 			{
	//$$ 				xBase = Math.max(xBase - 24 - width, 4);
	//$$ 			}
	//$$ 			if (yBase + height * scale + 6 > screen.height)
	//$$ 			{
	//$$ 				yBase += (screen.height - yBase - 12 - 1) / scale - height + 6;
	//$$ 			}
	//$$ 			return new Vector2i(xBase, yBase);
	//$$ 		};
	//$$ 	}
	//$$ 	return positioner;
	//$$ }
	//$$
	//#else

	private RenderTooltipArgs renderTooltipArgs$TKM = null;

	@Inject(
			//#if MC >= 11700
			//$$ method = "renderTooltipFromComponents",
			//#elseif MC >= 11600
			//$$ method = "renderOrderedTooltip",
			//#else
			method = "renderTooltip(Ljava/util/List;II)V",
			//#endif
			at = @At(
					value = "FIELD",
					target = "Lnet/minecraft/client/gui/screen/Screen;width:I",
					shift = At.Shift.BEFORE
			),
			locals = LocalCapture.CAPTURE_FAILHARD
	)
	private void fixHoverTextScale_storeLocals(
			//#if MC >= 11600
			//$$ MatrixStack matrices,
			//#endif
			List<?> text, int x, int y,
			CallbackInfo ci,
			int maxWidth, int xBase, int yBase, int j, int totalHeight
	)
	{
		this.renderTooltipArgs$TKM = new RenderTooltipArgs(xBase, yBase, maxWidth, totalHeight);
	}

	@ModifyExpressionValue(
			//#if MC >= 11700
			//$$ method = "renderTooltipFromComponents",
			//#elseif MC >= 11600
			//$$ method = "renderOrderedTooltip",
			//#else
			method = "renderTooltip(Ljava/util/List;II)V",
			//#endif
			at = @At(
					value = "FIELD",
					target = "Lnet/minecraft/client/gui/screen/Screen;width:I",
					ordinal = 0
			)
	)
	private int fixHoverTextScale_tweakWidthAdjust(int width)
	{
		if (this.hoverTextScale$TKM != null)
		{
			RenderTooltipArgs args = this.renderTooltipArgs$TKM;
			boolean shouldAdjust = args.xBase + args.maxWidth * this.hoverTextScale$TKM > this.width;

			return shouldAdjust ? Integer.MIN_VALUE : Integer.MAX_VALUE;
		}
		return width;
	}

	@ModifyExpressionValue(
			//#if MC >= 11700
			//$$ method = "renderTooltipFromComponents",
			//#elseif MC >= 11600
			//$$ method = "renderOrderedTooltip",
			//#else
			method = "renderTooltip(Ljava/util/List;II)V",
			//#endif
			at = @At(
					value = "FIELD",
					target = "Lnet/minecraft/client/gui/screen/Screen;height:I",
					ordinal = 0
			)
	)
	private int fixHoverTextScale_cancelVanillaHeightAdjust(int height)
	{
		if (this.hoverTextScale$TKM != null)
		{
			height = Integer.MAX_VALUE;
		}
		return height;
	}

	@ModifyVariable(
			//#if MC >= 11700
			//$$ method = "renderTooltipFromComponents",
			//#elseif MC >= 11600
			//$$ method = "renderOrderedTooltip",
			//#else
			method = "renderTooltip(Ljava/util/List;II)V",
			//#endif
			at = @At(
					value = "FIELD",
					target = "Lnet/minecraft/client/gui/screen/Screen;height:I",
					ordinal = 0
			),
			ordinal = 4
	)
	private int fixHoverTextScale_applyHeightAdjust(int yBase)
	{
		if (this.hoverTextScale$TKM != null)
		{
			int totalHeight = this.renderTooltipArgs$TKM.totalHeight;
			double scale = this.hoverTextScale$TKM;

			if (yBase + totalHeight * scale + 6 > this.height)
			{
				yBase += (this.height - yBase - 12 - 1) / scale - totalHeight + 6 + 1;
			}
		}
		return yBase;
	}

	//#endif
}
