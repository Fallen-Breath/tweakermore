package me.fallenbreath.tweakermore.impl.features.tweakmSchematicProPlace.restrict;

import fi.dy.masa.malilib.util.StringUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.property.Property;

import java.util.Optional;

public abstract class InteractAllowedTester
{
	private final String translationKey;

	protected InteractAllowedTester(String id)
	{
		this.translationKey = "tweakermore.impl.tweakmSchematicBlockPlacementRestriction.info.interact_not_allow." + id;
	}

	/**
	 * @return null if interaction is allowed,
	 * not-null if interaction is NOT allowed and the string value is the failure message
	 */
	public final Optional<String> isInteractionAllowed(PlayerEntity player, BlockState worldState, BlockState schematicState)
	{
		return this.test(player, worldState, schematicState) ?
				/* success */ Optional.empty() :
				/* failed */ Optional.of(StringUtils.translate(this.translationKey));

	}

	protected abstract boolean test(PlayerEntity player, BlockState worldState, BlockState schematicState);

	/*
	 * ----------------------------
	 *       Implementations
	 * ----------------------------
	 */

	/**
	 * Interaction is allowed only when the block unmatched or given property unmatched
	 */
	public static InteractAllowedTester unequalProperty(Property<?> property)
	{
		return new InteractAllowedTester("property_protection")
		{
			@Override
			public boolean test(PlayerEntity player, BlockState worldState, BlockState schematicState)
			{
				return worldState.getBlock() != schematicState.getBlock() || worldState.get(property) != schematicState.get(property);
			}
		};
	}
}
