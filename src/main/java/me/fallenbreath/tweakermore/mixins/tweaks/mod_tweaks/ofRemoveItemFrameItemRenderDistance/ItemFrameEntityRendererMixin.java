package me.fallenbreath.tweakermore.mixins.tweaks.mod_tweaks.ofRemoveItemFrameItemRenderDistance;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.util.ModIds;
import net.minecraft.client.render.entity.ItemFrameEntityRenderer;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Restriction(require = @Condition(ModIds.optifine))
@Mixin(ItemFrameEntityRenderer.class)
public abstract class ItemFrameEntityRendererMixin
{
	@Dynamic("Added by optifine")
	@ModifyExpressionValue(
			method = "isRenderItem",
			at = @At(
					value = "FIELD",
					target = "Lnet/minecraft/client/render/entity/ItemFrameEntityRenderer;itemRenderDistanceSq:D",
					remap = true
			),
			remap = false
	)
	private double ofDisableItemFrameItemRenderDistance(double itemRenderDistance)
	{
		if (TweakerMoreConfigs.OF_REMOVE_ITEM_FRAME_ITEM_RENDER_DISTANCE.getBooleanValue())
		{
			itemRenderDistance = Double.MAX_VALUE;
		}
		return itemRenderDistance;
	}
}
