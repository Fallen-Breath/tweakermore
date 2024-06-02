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

import me.fallenbreath.tweakermore.TweakerMoreMod;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.DamageUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.explosion.Explosion;

import java.util.Objects;

//#if MC >= 11904
//$$ import net.minecraft.registry.tag.DamageTypeTags;
//#endif

public class DamageCalculator
{
	private final LivingEntity entity;
	private float damageAmount;
	private final DamageSource damageSource;

	private ApplyStage currentStage;

	private DamageCalculator(LivingEntity entity, float damageAmount, DamageSource damageSource)
	{
		this.entity = entity;
		this.damageAmount = damageAmount;
		this.damageSource = damageSource;
		this.currentStage = ApplyStage.NONE;
	}

	/*
	 * ============================
	 *          Factories
	 * ============================
	 */

	public static DamageCalculator create(LivingEntity entity, float damageAmount, DamageSource damageSource)
	{
		return new DamageCalculator(entity, damageAmount, damageSource);
	}

	public static DamageCalculator explosion(Vec3d explosionCenter, float explosionPower, LivingEntity entity)
	{
		float exposure = Explosion.getExposure(explosionCenter, entity);
		float maxRange = explosionPower * 2.0F;
		double distanceRatio = Math.sqrt(entity.squaredDistanceTo(explosionCenter)) / (double) maxRange;
		int damage;
		if (distanceRatio <= 1.0)
		{
			double damageFactor = (1.0 - distanceRatio) * exposure;
			damage = (int)((damageFactor * damageFactor + damageFactor) / 2.0 * 7.0 * maxRange + 1.0);
		}
		else
		{
			damage = 0;
		}
		return new DamageCalculator(
				entity, damage,
				//#if MC >= 11904
				//$$ entity.getDamageSources().badRespawnPoint(explosionCenter)
				//#else
				DamageSource.netherBed(
						//#if MC >= 11903
						//$$ explosionCenter
						//#endif
				)
				//#endif
		);
	}

	/*
	 * ============================
	 *           Getters
	 * ============================
	 */

	public LivingEntity getEntity()
	{
		return this.entity;
	}

	public float getEntityHealthAfterDeal()
	{
		return Math.max(this.entity.getHealth() - this.damageAmount, 0.0F);
	}

	public float getDamageAmount()
	{
		return this.damageAmount;
	}

	public DamageSource getDamageSource()
	{
		return this.damageSource;
	}

	/*
	 * ============================
	 *           Modifiers
	 * ============================
	 */

	private void checkAndSetStage(ApplyStage stage)
	{
		if (stage.ordinal() <= this.currentStage.ordinal())
		{
			TweakerMoreMod.LOGGER.warn("DamageCalculator wrong apply order: current {}, applying {}", this.currentStage, stage);
		}
		this.currentStage = stage;
	}

	/**
	 * Reference: {@link net.minecraft.entity.player.PlayerEntity#damage}
	 */
	public DamageCalculator applyDifficulty(Difficulty difficulty)
	{
		this.checkAndSetStage(ApplyStage.DIFFICULTY);
		if (this.entity instanceof PlayerEntity)
		{
			if (this.damageSource.isScaledWithDifficulty())
			{
				this.damageAmount = me.fallenbreath.tweakermore.util.damage.DamageUtil.modifyDamageForDifficulty(this.damageAmount, difficulty);
			}
		}
		return this;
	}

	public DamageCalculator applyArmorAndResistanceAndEnchantment()
	{
		this.checkAndSetStage(ApplyStage.ARMOR);
		float amount = this.damageAmount;

		// ref: net.minecraft.entity.LivingEntity#applyDamage
		{
			// ref: net.minecraft.entity.LivingEntity#applyArmorToDamage
			if (!(
					//#if MC >= 11904
					//$$ this.damageSource.isIn(DamageTypeTags.BYPASSES_ARMOR)
					//#else
					this.damageSource.bypassesArmor()
					//#endif
			))
			{
				amount = DamageUtil.getDamageLeft(
						amount,
						//#if MC >= 12006
						//$$ this.damageSource,
						//#endif
						this.entity.getArmor(),
						(float)this.entity.getAttributeInstance(EntityAttributes.ARMOR_TOUGHNESS).getValue()
				);
			}

			// ref: net.minecraft.entity.LivingEntity#applyEnchantmentsToDamage (mc < 1.19)
			//      net.minecraft.entity.LivingEntity#modifyAppliedDamage (mc > 1.19)
			if (!(
					//#if MC >= 11904
					//$$ this.damageSource.isIn(DamageTypeTags.BYPASSES_EFFECTS)
					//#else
					this.damageSource.isUnblockable()
					//#endif
			))
			{
				if (this.entity.hasStatusEffect(StatusEffects.RESISTANCE) && this.damageSource !=
						//#if MC >= 11904
						//$$ this.entity.getDamageSources().outOfWorld()
						//#else
						DamageSource.OUT_OF_WORLD
						//#endif
				)
				{
					int level = Objects.requireNonNull(this.entity.getStatusEffect(StatusEffects.RESISTANCE)).getAmplifier() + 1;
					amount = Math.max(amount * (1 - level / 5.0F), 0.0F);
				}

				if (amount > 0.0F)
				{
					int level = EnchantmentHelper.getProtectionAmount(this.entity.getArmorItems(), this.damageSource);
					if (level > 0)
					{
						amount = DamageUtil.getInflictedDamage(amount, (float) level);
					}
				}
			}
		}

		this.damageAmount = amount;
		return this;
	}

	public DamageCalculator applyAbsorption()
	{
		this.checkAndSetStage(ApplyStage.ABSORPTION);
		this.damageAmount = Math.max(this.damageAmount - this.entity.getAbsorptionAmount(), 0.0F);
		return this;
	}

	private enum ApplyStage
	{
		NONE,
		DIFFICULTY,
		ARMOR,
		ABSORPTION
	}
}
