package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.signMultilinePasteSupport;

import me.fallenbreath.tweakermore.impl.mc_tweaks.signMultilinePasteSupport.SelectionManagerInSignEditScreen;
import me.fallenbreath.tweakermore.impl.mc_tweaks.signMultilinePasteSupport.SignEditScreenRowIndexController;
import net.minecraft.client.gui.screen.ingame.SignEditScreen;
import net.minecraft.client.util.SelectionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SignEditScreen.class)
public abstract class SignEditScreenMixin implements SignEditScreenRowIndexController
{
	@Shadow private SelectionManager selectionManager;
	@Shadow private int currentRow;

	@Inject(method = "init", at = @At("TAIL"))
	private void signMultilinePasteSupport_storeSelfInSelectionManager(CallbackInfo ci)
	{
		((SelectionManagerInSignEditScreen)this.selectionManager).setSignEditScreen$TKM(this);
	}

	@Override
	public boolean canAddCurrentRowIndex(int delta)
	{
		int newIndex = this.currentRow + delta;
		return 0 <= newIndex && newIndex <= 3;
	}

	@Override
	public void addCurrentRowIndex(int delta)
	{
		if (this.canAddCurrentRowIndex(delta))
		{
			this.currentRow += delta;
		}
	}
}
