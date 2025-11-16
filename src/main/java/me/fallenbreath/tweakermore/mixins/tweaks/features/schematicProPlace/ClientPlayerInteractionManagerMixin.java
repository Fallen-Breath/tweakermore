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
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.impl.features.schematicProPlace.ProPlaceImpl;
import me.fallenbreath.tweakermore.util.ModIds;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.item.UseOnContext;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//#if MC < 11900
import net.minecraft.client.multiplayer.ClientLevel;
//#endif

/**
 * For injection when tweakeroo is present, see {@link PlacementTweaksMixin}
 */
@Restriction(require = @Condition(ModIds.litematica), conflict = @Condition(ModIds.tweakeroo))
@Mixin(MultiPlayerGameMode.class)
public abstract class ClientPlayerInteractionManagerMixin
{
	@Inject(method = "useItemOn", at = @At("HEAD"), cancellable = true)
	private void schematicProPlace(
			LocalPlayer player,
			//#if MC < 11900
			ClientLevel world,
			//#endif
			InteractionHand hand,
			BlockHitResult hitResult,
			CallbackInfoReturnable<InteractionResult> cir
	)
	{
		BlockPlaceContext ctx = new BlockPlaceContext(new UseOnContext(player, hand, hitResult));

		ProPlaceImpl.handleRightClick(() -> Pair.of(hitResult, ctx), cir);
	}
}
