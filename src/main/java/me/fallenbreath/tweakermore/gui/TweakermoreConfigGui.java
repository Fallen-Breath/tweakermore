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
import me.fallenbreath.tweakermore.config.TweakerMoreOption;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class TweakermoreConfigGui extends GuiConfigsBase
{
    private static Config.Category category = Config.Category.MC_TWEAKS;
    @Nullable
    private Config.Type filteredType = null;
    @Nullable
    private SelectorDropDownList<Config.Type> typeFilterDropDownList = null;

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

        for (Config.Category category : Config.Category.values())
        {
            x += this.createNavigationButton(x, y, category);
        }

        Set<Config.Type> possibleTypes = TweakerMoreConfigs.getOptions(TweakermoreConfigGui.category).stream().map(TweakerMoreOption::getType).collect(Collectors.toSet());
        List<Config.Type> items = Arrays.stream(Config.Type.values()).filter(possibleTypes::contains).collect(Collectors.toList());
        items.add(0, null);
        SelectorDropDownList<Config.Type> dd = new SelectorDropDownList<>(this.width - 91, this.getListY() + 3, 80, 16, 200, items.size(), items);
        dd.setEntryChangeListener(type -> {
            if (type != this.filteredType)
            {
                this.filteredType = type;
                this.reDraw();
            }
        });
        this.addWidget(dd);
        this.typeFilterDropDownList = dd;
        dd.setSelectedEntry(this.filteredType);

        String labelTextKey = "tweakermore.gui.config_type.label_text";
        int labelWidth = this.getStringWidth(StringUtils.translate(labelTextKey));
        WidgetLabel label = new WidgetLabel(dd.getX() - labelWidth - 5, dd.getY() + 1, labelWidth, dd.getHeight(), 0xFFE0E0E0, labelTextKey);
        this.addWidget(label);
    }

    private int createNavigationButton(int x, int y, Config.Category category)
    {
        ButtonGeneric button = new ButtonGeneric(x, y, -1, 20, category.getDisplayName());
        button.setEnabled(TweakermoreConfigGui.category != category);
        this.addButton(button, (b, mouseButton) -> {
            TweakermoreConfigGui.category = category;
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
        List<IConfigBase> options = Lists.newArrayList();
        for (TweakerMoreOption tweakerMoreOption : TweakerMoreConfigs.getOptions(TweakermoreConfigGui.category))
        {
            if (this.filteredType == null || tweakerMoreOption.getType() == this.filteredType)
            {
                options.add(tweakerMoreOption.getOption());
            }
        }
        options.sort((a, b) -> a.getName().compareToIgnoreCase(b.getName()));
        return ConfigOptionWrapper.createFor(options);
    }
}
