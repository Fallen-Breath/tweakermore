package me.fallenbreath.tweakermore.gui;

import com.google.common.collect.Lists;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.widgets.WidgetLabel;
import fi.dy.masa.malilib.util.StringUtils;
import me.fallenbreath.tweakermore.TweakerMoreMod;
import me.fallenbreath.tweakermore.config.Config;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TweakermoreConfigGui extends GuiConfigsBase
{
    private static Tabs tab = Tabs.TWEAKS;
    @Nullable
    private Config.Type filteredType = null;
    @Nullable
    private SelectorDropDownList<Config.Type> typeFilterDropDownList = null;

    public TweakermoreConfigGui()
    {
        super(10, 50, TweakerMoreMod.MOD_ID, null, "tweakermore.gui.title", TweakerMoreMod.VERSION);
    }

    public static Tabs getCurrentTab()
    {
        return tab;
    }

    @Override
    public void initGui()
    {
        super.initGui();
        this.clearOptions();

        int x = 10;
        int y = 26;

        for (Tabs tab : Tabs.values())
        {
            x += this.createNavigationButton(x, y, tab);
        }

        if (TweakermoreConfigGui.tab != Tabs.CONFIG)
        {
            List<Config.Type> items = Arrays.stream(Config.Type.values()).filter(type -> type != Config.Type.CONFIG).collect(Collectors.toList());
            items.add(0, null);
            SelectorDropDownList<Config.Type> dd = new SelectorDropDownList<>(this.width - 91, this.getListY() + 3, 80, 16, 200, items.size(), items);
            dd.setEntryChangeListener(type -> {
                if (type != this.filteredType)
                {
                    this.filteredType = type;
                    this.reDraw();
                }
            });
            dd.setZLevel(3);
            dd.setSelectedEntry(this.filteredType);
            this.addWidget(dd);
            this.typeFilterDropDownList = dd;

            String labelTextKey = "tweakermore.gui.config_type.label_text";
            int labelWidth = this.getStringWidth(StringUtils.translate(labelTextKey));
            WidgetLabel label = new WidgetLabel(dd.getX() - labelWidth - 5, dd.getY() + 1, labelWidth, dd.getHeight(), 0xFFE0E0E0, labelTextKey);
            this.addWidget(label);
        }
        else
        {
            this.typeFilterDropDownList = null;
        }
    }

    private int createNavigationButton(int x, int y, Tabs tab)
    {
        ButtonGeneric button = new ButtonGeneric(x, y, -1, 20, tab.getDisplayName());
        button.setEnabled(TweakermoreConfigGui.tab != tab);
        this.addButton(button, (b, mouseButton) -> {
            TweakermoreConfigGui.tab = tab;
            this.reDraw();
        });
        return button.getWidth() + 2;
    }

    private void reDraw()
    {
        this.reCreateListWidget(); // apply the new config width
        Objects.requireNonNull(this.getListWidget()).resetScrollbarPosition();
        this.initGui();
    }

    public void renderDropDownList(int mouseX, int mouseY)
    {
        if (this.typeFilterDropDownList != null)
        {
            this.typeFilterDropDownList.render(mouseX, mouseY, this.typeFilterDropDownList.isMouseOver(mouseX, mouseY));
        }
    }

    @Override
    protected int getConfigWidth()
    {
        return 150;
    }

    @Override
    public List<ConfigOptionWrapper> getConfigs()
    {
        List<IConfigBase> configs = Lists.newArrayList();
        switch (TweakermoreConfigGui.tab)
        {
            case TWEAKS:
                configs.addAll(TweakerMoreConfigs.getOptions(type -> type != Config.Type.CONFIG && (this.filteredType == null || type == this.filteredType)));
                break;
            case CONFIG:
                configs.addAll(TweakerMoreConfigs.getOptions(Config.Type.CONFIG));
                break;
        }
        configs.sort((a, b) -> a.getName().compareToIgnoreCase(b.getName()));
        return ConfigOptionWrapper.createFor(configs);
    }

    public enum Tabs
    {
        TWEAKS("tweakermore.gui.button.config_gui.tweaks"),
        CONFIG("tweakermore.gui.button.config_gui.config");

        private final String translationKey;

        Tabs(String translationKey)
        {
            this.translationKey = translationKey;
        }

        public String getDisplayName()
        {
            return StringUtils.translate(this.translationKey);
        }
    }
}
