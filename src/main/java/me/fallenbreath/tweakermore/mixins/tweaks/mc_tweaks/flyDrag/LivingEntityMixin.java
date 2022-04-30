package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.flyDrag;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity
{
	public LivingEntityMixin(EntityType<?> type, World world)
	{
		super(type, world);
	}

	@ModifyVariable(
			method = "travel",
			at = @At(
					value = "INVOKE",
					//#if MC >= 11700
					//$$ target = "Lnet/minecraft/entity/LivingEntity;applyMovementInput(Lnet/minecraft/util/math/Vec3d;F)Lnet/minecraft/util/math/Vec3d;"
					//#elseif MC >= 11600
					//$$ target = "Lnet/minecraft/entity/LivingEntity;method_26318(Lnet/minecraft/util/math/Vec3d;F)Lnet/minecraft/util/math/Vec3d;"
					//#else
					target = "Lnet/minecraft/entity/LivingEntity;applyClimbingSpeed(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;"
					//#endif
			),
			ordinal = 1
	)
	private float creativeFlyDrag(float dragFactor)
	{
		LivingEntity self = (LivingEntity)(Object)this;
		if (self == MinecraftClient.getInstance().player && TweakerMoreConfigs.FLY_DRAG.isModified())
		{
			ClientPlayerEntity player = (ClientPlayerEntity) self;
			PlayerAbilities abilities =
					//#if MC >= 11700
					//$$ player.getAbilities();
					//#else
					player.abilities;
					//#endif

			if (abilities.flying && !this.onGround)
			{
				dragFactor = (float)(1.0 - TweakerMoreConfigs.FLY_DRAG.getDoubleValue());
			}
		}
		return dragFactor;
	}
}
