package me.fallenbreath.tweakermore.impl.tweakmAutoContainerProcess;

import net.minecraft.client.gui.screen.ingame.ContainerScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.container.Slot;

import java.util.List;

public interface Processor
{
	boolean isEnabled();

	boolean process(ClientPlayerEntity player, ContainerScreen<?> containerScreen, List<Slot> allSlots, List<Slot> playerInvSlots, List<Slot> containerInvSlots);
}
