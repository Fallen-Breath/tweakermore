package me.fallenbreath.tweakermore.impl.tweakmAutoContainerProcess;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.screen.slot.Slot;

import java.util.List;

public interface Processor
{
	boolean isEnabled();

	boolean process(ClientPlayerEntity player, HandledScreen<?> containerScreen, List<Slot> allSlots, List<Slot> playerInvSlots, List<Slot> containerInvSlots);
}
