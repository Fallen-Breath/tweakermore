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

package me.fallenbreath.tweakermore.impl.mod_tweaks.ofPlayerExtraModelOverride;

import me.fallenbreath.tweakermore.TweakerMoreMod;
import me.fallenbreath.tweakermore.config.options.TweakerMoreConfigOptionList;
import me.fallenbreath.tweakermore.config.options.listentries.OptifineExtraModelRenderStrategy;
import me.fallenbreath.tweakermore.util.FileUtils;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Supplier;

public class OverrideDefinition
{
	private final Supplier<OptifineExtraModelRenderStrategy> strategySupplier;
	private byte @Nullable [] cfg;
	private byte @Nullable [] model;
	private byte @Nullable [] texture;

	private OverrideDefinition(Supplier<OptifineExtraModelRenderStrategy> strategySupplier)
	{
		this.strategySupplier = strategySupplier;
		this.cfg = null;
		this.model = null;
		this.texture = null;
	}

	public static OverrideDefinition create(TweakerMoreConfigOptionList strategyOption)
	{
		return new OverrideDefinition(() -> (OptifineExtraModelRenderStrategy) strategyOption.getOptionListValue());
	}

	private static Optional<byte[]> readResource(String resourcePath)
	{
		try
		{
			return Optional.ofNullable(FileUtils.readResourceFileAsBytes(resourcePath));
		}
		catch (IOException e)
		{
			TweakerMoreMod.LOGGER.error("Failed to read resource from {}: {}", resourcePath, e);
		}
		return Optional.empty();
	}

	public OverrideDefinition cfg(String cfgPath)
	{
		readResource(cfgPath).ifPresent(b -> this.cfg = b);
		return this;
	}


	public OverrideDefinition model(String modelPath)
	{
		readResource(modelPath).ifPresent(b -> this.model = b);
		return this;
	}

	public OverrideDefinition texture(String texturePngPath)
	{
		readResource(texturePngPath).ifPresent(b -> this.texture = b);
		return this;
	}

	public OptifineExtraModelRenderStrategy getStrategy()
	{
		return this.strategySupplier.get();
	}

	public OverrideImpl getImpl()
	{
		if (this.cfg == null)
		{
			throw new RuntimeException("cfg unset");
		}
		return new OverrideImpl(this.cfg, this.model, this.texture);
	}
}
