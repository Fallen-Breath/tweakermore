package me.fallenbreath.tweakermore.mixins.tweaks.features.creativePickBlockWithState;

import fi.dy.masa.malilib.util.InfoUtils;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin
{
	@Inject(
			method = "doItemPick",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/item/ItemStack;isEmpty()Z",
					ordinal = 0
			),
			locals = LocalCapture.CAPTURE_FAILHARD
	)
	private void creativePickBlockWithState_storeStateInTag(CallbackInfo ci, boolean isCreative, ItemStack itemStack, HitResult.Type type, BlockPos blockPos, BlockState blockState, Block block)
	{
		if (isCreative && !itemStack.isEmpty())
		{
			if (TweakerMoreConfigs.CREATIVE_PICK_BLOCK_WITH_STATE.isKeybindHeld())
			{
				Item item = itemStack.getItem();
				// make sure the picked item is exactly what the selected block indicates
				// to avoid things like storing piston head's states into piston item which is not good
				if (item instanceof BlockItem && ((BlockItem)item).getBlock() != blockState.getBlock())
				{
					return;
				}

				CompoundTag nbt = new CompoundTag();
				blockState.getEntries().forEach((property, value) -> {
					nbt.putString(property.getName(), value.toString());
				});
				itemStack.getOrCreateTag().put("BlockStateTag", nbt);

				InfoUtils.printActionbarMessage("tweakermore.impl.creativePickBlockWithState.message", block.getName());
			}
		}
	}
}
