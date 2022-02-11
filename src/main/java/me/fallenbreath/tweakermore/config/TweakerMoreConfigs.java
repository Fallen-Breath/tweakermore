package me.fallenbreath.tweakermore.config;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.options.*;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.util.restrictions.ItemRestriction;
import fi.dy.masa.malilib.util.restrictions.UsageRestriction;
import me.fallenbreath.tweakermore.gui.TweakerMoreConfigGui;
import me.fallenbreath.tweakermore.impl.copySignTextToClipBoard.SignTextCopier;
import me.fallenbreath.tweakermore.util.RegistryUtil;
import me.fallenbreath.tweakermore.util.dependency.Condition;
import me.fallenbreath.tweakermore.util.dependency.Strategy;
import net.minecraft.item.Items;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static me.fallenbreath.tweakermore.config.ConfigFactory.*;
import static me.fallenbreath.tweakermore.util.ModIds.*;

public class TweakerMoreConfigs
{
	/**
	 * ============================
	 *     Config Declarations
	 * ============================
	 */

	////////////////////
	//    MC Tweaks   //
	////////////////////

	// Generic

	@Config(Config.Type.GENERIC)
	public static final ConfigInteger AUTO_FILL_CONTAINER_THRESHOLD = newConfigInteger("autoFillContainerThreshold", 2, 1, 36);

	@Config(value = Config.Type.GENERIC, strategy = @Strategy(disableWhen = @Condition(raise_chat_limit)))
	public static final ConfigInteger CHAT_MESSAGE_LIMIT = newConfigInteger("chatMessageLimit", 100, 100, 10000);

	@Config(Config.Type.GENERIC)
	public static final ConfigDouble NETHER_PORTAL_SOUND_CHANCE = newConfigDouble("netherPortalSoundChance", 0.01D, 0.0D, 0.01D);

	@Config(Config.Type.GENERIC)
	public static final ConfigDouble SAFE_AFK_HEALTH_THRESHOLD = newConfigDouble("safeAfkHealthThreshold", 10, 0, 100);

	@Config(Config.Type.GENERIC)
	public static final ConfigBoolean SHULKER_TOOLTIP_ENCHANTMENT_HINT = newConfigBoolean("shulkerTooltipEnchantmentHint", false);

	@Config(Config.Type.GENERIC)
	public static final ConfigBoolean VILLAGER_OFFER_USES_DISPLAY = newConfigBoolean("villagerOfferUsesDisplay", false);

	// Hotkey

	@Config(Config.Type.HOTKEY)
	public static final ConfigHotkey COPY_SIGN_TEXT_TO_CLIPBOARD = newConfigHotKey("copySignTextToClipBoard", "");

	// Generic

	@Config(value = Config.Type.LIST, strategy = @Strategy(enableWhen = @Condition(tweakeroo)))
	public static final ConfigOptionList HAND_RESTORE_LIST_TYPE = newConfigOptionList("handRestockListType", UsageRestriction.ListType.NONE);

	@Config(value = Config.Type.LIST, strategy = @Strategy(enableWhen = @Condition(tweakeroo)))
	public static final ConfigStringList HAND_RESTORE_WHITELIST = newConfigStringList("handRestockWhiteList", ImmutableList.of(RegistryUtil.getItemId(Items.BUCKET)));

	@Config(value = Config.Type.LIST, strategy = @Strategy(enableWhen = @Condition(tweakeroo)))
	public static final ConfigStringList HAND_RESTORE_BLACKLIST = newConfigStringList("handRestockBlackList", ImmutableList.of(RegistryUtil.getItemId(Items.LAVA_BUCKET)));

	public static final ItemRestriction HAND_RESTORE_RESTRICTION = new ItemRestriction();

	@Config(Config.Type.LIST)
	public static final ConfigStringList PRIORITIZED_COMMAND_SUGGESTIONS = newConfigStringList("prioritizedCommandSuggestions", ImmutableList.of());

	// Tweak

	@Config(value = Config.Type.TWEAK, strategy = @Strategy(enableWhen = @Condition(itemscroller)))
	public static final ConfigBooleanHotkeyed TWEAKM_AUTO_CLEAN_CONTAINER = newConfigBooleanHotkeyed("tweakmAutoCleanContainer");

	@Config(value = Config.Type.TWEAK, strategy = @Strategy(enableWhen = @Condition(itemscroller)))
	public static final ConfigBooleanHotkeyed TWEAKM_AUTO_FILL_CONTAINER = newConfigBooleanHotkeyed("tweakmAutoFillContainer");

	@Config(
			value = Config.Type.TWEAK,
			strategy = @Strategy(enableWhen = {
					@Condition(tweakeroo),
					@Condition(litematica)
			})
	)
	public static final ConfigBooleanHotkeyed TWEAKM_AUTO_PICK_SCHEMATIC_BLOCK = newConfigBooleanHotkeyed("tweakmAutoPickSchematicBlock");

	@Config(Config.Type.TWEAK)
	public static final ConfigBooleanHotkeyed TWEAKM_SAFE_AFK = newConfigBooleanHotkeyed("tweakmSafeAfk");

	// Disable

	@Config(Config.Type.DISABLE)
	public static final ConfigBooleanHotkeyed DISABLE_LIGHT_UPDATES = newConfigBooleanHotkeyed("disableLightUpdates");

	@Config(Config.Type.DISABLE)
	public static final ConfigBooleanHotkeyed DISABLE_REDSTONE_WIRE_PARTICLE = newConfigBooleanHotkeyed("disableRedstoneWireParticle");

	////////////////////
	//   Mod Tweaks   //
	////////////////////

	@Config(value = Config.Type.GENERIC, category = Config.Category.MOD_TWEAKS)
	public static final ConfigBoolean APPLY_TWEAKERMORE_OPTION_LABEL_GLOBALLY = newConfigBoolean("applyTweakerMoreOptionLabelGlobally", false);

	@Config(
			value = Config.Type.GENERIC,
			strategy = @Strategy(enableWhen = {
					@Condition(optifine),
					@Condition(value = minecraft, versionPredicates = ">=1.15")
			}),
			category = Config.Category.MOD_TWEAKS
	)
	public static final ConfigBoolean OF_UNLOCK_F3_FPS_LIMIT = newConfigBoolean("ofUnlockF3FpsLimit", false);

	@Config(
			value = Config.Type.GENERIC,
			strategy = @Strategy(enableWhen = @Condition(xaero_worldmap)),
			category = Config.Category.MOD_TWEAKS
	)
	public static final ConfigBoolean XMAP_NO_SESSION_FINALIZATION_WAIT = newConfigBoolean("xmapNoSessionFinalizationWait", false);

	//////////////////////////
	//  TweakerMore Setting //
	//////////////////////////

	@Config(value = Config.Type.GENERIC, category = Config.Category.SETTING)
	public static final ConfigBoolean HIDE_DISABLE_OPTIONS = newConfigBoolean("hideDisabledOptions", false);

	@Config(value = Config.Type.HOTKEY, category = Config.Category.SETTING)
	public static final ConfigHotkey OPEN_TWEAKERMORE_CONFIG_GUI = newConfigHotKey("openTweakerMoreConfigGui", "K,C");

	@Config(value = Config.Type.GENERIC, category = Config.Category.SETTING)
	public static final ConfigBoolean PRESERVE_CONFIG_UNKNOWN_ENTRIES = newConfigBoolean("preserveConfigUnknownEntries", true);

	@Config(value = Config.Type.TWEAK, category = Config.Category.SETTING)
	public static final ConfigBooleanHotkeyed TWEAKERMORE_DEBUG_MODE = newConfigBooleanHotkeyed("tweakerMoreDebugMode");

	/**
	 * ============================
	 *    Implementation Details
	 * ============================
	 */

	public static void initCallbacks()
	{
		COPY_SIGN_TEXT_TO_CLIPBOARD.getKeybind().setCallback(SignTextCopier::copySignText);
		OPEN_TWEAKERMORE_CONFIG_GUI.getKeybind().setCallback((action, key) -> {
			GuiBase.openGui(new TweakerMoreConfigGui());
			return true;
		});
		HIDE_DISABLE_OPTIONS.setValueChangeCallback(newValue -> {
			TweakerMoreConfigGui.getCurrentInstance().ifPresent(TweakerMoreConfigGui::reDraw);
		});
	}

	private static final List<TweakerMoreOption> OPTIONS = Lists.newArrayList();
	private static final Map<Config.Category, List<TweakerMoreOption>> CATEGORY_TO_OPTION = Maps.newLinkedHashMap();
	private static final Map<Config.Type, List<TweakerMoreOption>> TYPE_TO_OPTION = Maps.newLinkedHashMap();
	private static final Map<IConfigBase, TweakerMoreOption> CONFIG_TO_OPTION = Maps.newLinkedHashMap();

	static
	{
		for (Field field : TweakerMoreConfigs.class.getDeclaredFields())
		{
			Config annotation = field.getAnnotation(Config.class);
			if (annotation != null)
			{
				try
				{
					IConfigBase configBase = (IConfigBase)field.get(null);
					TweakerMoreOption tweakerMoreOption = new TweakerMoreOption(annotation, configBase);
					OPTIONS.add(tweakerMoreOption);
					CATEGORY_TO_OPTION.computeIfAbsent(tweakerMoreOption.getCategory(), k -> Lists.newArrayList()).add(tweakerMoreOption);
					TYPE_TO_OPTION.computeIfAbsent(tweakerMoreOption.getType(), k -> Lists.newArrayList()).add(tweakerMoreOption);
					CONFIG_TO_OPTION.put(tweakerMoreOption.getOption(), tweakerMoreOption);
				}
				catch (IllegalAccessException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	public static List<TweakerMoreOption> getOptions(Config.Category categoryType)
	{
		return CATEGORY_TO_OPTION.getOrDefault(categoryType, Collections.emptyList());
	}

	public static List<TweakerMoreOption> getOptions(Config.Type optionType)
	{
		return TYPE_TO_OPTION.getOrDefault(optionType, Collections.emptyList());
	}

	public static Stream<IConfigBase> getAllConfigOptionStream()
	{
		return OPTIONS.stream().map(TweakerMoreOption::getOption);
	}

	public static Optional<TweakerMoreOption> getOptionFromConfig(IConfigBase iConfigBase)
	{
		return Optional.ofNullable(CONFIG_TO_OPTION.get(iConfigBase));
	}

	public static boolean hasConfig(IConfigBase iConfigBase)
	{
		return getOptionFromConfig(iConfigBase).isPresent();
	}
}
