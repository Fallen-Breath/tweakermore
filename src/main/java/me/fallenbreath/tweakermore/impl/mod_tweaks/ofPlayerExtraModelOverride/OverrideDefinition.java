package me.fallenbreath.tweakermore.impl.mod_tweaks.ofPlayerExtraModelOverride;

import me.fallenbreath.tweakermore.TweakerMoreMod;
import me.fallenbreath.tweakermore.config.options.TweakerMoreConfigOptionList;
import me.fallenbreath.tweakermore.config.options.listentries.OptifineExtraModelRenderStrategy;
import me.fallenbreath.tweakermore.util.FileUtil;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Base64;
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
			return Optional.ofNullable(FileUtil.readResourceFileAsBytes(resourcePath));
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
