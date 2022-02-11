package me.fallenbreath.tweakermore.mixins.tweaks.legacyF3NLogic;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Keyboard.class)
public abstract class KeyboardMixin
{
	@Shadow @Final private MinecraftClient client;

	@Inject(
			method = "processF3",
			slice = @Slice(
					from = @At(
							value = "INVOKE",
							target = "Lnet/minecraft/client/Keyboard;copyLookAt(ZZ)V"
					)
			),
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/network/ClientPlayerEntity;isSpectator()Z",
					ordinal = 0
			),
			cancellable = true
	)
	private void legacyF3NLogic(CallbackInfoReturnable<Boolean> cir)
	{
		if (TweakerMoreConfigs.LEGACY_F3_N_LOGIC.getBooleanValue())
		{
			assert this.client.player != null;
			if (this.client.player.isCreative())
			{
				this.client.player.sendChatMessage("/gamemode spectator");
			}
			else
			{
				this.client.player.sendChatMessage("/gamemode creative");
			}
			cir.setReturnValue(true);
		}
	}
}
