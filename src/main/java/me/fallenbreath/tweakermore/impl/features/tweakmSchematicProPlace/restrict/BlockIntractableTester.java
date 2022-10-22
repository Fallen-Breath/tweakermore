package me.fallenbreath.tweakermore.impl.features.tweakmSchematicProPlace.restrict;

import com.google.common.collect.Lists;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class BlockIntractableTester
{
	private static final List<InteractTester> TESTERS = Lists.newArrayList();

	private static void add(Class<? extends Block> blockClass)
	{
		TESTERS.add(InteractTester.matches(blockClass));
	}

	static
	{
		add(AbstractButtonBlock.class);
		add(AbstractFurnaceBlock.class);
		add(AbstractSignBlock.class);
		add(AnvilBlock.class);
		add(BarrelBlock.class);
	}

	public static boolean canInteract(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
	{
		return TESTERS.stream().anyMatch(tester -> tester.canInteract(state, world, pos, player, hand, hit));
	}

	private interface InteractTester
	{
		boolean canInteract(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit);

		static InteractTester always()
		{
			return (state, world, pos, player, hand, hit) -> true;
		}

		static InteractTester matches(Class<? extends Block> blockClass)
		{
			return (state, world, pos, player, hand, hit) -> blockClass.isInstance(state.getBlock());
		}
	}
}
