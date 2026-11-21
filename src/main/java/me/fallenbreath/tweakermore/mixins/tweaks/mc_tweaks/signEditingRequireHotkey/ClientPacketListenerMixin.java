package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.signEditingRequireHotkey;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.util.ModIds;
import me.fallenbreath.tweakermore.util.mixin.DummyClass;
import org.spongepowered.asm.mixin.Mixin;

/**
 * mc >= 1.20.1: subproject 1.20.1
 * mc <  1.20.1: subproject 1.15.2 (main project)  <--------
 */
@Restriction(require = @Condition(value = ModIds.minecraft, versionPredicates = ">=1.20.1"))
@Mixin(DummyClass.class)
public abstract class ClientPacketListenerMixin
{
}
