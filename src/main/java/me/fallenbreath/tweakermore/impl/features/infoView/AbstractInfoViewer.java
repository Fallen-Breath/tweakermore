package me.fallenbreath.tweakermore.impl.features.infoView;

import fi.dy.masa.malilib.config.IConfigBoolean;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.config.options.TweakerMoreConfigOptionList;
import me.fallenbreath.tweakermore.config.options.listentries.InfoViewRenderStrategy;
import me.fallenbreath.tweakermore.config.options.listentries.InfoViewTargetStrategy;
import me.fallenbreath.tweakermore.util.render.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.function.Supplier;

public abstract class AbstractInfoViewer
{
	private final IConfigBoolean switchConfig;
	private final Supplier<InfoViewRenderStrategy> renderStrategySupplier;
	private final Supplier<InfoViewTargetStrategy> targetStrategySupplier;

	public AbstractInfoViewer(IConfigBoolean switchConfig, Supplier<InfoViewRenderStrategy> renderStrategySupplier, Supplier<InfoViewTargetStrategy> targetStrategySupplier)
	{
		this.switchConfig = switchConfig;
		this.renderStrategySupplier = renderStrategySupplier;
		this.targetStrategySupplier = targetStrategySupplier;
	}
	public AbstractInfoViewer(IConfigBoolean switchConfig, TweakerMoreConfigOptionList renderStrategyOption, TweakerMoreConfigOptionList targetStrategyOption)
	{
		this(switchConfig, () -> (InfoViewRenderStrategy)renderStrategyOption.getOptionListValue(), () -> (InfoViewTargetStrategy)targetStrategyOption.getOptionListValue());
	}
	public AbstractInfoViewer(IConfigBoolean switchConfig, TweakerMoreConfigOptionList renderStrategyOption, Supplier<InfoViewTargetStrategy> targetStrategySupplier)
	{
		this(switchConfig, () -> (InfoViewRenderStrategy)renderStrategyOption.getOptionListValue(), targetStrategySupplier);
	}

	/**
	 * If this viewer works for and should render for given context
	 *
	 * @param world      The current world get from {@link fi.dy.masa.malilib.util.WorldUtils#getBestWorld}
	 * @param blockPos   The block pos the player looking at
	 * @param blockState The block state the player looking at
	 */
	public abstract boolean shouldRenderFor(World world, BlockPos blockPos, BlockState blockState);

	public abstract boolean requireBlockEntitySyncing();

	public abstract void render(RenderContext context, World world, BlockPos blockPos, BlockState blockState, BlockEntity blockEntity);

	public boolean isRenderEnabled()
	{
		if (this.switchConfig.getBooleanValue())
		{
			switch (this.renderStrategySupplier.get())
			{
				case HOTKEY_HELD:
					return TweakerMoreConfigs.INFO_VIEW_RENDERING_KEY.isKeybindHeld();
				case ALWAYS:
					return true;
			}
		}
		return false;
	}

	public boolean isValidTarget(boolean crossHairPointed)
	{
		switch (this.targetStrategySupplier.get())
		{
			case POINTED:
				return crossHairPointed;
			case BEAM:
				return true;
		}
		return false;
	}

	public void onInfoViewStart()
	{
	}

	public void onInfoViewStop()
	{
	}

	public void onClientTick()
	{
	}
}
