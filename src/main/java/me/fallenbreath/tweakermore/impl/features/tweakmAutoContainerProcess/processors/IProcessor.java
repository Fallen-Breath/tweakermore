package me.fallenbreath.tweakermore.impl.features.tweakmAutoContainerProcess.processors;

import me.fallenbreath.tweakermore.config.options.TweakerMoreConfigBooleanHotkeyed;
import net.minecraft.client.gui.screen.ingame.ContainerScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.container.Slot;

import java.util.List;

public interface IProcessor
{
	default boolean isEnabled()
	{
		TweakerMoreConfigBooleanHotkeyed config = this.getConfig();
		return config.getBooleanValue() && config.getTweakerMoreOption().isEnabled();
	}

	TweakerMoreConfigBooleanHotkeyed getConfig();

	boolean shouldProcess(ContainerScreen<?> containerScreen);

	boolean process(ClientPlayerEntity player, ContainerScreen<?> containerScreen, List<Slot> allSlots, List<Slot> playerInvSlots, List<Slot> containerInvSlots);
}
