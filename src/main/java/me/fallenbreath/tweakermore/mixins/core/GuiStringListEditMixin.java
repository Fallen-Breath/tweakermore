package me.fallenbreath.tweakermore.mixins.core;

import fi.dy.masa.malilib.gui.GuiStringListEdit;
import me.fallenbreath.tweakermore.util.StringUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(GuiStringListEdit.class)
public abstract class GuiStringListEditMixin
{
	@ModifyArg(
			method = "<init>",
			at = @At(
					value = "INVOKE",
					target = "Lfi/dy/masa/malilib/util/StringUtils;translate(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;"
			),
			index = 1,
			remap = false
	)
	private Object[] removeTweakerMorePrefix(Object[] args)
	{
		if (args.length > 0 && args[0] instanceof String)
		{
			args[0] = StringUtil.removeTweakerMoreNameSpacePrefix((String)args[0]);
		}
		return args;
	}
}
