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

package me.fallenbreath.tweakermore.mixins.tweaks.features.tweakmSchematicProPlace;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.impl.features.tweakmSchematicProPlace.ProPlaceImpl;
import me.fallenbreath.tweakermore.util.ModIds;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//#if MC < 11900
import net.minecraft.client.world.ClientWorld;
//#endif

@Restriction(require = @Condition(ModIds.litematica))
@Mixin(ClientPlayerInteractionManager.class)
public abstract class PlacementTweaksMixin
{
	@Inject(method = "interactBlock", at = @At("HEAD"), cancellable = true)
	private void tweakmSchematicProPlace(
			ClientPlayerEntity player,
			//#if MC < 11900
			ClientWorld world,
			//#endif
			Hand hand,
			BlockHitResult hitResult,
			CallbackInfoReturnable<ActionResult> cir
	)
	{
		ItemPlacementContext ctx = new ItemPlacementContext(new ItemUsageContext(player, hand, hitResult));

		ProPlaceImpl.handleRightClick(hitResult, ctx, cir);
	}
}
