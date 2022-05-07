package me.fallenbreath.tweakermore.config;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import fi.dy.masa.malilib.config.HudAlignment;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.options.ConfigBoolean;
import fi.dy.masa.malilib.event.RenderEventHandler;
import fi.dy.masa.malilib.event.TickHandler;
import fi.dy.masa.malilib.hotkeys.KeybindSettings;
import fi.dy.masa.malilib.interfaces.IValueChangeCallback;
import fi.dy.masa.malilib.util.restrictions.ItemRestriction;
import fi.dy.masa.malilib.util.restrictions.UsageRestriction;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.TweakerMoreMod;
import me.fallenbreath.tweakermore.config.options.*;
import me.fallenbreath.tweakermore.gui.TweakerMoreConfigGui;
import me.fallenbreath.tweakermore.impl.copySignTextToClipBoard.SignTextCopier;
import me.fallenbreath.tweakermore.impl.eCraftMassCraftCompact.EasierCraftingRegistrar;
import me.fallenbreath.tweakermore.impl.refreshInventory.InventoryRefresher;
import me.fallenbreath.tweakermore.impl.serverDataSyncer.ServerDataSyncer;
import me.fallenbreath.tweakermore.impl.showRedstoneDustUpdateOrder.RedstoneDustUpdateOrderRenderer;
import me.fallenbreath.tweakermore.impl.tweakmAutoContainerProcess.AutoContainerProcessorHintRenderer;
import me.fallenbreath.tweakermore.impl.tweakmDebug.TweakerMoreDebugHelper;
import me.fallenbreath.tweakermore.impl.tweakmFlawlessFrames.FlawlessFramesHandler;
import me.fallenbreath.tweakermore.util.RegistryUtil;
import me.fallenbreath.tweakermore.util.doc.DocumentGenerator;
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

	private static final KeybindSettings KEYBIND_SETTINGS_ANY = KeybindSettings.create(KeybindSettings.Context.ANY, KeybindSettings.DEFAULT.getActivateOn(), KeybindSettings.DEFAULT.getAllowExtraKeys(), KeybindSettings.DEFAULT.isOrderSensitive(), KeybindSettings.DEFAULT.isExclusive(), KeybindSettings.DEFAULT.shouldCancel());

	////////////////////
	//    Features    //
	////////////////////

	@Config(type = Config.Type.GENERIC, category = Config.Category.FEATURES)
	public static final TweakerMoreConfigInteger AUTO_FILL_CONTAINER_THRESHOLD = newConfigInteger("autoFillContainerThreshold", 2, 1, 36);

	@Config(type = Config.Type.HOTKEY, category = Config.Category.FEATURES)
	public static final TweakerMoreConfigHotkey COPY_SIGN_TEXT_TO_CLIPBOARD = newConfigHotKey("copySignTextToClipBoard", "");

	@Config(type = Config.Type.GENERIC, category = Config.Category.FEATURES)
	public static final TweakerMoreConfigDouble REDSTONE_DUST_UPDATE_ORDER_TEXT_ALPHA = newConfigDouble("redstoneDustUpdateOrderTextAlpha", 0.6, 0, 1);

	@Config(type = Config.Type.HOTKEY, category = Config.Category.FEATURES)
	public static final TweakerMoreConfigHotkey REFRESH_INVENTORY = newConfigHotKey("refreshInventory", "", KEYBIND_SETTINGS_ANY);

	@Config(type = Config.Type.GENERIC, category = Config.Category.FEATURES)
	public static final TweakerMoreConfigDouble SAFE_AFK_HEALTH_THRESHOLD = newConfigDouble("safeAfkHealthThreshold", 10, 0, 100);

	@Config(type = Config.Type.TWEAK, restriction = @Restriction(require = @Condition(itemscroller)), category = Config.Category.FEATURES)
	public static final TweakerMoreConfigBooleanHotkeyed TWEAKM_AUTO_CLEAN_CONTAINER = newConfigBooleanHotkeyed("tweakmAutoCleanContainer");

	@Config(type = Config.Type.TWEAK, category = Config.Category.FEATURES)
	public static final TweakerMoreConfigBooleanHotkeyed TWEAKM_CONTAINER_PROCESSOR_HINT = newConfigBooleanHotkeyed("tweakmContainerProcessorHint");

	@Config(type = Config.Type.GENERIC, category = Config.Category.FEATURES)
	public static final TweakerMoreConfigOptionList TWEAKM_CONTAINER_PROCESSOR_HINT_POS = newConfigOptionList("tweakmContainerProcessorHintPos", HudAlignment.TOP_RIGHT);

	@Config(type = Config.Type.GENERIC, category = Config.Category.FEATURES)
	public static final TweakerMoreConfigDouble TWEAKM_CONTAINER_PROCESSOR_HINT_SCALE = newConfigDouble("tweakmContainerProcessorHintScale", 1, 0.25, 4);

	@Config(type = Config.Type.TWEAK, restriction = @Restriction(require = @Condition(itemscroller)), category = Config.Category.FEATURES)
	public static final TweakerMoreConfigBooleanHotkeyed TWEAKM_AUTO_FILL_CONTAINER = newConfigBooleanHotkeyed("tweakmAutoFillContainer");

	@Config(
			type = Config.Type.TWEAK,
			restriction = @Restriction(require = {
					@Condition(tweakeroo),
					@Condition(litematica)
			}),
			category = Config.Category.FEATURES
	)
	public static final TweakerMoreConfigBooleanHotkeyed TWEAKM_AUTO_PICK_SCHEMATIC_BLOCK = newConfigBooleanHotkeyed("tweakmAutoPickSchematicBlock");

	@Config(
			type = Config.Type.TWEAK,
			restriction = @Restriction(require = {
					@Condition(litematica),
					@Condition(itemscroller)
			}),
			category = Config.Category.FEATURES
	)
	public static final TweakerMoreConfigBooleanHotkeyed TWEAKM_AUTO_COLLECT_MATERIAL_LIST_ITEM = newConfigBooleanHotkeyed("tweakmAutoCollectMaterialListItem");

	@Config(type = Config.Type.TWEAK, category = Config.Category.FEATURES)
	public static final TweakerMoreConfigBooleanHotkeyed TWEAKM_SAFE_AFK = newConfigBooleanHotkeyed("tweakmSafeAfk");

	@Config(type = Config.Type.TWEAK, category = Config.Category.FEATURES)
	public static final TweakerMoreConfigBooleanHotkeyed TWEAKM_SHOW_REDSTONE_DUST_UPDATE_ORDER = newConfigBooleanHotkeyed("tweakmShowRedstoneDustUpdateOrder");

	@Config(type = Config.Type.GENERIC, category = Config.Category.FEATURES)
	public static final TweakerMoreConfigBoolean VILLAGER_OFFER_USES_DISPLAY = newConfigBoolean("villagerOfferUsesDisplay", false);

	////////////////////
	//    MC Tweaks   //
	////////////////////

	// Generic

	@Config(type = Config.Type.GENERIC, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigInteger BOSS_BAR_MAX_ENTRY = newConfigInteger("bossBarMaxEntry", -1, -1, 20);

	@Config(type = Config.Type.GENERIC, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigDouble BOSS_BAR_SCALE = newConfigDouble("bossBarScale", 1, 0.001, 2);

	@Config(
			type = Config.Type.GENERIC,
			restriction = @Restriction(conflict = {
					@Condition(compact_chat),
					@Condition(more_chat_history),
					@Condition(parachute),
					@Condition(raise_chat_limit),
					@Condition(wheres_my_chat_history)
			}),
			category = Config.Category.MC_TWEAKS
	)
	public static final TweakerMoreConfigInteger CHAT_MESSAGE_LIMIT = newConfigInteger("chatMessageLimit", 100, 100, 10000);

	@Config(type = Config.Type.GENERIC, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigInteger CONNECTION_SIMULATED_DELAY = newConfigInteger("connectionSimulatedDelay", 0, 0, 15_000);

	@Config(type = Config.Type.GENERIC, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigInteger DAYTIME_OVERRIDE_VALUE = newConfigInteger("daytimeOverrideValue", 0, 0, 24000);

	@Config(type = Config.Type.DISABLE, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigBooleanHotkeyed DISABLE_CAMERA_FRUSTUM_CULLING = newConfigBooleanHotkeyed("disableCameraFrustumCulling");

	@Config(type = Config.Type.DISABLE, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigBooleanHotkeyed DISABLE_BEACON_BEAM_RENDERING = newConfigBooleanHotkeyed("disableBeaconBeamRendering");

	@Config(type = Config.Type.DISABLE, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigBooleanHotkeyed DISABLE_LIGHT_UPDATES = newConfigBooleanHotkeyed("disableLightUpdates");

	@Config(type = Config.Type.DISABLE, restriction = @Restriction(require = @Condition(value = minecraft, versionPredicates = ">=1.17")), category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigBooleanHotkeyed DISABLE_PISTON_BLOCK_BREAKING_PARTICLE = newConfigBooleanHotkeyed("disablePistonBlockBreakingParticle");

	@Config(type = Config.Type.DISABLE, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigBooleanHotkeyed DISABLE_REDSTONE_PARTICLE = newConfigBooleanHotkeyed("disableRedstoneParticle");

	@Config(type = Config.Type.DISABLE, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigBooleanHotkeyed DISABLE_SIGN_TEXT_LENGTH_LIMIT = newConfigBooleanHotkeyed("disableSignTextLengthLimit");

	@Config(type = Config.Type.DISABLE, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigBooleanHotkeyed DISABLE_CAMERA_SUBMERSION_FOG = newConfigBooleanHotkeyed("disableCameraSubmersionFog");

	@Config(type = Config.Type.FIX, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigBoolean FIX_CHEST_MIRRORING = newConfigBoolean("fixChestMirroring", false);

	@Config(type = Config.Type.GENERIC, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigDouble FLY_DRAG = newConfigDouble("flyDrag", 0.09, 0, 1);

	@Config(type = Config.Type.GENERIC, restriction = @Restriction(require = @Condition(value = minecraft, versionPredicates = ">=1.16")), category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigBoolean LEGACY_F3_N_LOGIC = newConfigBoolean("legacyF3NLogic", false);

	@Config(type = Config.Type.GENERIC, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigInteger MAX_CHAT_HUD_HEIGHT = newConfigInteger("maxChatHudHeight", 160, 160, 1000);

	@Config(type = Config.Type.GENERIC, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigDouble NETHER_PORTAL_SOUND_CHANCE = newConfigDouble("netherPortalSoundChance", 0.01D, 0.0D, 0.01D);

	@Config(type = Config.Type.LIST, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigStringList PRIORITIZED_COMMAND_SUGGESTIONS = newConfigStringList("prioritizedCommandSuggestions", ImmutableList.of());

	@Config(type = Config.Type.GENERIC, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigDouble SCOREBOARD_SIDE_BAR_SCALE = newConfigDouble("scoreboardSideBarScale", 1, 0.001, 2);

	@Config(type = Config.Type.GENERIC, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigBoolean SHULKER_TOOLTIP_ENCHANTMENT_HINT = newConfigBoolean("shulkerTooltipEnchantmentHint", false);

	@Config(type = Config.Type.GENERIC, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigBoolean SHULKER_TOOLTIP_FILL_LEVEL_HINT = newConfigBoolean("shulkerTooltipFillLevelHint", false);

	@Config(type = Config.Type.TWEAK, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigBooleanHotkeyed TWEAKM_DAYTIME_OVERRIDE = newConfigBooleanHotkeyed("tweakmDaytimeOverride");

	@Config(type = Config.Type.TWEAK, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigBooleanHotkeyed TWEAKM_FAKE_NIGHT_VISION = newConfigBooleanHotkeyed("tweakmFakeNightVision");

	@Config(type = Config.Type.TWEAK, restriction = @Restriction(require = @Condition(replay_mod)), category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigBooleanHotkeyed TWEAKM_FLAWLESS_FRAMES = newConfigBooleanHotkeyed("tweakmFlawlessFrames");

	@Config(type = Config.Type.TWEAK, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigBooleanHotkeyed TWEAKM_UNLIMITED_ENTITY_RENDER_DISTANCE = newConfigBooleanHotkeyed("tweakmUnlimitedEntityRenderDistance");

	////////////////////
	//   Mod Tweaks   //
	////////////////////

	@Config(type = Config.Type.GENERIC, category = Config.Category.MOD_TWEAKS)
	public static final TweakerMoreConfigBoolean APPLY_TWEAKERMORE_OPTION_LABEL_GLOBALLY = newConfigBoolean("applyTweakerMoreOptionLabelGlobally", false);

	@Config(
			type = Config.Type.GENERIC,
			restriction = @Restriction(require = {
					@Condition(easier_crafting),
					@Condition(itemscroller)
			}),
			category = Config.Category.MOD_TWEAKS
	)
	public static final TweakerMoreConfigBoolean ECRAFT_ITEM_SCROLLER_COMPACT = newConfigBoolean("eCraftItemScrollerCompact", false);

	@Config(type = Config.Type.LIST, restriction = @Restriction(require = @Condition(tweakeroo)), category = Config.Category.MOD_TWEAKS)
	public static final TweakerMoreConfigOptionList HAND_RESTORE_LIST_TYPE = newConfigOptionList("handRestockListType", UsageRestriction.ListType.NONE);

	@Config(type = Config.Type.LIST, restriction = @Restriction(require = @Condition(tweakeroo)), category = Config.Category.MOD_TWEAKS)
	public static final TweakerMoreConfigStringList HAND_RESTORE_WHITELIST = newConfigStringList("handRestockWhiteList", ImmutableList.of(RegistryUtil.getItemId(Items.BUCKET)));

	@Config(type = Config.Type.LIST, restriction = @Restriction(require = @Condition(tweakeroo)), category = Config.Category.MOD_TWEAKS)
	public static final TweakerMoreConfigStringList HAND_RESTORE_BLACKLIST = newConfigStringList("handRestockBlackList", ImmutableList.of(RegistryUtil.getItemId(Items.LAVA_BUCKET)));

	public static final ItemRestriction HAND_RESTORE_RESTRICTION = new ItemRestriction();

	@Config(
			type = Config.Type.GENERIC,
			restriction = @Restriction(require = @Condition(optifine)),
			category = Config.Category.MOD_TWEAKS
	)
	public static final TweakerMoreConfigBoolean OF_REMOVE_SIGN_TEXT_RENDER_DISTANCE = newConfigBoolean("ofRemoveSignTextRenderDistance", false);

	@Config(
			type = Config.Type.GENERIC,
			restriction = @Restriction(require = @Condition(optifine)),
			category = Config.Category.MOD_TWEAKS
	)
	public static final TweakerMoreConfigBoolean OF_REMOVE_ITEM_FRAME_ITEM_RENDER_DISTANCE = newConfigBoolean("ofRemoveItemFrameItemRenderDistance", false);

	@Config(
			type = Config.Type.GENERIC,
			restriction = @Restriction(require = {
					@Condition(optifine),
					@Condition(value = minecraft, versionPredicates = ">=1.15")
			}),
			category = Config.Category.MOD_TWEAKS
	)
	public static final TweakerMoreConfigBoolean OF_UNLOCK_F3_FPS_LIMIT = newConfigBoolean("ofUnlockF3FpsLimit", false);

	@Config(
			type = Config.Type.GENERIC,
			restriction = @Restriction(require = @Condition(replay_mod)),
			category = Config.Category.MOD_TWEAKS
	)
	public static final TweakerMoreConfigInteger REPLAY_FLY_SPEED_LIMIT_MULTIPLIER = newConfigInteger("replayFlySpeedLimitMultiplier", 1, 1, 30);

	@Config(
			type = Config.Type.GENERIC,
			restriction = @Restriction(require = @Condition(replay_mod)),
			category = Config.Category.MOD_TWEAKS
	)
	public static final TweakerMoreConfigBoolean REPLAY_ACCURATE_TIMELINE_TIMESTAMP = newConfigBoolean("replayAccurateTimelineTimestamp", false);

	@Config(
			type = Config.Type.TWEAK,
			restriction = {
					@Restriction(require = @Condition(tweakeroo)),
					@Restriction(require = @Condition(litematica)),
					@Restriction(require = @Condition(minihud))
			},
			category = Config.Category.MOD_TWEAKS
	)
	public static final TweakerMoreConfigBooleanHotkeyed SERVER_DATA_SYNCER = newConfigBooleanHotkeyed("serverDataSyncer");

	@Config(type = Config.Type.GENERIC, category = Config.Category.MOD_TWEAKS)
	public static final TweakerMoreConfigInteger SERVER_DATA_SYNCER_QUERY_INTERVAL = newConfigInteger("serverDataSyncerQueryInterval", 1, 1, 100);

	@Config(type = Config.Type.GENERIC, category = Config.Category.MOD_TWEAKS)
	public static final TweakerMoreConfigInteger SERVER_DATA_SYNCER_QUERY_LIMIT = newConfigInteger("serverDataSyncerQueryLimit", 512, 1, 8192);

	@Config(
			type = Config.Type.GENERIC,
			restriction = {
					@Restriction(require = @Condition(optifine)),
					@Restriction(require = @Condition(iris))
			},
			category = Config.Category.MOD_TWEAKS
	)
	public static final TweakerMoreConfigBoolean SHADER_GAME_TIME_AS_WORLD_TIME = newConfigBoolean("shaderGameTimeAsWorldTime", false);

	@Config(
			type = Config.Type.GENERIC,
			restriction = @Restriction(require = @Condition(xaero_worldmap)),
			category = Config.Category.MOD_TWEAKS
	)
	public static final TweakerMoreConfigBoolean XMAP_NO_SESSION_FINALIZATION_WAIT = newConfigBoolean("xmapNoSessionFinalizationWait", false);

	//////////////////////////
	//        Porting       //
	//////////////////////////

	@Config(
			type = Config.Type.GENERIC,
			restriction = @Restriction(require = {
					@Condition(litematica),
					@Condition(value = minecraft, versionPredicates = "<1.16")
			}),
			category = Config.Category.PORTING
	)
	public static final TweakerMoreConfigBoolean LM_PICK_BLOCK_SHULKERS_PORTING = newConfigBoolean("lmPickBlockShulkersPorting", false);

	@Config(
			type = Config.Type.FIX,
			restriction = @Restriction(require = {
					@Condition(itemscroller),
					@Condition(value = minecraft, versionPredicates = "<1.18")
			}),
			category = Config.Category.PORTING
	)
	public static final TweakerMoreConfigBoolean IS_SCROLL_STACKS_FALLBACK_FIX_PORTING = newConfigBoolean("isScrollStacksFallbackFixPorting", false);

	@Config(
			type = Config.Type.DISABLE,
			restriction = @Restriction(require = {
					@Condition(value = minecraft, versionPredicates = "<1.17")
			}),
			category = Config.Category.PORTING
	)
	public static final TweakerMoreConfigBooleanHotkeyed TKR_DISABLE_NAUSEA_EFFECT_PORTING = newConfigBooleanHotkeyed("tkrDisableNauseaEffectPorting");

	//////////////////////////
	//  TweakerMore Setting //
	//////////////////////////

	@Config(type = Config.Type.GENERIC, category = Config.Category.SETTING)
	public static final TweakerMoreConfigBoolean HIDE_DISABLE_OPTIONS = newConfigBoolean("hideDisabledOptions", false);

	@Config(type = Config.Type.HOTKEY, category = Config.Category.SETTING)
	public static final TweakerMoreConfigHotkey OPEN_TWEAKERMORE_CONFIG_GUI = newConfigHotKey("openTweakerMoreConfigGui", "K,C");

	@Config(type = Config.Type.GENERIC, category = Config.Category.SETTING)
	public static final TweakerMoreConfigBoolean PRESERVE_CONFIG_UNKNOWN_ENTRIES = newConfigBoolean("preserveConfigUnknownEntries", true);

	@Config(type = Config.Type.TWEAK, category = Config.Category.SETTING)
	public static final TweakerMoreConfigBooleanHotkeyed TWEAKERMORE_DEBUG_MODE = newConfigBooleanHotkeyed("tweakerMoreDebugMode");

	@Config(type = Config.Type.GENERIC, category = Config.Category.SETTING, debug = true)
	public static final TweakerMoreConfigBoolean TWEAKERMORE_DEBUG_BOOL = newConfigBoolean("tweakerMoreDebugBool", false);

	@Config(type = Config.Type.GENERIC, category = Config.Category.SETTING, debug = true)
	public static final TweakerMoreConfigDouble TWEAKERMORE_DEBUG_DOUBLE = newConfigDouble("tweakerMoreDebugDouble", 0, -1, 1);

	@Config(type = Config.Type.GENERIC, category = Config.Category.SETTING, debug = true)
	public static final TweakerMoreConfigInteger TWEAKERMORE_DEBUG_INT = newConfigInteger("tweakerMoreDebugInt", 0, -1000, 1000);

	@Config(type = Config.Type.HOTKEY, category = Config.Category.SETTING, debug = true)
	public static final TweakerMoreConfigHotkey TWEAKERMORE_DEBUG_RESET_OPTION_STATISTIC = newConfigHotKey("tweakerMoreDebugResetOptionStatistic", "");

	@Config(type = Config.Type.HOTKEY, category = Config.Category.SETTING, devOnly = true)
	public static final TweakerMoreConfigHotkey TWEAKERMORE_DEV_MIXIN_AUDIT = newConfigHotKey("tweakerMoreDevMixinAudit", "");

	@Config(type = Config.Type.HOTKEY, category = Config.Category.SETTING, devOnly = true)
	public static final TweakerMoreConfigHotkey TWEAKERMORE_DEV_PRINT_DOC = newConfigHotKey("tweakerMoreDevPrintDoc", "");

	/**
	 * ============================
	 *    Implementation Details
	 * ============================
	 */

	// registering

	public static void initCallbacks()
	{
		// common callbacks
		IValueChangeCallback<ConfigBoolean> redrawConfigGui = newValue -> TweakerMoreConfigGui.getCurrentInstance().ifPresent(TweakerMoreConfigGui::reDraw);

		// hotkeys
		setHotkeyCallback(COPY_SIGN_TEXT_TO_CLIPBOARD, SignTextCopier::copySignText, false);
		setHotkeyCallback(OPEN_TWEAKERMORE_CONFIG_GUI, TweakerMoreConfigGui::openGui, true);
		setHotkeyCallback(REFRESH_INVENTORY, InventoryRefresher::refresh, false);

		// value listeners
		ECRAFT_ITEM_SCROLLER_COMPACT.setValueChangeCallback(EasierCraftingRegistrar::onConfigValueChanged);
		HIDE_DISABLE_OPTIONS.setValueChangeCallback(redrawConfigGui);
		TWEAKM_FLAWLESS_FRAMES.setValueChangeCallback(config -> FlawlessFramesHandler.setEnabled(config.getBooleanValue()));

		// debugs
		TWEAKERMORE_DEBUG_MODE.setValueChangeCallback(redrawConfigGui);
		setHotkeyCallback(TWEAKERMORE_DEBUG_RESET_OPTION_STATISTIC, TweakerMoreDebugHelper::resetAllConfigStatistic, false);
		setHotkeyCallback(TWEAKERMORE_DEV_MIXIN_AUDIT, TweakerMoreDebugHelper::forceLoadAllMixins, true);
		setHotkeyCallback(TWEAKERMORE_DEV_PRINT_DOC, DocumentGenerator::onHotKey, true);
	}

	private static void setHotkeyCallback(TweakerMoreConfigHotkey configHotkey, Runnable runnable, boolean cancelFurtherProcess)
	{
		configHotkey.setCallBack((action, key) -> {
			runnable.run();
			return cancelFurtherProcess;
		});
	}

	public static void initEventListeners()
	{
		TickHandler.getInstance().registerClientTickHandler(ServerDataSyncer.getInstance());
		RenderEventHandler.getInstance().registerWorldLastRenderer(new RedstoneDustUpdateOrderRenderer());
		RenderEventHandler.getInstance().registerGameOverlayRenderer(new AutoContainerProcessorHintRenderer());
	}

	// Config fields collecting

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
					Object config = field.get(null);
					if (!(config instanceof TweakerMoreIConfigBase))
					{
						TweakerMoreMod.LOGGER.warn("[TweakerMore] {} is not a subclass of TweakerMoreIConfigBase", config);
						continue;
					}
					TweakerMoreOption tweakerMoreOption = new TweakerMoreOption(annotation, (TweakerMoreIConfigBase)config);
					OPTIONS.add(tweakerMoreOption);
					CATEGORY_TO_OPTION.computeIfAbsent(tweakerMoreOption.getCategory(), k -> Lists.newArrayList()).add(tweakerMoreOption);
					TYPE_TO_OPTION.computeIfAbsent(tweakerMoreOption.getType(), k -> Lists.newArrayList()).add(tweakerMoreOption);
					CONFIG_TO_OPTION.put(tweakerMoreOption.getConfig(), tweakerMoreOption);
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
		if (categoryType == Config.Category.ALL)
		{
			return OPTIONS;
		}
		return CATEGORY_TO_OPTION.getOrDefault(categoryType, Collections.emptyList());
	}

	public static List<TweakerMoreOption> getOptions(Config.Type optionType)
	{
		return TYPE_TO_OPTION.getOrDefault(optionType, Collections.emptyList());
	}

	public static List<TweakerMoreOption> getAllOptions()
	{
		return OPTIONS;
	}

	public static Stream<TweakerMoreIConfigBase> getAllConfigOptionStream()
	{
		return OPTIONS.stream().map(TweakerMoreOption::getConfig);
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
