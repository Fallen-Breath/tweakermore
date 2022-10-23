package me.fallenbreath.tweakermore.impl.features.tweakmSchematicProPlace.restrict;

import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.entity.player.PlayerEntity;

/**
 * Test if the block is interact-able
 */
interface InteractAbleTester
{
	boolean isInteractAble(PlayerEntity player, BlockState worldState);

	default InteractAbleTester and(InteractAbleTester other)
	{
		return (player, worldState) -> this.isInteractAble(player, worldState) && other.isInteractAble(player, worldState);
	}

	static InteractAbleTester always()
	{
		return (player, worldState) -> true;
	}

	static InteractAbleTester playerCanModifyWorld()
	{
		return (player, worldState) -> player.canModifyWorld();
	}

	static InteractAbleTester notMetal()
	{
		return (player, worldState) -> worldState.getMaterial() != Material.METAL;
	}
}
