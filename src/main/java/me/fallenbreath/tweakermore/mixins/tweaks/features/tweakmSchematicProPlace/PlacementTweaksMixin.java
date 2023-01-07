package me.fallenbreath.tweakermore.mixins.tweaks.features.tweakmSchematicProPlace;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.impl.features.tweakmSchematicProPlace.ProPlaceImpl;
import me.fallenbreath.tweakermore.util.ModIds;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//#if MC < 11900
import net.minecraft.client.world.ClientWorld;
//#endif

@Restriction(require = @Condition(ModIds.litematica))
@Mixin(ClientPlayerInteractionManager.class)
public abstract class PlacementTweaksMixin
{
	@Inject(method = "interactBlock", at = @At("HEAD"), cancellable = true)
	private void tweakmSchematicProPlace(
			ClientPlayerEntity player,
			//#if MC < 11900
			ClientWorld world,
			//#endif
			Hand hand,
			BlockHitResult hitResult,
			CallbackInfoReturnable<ActionResult> cir
	)
	{
		ItemPlacementContext ctx = new ItemPlacementContext(new ItemUsageContext(player, hand, hitResult));

		ProPlaceImpl.handleRightClick(hitResult, ctx, cir);
	}
}
