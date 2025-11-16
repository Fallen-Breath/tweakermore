/*
 * This file is part of the TweakerMore project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2024  Fallen_Breath and contributors
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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.clientEntityTargetingSelectAll;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.impl.mc_tweaks.clientEntityTargetingSelectAll.MinecraftClientWithExtendedTargetEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientSuggestionProvider.class)
public abstract class ClientCommandSourceMixin
{
	@Shadow @Final private Minecraft minecraft;

	@ModifyExpressionValue(
			method = "getSelectedEntities",
			at = @At(
					value = "FIELD",
					target = "Lnet/minecraft/client/Minecraft;hitResult:Lnet/minecraft/world/phys/HitResult;"
			)
	)
	private HitResult clientEntityTargetingSelectAll_hackUuidSuggestion(HitResult crosshairTarget)
	{
		if (TweakerMoreConfigs.CLIENT_ENTITY_TARGETING_SUPPORT_ALL.getBooleanValue())
		{
			EntityHitResult entityHitResult = ((MinecraftClientWithExtendedTargetEntity)this.minecraft).getExtendedEntityHitResult$TKM();
			if (entityHitResult != null)
			{
				crosshairTarget = entityHitResult;
			}
		}
		return crosshairTarget;
	}
}
