package me.fallenbreath.tweakermore.mixins.tweaks.mod_tweaks.lmOriginOverride000;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.impl.mod_tweaks.lmOriginOverride000.LitematicaOriginOverrideGlobals;
import me.fallenbreath.tweakermore.util.ModIds;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Restriction(require = @Condition(ModIds.litematica))
@Mixin(targets = "fi.dy.masa.litematica.gui.GuiSchematicLoad$ButtonListener")
public abstract class GuiSchematicLoadButtonListenerMixin
{
	@Inject(
			method = "actionPerformedWithButton",
			at = @At(
					value = "INVOKE",
					target = "Lfi/dy/masa/litematica/data/DataManager;getSchematicPlacementManager()Lfi/dy/masa/litematica/schematic/placement/SchematicPlacementManager;",
					remap = false
			),
			remap = false
	)
	private void lmOriginOverride000_getReadyToSetOriginTo000(CallbackInfo ci)
	{
		LitematicaOriginOverrideGlobals.IS_USER_LOADING_SCHEMATIC.set(true);
	}
}
