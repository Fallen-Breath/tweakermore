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

package me.fallenbreath.tweakermore.util.damage;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.world.Difficulty;

public class DamageUtil
{
	/**
	 * Reference: {@link net.minecraft.entity.player.PlayerEntity#damage}
	 * Make sure {@link net.minecraft.entity.damage.DamageSource#isScaledWithDifficulty} is true
	 */
	public static float modifyDamageForDifficulty(float amount, Difficulty difficulty)
	{
		switch (difficulty)
		{
			case PEACEFUL:
				amount = 0.0F;
				break;
			case EASY:
				amount = Math.min(amount / 2.0F + 1.0F, amount);
				break;
			case HARD:
				amount = amount * 3.0F / 2.0F;
				break;
		}
		return amount;
	}

	public static float modifyDamageForDifficulty(float amount, Difficulty difficulty, DamageSource damageSource)
	{
		if (damageSource.isScaledWithDifficulty())
		{
			amount = modifyDamageForDifficulty(amount, difficulty);
		}
		return amount;
	}
}
