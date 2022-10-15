package me.fallenbreath.tweakermore.util.damage;

import me.fallenbreath.tweakermore.TweakerMoreMod;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.DamageUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.explosion.Explosion;

import java.util.Objects;

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
		return new DamageCalculator(entity, damage, DamageSource.netherBed());
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

		// ref: net.minecraft.entity.LivingEntity.applyDamage
		{
			// ref: net.minecraft.entity.LivingEntity.applyArmorToDamage
			if (!this.damageSource.bypassesArmor())
			{
				amount = DamageUtil.getDamageLeft(
						amount, this.entity.getArmor(),
						(float)this.entity.getAttributeInstance(EntityAttributes.ARMOR_TOUGHNESS).getValue()
				);
			}

			// ref: net.minecraft.entity.LivingEntity.applyEnchantmentsToDamage
			if (!this.damageSource.isUnblockable())
			{
				if (this.entity.hasStatusEffect(StatusEffects.RESISTANCE) && this.damageSource != DamageSource.OUT_OF_WORLD)
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
