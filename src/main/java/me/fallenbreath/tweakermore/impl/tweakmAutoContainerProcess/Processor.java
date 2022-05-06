package me.fallenbreath.tweakermore.impl.tweakmAutoContainerProcess;

import fi.dy.masa.malilib.config.options.ConfigBoolean;
import me.fallenbreath.tweakermore.config.options.TweakerMoreConfigBoolean;
import me.fallenbreath.tweakermore.config.options.TweakerMoreConfigBooleanHotkeyed;
import me.fallenbreath.tweakermore.config.options.TweakerMoreIConfigBase;
import net.minecraft.client.gui.screen.ingame.ContainerScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.container.Slot;

import java.util.List;

public interface Processor
{
	default boolean isEnabled()
	{
		return getConfig().getBooleanValue();
	}

	TweakerMoreConfigBooleanHotkeyed getConfig();

	boolean process(ClientPlayerEntity player, ContainerScreen<?> containerScreen, List<Slot> allSlots, List<Slot> playerInvSlots, List<Slot> containerInvSlots);
}
