package me.fallenbreath.tweakermore.mixins.tweaks.tweakmAutoContainerProcess;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.impl.tweakmAutoContainerProcess.AutoProcessableScreen;
import me.fallenbreath.tweakermore.util.ModIds;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Restriction(require = @Condition(ModIds.itemscroller))
@Mixin(Screen.class)
public abstract class ScreenMixin implements AutoProcessableScreen
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
