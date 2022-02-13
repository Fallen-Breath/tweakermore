package me.fallenbreath.tweakermore.mixins.tweaks.handRestoreRestriction;

import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.tweaks.PlacementTweaks;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.util.ModIds;
import me.fallenbreath.tweakermore.util.dependency.Condition;
import me.fallenbreath.tweakermore.util.dependency.Restriction;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

@Restriction(enableWhen = @Condition(ModIds.tweakeroo))
@Mixin(PlacementTweaks.class)
public abstract class PlacementTweaksMixin
{
	@Redirect(
			method = "onProcessRightClickPre",
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
	private static boolean applyHandRestoreRestriction(FeatureToggle featureToggle, /* parent method parameters -> */ PlayerEntity player, Hand hand)
	{
		Item currentItem = player.getStackInHand(hand).getItem();
		return featureToggle.getBooleanValue() && TweakerMoreConfigs.HAND_RESTORE_RESTRICTION.isAllowed(currentItem);
	}

	@Redirect(
			method = "cacheStackInHand",
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
	private static boolean applyHandRestoreRestriction(FeatureToggle featureToggle, /* parent method parameters -> */ Hand hand)
	{
		boolean result = featureToggle.getBooleanValue();
		PlayerEntity player = MinecraftClient.getInstance().player;
		if (player != null)
		{
			Item currentItem = player.getStackInHand(hand).getItem();
			result &= TweakerMoreConfigs.HAND_RESTORE_RESTRICTION.isAllowed(currentItem);
		}
		return result;
	}

	@Redirect(
			method = "tryRestockHand",
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
	private static boolean applyHandRestoreRestriction(FeatureToggle featureToggle, /* parent method parameters -> */ PlayerEntity player, Hand hand, ItemStack stackOriginal)
	{
		return featureToggle.getBooleanValue() && TweakerMoreConfigs.HAND_RESTORE_RESTRICTION.isAllowed(stackOriginal.getItem());
	}
}
