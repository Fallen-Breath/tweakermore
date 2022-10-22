package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.disableSlimeBlockBouncing;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.block.SlimeBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static me.fallenbreath.tweakermore.util.ModIds.minecraft;

@Restriction(require = @Condition(value = minecraft, versionPredicates = "<1.15"))
@Mixin(SlimeBlock.class)
public abstract class BedBlockMixin
{
	@Redirect(
			method = "onLandedUpon",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/entity/Entity;isSneaking()Z"
			),
			require = 0
	)
	private boolean disableSlimeBlockBouncing(Entity entity)
	{
		if (TweakerMoreConfigs.DISABLE_SLIME_BLOCK_BOUNCING.getBooleanValue())
		{
			if (entity == MinecraftClient.getInstance().player)
			{
				return true;
			}
		}
		// vanilla
		return entity.isSneaking();
	}
}
