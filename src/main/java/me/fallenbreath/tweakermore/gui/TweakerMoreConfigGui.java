/*
 * This file is part of the TweakerMore project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2023  Fallen_Breath and contributors
 *
 * TweakerMore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * TweakerMore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with TweakerMore.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.fallenbreath.tweakermore.gui;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.IConfigResettable;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.GuiTextFieldGeneric;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.widgets.WidgetBase;
import fi.dy.masa.malilib.gui.widgets.WidgetSearchBar;
import fi.dy.masa.malilib.interfaces.IStringValue;
import fi.dy.masa.malilib.util.StringUtils;
import me.fallenbreath.tweakermore.TweakerMoreMod;
import me.fallenbreath.tweakermore.config.Config;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.config.TweakerMoreOption;
import me.fallenbreath.tweakermore.config.options.TweakerMoreIConfigBase;
import me.fallenbreath.tweakermore.mixins.core.gui.access.WidgetSearchBarAccessor;
import me.fallenbreath.tweakermore.util.FabricUtils;
import me.fallenbreath.tweakermore.util.JsonSaveAble;
import me.fallenbreath.tweakermore.util.render.RenderUtils;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//#if MC >= 12111
//$$ import fi.dy.masa.malilib.render.GuiContext;
//#elseif MC >= 12000
//$$ import net.minecraft.client.gui.GuiGraphics;
//#elseif MC >= 11600
//$$ import com.mojang.blaze3d.vertex.PoseStack;
//#endif

public class TweakerMoreConfigGui extends GuiConfigsBase
{
	@Nullable
	private static TweakerMoreConfigGui currentInstance = null;
	private static final Setting SETTING = new Setting();

	private final List<WidgetBase> hoveringWidgets = Lists.newArrayList();
	@Nullable
	private Config.Type filteredType = null;
	private WidgetSearchBar searchBar = null;

	public TweakerMoreConfigGui()
	{
		super(10, 50, TweakerMoreMod.MOD_ID, null, "tweakermore.gui.title", TweakerMoreMod.VERSION);
	}

	@Override
	public void init()
	{
		super.init();
		currentInstance = this;
	}

	@Override
	public void removed()
	{
		currentInstance = null;
		super.removed();
	}

	public static Setting getSetting()
	{
		return SETTING;
	}

	public static Optional<TweakerMoreConfigGui> getCurrentInstance()
	{
		return Optional.ofNullable(currentInstance);
	}

	public void setSearchBar(WidgetSearchBar searchBar)
	{
		this.searchBar = searchBar;
	}

	public static void openGui()
	{
		GuiBase.openGui(new TweakerMoreConfigGui());
	}

	@Override
	public void initGui()
	{
		super.initGui();
		this.clearOptions();

		this.hoveringWidgets.clear();
		int x = 10;
		int y = 26;

		for (Config.Category category : Config.Category.values())
		{
			x += this.createNavigationButton(x, y, category);
		}

		x = this.width - 11;
		x = this.initTypeFilterDropDownList(x) - 5;
		x = this.initSortingStrategyDropDownList(x) - 5;
		if (this.searchBar != null)
		{
			GuiTextFieldGeneric searchBox = ((WidgetSearchBarAccessor)this.searchBar).getSearchBox();
			int deltaWidth = Math.max(50, x - this.searchBar.getX()) - this.searchBar.getWidth();
			this.searchBar.setWidth(this.searchBar.getWidth() + deltaWidth);
			searchBox.setWidth(searchBox.getWidth() + deltaWidth);
		}

		initBottomStatLine();
	}

	// filtered by category and filterType
	private Stream<TweakerMoreOption> getCurrentOptions()
	{
		return TweakerMoreConfigs.getOptions(SETTING.category).stream().
				filter(option -> {
					// drop down list filtering logic
					return this.filteredType == null || option.getType() == this.filteredType;
				});
	}

	private Stream<TweakerMoreOption> getCurrentValidOptions()
	{
		return this.getCurrentOptions().filter(this::isValidOption);
	}

	// dev & debug check
	@SuppressWarnings("RedundantIfStatement")
	private boolean isValidOption(TweakerMoreOption option)
	{
		// hide debug options unless debug mode on
		if (option.isDebug() && !TweakerMoreConfigs.TWEAKERMORE_DEBUG_MODE.getBooleanValue())
		{
			return false;
		}
		// hide dev only options unless debug mode on and is dev env
		if (option.isDevOnly() && !(TweakerMoreConfigs.TWEAKERMORE_DEBUG_MODE.getBooleanValue() && FabricUtils.isDevelopmentEnvironment()))
		{
			return false;
		}
		return true;
	}

	private void initBottomStatLine()
	{
		AtomicInteger enabled = new AtomicInteger(0);
		AtomicInteger disabled = new AtomicInteger(0);
		AtomicInteger modified = new AtomicInteger(0);
		this.getCurrentValidOptions().forEach(option -> {
			TweakerMoreIConfigBase config = option.getConfig();
			if (option.isEnabled())
			{
				enabled.getAndIncrement();
				if (config instanceof IConfigResettable && ((IConfigResettable)config).isModified())
				{
					modified.getAndIncrement();
				}
			}
			else
			{
				disabled.getAndIncrement();
			}
		});

		int total = enabled.get() + disabled.get();
		String bottomLine = StringUtils.translate(
				"tweakermore.gui.bottom_stat",
				total, enabled.get(), modified.get(), disabled.get()
		);

		int width = RenderUtils.getRenderWidth(bottomLine);
		int height = RenderUtils.TEXT_HEIGHT;
		int x = this.width - (this.width - this.getBrowserWidth()) / 2 - width;
		int y = this.height - height - TOP;
		this.addLabel(x, y, width, height, 0xFFCCCCCC, bottomLine);
	}

	private <T> void setDisplayParameter(T currentValue, T newValue, Runnable valueSetter, boolean keepSearchBar)
	{
		if (newValue != currentValue)
		{
			valueSetter.run();
			this.reDraw(keepSearchBar);
		}
	}

	private int createNavigationButton(int x, int y, Config.Category category)
	{
		ButtonGeneric button = new ButtonGeneric(x, y, -1, 20, category.getDisplayName());
		button.setEnabled(SETTING.category != category);
		button.setHoverStrings(category.getDescription());
		this.addButton(button, (b, mb) -> this.setDisplayParameter(SETTING.category, category, () -> SETTING.category = category, false));
		return button.getWidth() + 2;
	}

	private <T extends IStringValue> int initDropDownList(int x, List<T> entries, T defaultValue, Supplier<T> valueGetter, Consumer<T> valueSetter, String hoverTextKey, Consumer<SelectorDropDownList<T>> postProcessor)
	{
		int y = this.getListY() + 3;
		int height = 16;
		int maxTextWidth = entries.stream().
				filter(Objects::nonNull).
				mapToInt(e -> this.getStringWidth(e.getStringValue())).
				max().orElse(-1);
		// constant 20 reference: fi.dy.masa.malilib.gui.widgets.WidgetDropDownList.getRequiredWidth
		int width = Math.max(maxTextWidth, 40) + 20;

		SelectorDropDownList<T> dd = new SelectorDropDownList<>(x - width, y, width, height, 200, entries.size(), entries);
		dd.setEntryChangeListener(entry -> this.setDisplayParameter(valueGetter.get(), entry, () -> valueSetter.accept(entry), true));
		dd.setSelectedEntry(defaultValue);
		dd.setHoverText(hoverTextKey);
		postProcessor.accept(dd);

		this.addWidget(dd);
		this.hoveringWidgets.add(dd);

		return dd.getX();
	}

	private int initTypeFilterDropDownList(int x)
	{
		Set<Config.Type> possibleTypes = this.getCurrentValidOptions().
				map(TweakerMoreOption::getType).
				collect(Collectors.toSet());
		List<Config.Type> items = Arrays.stream(Config.Type.values()).filter(possibleTypes::contains).collect(Collectors.toList());
		items.add(0, null);

		return this.initDropDownList(
				x, items, this.filteredType,
				() -> this.filteredType, type -> this.filteredType = type,
				"tweakermore.gui.config_type.label_text",
				dd -> dd.setNullEntry(() -> StringUtils.translate("tweakermore.gui.selector_drop_down_list.all"))
		);
	}

	private int initSortingStrategyDropDownList(int x)
	{
		List<SortingStrategy> items = Arrays.asList(SortingStrategy.values());
		return this.initDropDownList(
				x, items, SETTING.sortingStrategy,
				() -> SETTING.sortingStrategy, strategy -> SETTING.sortingStrategy = strategy,
				"tweakermore.gui.sorting_strategy.label_text",
				dd -> {}
		);
	}

	public void reDraw(boolean keepSearchBar)
	{
		// storing search bar data
		String previousSearchBarText = null;
		boolean previousSearchBoxFocus = false;
		if (keepSearchBar && this.searchBar != null && this.searchBar.isSearchOpen())
		{
			previousSearchBarText = this.searchBar.getFilter();
			previousSearchBoxFocus = ((WidgetSearchBarAccessor)this.searchBar).getSearchBox().isFocused();
		}

		this.reCreateListWidget();

		// restoring search bar data
		if (this.searchBar != null && previousSearchBarText != null)
		{
			GuiTextFieldGeneric searchBox = ((WidgetSearchBarAccessor)this.searchBar).getSearchBox();
			//#if MC >= 1.21.8
			//$$ ((WidgetSearchBarSearchOpenStateAccess)this.searchBar).setSearchOpen$TKM(true);
			//#else
			this.searchBar.setSearchOpen(true);
			//#endif
			searchBox.setValue(previousSearchBarText);
			// malilib-fabric-1.19.4-0.15.4-sources.jar wrongly renames setFocused() to method_25365(), misleading the remap
			//#disable-remap
			searchBox.setFocused(previousSearchBoxFocus);
			//#enable-remap
		}

		Objects.requireNonNull(this.getListWidget()).resetScrollbarPosition();
		this.initGui();
	}

	public void reDraw()
	{
		this.reDraw(true);
	}

	public void renderDropDownList(
			//#if MC >= 12111
			//$$ GuiContext matrixStackOrDrawContext,
			//#elseif MC >= 12000
			//$$ GuiGraphics matrixStackOrDrawContext,
			//#elseif MC >= 11600
			//$$ PoseStack matrixStackOrDrawContext,
			//#endif
			int mouseX, int mouseY
	)
	{
		this.hoveringWidgets.forEach(widget -> widget.render(
				//#if MC >= 12106
				//$$ matrixStackOrDrawContext,
				//#endif
				mouseX, mouseY, widget.isMouseOver(mouseX, mouseY)
				//#if 11600 <= MC && MC < 12106
				//$$ , matrixStackOrDrawContext
				//#endif
		));
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
		labelWidth = Mth.clamp(guiWidth - panelWidth, maxTextWidth - 5, maxTextWidth + 100);
		// decrease the panel width if space is not enough
		panelWidth = Mth.clamp(guiWidth - labelWidth, 100, panelWidth);
		// decrease the label width for a bit if space is still way not enough (the label text might overlap with the panel now)
		labelWidth = Mth.clamp(guiWidth - panelWidth + 25, labelWidth - Math.max((int)(maxTextWidth * 0.4), 30), labelWidth);

		// just in case
		labelWidth = Math.max(labelWidth, 0);
		panelWidth = Math.max(panelWidth, 0);

		return Pair.of(labelWidth, panelWidth);
	}

	@Override
	public List<ConfigOptionWrapper> getConfigs()
	{
		Comparator<TweakerMoreOption> nameComparator = Comparator.comparing(c -> c.getConfig().getName(), String::compareToIgnoreCase);

		List<IConfigBase> configs = this.getCurrentValidOptions().
				filter(option -> {
					// hide disable options if config hideDisabledOptions is enabled
					if (TweakerMoreConfigs.HIDE_DISABLE_OPTIONS.getBooleanValue() && !option.isEnabled())
					{
						return false;
					}
					// hide options that don't work with current Minecraft versions, unless debug mode on
					if (!option.worksForCurrentMCVersion() && !TweakerMoreConfigs.TWEAKERMORE_DEBUG_MODE.getBooleanValue())
					{
						return false;
					}
					return true;
				}).
				sorted(SETTING.sortingStrategy.getComparator().thenComparing(nameComparator)).
				map(TweakerMoreOption::getConfig).
				collect(Collectors.toList());

		return ConfigOptionWrapper.createFor(configs);
	}

	private static class Setting implements JsonSaveAble
	{
		public Config.Category category = Config.Category.FEATURES;
		public SortingStrategy sortingStrategy = SortingStrategy.ALPHABET;

		@Override
		public void dumpToJson(JsonObject jsonObject)
		{
			jsonObject.addProperty("category", this.category.name());
			jsonObject.addProperty("sortingStrategy", this.sortingStrategy.name());
		}

		@Override
		public void loadFromJson(JsonObject jsonObject)
		{
			this.category = this.getEnumSafe(jsonObject, "category", this.category);
			this.sortingStrategy = this.getEnumSafe(jsonObject, "sortingStrategy", this.sortingStrategy);
		}
	}

	private enum SortingStrategy implements IStringValue
	{
		ALPHABET((a, b) -> 0),
		MOST_RECENTLY_USED(Collections.reverseOrder(Comparator.comparingLong(c -> c.getStatistic().lastUsedTime))),
		MOST_COMMONLY_USED(Collections.reverseOrder(Comparator.comparingLong(c -> c.getStatistic().useAmount)));

		private final Comparator<TweakerMoreOption> comparator;

		SortingStrategy(Comparator<TweakerMoreOption> comparator)
		{
			this.comparator = comparator;
		}

		public Comparator<TweakerMoreOption> getComparator()
		{
			return comparator;
		}

		@Override
		public String getStringValue()
		{
			return StringUtils.translate("tweakermore.gui.sorting_strategy." + this.name().toLowerCase());
		}
	}
}
