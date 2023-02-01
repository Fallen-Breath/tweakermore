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

package me.fallenbreath.tweakermore.impl.porting.lmCustomSchematicBaseDirectoryPorting;

import fi.dy.masa.malilib.util.FileUtils;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;

import java.io.File;

/**
 * Used in mc <=1.16
 */
public class LitematicaCustomSchematicBaseDirectoryPorting
{
	/**
	 * The same as {@link fi.dy.masa.litematica.data.DataManager#getDefaultBaseSchematicDirectory} in mc1.17+
	 */
	public static File getDefaultBaseSchematicDirectory()
	{
		return FileUtils.getCanonicalFileIfPossible(new File(FileUtils.getMinecraftDirectory(), "schematics"));
	}

	/**
	 * Reference: {@link fi.dy.masa.litematica.data.DataManager#getSchematicsBaseDirectory} in mc1.17+
	 */
	public static File modifiedBaseSchematicDirectory(File original)
	{
        if (TweakerMoreConfigs.LM_CUSTOM_SCHEMATIC_BASE_DIRECTORY_ENABLED_PORTING.getBooleanValue())
        {
	        return new File(TweakerMoreConfigs.LM_CUSTOM_SCHEMATIC_BASE_DIRECTORY_PORTING.getStringValue());
        }
		return original;
	}
}
