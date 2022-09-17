package me.fallenbreath.tweakermore.mixins.util.render;

import net.minecraft.client.font.TextHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TextHandler.class)
public interface TextHandlerAccessor
{
	@Accessor
	TextHandler.WidthRetriever getWidthRetriever();
}
