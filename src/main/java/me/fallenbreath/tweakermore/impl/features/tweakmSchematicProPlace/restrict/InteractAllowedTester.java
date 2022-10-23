package me.fallenbreath.tweakermore.impl.features.tweakmSchematicProPlace.restrict;

import fi.dy.masa.malilib.util.StringUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.property.Property;

abstract class InteractAllowedTester
{
	public abstract Result hasAllowedInteraction(PlayerEntity player, BlockState worldState, BlockState schematicState);

	private static String msg(String id)
	{
		return StringUtils.translate("tweakermore.config.tweakmSchematicBlockPlacementRestriction.info.interact_not_allow." + id);
	}

	/**
	 * Interaction is allowed only when the block unmatched or given property unmatched
	 */
	static InteractAllowedTester unequalProperty(Property<?> property)
	{
		return new InteractAllowedTester()
		{
			@Override
			public Result hasAllowedInteraction(PlayerEntity player, BlockState worldState, BlockState schematicState)
			{
				if (worldState.getBlock() == schematicState.getBlock() && worldState.get(property) == schematicState.get(property))
				{
					return new Result(false, msg("property_protection"));
				}
				return new Result(true, "");
			}
		};
	}

	public static class Result
	{
		public final boolean allowed;
		public final String failureMessage;

		public Result(boolean allowed, String failureMessage)
		{
			this.allowed = allowed;
			this.failureMessage = failureMessage;
		}
	}
}
