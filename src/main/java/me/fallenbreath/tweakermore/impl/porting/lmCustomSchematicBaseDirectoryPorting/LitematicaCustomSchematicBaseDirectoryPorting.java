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
