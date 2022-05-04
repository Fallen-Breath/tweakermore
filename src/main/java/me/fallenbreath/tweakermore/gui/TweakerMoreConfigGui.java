package me.fallenbreath.tweakermore.gui;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.hotkeys.IKeybind;
import fi.dy.masa.malilib.hotkeys.KeyAction;
import fi.dy.masa.malilib.interfaces.IStringValue;
import fi.dy.masa.malilib.util.StringUtils;
import me.fallenbreath.tweakermore.TweakerMoreMod;
import me.fallenbreath.tweakermore.config.Config;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.config.TweakerMoreOption;
import me.fallenbreath.tweakermore.config.ConfigStatistic;
import me.fallenbreath.tweakermore.util.FabricUtil;
import me.fallenbreath.tweakermore.util.JsonSaveAble;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

//#if MC >= 11600
//$$ import net.minecraft.client.util.math.MatrixStack;
//#endif

import java.util.*;
import java.util.stream.Collectors;

public class TweakerMoreConfigGui extends GuiConfigsBase
{
	@Nullable
	private static TweakerMoreConfigGui currentInstance = null;
	@Nullable
	private Config.Type filteredType = null;
	@Nullable
	private SelectorDropDownList<Config.Type> typeFilterDropDownList = null;

	private SortingStrategy sortingStrategy = SortingStrategy.ALPHABET;
	@Nullable
	private SelectorDropDownList<SortingStrategy> sortingStrategyDropDownList = null;

	private static final Setting SETTING = new Setting();

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

	public static Setting getSetting()
	{
		return SETTING;
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

		x = this.width - 150;
		x = this.initTypeFilterDropDownList(x);
		x = this.initSortingStrategyDropDownList(x);
	}

	private int initTypeFilterDropDownList(int x)
	{
		int y = this.getListY() + 3;
		int height = 16;

		Set<Config.Type> possibleTypes = TweakerMoreConfigs.getOptions(TweakerMoreConfigGui.category).stream().map(TweakerMoreOption::getType).collect(Collectors.toSet());
		List<Config.Type> items = Arrays.stream(Config.Type.values()).filter(possibleTypes::contains).collect(Collectors.toList());
		items.add(0, null);
		SelectorDropDownList<Config.Type> dd = new SelectorDropDownList<>(x, y, 70, height, 200, items.size(), items);
		dd.setEntryChangeListener(type -> {
			if (type != this.filteredType)
			{
				this.filteredType = type;
				this.reDraw();
			}
		});
		dd.setSelectedEntry(this.filteredType);
		dd.setNullEntry(() -> StringUtils.translate("tweakermore.gui.selector_drop_down_list.all"));
		dd.setHoverText("tweakermore.gui.config_type.label_text");
		this.typeFilterDropDownList = dd;
		this.addWidget(this.typeFilterDropDownList);
		x += dd.getWidth() + 10;

		return x;
	}

	private int initSortingStrategyDropDownList(int x)
	{
		int y = this.getListY() + 3;
		int height = 16;

		List<SortingStrategy> items = Arrays.asList(SortingStrategy.values());
		SelectorDropDownList<SortingStrategy> dd = new SelectorDropDownList<>(x, y, 70, height, 200, items.size(), items);
		dd.setEntryChangeListener(strategy -> {
			if (strategy != this.sortingStrategy)
			{
				this.sortingStrategy = strategy;
				this.reDraw();
			}
		});
		dd.setSelectedEntry(this.sortingStrategy);
		dd.setHoverText("tweakermore.gui.sorting_strategy.label_text");
		this.sortingStrategyDropDownList = dd;
		this.addWidget(this.sortingStrategyDropDownList);
		x += dd.getWidth() + 10;

		return x;
	}

	private int createNavigationButton(int x, int y, Config.Category category)
	{
		ButtonGeneric button = new ButtonGeneric(x, y, -1, 20, category.getDisplayName());
		button.setEnabled(SETTING.category != category);
		button.setHoverStrings(category.getDescription());
		this.addButton(button, (b, mouseButton) -> {
			SETTING.category = category;
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

	public void renderDropDownList(
			//#if MC >= 11600
			//$$ MatrixStack matrixStack,
			//#endif
			int mouseX, int mouseY
	)
	{
		if (this.typeFilterDropDownList != null)
		{
			this.typeFilterDropDownList.render(
					mouseX, mouseY, this.typeFilterDropDownList.isMouseOver(mouseX, mouseY)
					//#if MC >= 11600
					//$$ , matrixStack
					//#endif
			);
		}
		if (this.sortingStrategyDropDownList != null)
		{
			this.sortingStrategyDropDownList.render(
					mouseX, mouseY, this.sortingStrategyDropDownList.isMouseOver(mouseX, mouseY)
					//#if MC >= 11600
					//$$ , matrixStack
					//#endif
			);
		}
	}

	public Pair<Integer, Integer> adjustWidths(int guiWidth, int maxTextWidth)
	{
		int labelWidth;
		int panelWidth = 190;

		//#if MC >= 11800
		//$$ guiWidth -= 74;
		//#else
		guiWidth -= 75;
		//#endif

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
		List<TweakerMoreOption> configs = Lists.newArrayList();
		for (TweakerMoreOption tweakerMoreOption : TweakerMoreConfigs.getOptions(SETTING.category))
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
			configs.add(tweakerMoreOption);
		}

		configs.sort((a, b) -> {
			int comp = 0;
			ConfigStatistic as = a.getStatistic(), bs = b.getStatistic();
			switch (this.sortingStrategy)
			{
				case MOST_COMMONLY_USED:
					comp = -Long.compare(as.useAmount, bs.useAmount);
					break;
				case MOST_RECENTLY_USED:
					comp = -Long.compare(as.lastUsedTime, bs.lastUsedTime);
					break;
			}
			return comp == 0 ? comp : a.getConfig().getName().compareToIgnoreCase(b.getConfig().getName());
		});

		return ConfigOptionWrapper.createFor(configs.stream().map(TweakerMoreOption::getConfig).collect(Collectors.toList()));
	}

	private static class Setting implements JsonSaveAble
	{
		public Config.Category category = Config.Category.FEATURES;

		@Override
		public void dumpToJson(JsonObject jsonObject)
		{
			jsonObject.addProperty("category", this.category.name());
		}

		@Override
		public void loadFromJson(JsonObject jsonObject)
		{
			this.category = Config.Category.valueOf(jsonObject.get("category").getAsString());
		}
	}

	private enum SortingStrategy implements IStringValue
	{
		ALPHABET,
		MOST_RECENTLY_USED,
		MOST_COMMONLY_USED;

		@Override
		public String getStringValue()
		{
			return StringUtils.translate("tweakermore.gui.sorting_strategy." + this.name().toLowerCase());
		}
	}
}
