package me.fallenbreath.tweakermore.impl.features.tweakmSchematicProPlace.restrict;

import com.google.common.collect.Maps;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;

import java.util.Map;
import java.util.Optional;

import static me.fallenbreath.tweakermore.impl.features.tweakmSchematicProPlace.restrict.InteractAbleTester.notMetal;
import static me.fallenbreath.tweakermore.impl.features.tweakmSchematicProPlace.restrict.InteractAbleTester.playerCanModifyWorld;
import static me.fallenbreath.tweakermore.impl.features.tweakmSchematicProPlace.restrict.InteractAllowedTester.cauldronTester;
import static me.fallenbreath.tweakermore.impl.features.tweakmSchematicProPlace.restrict.InteractAllowedTester.unequalProperty;

public class BlockInteractionRestrictor
{
	// interact-able: check if the block can be interacted
	// interact-allowed: check if the block interaction is allowed
	private static final Map<Class<? extends Block>, InteractAbleTester> INTERACT_ABLE_TESTER_MAP = Maps.newHashMap();
	private static final Map<Class<? extends Block>, InteractAllowedTester> INTERACT_ALLOWED_TESTER_MAP = Maps.newHashMap();

	@SuppressWarnings("UnusedReturnValue")
	private static class RequirementBuilder
	{
		private final Class<? extends Block> blockClass;

		private RequirementBuilder(Class<? extends Block> blockClass)
		{
			this.blockClass = blockClass;
			INTERACT_ABLE_TESTER_MAP.put(this.blockClass, InteractAbleTester.always());
		}

		/**
		 * Set interact-able testers
		 */
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

		/**
		 * Set interact-allowed tester
		 */
		private RequirementBuilder allowIf(InteractAllowedTester tester)
		{
			INTERACT_ALLOWED_TESTER_MAP.put(this.blockClass, tester);
			return this;
		}

		private void notAllowed()
		{
			INTERACT_ALLOWED_TESTER_MAP.put(this.blockClass, InteractAllowedTester.notAllowed());
		}
	}

	private static RequirementBuilder interactAble(Class<? extends Block> blockClass)
	{
		return new RequirementBuilder(blockClass);
	}

	static
	{
		interactAble(AbstractButtonBlock.class   ).notAllowed();
		interactAble(AbstractFurnaceBlock.class  );
		interactAble(AbstractSignBlock.class     );
		interactAble(AnvilBlock.class            );
		interactAble(BarrelBlock.class           );
		interactAble(BeaconBlock.class           );
		interactAble(BedBlock.class              );
		interactAble(BrewingStandBlock.class     );
		interactAble(CakeBlock.class             ).when((player, worldState) -> player.canConsume(false)).allowIf(unequalProperty(CakeBlock.BITES));
		interactAble(CartographyTableBlock.class );
		interactAble(CauldronBlock.class         ).allowIf(cauldronTester());
		interactAble(ChestBlock.class            );
		interactAble(ComparatorBlock.class       ).when(playerCanModifyWorld()).allowIf(unequalProperty(ComparatorBlock.MODE));
		interactAble(CraftingTableBlock.class    );
		interactAble(DaylightDetectorBlock.class ).when(playerCanModifyWorld()).allowIf(unequalProperty(DaylightDetectorBlock.INVERTED));
		interactAble(DispenserBlock.class        );
		interactAble(DoorBlock.class             ).when(notMetal()).allowIf(unequalProperty(DoorBlock.OPEN));
		interactAble(EnchantingTableBlock.class  );
		interactAble(EnderChestBlock.class       );
		interactAble(FenceGateBlock.class        ).allowIf(unequalProperty(FenceGateBlock.OPEN));
		interactAble(GrindstoneBlock.class       );
		interactAble(LeverBlock.class            ).allowIf(unequalProperty(LeverBlock.POWERED));
		interactAble(LoomBlock.class             );
		interactAble(NoteBlock.class             ).allowIf(unequalProperty(NoteBlock.NOTE));
		interactAble(RepeaterBlock.class         ).when(playerCanModifyWorld()).allowIf(unequalProperty(RepeaterBlock.DELAY));
		interactAble(ShulkerBoxBlock.class       );
		interactAble(StonecutterBlock.class      );
		interactAble(TrapdoorBlock.class         ).when(notMetal()).allowIf(unequalProperty(DoorBlock.OPEN));
	}

	public static Result checkInteract(PlayerEntity player, BlockState worldState, BlockState schematicState)
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
			return Result.no();
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
					Optional<String> failureMessage = tester2.isInteractionAllowed(player, worldState, schematicState);
					if (failureMessage.isPresent())
					{
						return Result.bad(failureMessage.get());
					}
				}
			}
		}
		return canConsume ? Result.good() : Result.no();
	}

	public static class Result
	{
		private final ResultType type;
		private final String message;

		private Result(ResultType type, String message)
		{
			this.type = type;
			this.message = message;
		}

		public static Result no()
		{
			return new Result(ResultType.NO_INTERACTION, "");
		}

		public static Result good()
		{
			return new Result(ResultType.GOOD_INTERACTION, "");
		}

		public static Result bad(String message)
		{
			return new Result(ResultType.BAD_INTERACTION, message);
		}

		public ResultType getType()
		{
			return this.type;
		}

		public String getMessage()
		{
			return this.message;
		}
	}

	public enum ResultType
	{
		/**
		 * There's no valid / available interaction for this block
		 */
		NO_INTERACTION,
		/**
		 * There are some interaction, and all of them are allowed
		 */
		GOOD_INTERACTION,
		/**
		 * There are some interaction, and some of them are NOT allowed
		 */
		BAD_INTERACTION
	}
}
