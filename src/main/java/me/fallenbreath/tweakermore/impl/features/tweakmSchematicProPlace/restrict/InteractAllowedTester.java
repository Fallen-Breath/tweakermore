package me.fallenbreath.tweakermore.impl.features.tweakmSchematicProPlace.restrict;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;

import java.util.Optional;

public interface InteractAllowedTester
{
	/**
	 * @return null if interaction is allowed,
	 * not-null if interaction is NOT allowed and the string value is the failure message
	 */
	Optional<String> isInteractionAllowed(PlayerEntity player, BlockState worldState, BlockState schematicState);
}
