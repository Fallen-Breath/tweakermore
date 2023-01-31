package me.fallenbreath.tweakermore.mixins.tweaks.porting.lmCustomSchematicBaseDirectoryPorting;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.util.ModIds;
import me.fallenbreath.tweakermore.util.mixin.DummyClass;
import org.spongepowered.asm.mixin.Mixin;

@Restriction(require = {
		@Condition(ModIds.litematica),
		@Condition(value = ModIds.minecraft, versionPredicates = "<1.17")
})
@Mixin(DummyClass.class)
public abstract class DataManagerMixin
{
}
