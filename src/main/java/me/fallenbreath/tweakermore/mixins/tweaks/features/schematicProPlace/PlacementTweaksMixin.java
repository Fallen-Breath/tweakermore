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
import me.fallenbreath.tweakermore.util.BlockUtil;
import me.fallenbreath.tweakermore.util.ModIds;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * For injection when tweakeroo is NOT present, see {@link ClientPlayerInteractionManagerMixin}
 */
@Restriction(require = {@Condition(ModIds.tweakeroo), @Condition(ModIds.litematica)})
@Mixin(PlacementTweaks.class)
public abstract class PlacementTweaksMixin
{
	@Shadow(remap = false)
	private static boolean firstWasRotation;

	@Shadow
	private static boolean isFacingValidFor(Direction facing, ItemStack stack)
	{
		return false;
	}

	@Shadow private static ItemStack[] stackBeforeUse;

	/**
	 * There's still some final block placement tweaks inside the processRightClickBlockWrapper method.
	 * We need to simulate them in order to get the final block placement arguments, for compatibilities
	 * with tweakeroo's tweaks
	 * <p>
	 * We cannot delay the injection point until those block placement tweaks, because tweakeroo doesn't
	 * allow item in player hand tobe  changed after those tweaks, e.g. for hand restore thing
	 * <p>
	 * Copied from {@link fi.dy.masa.tweakeroo.tweaks.PlacementTweaks.processRightClickBlockWrapper}.
	 * Not elegant but that's the only way
	 */
	@Unique
	@SuppressWarnings("PointlessBooleanExpression")
	private static BlockHitResult finalBlockPlacementTweak$TKM(
			ClientPlayerEntity player,
			ClientWorld world,
			BlockPos posIn,
			Direction sideIn,
			Vec3d hitVecIn,
			Hand hand
	)
	{
		BlockHitResult hitResult = new BlockHitResult(hitVecIn, sideIn, posIn, false);
		ItemPlacementContext ctx = new ItemPlacementContext(new ItemUsageContext(player, hand, hitResult));
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
			stackOriginal = player.getStackInHand(hand).copy();
		}

		if (FeatureToggle.TWEAK_PLACEMENT_RESTRICTION.getBooleanValue() &&
				state.canReplace(ctx) == false && BlockUtil.isReplaceable(state))
		{
			posIn = posIn.offset(sideIn.getOpposite());
		}

		final int afterClickerClickCount = MathHelper.clamp(Configs.Generic.AFTER_CLICKER_CLICK_COUNT.getIntegerValue(), 0, 32);

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
				//$$ Configs.Generic.ACCURATE_PLACEMENT_PROTOCOL.getBooleanValue() &&
				//#elseif MC >= 11700
				//$$ Configs.Generic.CARPET_ACCURATE_PLACEMENT_PROTOCOL.getBooleanValue() &&
				//#else
				FeatureToggle.CARPET_ACCURATE_PLACEMENT_PROTOCOL.getBooleanValue() &&
				//#endif
				isFacingValidFor(facing, stackOriginal))
		{
			facing = facing.getOpposite(); // go from block face to click on to the requested facing
			//double relX = hitVecIn.x - posIn.getX();
			//double x = posIn.getX() + relX + 2 + (facing.getId() * 2);
			double x = posIn.getX() + 2 + (facing.getId() * 2);

			if (FeatureToggle.TWEAK_AFTER_CLICKER.getBooleanValue())
			{
				x += afterClickerClickCount * 16;
			}

			//System.out.printf("processRightClickBlockWrapper req facing: %s, x: %.3f, pos: %s, sideIn: %s\n", facing, x, posIn, sideIn);
			hitVecIn = new Vec3d(x, hitVecIn.y, hitVecIn.z);
		}

		if (FeatureToggle.TWEAK_Y_MIRROR.getBooleanValue() && Hotkeys.PLACEMENT_Y_MIRROR.getKeybind().isKeybindHeld())
		{
			double y = 1 - hitVecIn.y + 2 * posIn.getY(); // = 1 - (hitVec.y - pos.getY()) + pos.getY();
			hitVecIn = new Vec3d(hitVecIn.x, y, hitVecIn.z);

			if (sideIn.getAxis() == Direction.Axis.Y)
			{
				posIn = posIn.offset(sideIn);
				sideIn = sideIn.getOpposite();
			}
		}

		return new BlockHitResult(hitVecIn, sideIn, posIn, false);
	}


	@Inject(method = "processRightClickBlockWrapper", at = @At("HEAD"), cancellable = true, remap = false)
	private static void schematicProPlace(
			ClientPlayerInteractionManager controller,
			ClientPlayerEntity player,
			ClientWorld world,
			BlockPos posIn,
			Direction sideIn,
			Vec3d hitVecIn,
			Hand hand,
			CallbackInfoReturnable<ActionResult> cir
	)
	{
		ProPlaceImpl.handleRightClick(
				() -> {
					BlockHitResult hitResult = finalBlockPlacementTweak$TKM(player, world, posIn, sideIn, hitVecIn, hand);
					ItemPlacementContext ctx = new ItemPlacementContext(new ItemUsageContext(player, hand, hitResult));
					return Pair.of(hitResult, ctx);
				},
				cir
		);
	}
}
