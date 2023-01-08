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
