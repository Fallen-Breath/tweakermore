package me.fallenbreath.tweakermore.gui;

import com.google.common.collect.Lists;
import fi.dy.masa.malilib.config.ConfigType;
import fi.dy.masa.malilib.config.ConfigUtils;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.util.StringUtils;
import me.fallenbreath.tweakermore.TweakerMoreMod;
import me.fallenbreath.tweakermore.config.Config;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.config.TweakerMoreToggles;

import java.util.List;

public class TweakermoreConfigGui extends GuiConfigsBase
{
    private static ConfigGuiTab tab = ConfigGuiTab.TWEAKS;

    public TweakermoreConfigGui()
    {
        super(10, 50, TweakerMoreMod.MOD_ID, null, "tweakermore.gui.title", TweakerMoreMod.VERSION);
    }

    @Override
    public void initGui()
    {
        super.initGui();
        this.clearOptions();

        int x = 10;
        int y = 26;

        for (ConfigGuiTab tab : ConfigGuiTab.values())
        {
            x += this.createButton(x, y, -1, tab);
        }
    }

    private int createButton(int x, int y, int width, ConfigGuiTab tab)
    {
        ButtonGeneric button = new ButtonGeneric(x, y, width, 20, tab.getDisplayName());
        button.setEnabled(TweakermoreConfigGui.tab != tab);
        this.addButton(button, new ButtonListener(tab, this));

        return button.getWidth() + 2;
    }

    @Override
    protected int getConfigWidth()
    {
        return 120;
    }

    @Override
    public List<ConfigOptionWrapper> getConfigs()
    {
        List<IConfigBase> configs = Lists.newArrayList();
        switch (TweakermoreConfigGui.tab)
        {
            case TWEAKS:
                configs.addAll(ConfigUtils.createConfigWrapperForType(ConfigType.BOOLEAN, TweakerMoreToggles.getFeatureToggles()));
                configs.addAll(TweakerMoreConfigs.getOptions(type -> type != Config.Type.HOTKEY && type != Config.Type.CONFIG));
                break;
            case HOTKEYS:
                configs.addAll(TweakerMoreConfigs.getOptions(Config.Type.HOTKEY));
                configs.addAll(ConfigUtils.createConfigWrapperForType(ConfigType.HOTKEY, TweakerMoreToggles.getFeatureToggles()));
                break;
            case CONFIG:
                configs.addAll(TweakerMoreConfigs.getOptions(Config.Type.CONFIG));
                break;
        }
        configs.sort((a, b) -> a.getName().compareToIgnoreCase(b.getName()));
        return ConfigOptionWrapper.createFor(configs);
    }

    private static class ButtonListener implements IButtonActionListener
    {
        private final TweakermoreConfigGui parent;
        private final ConfigGuiTab tab;

        public ButtonListener(ConfigGuiTab tab, TweakermoreConfigGui parent)
        {
            this.tab = tab;
            this.parent = parent;
        }

        @Override
        public void actionPerformedWithButton(ButtonBase button, int mouseButton)
        {
            TweakermoreConfigGui.tab = this.tab;
            this.parent.reCreateListWidget(); // apply the new config width
            this.parent.getListWidget().resetScrollbarPosition();
            this.parent.initGui();
        }
    }

    public enum ConfigGuiTab
    {
        TWEAKS("tweakermore.gui.button.config_gui.tweaks"),
        HOTKEYS("tweakermore.gui.button.config_gui.hotkeys"),
        CONFIG("tweakermore.gui.button.config_gui.config");

        private final String translationKey;

        ConfigGuiTab(String translationKey)
        {
            this.translationKey = translationKey;
        }

        public String getDisplayName()
        {
            return StringUtils.translate(this.translationKey);
        }
    }
}
