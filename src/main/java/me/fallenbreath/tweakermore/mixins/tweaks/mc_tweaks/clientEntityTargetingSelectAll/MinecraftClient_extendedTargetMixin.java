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

import me.fallenbreath.tweakermore.impl.mc_tweaks.clientEntityTargetingSelectAll.MinecraftClientWithExtendedTargetEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.hit.EntityHitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClient_extendedTargetMixin implements MinecraftClientWithExtendedTargetEntity
{
	@Unique
	private EntityHitResult extendedEntityHitResult = null;

	@Nullable
	@Override
	public EntityHitResult getExtendedEntityHitResult$TKM()
	{
		return this.extendedEntityHitResult;
	}

	@Override
	public void setExtendedEntityHitResult$TKM(@Nullable EntityHitResult entityHitResult)
	{
		this.extendedEntityHitResult = entityHitResult;
	}
}
