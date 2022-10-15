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
