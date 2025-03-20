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
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;

import java.util.Objects;

//#if MC >= 12103
//$$ import net.minecraft.world.explosion.ExplosionImpl;
//#else
import net.minecraft.world.explosion.Explosion;
//#endif

//#if MC >= 12100
//$$ import net.minecraft.enchantment.Enchantments;
//#endif

//#if MC >= 11904
//$$ import net.minecraft.registry.tag.DamageTypeTags;
//#endif

@SuppressWarnings("UnusedReturnValue")
public class DamageCalculator
{
	private final LivingEntity entity;
	private float damageAmount;
	private final DamageSource damageSource;

	//#if MC < 12100
	@SuppressWarnings("FieldCanBeLocal")
	//#endif
	private ServerWorld serverWorld = null;

	private ApplyStage currentStage;

	private DamageCalculator(LivingEntity entity, float damageAmount, DamageSource damageSource)
	{
		this.entity = entity;
		this.damageAmount = damageAmount;
		this.damageSource = damageSource;
		this.currentStage = ApplyStage.NONE;
	}

	// mc1.21+ EPF calculation needs this
	public void setServerWorld(ServerWorld serverWorld)
	{
		this.serverWorld = serverWorld;
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
		float exposure =
				//#if MC >= 12103
				//$$ ExplosionImpl.calculateReceivedDamage
				//#else
				Explosion.getExposure
				//#endif
						(explosionCenter, entity);
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
						//#if MC >= 12100
						//$$ this.entity,
						//#endif
						amount,
						//#if MC >= 12006
						//$$ this.damageSource,
						//#endif
						this.entity.getArmor(),

						(float)this.entity.
								//#if MC >= 11600
								//$$ getAttributeValue(EntityAttributes.GENERIC_ARMOR_TOUGHNESS)
								//#else
								getAttributeInstance(EntityAttributes.ARMOR_TOUGHNESS).getValue()
								//#endif
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

				//noinspection PointlessBooleanExpression
				if (amount > 0.0F && !(
						//#if MC >= 11904
						//$$ this.damageSource.isIn(DamageTypeTags.BYPASSES_ENCHANTMENTS)
						//#else
						false
						//#endif
				))
				{
					float level = this.calculateProtectionEnchantmentAmount();
					if (level > 0)
					{
						amount = DamageUtil.getInflictedDamage(amount, level);
					}
				}
			}
		}

		this.damageAmount = amount;
		return this;
	}

	private float calculateProtectionEnchantmentAmount()
	{
		//#if MC >= 12100
		//$$ if (this.serverWorld != null)
		//$$ {
		//$$ 	return EnchantmentHelper.getProtectionAmount(this.serverWorld, this.entity, this.damageSource);
		//$$ }
		//$$ else
		//$$ {
		//$$ 	// it's impossible do this client-side
		//$$ 	// what we can do best, is to simulate the basic legacy vanilla behavior
		//$$
		//$$ 	// reference: mc1.20.6 net.minecraft.enchantment.ProtectionEnchantment.getProtectionAmount
		//$$ 	var source = this.damageSource;
		//$$ 	if (source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY))
		//$$ 	{
		//$$ 		return 0;
		//$$ 	}
		//$$
		//$$ 	float epf = 0;
		//$$ 	for (var item : this.entity.getAllArmorItems())
		//$$ 	{
		//$$ 		for (var enchantmentEntry : item.getEnchantments().getEnchantmentEntries())
		//$$ 		{
		//$$ 			var enchantment = enchantmentEntry.getKey().getKey().orElse(null);
		//$$ 			if (enchantment == null)
		//$$ 			{
		//$$ 				continue;
		//$$ 			}
		//$$ 			int level = enchantmentEntry.getIntValue();
		//$$ 			if (enchantment == Enchantments.PROTECTION)
		//$$ 			{
		//$$ 				epf += level;
		//$$ 			}
		//$$ 			else if (enchantment == Enchantments.FIRE_PROTECTION && source.isIn(DamageTypeTags.IS_FIRE))
		//$$ 			{
		//$$ 				epf += level * 2;
		//$$ 			}
		//$$ 			else if (enchantment == Enchantments.FEATHER_FALLING && source.isIn(DamageTypeTags.IS_FALL))
		//$$ 			{
		//$$ 				epf += level * 3;
		//$$ 			}
		//$$ 			else if (enchantment == Enchantments.BLAST_PROTECTION && source.isIn(DamageTypeTags.IS_EXPLOSION))
		//$$ 			{
		//$$ 				epf += level * 2;
		//$$ 			}
		//$$ 			else if (enchantment == Enchantments.PROJECTILE_PROTECTION && source.isIn(DamageTypeTags.IS_PROJECTILE))
		//$$ 			{
		//$$ 				epf += level * 2;
		//$$ 			}
		//$$ 		}
		//$$ 	}
		//$$ 	return epf;
		//$$ }
		//#else
		return EnchantmentHelper.getProtectionAmount(this.entity.getArmorItems(), this.damageSource);
		//#endif
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
