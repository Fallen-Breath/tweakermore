package me.fallenbreath.tweakermore.mixins.tweaks.features.tweakmShowRedstoneDustUpdateOrder;

import fi.dy.masa.malilib.event.RenderEventHandler;
import me.fallenbreath.tweakermore.impl.showRedstoneDustUpdateOrder.RedstoneDustUpdateOrderRenderer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(RenderEventHandler.class)
public abstract class RenderEventHandlerMixin
{
	/*
	 * We're registering RedstoneDustUpdateOrderRenderer here to make sure it's the first in the list
	 * cuz in 1.14.4, litematica's WorldLastRenderer messes up the color of the rendered (they become gray)
	 */
	static
	{
		RenderEventHandler.getInstance().registerWorldLastRenderer(new RedstoneDustUpdateOrderRenderer());
	}
}
