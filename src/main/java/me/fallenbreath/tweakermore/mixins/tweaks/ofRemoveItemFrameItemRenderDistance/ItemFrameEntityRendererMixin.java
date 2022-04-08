package me.fallenbreath.tweakermore.mixins.tweaks.ofRemoveItemFrameItemRenderDistance;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.client.render.entity.ItemFrameEntityRenderer;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static me.fallenbreath.tweakermore.util.ModIds.optifine;

@Restriction(require = @Condition(optifine))
@Mixin(ItemFrameEntityRenderer.class)
public abstract class ItemFrameEntityRendererMixin
{
	@SuppressWarnings("target")
	@Dynamic("Added by optifine")
	@Shadow(remap = false)
	private static double itemRenderDistanceSq;

	@Dynamic("Added by optifine")
	@Redirect(
			method = "isRenderItem",
			at = @At(
					value = "FIELD",
					target = "Lnet/minecraft/client/render/entity/ItemFrameEntityRenderer;itemRenderDistanceSq:D",
					remap = true
			),
			remap = false
	)
	private double ofDisableItemFrameItemRenderDistance()
	{
		if (TweakerMoreConfigs.OF_REMOVE_ITEM_FRAME_ITEM_RENDER_DISTANCE.getBooleanValue())
		{
			return Double.MAX_VALUE;
		}
		// "vanilla" optifine behavior
		return itemRenderDistanceSq;
	}
}
