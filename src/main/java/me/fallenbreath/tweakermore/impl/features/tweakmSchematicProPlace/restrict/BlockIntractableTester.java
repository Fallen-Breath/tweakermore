package me.fallenbreath.tweakermore.impl.features.tweakmSchematicProPlace.restrict;

import com.google.common.collect.Maps;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;

import java.util.Map;

import static me.fallenbreath.tweakermore.impl.features.tweakmSchematicProPlace.restrict.InteractAbleTester.notMetal;
import static me.fallenbreath.tweakermore.impl.features.tweakmSchematicProPlace.restrict.InteractAbleTester.playerCanModifyWorld;
import static me.fallenbreath.tweakermore.impl.features.tweakmSchematicProPlace.restrict.InteractAllowedTester.Result;
import static me.fallenbreath.tweakermore.impl.features.tweakmSchematicProPlace.restrict.InteractAllowedTester.unequalProperty;

public class BlockIntractableTester
{
	private static final Map<Class<? extends Block>, InteractAbleTester> INTERACT_ABLE_TESTER_MAP = Maps.newHashMap();
	private static final Map<Class<? extends Block>, InteractAllowedTester> INTERACT_ALLOWED_TESTER_MAP = Maps.newHashMap();

	@SuppressWarnings("UnusedReturnValue")
	private static class RequirementBuilder
	{
		private final Class<? extends Block> blockClass;

		private RequirementBuilder(Class<? extends Block> blockClass)
		{
			this.blockClass = blockClass;
		}

		private RequirementBuilder alwaysInteractAble()
		{
			INTERACT_ABLE_TESTER_MAP.put(this.blockClass, InteractAbleTester.always());
			return this;
		}

		private RequirementBuilder when(InteractAbleTester ...testers)
		{
			InteractAbleTester tester = InteractAbleTester.always();
			for (InteractAbleTester t : testers)
			{
				tester = tester.and(t);
			}
			INTERACT_ABLE_TESTER_MAP.put(this.blockClass, tester);
			return this;
		}

		private RequirementBuilder allowIf(InteractAllowedTester tester)
		{
			INTERACT_ALLOWED_TESTER_MAP.put(this.blockClass, tester);
			return this;
		}
	}

	private static RequirementBuilder interactAble(Class<? extends Block> blockClass)
	{
		return new RequirementBuilder(blockClass).alwaysInteractAble();
	}

	static
	{
		interactAble(AbstractButtonBlock.class);
		interactAble(AbstractFurnaceBlock.class);
		interactAble(AbstractSignBlock.class);
		interactAble(AnvilBlock.class);
		interactAble(BarrelBlock.class);
		interactAble(BeaconBlock.class);
		interactAble(BedBlock.class);
		interactAble(BrewingStandBlock.class);
		interactAble(CakeBlock.class).
				when((player, worldState) -> player.canConsume(false)).
				allowIf(unequalProperty(CakeBlock.BITES));
		interactAble(CartographyTableBlock.class);
		interactAble(ChestBlock.class);
		interactAble(ComparatorBlock.class).
				when(playerCanModifyWorld()).
				allowIf(unequalProperty(ComparatorBlock.MODE));
		interactAble(CraftingTableBlock.class);
		interactAble(DaylightDetectorBlock.class).
				when(playerCanModifyWorld()).
				allowIf(unequalProperty(DaylightDetectorBlock.INVERTED));
		interactAble(DispenserBlock.class);
		interactAble(DoorBlock.class).
				when(notMetal()).
				allowIf(unequalProperty(DoorBlock.OPEN));
		interactAble(EnchantingTableBlock.class);
		interactAble(EnderChestBlock.class);
		interactAble(FenceGateBlock.class).
				allowIf(unequalProperty(FenceGateBlock.OPEN));
		interactAble(GrindstoneBlock.class);
		interactAble(LeverBlock.class).
				allowIf(unequalProperty(LeverBlock.POWERED));
		interactAble(LoomBlock.class);
		interactAble(NoteBlock.class).
				allowIf(unequalProperty(NoteBlock.NOTE));
		interactAble(RepeaterBlock.class).
				when(playerCanModifyWorld()).
				allowIf(unequalProperty(RepeaterBlock.DELAY));
		interactAble(ShulkerBoxBlock.class);
		interactAble(StonecutterBlock.class);
		interactAble(TrapdoorBlock.class).
				when(notMetal()).allowIf(unequalProperty(DoorBlock.OPEN));
	}

	public static CheckResult checkInteract(PlayerEntity player, BlockState worldState, BlockState schematicState)
	{
		// ref: net.minecraft.client.network.ClientPlayerInteractionManager.interactBlock
		boolean hasItemOnHand = !player.getMainHandStack().isEmpty() || !player.getOffHandStack().isEmpty();
		boolean shiftingAndHasItem =
				//#if MC >= 11500
				player.shouldCancelInteraction()
				//#else
				//$$ player.isSneaking()
				//#endif
						&& hasItemOnHand;

		if (shiftingAndHasItem)
		{
			return CheckResult.NO_INTERACTION;
		}

		boolean canConsume = false;
		for (Class<?> clazz = worldState.getBlock().getClass(); clazz != null && clazz != Block.class; clazz = clazz.getSuperclass())
		{
			InteractAbleTester tester1 = INTERACT_ABLE_TESTER_MAP.get(clazz);
			if (tester1 != null && tester1.isInteractAble(player, worldState))
			{
				canConsume = true;
				InteractAllowedTester tester2 = INTERACT_ALLOWED_TESTER_MAP.get(clazz);
				if (tester2 != null)
				{
					Result result = tester2.hasAllowedInteraction(player, worldState, schematicState);
					if (!result.allowed)
					{
						return new CheckResult(CheckResultType.BAD_INTERACTION, result.failureMessage);
					}
				}
			}
		}
		return canConsume ? CheckResult.GOOD_INTERACTION : CheckResult.NO_INTERACTION;
	}

	public static class CheckResult
	{
		private final CheckResultType type;
		private final String message;

		public static final CheckResult NO_INTERACTION = new CheckResult(CheckResultType.NO_INTERACTION, "");
		public static final CheckResult GOOD_INTERACTION = new CheckResult(CheckResultType.GOOD_INTERACTION, "");

		public CheckResult(CheckResultType type, String message)
		{
			this.type = type;
			this.message = message;
		}

		public CheckResultType getType()
		{
			return this.type;
		}

		public String getMessage()
		{
			return this.message;
		}
	}

	public enum CheckResultType
	{
		NO_INTERACTION,
		GOOD_INTERACTION,
		BAD_INTERACTION
	}
}
