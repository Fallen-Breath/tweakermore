package me.fallenbreath.tweakermore.mixins.core.gui;

import fi.dy.masa.malilib.gui.GuiTextFieldGeneric;
import fi.dy.masa.malilib.gui.widgets.WidgetSearchBar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(WidgetSearchBar.class)
public interface WidgetSearchBarAccessor
{
	@Accessor(remap = false)
	GuiTextFieldGeneric getSearchBox();
}
