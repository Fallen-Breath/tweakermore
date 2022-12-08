package me.fallenbreath.tweakermore.mixins.tweaks.mod_tweaks.lmOriginOverride000;

import me.fallenbreath.tweakermore.impl.mod_tweaks.lmOriginOverride000.LitematicaOriginOverrideGlobals;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "fi.dy.masa.litematica.gui.widgets.WidgetSchematicEntry$ButtonListener")
public abstract class WidgetSchematicEntryButtonListenerMixin
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
