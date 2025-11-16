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

package me.fallenbreath.tweakermore.mixins.core.gui.panel.dropDownListRedraw;

import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.widgets.WidgetListBase;
import fi.dy.masa.malilib.gui.widgets.WidgetListConfigOptions;
import fi.dy.masa.malilib.gui.widgets.WidgetListEntryBase;
import me.fallenbreath.tweakermore.gui.TweakerMoreConfigGui;
import me.fallenbreath.tweakermore.mixins.core.gui.access.WidgetListConfigOptionsAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//#if MC >= 12000
//$$ import net.minecraft.client.gui.GuiGraphics;
//#elseif MC >= 11600
//$$ import com.mojang.blaze3d.vertex.PoseStack;
//#endif

@Mixin(WidgetListBase.class)
public abstract class WidgetListBaseMixin<TYPE, WIDGET extends WidgetListEntryBase<TYPE>>
{
	// to make sure it only once gets rendered
	@Unique
	private boolean shouldRenderTweakerMoreConfigGuiDropDownList = false;

	@Inject(method = "drawContents", at = @At("HEAD"), remap = false)
	private void drawTweakerMoreConfigGuiDropDownListSetFlag(CallbackInfo ci)
	{
		shouldRenderTweakerMoreConfigGuiDropDownList = true;
	}

	//#if MC < 11904
	@Inject(
			method = "drawContents",
			at = @At(
					value = "INVOKE",
					//#if MC >= 11600
					//$$ target = "Lfi/dy/masa/malilib/gui/widgets/WidgetBase;postRenderHovered(IIZLnet/minecraft/client/util/math/MatrixStack;)V",
					//$$ remap = true
					//#else
					target = "Lfi/dy/masa/malilib/gui/widgets/WidgetBase;postRenderHovered(IIZ)V",
					remap = false
					//#endif
			),
			remap = false
	)
	private void drawTweakerMoreConfigGuiDropDownListAgainBeforeHover(
			//#if MC >= 11600
			//$$ PoseStack matrixStack,
			//#endif
			int mouseX, int mouseY, float partialTicks, CallbackInfo ci
	)
	{
		this.drawTweakerMoreConfigGuiDropDownListAgain(
				//#if MC >= 11600
				//$$ matrixStack,
				//#endif
				mouseX, mouseY
		);
	}
	//#endif

	@Inject(method = "drawContents", at = @At("TAIL"), remap = false)
	private void drawTweakerMoreConfigGuiDropDownListAgainAfterHover(
			//#if MC >= 12000
			//$$ GuiGraphics matrixStackOrDrawContext,
			//#elseif MC >= 11600
			//$$ PoseStack matrixStackOrDrawContext,
			//#endif
			int mouseX, int mouseY, float partialTicks, CallbackInfo ci
	)
	{
		this.drawTweakerMoreConfigGuiDropDownListAgain(
				//#if MC >= 11600
				//$$ matrixStackOrDrawContext,
				//#endif
				mouseX, mouseY
		);
	}

	@SuppressWarnings("ConstantConditions")
	@Unique
	private boolean isTweakerMoreConfigGui()
	{
		if ((WidgetListBase<?, ?>)(Object)this instanceof WidgetListConfigOptions)
		{
			GuiConfigsBase guiConfig = ((WidgetListConfigOptionsAccessor) this).getParent();
			return guiConfig instanceof TweakerMoreConfigGui;
		}
		return false;
	}

	@Unique
	private void drawTweakerMoreConfigGuiDropDownListAgain(
			//#if MC >= 12000
			//$$ GuiGraphics matrixStackOrDrawContext,
			//#elseif MC >= 11600
			//$$ PoseStack matrixStackOrDrawContext,
			//#endif
			int mouseX, int mouseY
	)
	{
		if (this.isTweakerMoreConfigGui() && this.shouldRenderTweakerMoreConfigGuiDropDownList)
		{
			GuiConfigsBase guiConfig = ((WidgetListConfigOptionsAccessor) this).getParent();

			// render it again to make sure it's on the top but below hovering widgets
			((TweakerMoreConfigGui)guiConfig).renderDropDownList(
					//#if MC >= 11600
					//$$ matrixStackOrDrawContext,
					//#endif
					mouseX, mouseY
			);
			this.shouldRenderTweakerMoreConfigGuiDropDownList = false;
		}
	}
}
