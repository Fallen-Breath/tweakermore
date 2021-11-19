package me.fallenbreath.tweakermore.mixins.tweaks.tweakmAutoContainerProcess;

import me.fallenbreath.tweakermore.impl.tweakmAutoContainerProcess.IScreen;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Screen.class)
public abstract class ScreenMixin implements IScreen
{
	@Unique
	private boolean shouldProcessFlag = false;

	@Override
	public void setShouldProcess(boolean value)
	{
		this.shouldProcessFlag = value;
	}

	@Override
	public boolean shouldProcess()
	{
		return this.shouldProcessFlag;
	}
}
