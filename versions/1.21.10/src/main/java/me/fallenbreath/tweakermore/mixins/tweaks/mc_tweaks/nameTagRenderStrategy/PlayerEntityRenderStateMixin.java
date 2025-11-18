/*
 * This file is part of the TweakerMore project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2025  Fallen_Breath and contributors
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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.nameTagRenderStrategy;

import me.fallenbreath.tweakermore.impl.mc_tweaks.nameTagRenderStrategy.PlayerEntityRenderStateWithPlayerProfileName;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PlayerEntityRenderState.class)
public abstract class PlayerEntityRenderStateMixin implements PlayerEntityRenderStateWithPlayerProfileName
{
	@Unique
	@Nullable
	private String playerProfileName$TKM = null;

	@Override
	@Nullable
	public String getPlayerProfileName$TKM()
	{
		return this.playerProfileName$TKM;
	}

	@Override
	public void setPlayerProfileName$TKM(@Nullable  String playerProfileName$TKM)
	{
		this.playerProfileName$TKM = playerProfileName$TKM;
	}
}
