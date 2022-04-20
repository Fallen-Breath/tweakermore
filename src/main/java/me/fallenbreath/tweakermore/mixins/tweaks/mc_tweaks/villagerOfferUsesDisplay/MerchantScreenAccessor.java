package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.villagerOfferUsesDisplay;

import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MerchantScreen.class)
public interface MerchantScreenAccessor
{
	@Accessor(
			//#if MC < 11500
			//$$ "field_19163"
			//#endif
	)
	int getIndexStartOffset();
}
