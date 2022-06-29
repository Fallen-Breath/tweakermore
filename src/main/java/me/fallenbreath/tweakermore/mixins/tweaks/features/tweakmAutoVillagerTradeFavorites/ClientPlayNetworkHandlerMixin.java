package me.fallenbreath.tweakermore.mixins.tweaks.features.tweakmAutoVillagerTradeFavorites;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.impl.features.tweakmAutoVillagerTradeFavorites.MerchantAutoFavoritesTrader;
import me.fallenbreath.tweakermore.util.ModIds;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Restriction(require = @Condition(ModIds.itemscroller))
@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin
{
	@Shadow private MinecraftClient client;

	@Inject(
			method = "onSetTradeOffers",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/container/MerchantContainer;setRefreshTrades(Z)V",
					shift = At.Shift.AFTER
			)
	)
	private void tweakmAutoVillagerTradeFavorites(CallbackInfo ci)
	{
		MerchantAutoFavoritesTrader.doAutoTrade(this.client);
	}
}
