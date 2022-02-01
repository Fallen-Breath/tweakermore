package me.fallenbreath.tweakermore.compat.modmenu;

import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;
import me.fallenbreath.tweakermore.gui.TweakermoreConfigGui;

public class ModMenuApiImpl implements ModMenuApi
{
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory()
    {
        return (screen) -> {
            TweakermoreConfigGui gui = new TweakermoreConfigGui();
            gui.setParent(screen);
            return gui;
        };
    }
}
