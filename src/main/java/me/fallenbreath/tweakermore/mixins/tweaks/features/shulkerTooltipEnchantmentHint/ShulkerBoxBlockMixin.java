package me.fallenbreath.tweakermore.mixins.tweaks.features.shulkerTooltipEnchantmentHint;

import me.fallenbreath.tweakermore.impl.shulkerTooltipHints.ShulkerToolTipEnhancer;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Text;
import net.minecraft.util.DefaultedList;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.List;

//#if MC >= 11600
//$$ import net.minecraft.text.MutableText;
//#endif

@Mixin(ShulkerBoxBlock.class)
public abstract class ShulkerBoxBlockMixin
{
	@Inject(
			//#if MC >= 11600
			//$$ method = "appendTooltip",
			//#else
			method = "buildTooltip",
			//#endif
			slice = @Slice(
					from = @At(
							value = "CONSTANT",
							args = "stringValue= x"
					)
			),
			at = @At(
					value = "INVOKE",
					target = "Ljava/util/List;add(Ljava/lang/Object;)Z",
					ordinal = 0
			),
			locals = LocalCapture.CAPTURE_FAILHARD
	)
	private void shulkerTooltipEnchantmentHint(
			ItemStack shulker, BlockView view, List<Text> tooltip, TooltipContext options,
			CallbackInfo ci,
			CompoundTag compoundTag, DefaultedList<ItemStack> defaultedList, int i_, int j_, Iterator<ItemStack> var9, ItemStack itemStack,
			//#if MC >= 11600
			//$$ MutableText text
			//#else
			Text text
			//#endif
	)
	{
		ShulkerToolTipEnhancer.appendEnchantmentHints(itemStack, text);
	}
}
