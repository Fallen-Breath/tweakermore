package me.fallenbreath.tweakermore.mixins.tweaks.shulkerTooltipEnchantmentHint;

import me.fallenbreath.tweakermore.impl.shulkerTooltipHints.ShulkerToolTipEnhancer;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.List;

@Mixin(ShulkerBoxBlock.class)
public abstract class ShulkerBoxBlockMixin
{
	@Inject(
			method = "appendTooltip",
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
	private void shulkerTooltipEnchantmentHint(ItemStack shulker, BlockView world, List<Text> tooltip, TooltipContext options, CallbackInfo ci, NbtCompound nbtCompound, DefaultedList<ItemStack> defaultedList, int i_, int j_, Iterator<ItemStack> var9, ItemStack itemStack, MutableText text)
	{
		ShulkerToolTipEnhancer.appendEnchantmentHints(itemStack, text);
	}
}
