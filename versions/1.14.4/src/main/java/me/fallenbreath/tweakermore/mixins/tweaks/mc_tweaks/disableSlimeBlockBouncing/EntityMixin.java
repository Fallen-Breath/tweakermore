package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.disableSlimeBlockBouncing;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.util.mixin.DummyClass;
import org.spongepowered.asm.mixin.Mixin;

import static me.fallenbreath.tweakermore.util.ModIds.minecraft;

@Restriction(require = @Condition(value = minecraft, versionPredicates = ">=1.15"))
@Mixin(DummyClass.class)
public abstract class EntityMixin
{
}
