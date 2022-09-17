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
import me.fallenbreath.tweakermore.config.options.listentries.*;
import me.fallenbreath.tweakermore.gui.TweakerMoreConfigGui;
import me.fallenbreath.tweakermore.impl.features.copySignTextToClipBoard.SignTextCopier;
import me.fallenbreath.tweakermore.impl.features.infoView.InfoViewRenderer;
import me.fallenbreath.tweakermore.impl.features.refreshInventory.InventoryRefresher;
import me.fallenbreath.tweakermore.impl.features.tweakmAutoContainerProcess.AutoContainerProcessorHintRenderer;
import me.fallenbreath.tweakermore.impl.features.tweakmSchematicProPlace.ProPlaceImpl;
import me.fallenbreath.tweakermore.impl.mc_tweaks.tweakmFlawlessFrames.FlawlessFramesHandler;
import me.fallenbreath.tweakermore.impl.mod_tweaks.eCraftMassCraftCompact.EasierCraftingRegistrar;
import me.fallenbreath.tweakermore.impl.mod_tweaks.ofPlayerExtraModelOverride.OptifinePlayerExtraModelOverrider;
import me.fallenbreath.tweakermore.impl.mod_tweaks.serverDataSyncer.serverDataSyncer.ServerDataSyncer;
import me.fallenbreath.tweakermore.impl.porting.lmCustomSchematicBaseDirectoryPorting.LitematicaCustomSchematicBaseDirectoryPorting;
import me.fallenbreath.tweakermore.impl.setting.tweakmDebug.TweakerMoreDebugHelper;
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

	@Config(type = Config.Type.HOTKEY, category = Config.Category.FEATURES)
	public static final TweakerMoreConfigHotkeyWithSwitch CREATIVE_PICK_BLOCK_WITH_STATE = newConfigHotKeyWithSwitch("creativePickBlockWithState", false, "LEFT_ALT", KeybindSettings.MODIFIER_INGAME);

	@Config(type = Config.Type.GENERIC, category = Config.Category.FEATURES)
	public static final TweakerMoreConfigDouble INFO_VIEW_BEAM_ANGLE = newConfigDouble("infoViewBeamAngle", 40, 1, 120);

	@Config(type = Config.Type.GENERIC, category = Config.Category.FEATURES)
	public static final TweakerMoreConfigDouble INFO_VIEW_TARGET_DISTANCE = newConfigDouble("infoViewTargetDistance", 8, 4, 32);

	@Config(type = Config.Type.HOTKEY, category = Config.Category.FEATURES)
	public static final TweakerMoreConfigHotkey INFO_VIEW_RENDERING_KEY = newConfigHotKey("infoViewRenderingKey", "RIGHT_ALT", KeybindSettings.MODIFIER_INGAME);

	@Config(type = Config.Type.TWEAK, category = Config.Category.FEATURES)
	public static final TweakerMoreConfigBooleanHotkeyed INFO_VIEW_COMMAND_BLOCK = newConfigBooleanHotkeyed("infoViewCommandBlock");

	@Config(type = Config.Type.GENERIC, category = Config.Category.FEATURES)
	public static final TweakerMoreConfigInteger INFO_VIEW_COMMAND_BLOCK_MAX_WIDTH = newConfigInteger("infoViewCommandBlockMaxWidth", 200, 10, 2000);

	@Config(type = Config.Type.LIST, category = Config.Category.FEATURES)
	public static final TweakerMoreConfigOptionList INFO_VIEW_COMMAND_BLOCK_RENDER_STRATEGY = newConfigOptionList("infoViewCommandBlockRenderStrategy", InfoViewRenderStrategy.DEFAULT);

	@Config(type = Config.Type.LIST, category = Config.Category.FEATURES)
	public static final TweakerMoreConfigOptionList INFO_VIEW_COMMAND_BLOCK_TARGET_STRATEGY = newConfigOptionList("infoViewCommandBlockTargetStrategy", InfoViewTargetStrategy.DEFAULT);

	@Config(type = Config.Type.GENERIC, category = Config.Category.FEATURES)
	public static final TweakerMoreConfigDouble INFO_VIEW_COMMAND_BLOCK_TEXT_SCALE = newConfigDouble("infoViewCommandBlockTextScale", 1, 0.1, 3);

	@Config(type = Config.Type.TWEAK, category = Config.Category.FEATURES)
	public static final TweakerMoreConfigBooleanHotkeyed INFO_VIEW_REDSTONE_DUST_UPDATE_ORDER = newConfigBooleanHotkeyed("infoViewRedstoneDustUpdateOrder");

	@Config(type = Config.Type.LIST, category = Config.Category.FEATURES)
	public static final TweakerMoreConfigOptionList INFO_VIEW_REDSTONE_DUST_UPDATE_ORDER_RENDER_STRATEGY = newConfigOptionList("infoViewRedstoneDustUpdateOrderRenderStrategy", InfoViewRenderStrategy.DEFAULT);

	@Config(type = Config.Type.GENERIC, category = Config.Category.FEATURES)
	public static final TweakerMoreConfigDouble INFO_VIEW_REDSTONE_DUST_UPDATE_ORDER_TEXT_ALPHA = newConfigDouble("infoViewRedstoneDustUpdateOrderTextAlpha", 0.8, 0, 1);

	@Config(type = Config.Type.TWEAK, category = Config.Category.FEATURES)
	public static final TweakerMoreConfigBooleanHotkeyed INFO_VIEW_RESPAWN_BLOCK_EXPLOSION = newConfigBooleanHotkeyed("infoViewRespawnBlockExplosion");

	@Config(type = Config.Type.GENERIC, category = Config.Category.FEATURES)
	public static final TweakerMoreConfigDouble INFO_VIEW_RESPAWN_BLOCK_EXPLOSION_TEXT_ALPHA = newConfigDouble("infoViewRespawnBlockExplosionTextAlpha", 0.8, 0, 1);

	@Config(type = Config.Type.LIST, category = Config.Category.FEATURES)
	public static final TweakerMoreConfigOptionList INFO_VIEW_RESPAWN_BLOCK_EXPLOSION_RENDER_STRATEGY = newConfigOptionList("infoViewRespawnBlockExplosionRenderStrategy", InfoViewRenderStrategy.ALWAYS);

	@Config(type = Config.Type.LIST, category = Config.Category.FEATURES)
	public static final TweakerMoreConfigOptionList INFO_VIEW_RESPAWN_BLOCK_EXPLOSION_TARGET_STRATEGY = newConfigOptionList("infoViewRespawnBlockExplosionTargetStrategy", InfoViewTargetStrategy.BEAM);

	@Config(type = Config.Type.HOTKEY, category = Config.Category.FEATURES)
	public static final TweakerMoreConfigHotkey REFRESH_INVENTORY = newConfigHotKey("refreshInventory", "", KEYBIND_SETTINGS_ANY);

	@Config(type = Config.Type.GENERIC, category = Config.Category.FEATURES)
	public static final TweakerMoreConfigDouble SAFE_AFK_HEALTH_THRESHOLD = newConfigDouble("safeAfkHealthThreshold", 10, 0, 100);

	@Config(type = Config.Type.LIST, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigOptionListHotkeyed SERVER_MSPT_METRICS_STATISTIC_TYPE = newConfigOptionListHotkeyed("serverMsptMetricsStatisticType", ServerMsptMetricsStatisticType.DEFAULT);

	@Config(type = Config.Type.TWEAK, restriction = @Restriction(require = @Condition(itemscroller)), category = Config.Category.FEATURES)
	public static final TweakerMoreConfigBooleanHotkeyed TWEAKM_AUTO_CLEAN_CONTAINER = newConfigBooleanHotkeyed("tweakmAutoCleanContainer");

	@Config(type = Config.Type.LIST, restriction = @Restriction(require = @Condition(itemscroller)), category = Config.Category.FEATURES)
	public static final TweakerMoreConfigOptionList TWEAKM_AUTO_CLEAN_CONTAINER_LIST_TYPE = newConfigOptionList("tweakmAutoCleanContainerListType", UsageRestriction.ListType.NONE);

	@Config(type = Config.Type.LIST, restriction = @Restriction(require = @Condition(itemscroller)), category = Config.Category.FEATURES)
	public static final TweakerMoreConfigStringList TWEAKM_AUTO_CLEAN_CONTAINER_WHITELIST = newConfigStringList("tweakmAutoCleanContainerWhiteList", ImmutableList.of(RegistryUtil.getItemId(Items.SHULKER_BOX)));

	@Config(type = Config.Type.LIST, restriction = @Restriction(require = @Condition(itemscroller)), category = Config.Category.FEATURES)
	public static final TweakerMoreConfigStringList TWEAKM_AUTO_CLEAN_CONTAINER_BLACKLIST = newConfigStringList("tweakmAutoCleanContainerBlackList", ImmutableList.of(RegistryUtil.getItemId(Items.SHULKER_BOX)));

	public static final ItemRestriction TWEAKM_AUTO_CLEAN_CONTAINER_RESTRICTION = new ItemRestriction();

	@Config(
			type = Config.Type.TWEAK,
			restriction = @Restriction(require = {
					@Condition(litematica),
					@Condition(itemscroller)
			}),
			category = Config.Category.FEATURES
	)
	public static final TweakerMoreConfigBooleanHotkeyed TWEAKM_AUTO_COLLECT_MATERIAL_LIST_ITEM = newConfigBooleanHotkeyed("tweakmAutoCollectMaterialListItem");

	@Config(type = Config.Type.TWEAK, restriction = @Restriction(require = @Condition(itemscroller)), category = Config.Category.FEATURES)
	public static final TweakerMoreConfigBooleanHotkeyed TWEAKM_AUTO_FILL_CONTAINER = newConfigBooleanHotkeyed("tweakmAutoFillContainer");

	@Config(
			type = Config.Type.TWEAK,
			restriction = @Restriction(require = @Condition(litematica)),
			category = Config.Category.FEATURES
	)
	public static final TweakerMoreConfigBooleanHotkeyed TWEAKM_AUTO_PICK_SCHEMATIC_BLOCK = newConfigBooleanHotkeyed("tweakmAutoPickSchematicBlock");

	@Config(type = Config.Type.TWEAK, restriction = @Restriction(require = @Condition(itemscroller)), category = Config.Category.FEATURES)
	public static final TweakerMoreConfigBooleanHotkeyed TWEAKM_AUTO_PUT_BACK_EXISTED_ITEM = newConfigBooleanHotkeyed("tweakmAutoPutBackExistedItem");

	@Config(
			type = Config.Type.TWEAK,
			restriction = @Restriction(require = {
					@Condition(itemscroller),
					@Condition(value = minecraft, versionPredicates = ">=1.16")
			}),
			category = Config.Category.FEATURES
	)
	public static final TweakerMoreConfigBooleanHotkeyed TWEAKM_AUTO_VILLAGER_TRADE_FAVORITES = newConfigBooleanHotkeyed("tweakmAutoVillagerTradeFavorites");

	@Config(type = Config.Type.TWEAK, category = Config.Category.FEATURES)
	public static final TweakerMoreConfigBooleanHotkeyed TWEAKM_CONTAINER_PROCESSOR_HINT = newConfigBooleanHotkeyed("tweakmContainerProcessorHint");

	@Config(type = Config.Type.GENERIC, category = Config.Category.FEATURES)
	public static final TweakerMoreConfigOptionList TWEAKM_CONTAINER_PROCESSOR_HINT_POS = newConfigOptionList("tweakmContainerProcessorHintPos", HudAlignment.TOP_RIGHT);

	@Config(type = Config.Type.GENERIC, category = Config.Category.FEATURES)
	public static final TweakerMoreConfigDouble TWEAKM_CONTAINER_PROCESSOR_HINT_SCALE = newConfigDouble("tweakmContainerProcessorHintScale", 1, 0.25, 4);

	@Config(type = Config.Type.TWEAK, category = Config.Category.FEATURES)
	public static final TweakerMoreConfigBooleanHotkeyed TWEAKM_INFO_VIEW = newConfigBooleanHotkeyed("tweakmInfoView");

	@Config(
			type = Config.Type.TWEAK,
			restriction = @Restriction(require = @Condition(litematica)),
			category = Config.Category.FEATURES
	)
	public static final TweakerMoreConfigBooleanHotkeyed TWEAKM_SCHEMATIC_PRO_PLACE = newConfigBooleanHotkeyed("tweakmSchematicProPlace");

	@Config(type = Config.Type.TWEAK, category = Config.Category.FEATURES)
	public static final TweakerMoreConfigBooleanHotkeyed TWEAKM_SAFE_AFK = newConfigBooleanHotkeyed("tweakmSafeAfk");

	@Config(
			type = Config.Type.TWEAK,
			restriction = @Restriction(require = @Condition(litematica)),
			category = Config.Category.FEATURES
	)
	public static final TweakerMoreConfigBooleanHotkeyed TWEAKM_SCHEMATIC_BLOCK_PLACEMENT_RESTRICTION = newConfigBooleanHotkeyed("tweakmSchematicBlockPlacementRestriction");

	@Config(type = Config.Type.LIST, category = Config.Category.FEATURES)
	public static final TweakerMoreConfigOptionList TWEAKM_SCHEMATIC_BLOCK_PLACEMENT_RESTRICTION_HINT = newConfigOptionList("tweakmSchematicBlockPlacementRestrictionHint", SchematicBlockPlacementRestrictionHintType.DEFAULT);

	@Config(type = Config.Type.LIST, category = Config.Category.FEATURES)
	public static final TweakerMoreConfigStringList TWEAKM_SCHEMATIC_BLOCK_PLACEMENT_RESTRICTION_ITEM_WHITELIST = newConfigStringList("tweakmSchematicBlockPlacementRestrictionItemWhitelist", ImmutableList.of());

	@Config(type = Config.Type.GENERIC, category = Config.Category.FEATURES)
	public static final TweakerMoreConfigInteger TWEAKM_SCHEMATIC_BLOCK_PLACEMENT_RESTRICTION_MARGIN = newConfigInteger("tweakmSchematicBlockPlacementRestrictionMargin", 2, 0, 16);

	@Config(type = Config.Type.TWEAK, category = Config.Category.FEATURES)
	public static final TweakerMoreConfigBooleanHotkeyed TWEAKM_SCHEMATIC_BLOCK_PLACEMENT_RESTRICTION_CHECK_FACING = newConfigBooleanHotkeyed("tweakmSchematicBlockPlacementRestrictionCheckFacing", true, "");

	@Config(type = Config.Type.TWEAK, category = Config.Category.FEATURES)
	public static final TweakerMoreConfigBooleanHotkeyed TWEAKM_SCHEMATIC_BLOCK_PLACEMENT_RESTRICTION_CHECK_SLAB = newConfigBooleanHotkeyed("tweakmSchematicBlockPlacementRestrictionCheckSlab", true, "");

	@Config(type = Config.Type.GENERIC, category = Config.Category.FEATURES)
	public static final TweakerMoreConfigBoolean TWEAKM_SCHEMATIC_BLOCK_PLACEMENT_RESTRICTION_STRICT = newConfigBoolean("tweakmSchematicBlockPlacementRestrictionStrict", true);

	@Config(type = Config.Type.GENERIC, category = Config.Category.FEATURES)
	public static final TweakerMoreConfigBoolean VILLAGER_OFFER_USES_DISPLAY = newConfigBoolean("villagerOfferUsesDisplay", false);

	////////////////////
	//    MC Tweaks   //
	////////////////////

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
	public static final TweakerMoreConfigBooleanHotkeyed DISABLE_BEACON_BEAM_RENDERING = newConfigBooleanHotkeyed("disableBeaconBeamRendering");

	@Config(type = Config.Type.DISABLE, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigBooleanHotkeyed DISABLE_CAMERA_FRUSTUM_CULLING = newConfigBooleanHotkeyed("disableCameraFrustumCulling");

	@Config(type = Config.Type.DISABLE, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigBooleanHotkeyed DISABLE_CAMERA_SUBMERSION_FOG = newConfigBooleanHotkeyed("disableCameraSubmersionFog");

	@Config(type = Config.Type.DISABLE, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigBooleanHotkeyed DISABLE_CREATIVE_FLY_CLIMBING_CHECK = newConfigBooleanHotkeyed("disableCreativeFlyClimbingCheck");

	@Config(type = Config.Type.DISABLE, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigBooleanHotkeyed DISABLE_DARK_SKY_RENDERING = newConfigBooleanHotkeyed("disableDarkSkyRendering");

	@Config(type = Config.Type.DISABLE, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigBooleanHotkeyed DISABLE_ENTITY_MODEL_RENDERING = newConfigBooleanHotkeyed("disableEntityModelRendering");

	@Config(type = Config.Type.DISABLE, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigBooleanHotkeyed DISABLE_ENTITY_RENDER_INTERPOLATION = newConfigBooleanHotkeyed("disableEntityRenderInterpolation");

	@Config(type = Config.Type.DISABLE, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigBooleanHotkeyed DISABLE_HORIZON_SHADING_RENDERING = newConfigBooleanHotkeyed("disableHorizonShadingRendering");

	@Config(type = Config.Type.DISABLE, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigBooleanHotkeyed DISABLE_LIGHT_UPDATES = newConfigBooleanHotkeyed("disableLightUpdates");

	@Config(type = Config.Type.DISABLE, restriction = @Restriction(require = @Condition(value = minecraft, versionPredicates = ">=1.17")), category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigBooleanHotkeyed DISABLE_PISTON_BLOCK_BREAKING_PARTICLE = newConfigBooleanHotkeyed("disablePistonBlockBreakingParticle");

	@Config(type = Config.Type.DISABLE, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigBooleanHotkeyed DISABLE_REDSTONE_PARTICLE = newConfigBooleanHotkeyed("disableRedstoneParticle");

	@Config(type = Config.Type.DISABLE, restriction = @Restriction(conflict = @Condition(value = caxton, versionPredicates = "<0.3.0-beta.2")), category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigBooleanHotkeyed DISABLE_SIGN_TEXT_LENGTH_LIMIT = newConfigBooleanHotkeyed("disableSignTextLengthLimit");

	@Config(type = Config.Type.DISABLE, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigBooleanHotkeyed DISABLE_SLIME_BLOCK_BOUNCING = newConfigBooleanHotkeyed("disableSlimeBlockBouncing");

	@Config(type = Config.Type.DISABLE, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigBooleanHotkeyed DISABLE_VIGNETTE_DARKNESS = newConfigBooleanHotkeyed("disableVignetteDarkness");

	@Config(type = Config.Type.FIX, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigBoolean FIX_CHEST_MIRRORING = newConfigBoolean("fixChestMirroring", false);

	@Config(type = Config.Type.FIX, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigBoolean FIX_HOVER_TEXT_SCALE = newConfigBoolean("fixHoverTextScale", false);

	@Config(type = Config.Type.GENERIC, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigDouble FLY_DRAG = newConfigDouble("flyDrag", 0.09, 0, 1);

	@Config(type = Config.Type.GENERIC, restriction = @Restriction(require = @Condition(value = minecraft, versionPredicates = ">=1.16")), category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigBoolean LEGACY_F3_N_LOGIC = newConfigBoolean("legacyF3NLogic", false);

	@Config(type = Config.Type.GENERIC, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigInteger MAX_CHAT_HUD_HEIGHT = newConfigInteger("maxChatHudHeight", 160, 160, 1000);

	@Config(type = Config.Type.GENERIC, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigDouble NETHER_PORTAL_SOUND_CHANCE = newConfigDouble("netherPortalSoundChance", 0.01D, 0.0D, 0.01D);

	@Config(type = Config.Type.LIST, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigStringList PLAYER_NAME_TAG_RENDER_STRATEGY_LIST = newConfigStringList("playerNameTagRenderStrategyList", ImmutableList.of());

	@Config(type = Config.Type.LIST, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigOptionList PLAYER_NAME_TAG_RENDER_STRATEGY_TYPE = newConfigOptionList("playerNameTagRenderStrategyType", RestrictionType.NONE);

	@Config(type = Config.Type.GENERIC, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigBoolean PLAYER_SKIN_BLOCKING_LOADING = newConfigBoolean("playerSkinBlockingLoading", false);

	@Config(type = Config.Type.LIST, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigStringList PRIORITIZED_COMMAND_SUGGESTIONS = newConfigStringList("prioritizedCommandSuggestions", ImmutableList.of());

	@Config(type = Config.Type.GENERIC, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigDouble SCOREBOARD_SIDE_BAR_SCALE = newConfigDouble("scoreboardSideBarScale", 1, 0.001, 2);

	@Config(type = Config.Type.GENERIC, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigBoolean SHULKER_TOOLTIP_ENCHANTMENT_HINT = newConfigBoolean("shulkerTooltipEnchantmentHint", false);

	@Config(type = Config.Type.GENERIC, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigBoolean SHULKER_TOOLTIP_FILL_LEVEL_HINT = newConfigBoolean("shulkerTooltipFillLevelHint", false);

	@Config(type = Config.Type.GENERIC, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigInteger SHULKER_TOOLTIP_HINT_LENGTH_LIMIT = newConfigInteger("shulkerTooltipHintLengthLimit", 120, 0, 600);

	@Config(type = Config.Type.GENERIC, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigBoolean SHULKER_TOOLTIP_POTION_INFO_HINT = newConfigBoolean("shulkerTooltipPotionInfoHint", false);

	@Config(type = Config.Type.GENERIC, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigBoolean SIGN_MULTILINE_PASTE_SUPPORT = newConfigBoolean("signMultilinePasteSupport", false);

	@Config(type = Config.Type.GENERIC, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigBoolean SPECTATOR_TELEPORT_MENU_INCLUDE_SPECTATOR = newConfigBoolean("spectatorTeleportMenuIncludeSpectator", false);

	@Config(type = Config.Type.TWEAK, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigBooleanHotkeyed TWEAKM_DAYTIME_OVERRIDE = newConfigBooleanHotkeyed("tweakmDaytimeOverride");

	@Config(type = Config.Type.TWEAK, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigBooleanHotkeyed TWEAKM_FAKE_NIGHT_VISION = newConfigBooleanHotkeyed("tweakmFakeNightVision");

	@Config(type = Config.Type.TWEAK, restriction = @Restriction(require = @Condition(replay_mod)), category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigBooleanHotkeyed TWEAKM_FLAWLESS_FRAMES = newConfigBooleanHotkeyed("tweakmFlawlessFrames");

	@Config(type = Config.Type.TWEAK, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigBooleanHotkeyed TWEAKM_UNLIMITED_BLOCK_ENTITY_RENDER_DISTANCE = newConfigBooleanHotkeyed("tweakmUnlimitedBlockEntityRenderDistance");

	@Config(type = Config.Type.TWEAK, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigBooleanHotkeyed TWEAKM_UNLIMITED_ENTITY_RENDER_DISTANCE = newConfigBooleanHotkeyed("tweakmUnlimitedEntityRenderDistance");

	@Config(type = Config.Type.TWEAK, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigBooleanHotkeyed TWEAKM_WEATHER_OVERRIDE = newConfigBooleanHotkeyed("tweakmWeatherOverride");

	@Config(type = Config.Type.LIST, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigOptionList WEATHER_OVERRIDE_VALUE = newConfigOptionList("weatherOverrideValue", WeatherOverrideValue.DEFAULT);

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

	@Config(type = Config.Type.GENERIC, restriction = @Restriction(require = @Condition(extra_player_renderer)), category = Config.Category.MOD_TWEAKS)
	public static final TweakerMoreConfigBoolean EPR_HIDE_ON_DEBUG_HUD = newConfigBoolean("eprHideOnDebugHud", false);

	@Config(type = Config.Type.LIST, restriction = @Restriction(require = @Condition(tweakeroo)), category = Config.Category.MOD_TWEAKS)
	public static final TweakerMoreConfigOptionList HAND_RESTORE_LIST_TYPE = newConfigOptionList("handRestockListType", UsageRestriction.ListType.NONE);

	@Config(type = Config.Type.LIST, restriction = @Restriction(require = @Condition(tweakeroo)), category = Config.Category.MOD_TWEAKS)
	public static final TweakerMoreConfigStringList HAND_RESTORE_WHITELIST = newConfigStringList("handRestockWhiteList", ImmutableList.of(RegistryUtil.getItemId(Items.BUCKET)));

	@Config(type = Config.Type.LIST, restriction = @Restriction(require = @Condition(tweakeroo)), category = Config.Category.MOD_TWEAKS)
	public static final TweakerMoreConfigStringList HAND_RESTORE_BLACKLIST = newConfigStringList("handRestockBlackList", ImmutableList.of(RegistryUtil.getItemId(Items.LAVA_BUCKET)));

	public static final ItemRestriction HAND_RESTORE_RESTRICTION = new ItemRestriction();

	@Config(
			type = Config.Type.GENERIC,
			restriction = @Restriction(require = @Condition(litematica)),
			category = Config.Category.MOD_TWEAKS
	)
	public static final TweakerMoreConfigBoolean LM_ORIGIN_OVERRIDE_000 = newConfigBoolean("lmOriginOverride000", false);

	@Config(
			type = Config.Type.GENERIC,
			restriction = @Restriction(require = @Condition(minihud)),
			category = Config.Category.MOD_TWEAKS
	)
	public static final TweakerMoreConfigBoolean MINIHUD_DISABLE_LIGHT_OVERLAY_SPAWN_CHECK = newConfigBoolean("minihudDisableLightOverlaySpawnCheck", false);

	@Config(
			type = Config.Type.GENERIC,
			restriction = @Restriction(require = @Condition(minihud)),
			category = Config.Category.MOD_TWEAKS
	)
	public static final TweakerMoreConfigBoolean MINIHUD_HIDE_IF_CHAT_SCREEN_OPENED = newConfigBoolean("minihudHideIfChatScreenOpened", false);

	@Config(
			type = Config.Type.LIST,
			restriction = @Restriction(require = @Condition(optifine)),
			category = Config.Category.MOD_TWEAKS
	)
	public static final TweakerMoreConfigOptionListHotkeyed OF_SANTA_HAT = newConfigOptionListHotkeyed("ofSantaHat", OptifineExtraModelRenderStrategy.DEFAULT);

	@Config(
			type = Config.Type.LIST,
			restriction = @Restriction(require = @Condition(optifine)),
			category = Config.Category.MOD_TWEAKS
	)
	public static final TweakerMoreConfigOptionListHotkeyed OF_WITCH_HAT = newConfigOptionListHotkeyed("ofWitchHat", OptifineExtraModelRenderStrategy.DEFAULT);

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
			restriction = @Restriction(require = @Condition(optifine)),
			category = Config.Category.MOD_TWEAKS
	)
	public static final TweakerMoreConfigBoolean OF_USE_VANILLA_BRIGHTNESS_CACHE = newConfigBoolean("ofUseVanillaBrightnessCache", false);

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
					@Condition(value = minecraft, versionPredicates = "<1.17")
			}),
			category = Config.Category.PORTING
	)
	public static final TweakerMoreConfigBoolean LM_CUSTOM_SCHEMATIC_BASE_DIRECTORY_ENABLED_PORTING = newConfigBoolean("lmCustomSchematicBaseDirectoryEnabledPorting", false);

	@Config(
			type = Config.Type.GENERIC,
			restriction = @Restriction(require = {
					@Condition(litematica),
					@Condition(value = minecraft, versionPredicates = "<1.17")
			}),
			category = Config.Category.PORTING
	)
	public static final TweakerMoreConfigString LM_CUSTOM_SCHEMATIC_BASE_DIRECTORY_PORTING = newConfigString( "lmCustomSchematicBaseDirectoryPorting", LitematicaCustomSchematicBaseDirectoryPorting.getDefaultBaseSchematicDirectory().getAbsolutePath());

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

	public static void initConfigs()
	{
		//////////// Callbacks ////////////

		// common callbacks
		IValueChangeCallback<ConfigBoolean> redrawConfigGui = newValue -> TweakerMoreConfigGui.getCurrentInstance().ifPresent(TweakerMoreConfigGui::reDraw);

		// hotkeys
		setHotkeyCallback(COPY_SIGN_TEXT_TO_CLIPBOARD, SignTextCopier::copySignText, false);
		setHotkeyCallback(OPEN_TWEAKERMORE_CONFIG_GUI, TweakerMoreConfigGui::openGui, true);
		setHotkeyCallback(REFRESH_INVENTORY, InventoryRefresher::refresh, false);

		// value listeners
		ECRAFT_ITEM_SCROLLER_COMPACT.setValueChangeCallback(EasierCraftingRegistrar::onConfigValueChanged);
		HIDE_DISABLE_OPTIONS.setValueChangeCallback(redrawConfigGui);
		OF_SANTA_HAT.setValueChangeCallback(OptifinePlayerExtraModelOverrider::onConfigValueChanged);
		OF_WITCH_HAT.setValueChangeCallback(OptifinePlayerExtraModelOverrider::onConfigValueChanged);
		TWEAKM_FLAWLESS_FRAMES.setValueChangeCallback(config -> FlawlessFramesHandler.setEnabled(config.getBooleanValue()));

		// debugs
		TWEAKERMORE_DEBUG_MODE.setValueChangeCallback(redrawConfigGui);
		setHotkeyCallback(TWEAKERMORE_DEBUG_RESET_OPTION_STATISTIC, TweakerMoreDebugHelper::resetAllConfigStatistic, false);
		setHotkeyCallback(TWEAKERMORE_DEV_MIXIN_AUDIT, TweakerMoreDebugHelper::forceLoadAllMixins, true);
		setHotkeyCallback(TWEAKERMORE_DEV_PRINT_DOC, DocumentGenerator::onHotKey, true);

		//////////// Event Listeners ////////////

		TickHandler.getInstance().registerClientTickHandler(ServerDataSyncer.getInstance());
		RenderEventHandler.getInstance().registerWorldLastRenderer(InfoViewRenderer.getInstance());
		RenderEventHandler.getInstance().registerGameOverlayRenderer(new AutoContainerProcessorHintRenderer());

		//////////// Misc ////////////

		TWEAKM_CONTAINER_PROCESSOR_HINT.setCommentModifier(AutoContainerProcessorHintRenderer::modifyComment);
		TWEAKM_SCHEMATIC_PRO_PLACE.setCommentModifier(ProPlaceImpl::modifyComment);
	}

	private static void setHotkeyCallback(TweakerMoreConfigHotkey configHotkey, Runnable runnable, boolean cancelFurtherProcess)
	{
		configHotkey.setCallBack((action, key) -> {
			runnable.run();
			return cancelFurtherProcess;
		});
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
