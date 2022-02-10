package me.fallenbreath.tweakermore.compat.modmenu;

import io.github.prospector.modmenu.api.ModMenuApi;
import me.fallenbreath.tweakermore.TweakerMoreMod;
import me.fallenbreath.tweakermore.gui.TweakerMoreConfigGui;
import net.minecraft.client.gui.screen.Screen;

import java.util.function.Function;

@SuppressWarnings("deprecation")
public class ModMenuApiImpl implements ModMenuApi
{
    public String getModId()
    {
        return TweakerMoreMod.MOD_ID;
    }

    @Override
    public Function<Screen, ? extends Screen> getConfigScreenFactory()
    {
        return (screen) -> {
            TweakerMoreConfigGui gui = new TweakerMoreConfigGui();
            gui.setParent(screen);
            return gui;
        };
    }
}
