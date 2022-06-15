package me.fallenbreath.tweakermore.mixins.tweaks.features.tweakmBlockSchematicPlacementRestriction;

import fi.dy.masa.litematica.util.WorldUtils;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.util.ModIds;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Restriction(require = @Condition(ModIds.litematica))
@Mixin(WorldUtils.class)
public interface WorldUtilsAccessor
{
	@Invoker
	static boolean invokePlacementRestrictionInEffect(MinecraftClient mc)
	{
		throw new RuntimeException();
	}
}
