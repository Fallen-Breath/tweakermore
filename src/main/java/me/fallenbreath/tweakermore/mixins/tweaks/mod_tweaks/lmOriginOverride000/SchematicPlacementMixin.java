package me.fallenbreath.tweakermore.mixins.tweaks.mod_tweaks.lmOriginOverride000;

import fi.dy.masa.litematica.schematic.LitematicaSchematic;
import fi.dy.masa.litematica.schematic.placement.SchematicPlacement;
import fi.dy.masa.malilib.gui.Message;
import fi.dy.masa.malilib.util.InfoUtils;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.impl.mod_tweaks.lmOriginOverride000.LitematicaOriginOverrideGlobals;
import me.fallenbreath.tweakermore.impl.mod_tweaks.lmOriginOverride000.LitematicaSchematic000Origin;
import me.fallenbreath.tweakermore.util.ModIds;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Restriction(require = @Condition(ModIds.litematica))
@Mixin(SchematicPlacement.class)
public abstract class SchematicPlacementMixin
{
	@Shadow(remap = false)
	private LitematicaSchematic schematic;

	@Shadow(remap = false)
	private BlockPos origin;

	@Inject(method = "<init>", at = @At("TAIL"))
	private void lmOriginOverride000_tweakPlacementOriginTo000(CallbackInfo ci)
	{
		if (TweakerMoreConfigs.LM_ORIGIN_OVERRIDE_000.getBooleanValue())
		{
			if (((LitematicaSchematic000Origin)this.schematic).is000Origin() && LitematicaOriginOverrideGlobals.IS_USER_LOADING_SCHEMATIC.get())
			{
				InfoUtils.showGuiMessage(Message.MessageType.SUCCESS, "tweakermore.impl.lmOriginOverride000.placement_tweaked", this.schematic.getMetadata().getName());
				this.origin = LitematicaOriginOverrideGlobals.POS_000;

				LitematicaOriginOverrideGlobals.IS_USER_LOADING_SCHEMATIC.remove();
			}
		}
	}
}
