package me.fallenbreath.tweakermore.impl.features.tweakmSchematicProPlace.restrict;

import fi.dy.masa.malilib.util.StringUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.property.Property;

import java.util.Optional;

public class InteractAllowedTesters
{
	private InteractAllowedTesters() {}

	public interface Impl
	{
		boolean test(PlayerEntity player, BlockState worldState, BlockState schematicState);
	}

	private static String msg(String id)
	{
		return StringUtils.translate("tweakermore.impl.tweakmSchematicBlockPlacementRestriction.info.interact_not_allow." + id);
	}

	public static InteractAllowedTester of(String id, Impl testerImpl)
	{
		return (player, worldState, schematicState) ->
				testerImpl.test(player, worldState, schematicState) ?
						/* success */ Optional.empty() :
						/* failed */ Optional.of(msg(id));
	}

	/**
	 * Simple InteractAllowedTester factory that you don't need to care about the failure message
	 * The failure message is: not allowed
	 */
	public static InteractAllowedTester simple(Impl testerImpl)
	{
		return of("not_allowed", testerImpl);
	}

	/**
	 * Always not-allowed interaction
	 */
	public static InteractAllowedTester notAllowed()
	{
		return simple((player, worldState, schematicState) -> false);
	}

	/**
	 * Interaction is allowed only when the block unmatched or given property unmatched
	 */
	public static InteractAllowedTester unequalProperty(Property<?> property)
	{
		return of("property_protection", (player, worldState, schematicState) ->
				worldState.getBlock() != schematicState.getBlock() || worldState.get(property) != schematicState.get(property)
		);
	}
}
