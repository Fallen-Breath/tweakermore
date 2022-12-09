package me.fallenbreath.tweakermore.mixins.tweaks.mod_tweaks.lmOriginOverride000;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import fi.dy.masa.litematica.scheduler.tasks.TaskSaveSchematic;
import fi.dy.masa.litematica.schematic.LitematicaSchematic;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.impl.mod_tweaks.lmOriginOverride000.LitematicaOriginOverrideGlobals;
import me.fallenbreath.tweakermore.impl.mod_tweaks.lmOriginOverride000.LitematicaSchematic000Origin;
import me.fallenbreath.tweakermore.util.ModIds;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Restriction(require = @Condition(ModIds.litematica))
@Mixin(TaskSaveSchematic.class)
public abstract class TaskSaveSchematicMixin
{
	@Shadow(remap = false) @Final
	private LitematicaSchematic schematic;

	@ModifyExpressionValue(
			//#if MC >= 11600
			//$$ method = "<init>(Ljava/io/File;Ljava/lang/String;Lfi/dy/masa/litematica/schematic/LitematicaSchematic;Lfi/dy/masa/litematica/selection/AreaSelection;Lfi/dy/masa/litematica/schematic/LitematicaSchematic$SchematicSaveInfo;Z)V",
			//#else
			method = "<init>(Ljava/io/File;Ljava/lang/String;Lfi/dy/masa/litematica/schematic/LitematicaSchematic;Lfi/dy/masa/litematica/selection/AreaSelection;ZZ)V",
			//#endif
			at = @At(
					value = "INVOKE",
					target = "Lfi/dy/masa/litematica/selection/AreaSelection;getEffectiveOrigin()Lnet/minecraft/util/math/BlockPos;",
					remap = true
			),
			remap = false
	)
	private BlockPos lmOriginOverride000_modifyTaskOrigin(BlockPos origin)
	{
		if (((LitematicaSchematic000Origin)this.schematic).is000Origin())
		{
			origin = LitematicaOriginOverrideGlobals.POS_000;
		}
		return origin;
	}
}
