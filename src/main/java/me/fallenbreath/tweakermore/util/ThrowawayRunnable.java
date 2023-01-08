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

package me.fallenbreath.tweakermore.util;

/**
 * A runnable wrapper that only runs the delegate Runnable at the first invocation
 */
public class ThrowawayRunnable implements Runnable
{
	private final Runnable delegate;
	private boolean processed;

	private ThrowawayRunnable(Runnable delegate)
	{
		this.delegate = delegate;
		this.processed = false;
	}
	
	public static ThrowawayRunnable of(Runnable runnable)
	{
		return new ThrowawayRunnable(runnable);
	}

	@Override
	public void run()
	{
		if (!this.processed)
		{
			this.processed = true;
			this.delegate.run();
		}
	}
}
