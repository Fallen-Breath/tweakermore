package me.fallenbreath.tweakermore.mixins.tweaks.features.shulkerTooltipFillLevelHint;

import me.fallenbreath.tweakermore.impl.mc_tweaks.shulkerTooltipHints.ShulkerToolTipEnhancer;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin
{
	@Inject(method = "getTooltip", at = @At("TAIL"))
	private void shulkerTooltipFillLevelHint(@Nullable PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> cir)
	{
		ShulkerToolTipEnhancer.applyFillLevelHint((ItemStack)(Object)this, cir.getReturnValue());
	}
}
