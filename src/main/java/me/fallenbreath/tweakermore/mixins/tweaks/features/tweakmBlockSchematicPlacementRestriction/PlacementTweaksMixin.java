package me.fallenbreath.tweakermore.mixins.tweaks.features.tweakmBlockSchematicPlacementRestriction;

import fi.dy.masa.litematica.config.Configs;
import fi.dy.masa.malilib.util.PositionUtils;
import fi.dy.masa.tweakeroo.tweaks.PlacementTweaks;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.util.ModIds;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 2000 priority to make it inject later than {@link me.fallenbreath.tweakermore.mixins.tweaks.features.tweakmAutoPickSchematicBlock.PlacementTweaksMixin}
 */
@Restriction(require = {@Condition(ModIds.tweakeroo), @Condition(ModIds.litematica)})
@Mixin(value = PlacementTweaks.class, priority = 2000)
public abstract class PlacementTweaksMixin
{
	@Inject(method = "tryPlaceBlock", at = @At("HEAD"), cancellable = true, remap = false)
	private static void tweakmBlockSchematicPlacementRestriction(
			ClientPlayerInteractionManager controller,
			ClientPlayerEntity player,
			ClientWorld world,
			BlockPos posIn,
			Direction sideIn,
			Direction sideRotatedIn,
			float playerYaw,
			Vec3d hitVec,
			Hand hand,
			PositionUtils.HitPart hitPart,
			boolean isFirstClick,
			CallbackInfoReturnable<ActionResult> cir
	)
	{
		MinecraftClient mc = MinecraftClient.getInstance();
		if (mc.player != null)
		{
			if (
					!Configs.Generic.EASY_PLACE_MODE.getBooleanValue() &&
					!Configs.Generic.PLACEMENT_RESTRICTION.getBooleanValue() &&
					TweakerMoreConfigs.TWEAKM_SCHEMATIC_BLOCK_PLACEMENT_RESTRICTION.getBooleanValue()
			)
			{
				if (WorldUtilsAccessor.invokePlacementRestrictionInEffect(mc))
				{
					cir.setReturnValue(ActionResult.PASS);
				}
			}
		}
	}
}
