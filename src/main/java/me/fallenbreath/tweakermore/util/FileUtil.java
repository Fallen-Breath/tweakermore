package me.fallenbreath.tweakermore.util;

import me.fallenbreath.tweakermore.TweakerMoreMod;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class FileUtil
{
	private static final String CONFIG_FILE_NAME = TweakerMoreMod.MOD_ID + ".json";

	/**
	 * Use deprecation API for better old fabric loader version compatibility
	 */
	@SuppressWarnings("deprecation")
	public static File getConfigFile()
	{
		return FabricLoader.getInstance().getConfigDirectory().toPath().resolve(CONFIG_FILE_NAME).toFile();
	}

	public static byte[] readResourceFileAsBytes(String path) throws IOException
	{
		InputStream inputStream = FileUtil.class.getClassLoader().getResourceAsStream(path);
		if (inputStream == null)
		{
			throw new IOException("Null input stream from path " + path);
		}
		return IOUtils.toByteArray(inputStream);
	}
}
