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
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.RayTraceContext;
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

	public static float getBlockDensity(Vec3d center, Entity entity)
	{
		Box bb = entity.getBoundingBox();
		double d0 = 1.0D / ((bb.x2 - bb.x1) * 2.0D + 1.0D);
		double d1 = 1.0D / ((bb.y2 - bb.y1) * 2.0D + 1.0D);
		double d2 = 1.0D / ((bb.z2 - bb.z1) * 2.0D + 1.0D);
		double d3 = (1.0D - Math.floor(1.0D / d0) * d0) / 2.0D;
		double d4 = (1.0D - Math.floor(1.0D / d2) * d2) / 2.0D;

		if (!(d0 < 0.0D) && !(d1 < 0.0D) && !(d2 < 0.0D))
		{
			int i = 0;
			int j = 0;

			for (float f = 0.0F; f <= 1.0F; f = (float)((double)f + d0))
			{
				for (float f1 = 0.0F; f1 <= 1.0F; f1 = (float)((double)f1 + d1))
				{
					for (float f2 = 0.0F; f2 <= 1.0F; f2 = (float)((double)f2 + d2))
					{
						double d5 = bb.x1 + (bb.x2 - bb.x1) * (double)f;
						double d6 = bb.y1 + (bb.y2 - bb.y1) * (double)f1;
						double d7 = bb.z1 + (bb.z2 - bb.z1) * (double)f2;

						if (entity.world.rayTrace(
								new RayTraceContext(new Vec3d(d5 + d3, d6, d7 + d4), center,
										RayTraceContext.ShapeType.OUTLINE, RayTraceContext.FluidHandling.NONE, entity
								)
						).getType() == HitResult.Type.MISS)
						{
							++i;
						}

						++j;
					}
				}
			}

			return (float)i / (float)j;
		}
		else
		{
			return 0.0F;
		}
	}

	public static DamageCalculator explosion(Vec3d explosionCenter, float explosionPower, LivingEntity entity)
	{
		float exposure =
				System.currentTimeMillis() / 1000 % 2 == 0 ?
				getBlockDensity(explosionCenter, entity) :
				Explosion.getExposure(explosionCenter, entity);
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
