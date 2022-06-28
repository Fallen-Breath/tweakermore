package me.fallenbreath.tweakermore.impl.features.tweakmAutoContainerProcess.processors;

import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.ContainerScreen;
import net.minecraft.client.gui.screen.ingame.CraftingTableScreen;

public interface IContainerProcessor extends IProcessor
{
	@Override
	default boolean shouldProcess(ContainerScreen<?> containerScreen)
	{
		return !(containerScreen instanceof AbstractInventoryScreen) && !(containerScreen instanceof CraftingTableScreen);
	}
}
