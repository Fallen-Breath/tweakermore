package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.spectatorTeleportMenuIncludeSpectator;

import me.fallenbreath.tweakermore.impl.mc_tweaks.spectatorTeleportMenuIncludeSpectator.CommandEntryWithSpectatorMark;
import net.minecraft.client.gui.hud.spectator.TeleportToSpecificPlayerSpectatorCommand;
import net.minecraft.text.BaseText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TeleportToSpecificPlayerSpectatorCommand.class)
public abstract class TeleportToSpecificPlayerSpectatorCommandMixin implements CommandEntryWithSpectatorMark
{
	private boolean isSpectator$tkm = false;

	@Override
	public void setIsSpectator(boolean value)
	{
		this.isSpectator$tkm = value;
	}

	/**
	 * Make the spectator name have style like in player list hud (gray italic)
	 */
	@Inject(method = "getName", at = @At("TAIL"))
	private void spectatorTeleportMenuIncludeSpectator_modifyEntryNameForSpectator(CallbackInfoReturnable<Text> cir)
	{
		if (this.isSpectator$tkm)
		{
			BaseText text = (BaseText)cir.getReturnValue();
			Style style = text.getStyle();

			//#if MC >= 11600
			//$$ style = style.withColor(Formatting.GRAY);
			//$$ style = style.withItalic(true);
			//#else
			style.setColor(Formatting.GRAY);
			style.setItalic(true);
			//#endif

			text.setStyle(style);
		}
	}
}
