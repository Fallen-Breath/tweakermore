package me.fallenbreath.tweakermore.mixins.tweaks.tweakmUnlimitedEntityRenderDistance;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin
{
	@Inject(method = "shouldRender(DDD)Z", at = @At("HEAD"), cancellable = true)
	private void tweakmUnlimitedEntityRenderDistance(CallbackInfoReturnable<Boolean> cir)
	{
		if (TweakerMoreConfigs.TWEAKM_UNLIMITED_ENTITY_RENDER_DISTANCE.getBooleanValue())
		{
			cir.setReturnValue(true);
		}
	}
}
