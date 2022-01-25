package me.fallenbreath.tweakermore.mixins.tweaks.shulkerTooltipEnchantmentHint;

import com.google.common.collect.Lists;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Formatting;
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
	private static final int MAX_ENCHANTMENT_DISPLAY = 3;

	@Inject(
			method = "buildTooltip",
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
	private void shulkerTooltipEnchantmentHint(ItemStack shulker, BlockView view, List<Text> tooltip, TooltipContext options, CallbackInfo ci, CompoundTag compoundTag, DefaultedList<ItemStack> defaultedList, int i_, int j_, Iterator<ItemStack> var9, ItemStack itemStack, Text text)
	{

		if (TweakerMoreConfigs.SHULKER_TOOLTIP_ENCHANTMENT_HINT.getBooleanValue())
		{
			List<Text> enchantmentTexts = Lists.newArrayList();
			ListTag enchantmentTag = itemStack.getItem() instanceof EnchantedBookItem ? EnchantedBookItem.getEnchantmentTag(itemStack) : itemStack.getEnchantments();
			ItemStack.appendEnchantments(enchantmentTexts, enchantmentTag);
			int amount = enchantmentTexts.size();
			if (amount > 0)
			{
				text.append(new LiteralText(" | ").formatted(Formatting.DARK_GRAY));
				for (int i = 0; i < Math.min(amount, MAX_ENCHANTMENT_DISPLAY); i++)
				{
					text.append(enchantmentTexts.get(i));
					if (i < amount - 1)
					{
						text.append(new LiteralText(", ").formatted(Formatting.GRAY));
					}
				}
				if (amount > MAX_ENCHANTMENT_DISPLAY)
				{
					text.append(new TranslatableText("shulkerTooltipEnchantmentHint.more", amount- MAX_ENCHANTMENT_DISPLAY).formatted(Formatting.GRAY));
				}
			}
		}
	}
}
