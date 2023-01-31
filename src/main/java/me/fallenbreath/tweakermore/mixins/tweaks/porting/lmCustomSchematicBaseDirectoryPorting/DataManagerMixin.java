package me.fallenbreath.tweakermore.mixins.tweaks.porting.lmCustomSchematicBaseDirectoryPorting;

import fi.dy.masa.litematica.data.DataManager;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.impl.porting.lmCustomSchematicBaseDirectoryPorting.LitematicaCustomSchematicBaseDirectoryPorting;
import me.fallenbreath.tweakermore.util.ModIds;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.io.File;

@Restriction(require = {
		@Condition(ModIds.litematica),
		@Condition(value = ModIds.minecraft, versionPredicates = "<1.17")
})
@Mixin(DataManager.class)
public abstract class DataManagerMixin
{
	@ModifyVariable(
			method = "getSchematicsBaseDirectory",
			at = @At(
					value = "INVOKE_ASSIGN",
					target = "Lfi/dy/masa/malilib/util/FileUtils;getCanonicalFileIfPossible(Ljava/io/File;)Ljava/io/File;",
					shift = At.Shift.AFTER,
					remap = false
			),
			remap = false
	)
	private static File lmCustomSchematicBaseDirectoryPortImpl(File dir)
	{
		return LitematicaCustomSchematicBaseDirectoryPorting.modifiedBaseSchematicDirectory(dir);
	}
}
