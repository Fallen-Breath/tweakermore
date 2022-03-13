package me.fallenbreath.tweakermore.mixins.tweaks.tweakmAutoCollectMaterialListItem;

import fi.dy.masa.litematica.materials.MaterialListHudRenderer;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.util.ModIds;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Restriction(require = @Condition(ModIds.litematica))
@Mixin(MaterialListHudRenderer.class)
public interface MaterialListHudRendererAccessor
{
	@Accessor(remap = false)
	void setLastUpdateTime(long value);

	@Invoker(remap = false)
	String invokeGetFormattedCountString(int count, int maxStackSize);
}
