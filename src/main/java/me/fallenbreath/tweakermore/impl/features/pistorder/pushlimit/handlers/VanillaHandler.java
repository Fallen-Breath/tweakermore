/*
 * This file is part of the Pistorder project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2023  Fallen_Breath and contributors
 *
 * Pistorder is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Pistorder is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Pistorder.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.fallenbreath.tweakermore.impl.features.pistorder.pushlimit.handlers;

public class VanillaHandler implements PushLimitHandler
{
	private Integer currentPushLimit = 12;  // vanilla value

	@Override
	public String getModId()
	{
		return null;
	}

	@Override
	public void setPushLimit(int pushLimit)
	{
		this.currentPushLimit = pushLimit;
	}

	@Override
	public int getPushLimit()
	{
		return this.currentPushLimit;
	}
}
