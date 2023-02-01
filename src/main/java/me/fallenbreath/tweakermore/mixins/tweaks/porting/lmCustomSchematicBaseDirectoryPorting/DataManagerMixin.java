/*
 * This file is part of the TweakerMore project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2023  Fallen_Breath and contributors
 *
 * TweakerMore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * TweakerMore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with TweakerMore.  If not, see <https://www.gnu.org/licenses/>.
 */

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
