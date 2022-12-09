package me.fallenbreath.tweakermore.mixins.tweaks.features.villagerOfferUsesDisplay;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.village.TradeOffer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//#if MC >= 11600
//$$ import net.minecraft.client.util.math.MatrixStack;
//$$ import net.minecraft.text.Text;
//$$ import me.fallenbreath.tweakermore.util.Messenger;
//#endif

@Mixin(targets = "net.minecraft.client.gui.screen.ingame.MerchantScreen$WidgetButtonPage")
public abstract class MerchantScreenWidgetButtonPageMixin extends ButtonWidget
{
	@SuppressWarnings("target")
	@Shadow @Final MerchantScreen field_19166;

	@Shadow @Final
	//#if MC >= 11500
	int index;
	//#else
	//$$ int field_19165;
	//#endif

	public MerchantScreenWidgetButtonPageMixin(
			int x, int y, int width, int height,
			//#if MC >= 11600
			//$$ Text message,
			//#else
			String message,
			//#endif
			PressAction onPress

			//#if MC >= 11903
			//$$ , NarrationSupplier narrationSupplier
			//#endif
	)
	{
		super(
				x, y, width, height, message, onPress
				//#if MC >= 11903
				//$$ , narrationSupplier
				//#endif
		);
	}

	@Inject(
			//#if MC >= 11700
			//$$ method = "renderTooltip(Lnet/minecraft/client/util/math/MatrixStack;II)V",
			//#elseif MC >= 11600
			//$$ method = "renderToolTip(Lnet/minecraft/client/util/math/MatrixStack;II)V",
			//#else
			method = "renderToolTip(II)V",
			//#endif
			at = @At(
					//#if MC >= 11903
					//$$ value = "INVOKE",
					//$$ target = "Lnet/minecraft/client/gui/screen/ingame/MerchantScreen$WidgetButtonPage;getX()I",
					//#else
					value = "FIELD",
					target = "Lnet/minecraft/client/gui/screen/ingame/MerchantScreen$WidgetButtonPage;x:I",
					//#endif
					ordinal = 0
			)
	)
	private void renderMaxUsesAmount(
			//#if MC >= 11600
			//$$ MatrixStack matrices,
			//#endif
			int mouseX, int mouseY, CallbackInfo ci
	)
	{
		if (TweakerMoreConfigs.VILLAGER_OFFER_USES_DISPLAY.getBooleanValue())
		{
			int x =
					//#if MC >= 11903
					//$$ this.getX();
					//#else
					this.x;
					//#endif
			if (x + 50 <= mouseX && mouseX <= x + 65)
			{
				//#if MC >= 11500
				int index = this.index;
				//#else
				//$$ int index = this.field_19165;
				//#endif
				TradeOffer offer = this.field_19166.getContainer().getRecipes().get(index + ((MerchantScreenAccessor)this.field_19166).getIndexStartOffset());

				String text = String.format("%d / %d", offer.getUses(), offer.getMaxUses());
				this.field_19166.renderTooltip(
						//#if MC >= 11600
						//$$ matrices, Messenger.s(text),
						//#else
						text,
						//#endif
						mouseX, mouseY)
				;
			}
		}
	}
}
