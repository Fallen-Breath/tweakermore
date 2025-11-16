/*
 * This file is part of the TweakerMore project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2023  Fallen_Breath and contributors
 *
 * TweakerMore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * TweakerMore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with TweakerMore.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.fallenbreath.tweakermore.mixins.tweaks.features.schematicProPlace;

import com.mojang.datafixers.util.Pair;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.config.Hotkeys;
import fi.dy.masa.tweakeroo.tweaks.PlacementTweaks;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.impl.features.schematicProPlace.ProPlaceImpl;
import me.fallenbreath.tweakermore.util.ModIds;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseOnContext;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//#if MC >= 12100
//$$ import fi.dy.masa.malilib.util.game.BlockUtils;
//$$ import me.fallenbreath.tweakermore.util.compat.tweakeroo.TweakerooAccess;
//#endif

/**
 * For injection when tweakeroo is NOT present, see {@link ClientPlayerInteractionManagerMixin}
 */
@Restriction(require = {@Condition(ModIds.tweakeroo), @Condition(ModIds.litematica)})
@Mixin(PlacementTweaks.class)
public abstract class PlacementTweaksMixin
{
	@Shadow(remap = false)
	private static boolean firstWasRotation;

	//#if MC < 12100
	@Shadow
	private static boolean isFacingValidFor(Direction facing, ItemStack stack)
	{
		return false;
	}
	//#endif

	@Shadow private static ItemStack[] stackBeforeUse;

	/**
	 * There's still some final block placement tweaks inside the processRightClickBlockWrapper method.
	 * We need to simulate them in order to get the final block placement arguments, for compatibilities
	 * with tweakeroo's tweaks
	 * <p>
	 * We cannot delay the injection point until those block placement tweaks, because tweakeroo doesn't
	 * allow item in player hand to be changed after those tweaks, e.g. for hand restore thing
	 * <p>
	 * Copied from {@link fi.dy.masa.tweakeroo.tweaks.PlacementTweaks#processRightClickBlockWrapper}.
	 * Not elegant but that's the only way
	 */
	@Unique
	@SuppressWarnings("PointlessBooleanExpression")
	private static BlockHitResult finalBlockPlacementTweak$TKM(
			LocalPlayer player,
			ClientLevel world,
			BlockPos posIn,
			Direction sideIn,
			Vec3 hitVecIn,
			InteractionHand hand
	)
	{
		BlockHitResult hitResult = new BlockHitResult(hitVecIn, sideIn, posIn, false);
		BlockPlaceContext ctx = new BlockPlaceContext(new UseOnContext(player, hand, hitResult));
		BlockState state = world.getBlockState(posIn);
		ItemStack stackOriginal;

		if (stackBeforeUse[hand.ordinal()].isEmpty() == false &&
				FeatureToggle.TWEAK_HOTBAR_SLOT_CYCLE.getBooleanValue() == false &&
				FeatureToggle.TWEAK_HOTBAR_SLOT_RANDOMIZER.getBooleanValue() == false)
		{
			stackOriginal = stackBeforeUse[hand.ordinal()];
		}
		else
		{
			stackOriginal = player.getItemInHand(hand).copy();
		}

		if (FeatureToggle.TWEAK_PLACEMENT_RESTRICTION.getBooleanValue() &&
				state.canBeReplaced(ctx) == false && me.fallenbreath.tweakermore.util.BlockUtils.isReplaceable(state))
		{
			posIn = posIn.relative(sideIn.getOpposite());
		}

		final int afterClickerClickCount = Mth.clamp(Configs.Generic.AFTER_CLICKER_CLICK_COUNT.getIntegerValue(), 0, 32);

		Direction facing = sideIn;
		boolean flexible = FeatureToggle.TWEAK_FLEXIBLE_BLOCK_PLACEMENT.getBooleanValue();
		boolean rotationHeld = Hotkeys.FLEXIBLE_BLOCK_PLACEMENT_ROTATION.getKeybind().isKeybindHeld();
		boolean rememberFlexible =
				//#if MC >= 11700
				//$$ Configs.Generic.REMEMBER_FLEXIBLE.getBooleanValue();
				//#else
				FeatureToggle.REMEMBER_FLEXIBLE.getBooleanValue();
				//#endif
		boolean rotation = rotationHeld || (rememberFlexible && firstWasRotation);
		boolean accurate = FeatureToggle.TWEAK_ACCURATE_BLOCK_PLACEMENT.getBooleanValue();
		boolean keys = Hotkeys.ACCURATE_BLOCK_PLACEMENT_IN.getKeybind().isKeybindHeld() || Hotkeys.ACCURATE_BLOCK_PLACEMENT_REVERSE.getKeybind().isKeybindHeld();
		accurate = accurate && keys;

		// Carpet-Extra mod accurate block placement protocol support
		if (flexible && rotation && accurate == false &&
				//#if MC >= 12100
				//$$ TweakerooAccess.getAccuratePlacementProtocolValue() &&
				//#elseif MC >= 11700
				//$$ Configs.Generic.CARPET_ACCURATE_PLACEMENT_PROTOCOL.getBooleanValue() &&
				//#else
				FeatureToggle.CARPET_ACCURATE_PLACEMENT_PROTOCOL.getBooleanValue() &&
				//#endif
				//#if MC >= 12100
				//$$ BlockUtils.isFacingValidForDirection(stackOriginal, facing))
				//#else
				isFacingValidFor(facing, stackOriginal))
				//#endif
		{
			facing = facing.getOpposite(); // go from block face to click on to the requested facing
			//double relX = hitVecIn.x - posIn.getX();
			//double x = posIn.getX() + relX + 2 + (facing.getId() * 2);
			double x = posIn.getX() + 2 + (facing.get3DDataValue() * 2);

			if (FeatureToggle.TWEAK_AFTER_CLICKER.getBooleanValue())
			{
				x += afterClickerClickCount * 16;
			}

			//System.out.printf("processRightClickBlockWrapper req facing: %s, x: %.3f, pos: %s, sideIn: %s\n", facing, x, posIn, sideIn);
			hitVecIn = new Vec3(x, hitVecIn.y, hitVecIn.z);
		}
		//#if MC >= 12103
		//$$ else if (flexible && rotation && accurate == false &&
		//$$ 		TweakerooAccess.getAccuratePlacementProtocolValue() &&
		//$$ 		BlockUtils.isFacingValidForOrientation(stackOriginal, facing))
		//$$ {
		//$$ 	facing = facing.getOpposite(); // go from block face to click on to the requested facing
		//$$ 	//double relX = hitVecIn.x - posIn.getX();
		//$$ 	//double x = posIn.getX() + relX + 2 + (facing.getId() * 2);
		//$$
		//$$ 	int facingIndex = BlockUtils.getOrientationFacingIndex(stackOriginal, facing);
		//$$ 	double x;
		//$$ 	if (facingIndex >= 0)
		//$$ 	{
		//$$ 		x = posIn.getX() + 2 + (facingIndex * 2);
		//$$ 	}
		//$$ 	else
		//$$ 	{
		//$$ 		x = posIn.getX() + 2 + (facing.getId() * 2);
		//$$ 	}
		//$$
		//$$ 	if (FeatureToggle.TWEAK_AFTER_CLICKER.getBooleanValue())
		//$$ 	{
		//$$ 		x += afterClickerClickCount * 16;
		//$$ 	}
		//$$
		//$$ 	//System.out.printf("processRightClickBlockWrapper/Orientation req facing: %s, x: %.3f, pos: %s, sideIn: %s\n", facing, x, posIn, sideIn);
		//$$ 	hitVecIn = new Vec3d(x, hitVecIn.y, hitVecIn.z);
		//$$ }
		//#endif

		if (FeatureToggle.TWEAK_Y_MIRROR.getBooleanValue() && Hotkeys.PLACEMENT_Y_MIRROR.getKeybind().isKeybindHeld())
		{
			double y = 1 - hitVecIn.y + 2 * posIn.getY(); // = 1 - (hitVec.y - pos.getY()) + pos.getY();
			hitVecIn = new Vec3(hitVecIn.x, y, hitVecIn.z);

			if (sideIn.getAxis() == Direction.Axis.Y)
			{
				posIn = posIn.relative(sideIn);
				sideIn = sideIn.getOpposite();
			}
		}

		return new BlockHitResult(hitVecIn, sideIn, posIn, false);
	}


	@Inject(method = "processRightClickBlockWrapper", at = @At("HEAD"), cancellable = true, remap = false)
	private static void schematicProPlace(
			MultiPlayerGameMode controller,
			LocalPlayer player,
			ClientLevel world,
			BlockPos posIn,
			Direction sideIn,
			Vec3 hitVecIn,
			InteractionHand hand,
			CallbackInfoReturnable<InteractionResult> cir
	)
	{
		ProPlaceImpl.handleRightClick(
				() -> {
					BlockHitResult hitResult = finalBlockPlacementTweak$TKM(player, world, posIn, sideIn, hitVecIn, hand);
					BlockPlaceContext ctx = new BlockPlaceContext(new UseOnContext(player, hand, hitResult));
					return Pair.of(hitResult, ctx);
				},
				cir
		);
	}
}
