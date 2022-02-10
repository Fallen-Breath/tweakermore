package me.fallenbreath.tweakermore.mixins.tweaks.handRestoreRestriction;

import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.util.InventoryUtils;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.util.ModIds;
import me.fallenbreath.tweakermore.util.mixin.Condition;
import me.fallenbreath.tweakermore.util.mixin.ModRequire;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

@ModRequire(enableWhen = @Condition(ModIds.tweakeroo))
@Mixin(InventoryUtils.class)
public abstract class InventoryUtilsMixin
{

	@Redirect(
			method = "preRestockHand",
			slice = @Slice(
					from = @At(
							value = "FIELD",
							target = "Lfi/dy/masa/tweakeroo/config/FeatureToggle;TWEAK_HAND_RESTOCK:Lfi/dy/masa/tweakeroo/config/FeatureToggle;",
							remap = false
					)
			),
			at = @At(
					value = "INVOKE",
					target = "Lfi/dy/masa/tweakeroo/config/FeatureToggle;getBooleanValue()Z",
					ordinal = 0,
					remap = false
			),
			remap = false
	)
	private static boolean applyHandRestoreRestriction(FeatureToggle featureToggle, /* parent method parameters -> */ PlayerEntity player, Hand hand, boolean allowHotbar)
	{
		Item currentItem = player.getStackInHand(hand).getItem();
		return featureToggle.getBooleanValue() && TweakerMoreConfigs.HAND_RESTORE_RESTRICTION.isAllowed(currentItem);
	}
}
