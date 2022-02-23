package me.fallenbreath.tweakermore.gui;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.widgets.WidgetLabel;
import fi.dy.masa.malilib.hotkeys.IKeybind;
import fi.dy.masa.malilib.hotkeys.KeyAction;
import fi.dy.masa.malilib.util.StringUtils;
import me.fallenbreath.tweakermore.TweakerMoreMod;
import me.fallenbreath.tweakermore.config.Config;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.config.TweakerMoreOption;
import me.fallenbreath.tweakermore.util.FabricUtil;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class TweakerMoreConfigGui extends GuiConfigsBase
{
    @Nullable
    private static TweakerMoreConfigGui currentInstance = null;
    private static Config.Category category = Config.Category.MC_TWEAKS;
    @Nullable
    private Config.Type filteredType = null;
    @Nullable
    private SelectorDropDownList<Config.Type> typeFilterDropDownList = null;

    public TweakerMoreConfigGui()
    {
        super(10, 50, TweakerMoreMod.MOD_ID, null, "tweakermore.gui.title", TweakerMoreMod.VERSION);
        currentInstance = this;
    }

    @Override
    public void removed()
    {
        super.removed();
        currentInstance = null;
    }

    public static Optional<TweakerMoreConfigGui> getCurrentInstance()
    {
        return Optional.ofNullable(currentInstance);
    }

    public static boolean onOpenGuiHotkey(KeyAction keyAction, IKeybind iKeybind)
    {
        GuiBase.openGui(new TweakerMoreConfigGui());
        return true;
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

        Set<Config.Type> possibleTypes = TweakerMoreConfigs.getOptions(TweakerMoreConfigGui.category).stream().map(TweakerMoreOption::getType).collect(Collectors.toSet());
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
        button.setEnabled(TweakerMoreConfigGui.category != category);
        this.addButton(button, (b, mouseButton) -> {
            TweakerMoreConfigGui.category = category;
            this.reDraw();
        });
        return button.getWidth() + 2;
    }

    public void reDraw()
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

    public Pair<Integer, Integer> adjustWidths(int guiWidth, int maxTextWidth)
    {
        int labelWidth;
        int panelWidth = 190;
        guiWidth -= 75;

        // tweak label width first, to make sure the panel is not too close or too far from the label
        labelWidth = MathHelper.clamp(guiWidth - panelWidth, maxTextWidth - 5, maxTextWidth + 100);
        // decrease the panel width if space is not enough
        panelWidth = MathHelper.clamp(guiWidth - labelWidth, 100, panelWidth);
        // decrease the label width for a bit if space is still way not enough (the label text might overlap with the panel now)
        labelWidth = MathHelper.clamp(guiWidth - panelWidth + 25, labelWidth - Math.max((int)(maxTextWidth * 0.4), 30), labelWidth);

        // just in case
        labelWidth = Math.max(labelWidth, 0);
        panelWidth = Math.max(panelWidth, 0);

        return Pair.of(labelWidth, panelWidth);
    }

    @Override
    public List<ConfigOptionWrapper> getConfigs()
    {
        List<IConfigBase> configs = Lists.newArrayList();
        for (TweakerMoreOption tweakerMoreOption : TweakerMoreConfigs.getOptions(TweakerMoreConfigGui.category))
        {
            // drop down list filtering logic
            if (this.filteredType != null && tweakerMoreOption.getType() != this.filteredType)
            {
                continue;
            }
            // hide disable options if config hideDisabledOptions is enabled
            if (TweakerMoreConfigs.HIDE_DISABLE_OPTIONS.getBooleanValue() && !tweakerMoreOption.isEnabled())
            {
                continue;
            }
            // hide options that don't work with current Minecraft versions, unless debug mode on
            if (!tweakerMoreOption.worksForCurrentMCVersion() && !TweakerMoreConfigs.TWEAKERMORE_DEBUG_MODE.getBooleanValue())
            {
                continue;
            }
            // hide debug options unless debug mode on
            if (tweakerMoreOption.isDebug() && !TweakerMoreConfigs.TWEAKERMORE_DEBUG_MODE.getBooleanValue())
            {
                continue;
            }
            // hide dev only options unless debug mode on and is dev env
            if (tweakerMoreOption.isDevOnly() && !(TweakerMoreConfigs.TWEAKERMORE_DEBUG_MODE.getBooleanValue() && FabricUtil.isDevelopmentEnvironment()))
            {
                continue;
            }
            configs.add(tweakerMoreOption.getConfig());
        }
        configs.sort((a, b) -> a.getName().compareToIgnoreCase(b.getName()));
        return ConfigOptionWrapper.createFor(configs);
    }
}
