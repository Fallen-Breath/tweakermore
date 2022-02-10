package me.fallenbreath.tweakermore.mixins.core;

import fi.dy.masa.malilib.gui.widgets.WidgetListConfigOptions;
import me.fallenbreath.tweakermore.util.StringUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(WidgetListConfigOptions.class)
public abstract class WidgetListConfigOptionsMixin
{
	@ModifyArg(
			method = "getMaxNameLengthWrapped",
			at = @At(
					value = "INVOKE",
					target = "Lfi/dy/masa/malilib/gui/widgets/WidgetListConfigOptions;getStringWidth(Ljava/lang/String;)I"
			),
			index = 0,
			remap = false
	)
	private String removeTweakerMorePrefix_readConfigBase_has(String name)
	{
		return StringUtil.removeTweakerMoreNameSpacePrefix(name);
	}
}
