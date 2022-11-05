package me.fallenbreath.tweakermore.mixins.tweaks.features.tweakmSchematicProPlace;

import fi.dy.masa.tweakeroo.tweaks.PlacementTweaks;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.impl.features.tweakmSchematicProPlace.ProPlaceImpl;
import me.fallenbreath.tweakermore.util.ModIds;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Restriction(require = {@Condition(ModIds.tweakeroo), @Condition(ModIds.litematica)})
@Mixin(PlacementTweaks.class)
public abstract class PlacementTweaksMixin
{
	@Inject(method = "processRightClickBlockWrapper", at = @At("HEAD"), cancellable = true, remap = false)
	private static void tweakmSchematicProPlace(
			ClientPlayerInteractionManager controller,
			ClientPlayerEntity player,
			ClientWorld world,
			BlockPos posIn,
			Direction sideIn,
			Vec3d hitVecIn,
			Hand hand,
			CallbackInfoReturnable<ActionResult> cir
	)
	{
		BlockHitResult hitResult = new BlockHitResult(hitVecIn, sideIn, posIn, false);
		ItemPlacementContext ctx = new ItemPlacementContext(new ItemUsageContext(player, hand, hitResult));

		ProPlaceImpl.handleRightClick(hitResult, ctx, cir);
	}
}
