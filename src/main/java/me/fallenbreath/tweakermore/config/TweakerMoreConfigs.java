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
import fi.dy.masa.malilib.config.IHotkeyTogglable;
import fi.dy.masa.malilib.config.options.ConfigBoolean;
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
import me.fallenbreath.tweakermore.impl.features.autoContainerProcess.AutoContainerProcessorHintRenderer;
import me.fallenbreath.tweakermore.impl.features.copyItemDataToClipBoard.ItemDataCopier;
import me.fallenbreath.tweakermore.impl.features.copySignTextToClipBoard.SignTextCopier;
import me.fallenbreath.tweakermore.impl.features.infoView.InfoViewRenderer;
import me.fallenbreath.tweakermore.impl.features.pistorder.PistorderRenderer;
import me.fallenbreath.tweakermore.impl.features.refreshInventory.InventoryRefresher;
import me.fallenbreath.tweakermore.impl.features.schematicProPlace.ProPlaceImpl;
import me.fallenbreath.tweakermore.impl.features.spectatorTeleportCommand.SpectatorTeleportCommand;
import me.fallenbreath.tweakermore.impl.mc_tweaks.flawlessFrames.FlawlessFramesHandler;
import me.fallenbreath.tweakermore.impl.mc_tweaks.particleLimit.ParticleLimitHelper;
import me.fallenbreath.tweakermore.impl.mc_tweaks.windowSize.WindowSizeHelper;
import me.fallenbreath.tweakermore.impl.mod_tweaks.eCraftMassCraftCompact.EasierCraftingRegistrar;
import me.fallenbreath.tweakermore.impl.mod_tweaks.lmRemoveEntityCommand.LitematicaRemoveEntityCommandOverrider;
import me.fallenbreath.tweakermore.impl.mod_tweaks.ofPlayerExtraModelOverride.OptifinePlayerExtraModelOverrider;
import me.fallenbreath.tweakermore.impl.mod_tweaks.serverDataSyncer.ServerDataSyncer;
import me.fallenbreath.tweakermore.impl.porting.lmCustomSchematicBaseDirectoryPorting.LitematicaCustomSchematicBaseDirectoryPorting;
import me.fallenbreath.tweakermore.impl.setting.debug.TweakerMoreDebugHelper;
import me.fallenbreath.tweakermore.util.ModIds;
import me.fallenbreath.tweakermore.util.RegistryUtil;
import me.fallenbreath.tweakermore.util.doc.DocumentGenerator;
import me.fallenbreath.tweakermore.util.render.TweakerMoreRenderEventHandler;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static me.fallenbreath.tweakermore.config.ConfigFactory.*;

@SuppressWarnings("unused")
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

	@Config(type = Config.Type.TWEAK, restriction = @Restriction(require = @Condition(ModIds.itemscroller)), category = Config.Category.FEATURES)
	public static final TweakerMoreConfigBooleanHotkeyed AUTO_CLEAN_CONTAINER = newConfigBooleanHotkeyed("autoCleanContainer");

	@Config(type = Config.Type.TWEAK, restriction = @Restriction(require = @Condition(ModIds.itemscroller)), category = Config.Category.FEATURES)
	public static final TweakerMoreConfigBooleanHotkeyed AUTO_CLEAN_CONTAINER_IGNORE_ENDER_CHEST = newConfigBooleanHotkeyed("autoCleanContainerIgnoreEnderChest");

	@Config(type = Config.Type.LIST, restriction = @Restriction(require = @Condition(ModIds.itemscroller)), category = Config.Category.FEATURES)
	public static final TweakerMoreConfigOptionList AUTO_CLEAN_CONTAINER_LIST_TYPE = newConfigOptionList("autoCleanContainerListType", UsageRestriction.ListType.NONE);

	@Config(type = Config.Type.LIST, restriction = @Restriction(require = @Condition(ModIds.itemscroller)), category = Config.Category.FEATURES)
	public static final TweakerMoreConfigStringList AUTO_CLEAN_CONTAINER_WHITELIST = newConfigStringList("autoCleanContainerWhiteList", ImmutableList.of(RegistryUtil.getItemId(Items.SHULKER_BOX)));

	@Config(type = Config.Type.LIST, restriction = @Restriction(require = @Condition(ModIds.itemscroller)), category = Config.Category.FEATURES)
	public static final TweakerMoreConfigStringList AUTO_CLEAN_CONTAINER_BLACKLIST = newConfigStringList("autoCleanContainerBlackList", ImmutableList.of(RegistryUtil.getItemId(Items.SHULKER_BOX)));

	public static final ItemRestriction AUTO_CLEAN_CONTAINER_RESTRICTION = new ItemRestriction();

	@Config(
			type = Config.Type.TWEAK,
			restriction = @Restriction(require = {
					@Condition(ModIds.litematica),
					@Condition(ModIds.itemscroller)
			}),
			category = Config.Category.FEATURES
	)
	public static final TweakerMoreConfigBooleanHotkeyed AUTO_COLLECT_MATERIAL_LIST_ITEM = newConfigBooleanHotkeyed("autoCollectMaterialListItem");

	@Config(
			type = Config.Type.LIST,
			restriction = @Restriction(require = {
					@Condition(ModIds.litematica),
					@Condition(ModIds.itemscroller)
			}),
			category = Config.Category.FEATURES
	)
	public static final TweakerMoreConfigOptionList AUTO_COLLECT_MATERIAL_LIST_ITEM_MESSAGE_TYPE = newConfigOptionList("autoCollectMaterialListItemMessageType", AutoCollectMaterialListItemLogType.DEFAULT);

	@Config(
			type = Config.Type.GENERIC,
			restriction = @Restriction(require = {
					@Condition(ModIds.litematica),
					@Condition(ModIds.itemscroller)
			}),
			category = Config.Category.FEATURES
	)
	public static final TweakerMoreConfigBoolean AUTO_COLLECT_MATERIAL_LIST_ITEM_CLOSE_GUI = newConfigBoolean("autoCollectMaterialListItemCloseGui", true);

	@Config(type = Config.Type.TWEAK, restriction = @Restriction(require = @Condition(ModIds.itemscroller)), category = Config.Category.FEATURES)
	public static final TweakerMoreConfigBooleanHotkeyed AUTO_FILL_CONTAINER = newConfigBooleanHotkeyed("autoFillContainer");

	@Config(type = Config.Type.TWEAK, restriction = @Restriction(require = @Condition(ModIds.itemscroller)), category = Config.Category.FEATURES)
	public static final TweakerMoreConfigBooleanHotkeyed AUTO_FILL_CONTAINER_IGNORE_ENDER_CHEST = newConfigBooleanHotkeyed("autoFillContainerIgnoreEnderChest");

	@Config(type = Config.Type.GENERIC, category = Config.Category.FEATURES)
	public static final TweakerMoreConfigInteger AUTO_FILL_CONTAINER_THRESHOLD = newConfigInteger("autoFillContainerThreshold", 2, 1, 36);

	@Config(
			type = Config.Type.TWEAK,
			restriction = @Restriction(require = @Condition(ModIds.litematica)),
			category = Config.Category.FEATURES
	)
	public static final TweakerMoreConfigBooleanHotkeyed AUTO_PICK_SCHEMATIC_BLOCK = newConfigBooleanHotkeyed("autoPickSchematicBlock");

	@Config(type = Config.Type.TWEAK, restriction = @Restriction(require = @Condition(ModIds.itemscroller)), category = Config.Category.FEATURES)
	public static final TweakerMoreConfigBooleanHotkeyed AUTO_PUT_BACK_EXISTED_ITEM = newConfigBooleanHotkeyed("autoPutBackExistedItem");

	@Config(
			type = Config.Type.TWEAK,
			restriction = @Restriction(require = {
					@Condition(ModIds.itemscroller),
					@Condition(value = ModIds.minecraft, versionPredicates = ">=1.16")
			}),
			category = Config.Category.FEATURES
	)
	public static final TweakerMoreConfigBooleanHotkeyed AUTO_VILLAGER_TRADE_FAVORITES = newConfigBooleanHotkeyed("autoVillagerTradeFavorites");

	@Config(type = Config.Type.TWEAK, category = Config.Category.FEATURES)
	public static final TweakerMoreConfigBooleanHotkeyed AUTO_RESPAWN = newConfigBooleanHotkeyed("autoRespawn");

	@Config(type = Config.Type.TWEAK, category = Config.Category.FEATURES)
	public static final TweakerMoreConfigBooleanHotkeyed CONTAINER_PROCESSOR_HINT = newConfigBooleanHotkeyed("containerProcessorHint");

	@Config(type = Config.Type.GENERIC, category = Config.Category.FEATURES)
	public static final TweakerMoreConfigOptionList CONTAINER_PROCESSOR_HINT_POS = newConfigOptionList("containerProcessorHintPos", HudAlignment.TOP_RIGHT);

	@Config(type = Config.Type.GENERIC, category = Config.Category.FEATURES)
	public static final TweakerMoreConfigDouble CONTAINER_PROCESSOR_HINT_SCALE = newConfigDouble("containerProcessorHintScale", 1, 0.25, 4);

	@Config(type = Config.Type.HOTKEY, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigHotkeyWithSwitch COPY_ITEM_DATA_TO_CLIPBOARD = newConfigHotKeyWithSwitch("copyItemDataToClipBoard", false, "F3,I", KeybindSettings.GUI);

	@Config(type = Config.Type.HOTKEY, category = Config.Category.FEATURES)
	public static final TweakerMoreConfigHotkey COPY_SIGN_TEXT_TO_CLIPBOARD = newConfigHotKey("copySignTextToClipBoard", "");

	@Config(type = Config.Type.HOTKEY, category = Config.Category.FEATURES)
	public static final TweakerMoreConfigHotkeyWithSwitch CREATIVE_PICK_BLOCK_WITH_STATE = newConfigHotKeyWithSwitch("creativePickBlockWithState", false, "LEFT_ALT", KeybindSettings.MODIFIER_INGAME);

	@Config(type = Config.Type.TWEAK, category = Config.Category.FEATURES)
	public static final TweakerMoreConfigBooleanHotkeyed FIREWORK_ROCKET_THROTTLER = newConfigBooleanHotkeyed("fireworkRocketThrottler");

	@Config(type = Config.Type.GENERIC, category = Config.Category.FEATURES)
	public static final TweakerMoreConfigDouble FIREWORK_ROCKET_THROTTLER_COOLDOWN = newConfigDouble("fireworkRocketThrottlerCooldown", 1, 0, 5);

	@Config(type = Config.Type.TWEAK, category = Config.Category.FEATURES)
	public static final TweakerMoreConfigBooleanHotkeyed INFO_VIEW = newConfigBooleanHotkeyed("infoView");

	@Config(type = Config.Type.GENERIC, category = Config.Category.FEATURES)
	public static final TweakerMoreConfigDouble INFO_VIEW_BEAM_ANGLE = newConfigDouble("infoViewBeamAngle", 40, 1, 120);

	@Config(type = Config.Type.GENERIC, category = Config.Category.FEATURES)
	public static final TweakerMoreConfigDouble INFO_VIEW_TARGET_DISTANCE = newConfigDouble("infoViewTargetDistance", 8, 4, 32);

	@Config(type = Config.Type.HOTKEY, category = Config.Category.FEATURES)
	public static final TweakerMoreConfigHotkey INFO_VIEW_RENDERING_KEY = newConfigHotKey("infoViewRenderingKey", "RIGHT_ALT", KeybindSettings.MODIFIER_INGAME);

	@Config(type = Config.Type.GENERIC, category = Config.Category.FEATURES)
	public static final TweakerMoreConfigInteger INFO_VIEW_SCANNING_PER_SECOND  = newConfigInteger("infoViewScanningPerSecond", 20, 0, 120);

	@Config(type = Config.Type.TWEAK, category = Config.Category.FEATURES)
	public static final TweakerMoreConfigBooleanHotkeyed INFO_VIEW_BEACON = newConfigBooleanHotkeyed("infoViewBeacon");

	@Config(type = Config.Type.LIST, category = Config.Category.FEATURES)
	public static final TweakerMoreConfigOptionList INFO_VIEW_BEACON_RENDER_STRATEGY = newConfigOptionList("infoViewBeaconRenderStrategy", InfoViewRenderStrategy.ALWAYS);

	@Config(type = Config.Type.LIST, category = Config.Category.FEATURES)
	public static final TweakerMoreConfigOptionList INFO_VIEW_BEACON_TARGET_STRATEGY = newConfigOptionList("infoViewBeaconTargetStrategy", InfoViewTargetStrategy.DEFAULT);

	@Config(type = Config.Type.TWEAK, category = Config.Category.FEATURES)
	public static final TweakerMoreConfigBooleanHotkeyed INFO_VIEW_COMPARATOR = newConfigBooleanHotkeyed("infoViewComparator");

	@Config(type = Config.Type.LIST, category = Config.Category.FEATURES)
	public static final TweakerMoreConfigOptionList INFO_VIEW_COMPARATOR_RENDER_STRATEGY = newConfigOptionList("infoViewComparatorRenderStrategy", InfoViewRenderStrategy.ALWAYS);

	@Config(type = Config.Type.LIST, category = Config.Category.FEATURES)
	public static final TweakerMoreConfigOptionList INFO_VIEW_COMPARATOR_TARGET_STRATEGY = newConfigOptionList("infoViewComparatorTargetStrategy", InfoViewTargetStrategy.BEAM);

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
	public static final TweakerMoreConfigBooleanHotkeyed INFO_VIEW_GROWTH_SPEED = newConfigBooleanHotkeyed("infoViewGrowthSpeed");

	@Config(type = Config.Type.LIST, category = Config.Category.FEATURES)
	public static final TweakerMoreConfigOptionList INFO_VIEW_GROWTH_SPEED_RENDER_STRATEGY = newConfigOptionList("infoViewGrowthSpeedRenderStrategy", InfoViewRenderStrategy.ALWAYS);

	@Config(type = Config.Type.LIST, category = Config.Category.FEATURES)
	public static final TweakerMoreConfigOptionList INFO_VIEW_GROWTH_SPEED_TARGET_STRATEGY = newConfigOptionList("infoViewGrowthSpeedTargetStrategy", InfoViewTargetStrategy.RANGE);

	@Config(type = Config.Type.GENERIC, category = Config.Category.FEATURES)
	public static final TweakerMoreConfigDouble INFO_VIEW_GROWTH_SPEED_TEXT_SCALE = newConfigDouble("infoViewGrowthSpeedTextScale", 0.6, 0.1, 3);

	@Config(type = Config.Type.TWEAK, category = Config.Category.FEATURES)
	public static final TweakerMoreConfigBooleanHotkeyed INFO_VIEW_HOPPER = newConfigBooleanHotkeyed("infoViewHopper");

	@Config(type = Config.Type.LIST, category = Config.Category.FEATURES)
	public static final TweakerMoreConfigOptionList INFO_VIEW_HOPPER_RENDER_STRATEGY = newConfigOptionList("infoViewHopperRenderStrategy", InfoViewRenderStrategy.ALWAYS);

	@Config(type = Config.Type.LIST, category = Config.Category.FEATURES)
	public static final TweakerMoreConfigOptionList INFO_VIEW_HOPPER_TARGET_STRATEGY = newConfigOptionList("infoViewHopperTargetStrategy", InfoViewTargetStrategy.BEAM);

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

	@Config(type = Config.Type.TWEAK, category = Config.Category.FEATURES)
	public static final TweakerMoreConfigBooleanHotkeyed INFO_VIEW_STRUCTURE_BLOCK = newConfigBooleanHotkeyed("infoViewStructureBlock");

	@Config(type = Config.Type.GENERIC, category = Config.Category.FEATURES)
	public static final TweakerMoreConfigInteger INFO_VIEW_STRUCTURE_BLOCK_MAX_WIDTH = newConfigInteger("infoViewStructureBlockMaxWidth", 200, 10, 2000);

	@Config(type = Config.Type.LIST, category = Config.Category.FEATURES)
	public static final TweakerMoreConfigOptionList INFO_VIEW_STRUCTURE_BLOCK_RENDER_STRATEGY = newConfigOptionList("infoViewStructureBlockRenderStrategy", InfoViewRenderStrategy.ALWAYS);

	@Config(type = Config.Type.LIST, category = Config.Category.FEATURES)
	public static final TweakerMoreConfigOptionList INFO_VIEW_STRUCTURE_BLOCK_TARGET_STRATEGY = newConfigOptionList("infoViewStructureBlockTargetStrategy", InfoViewTargetStrategy.DEFAULT);

	@Config(type = Config.Type.GENERIC, category = Config.Category.FEATURES)
	public static final TweakerMoreConfigDouble INFO_VIEW_STRUCTURE_BLOCK_TEXT_SCALE = newConfigDouble("infoViewStructureBlockTextScale", 1, 0.1, 3);

	@Config(type = Config.Type.GENERIC, restriction = @Restriction(conflict = @Condition(value = ModIds.pistorder, versionPredicates = "<=1.6.0")), category = Config.Category.FEATURES)
	public static final TweakerMoreConfigBoolean PISTORDER = newConfigBoolean("pistorder", false);

	@Config(type = Config.Type.HOTKEY, restriction = @Restriction(conflict = @Condition(value = ModIds.pistorder, versionPredicates = "<=1.6.0")), category = Config.Category.FEATURES)
	public static final TweakerMoreConfigHotkey PISTORDER_CLEAR_DISPLAY = newConfigHotKey("pistorderClearDisplay", "P");

	@Config(type = Config.Type.GENERIC, restriction = @Restriction(conflict = @Condition(value = ModIds.pistorder, versionPredicates = "<=1.6.0")), category = Config.Category.FEATURES)
	public static final TweakerMoreConfigBoolean PISTORDER_DYNAMICALLY_INFO_UPDATE = newConfigBoolean("pistorderDynamicallyInfoUpdate", true);

	@Config(type = Config.Type.GENERIC, restriction = @Restriction(conflict = @Condition(value = ModIds.pistorder, versionPredicates = "<=1.6.0")), category = Config.Category.FEATURES)
	public static final TweakerMoreConfigInteger PISTORDER_MAX_RENDER_DISTANCE = newConfigInteger("pistorderMaxRenderDistance", 256, 0, 2048);

	@Config(type = Config.Type.GENERIC, restriction = @Restriction(conflict = @Condition(value = ModIds.pistorder, versionPredicates = "<=1.6.0")), category = Config.Category.FEATURES)
	public static final TweakerMoreConfigInteger PISTORDER_MAX_SIMULATION_PUSH_LIMIT = newConfigInteger("pistorderMaxSimulationPushLimit", 128, 12, 1024);

	@Config(type = Config.Type.GENERIC, restriction = @Restriction(conflict = @Condition(value = ModIds.pistorder, versionPredicates = "<=1.6.0")), category = Config.Category.FEATURES)
	public static final TweakerMoreConfigBoolean PISTORDER_SWING_HAND = newConfigBoolean("pistorderSwingHand", true);

	@Config(type = Config.Type.GENERIC, restriction = @Restriction(conflict = @Condition(value = ModIds.pistorder, versionPredicates = "<=1.6.0")), category = Config.Category.FEATURES)
	public static final TweakerMoreConfigDouble PISTORDER_TEXT_ALPHA = newConfigDouble("pistorderTextAlpha", 1, 0, 1);

	@Config(type = Config.Type.GENERIC, restriction = @Restriction(conflict = @Condition(value = ModIds.pistorder, versionPredicates = "<=1.6.0")), category = Config.Category.FEATURES)
	public static final TweakerMoreConfigDouble PISTORDER_TEXT_SCALE = newConfigDouble("pistorderTextScale", 1, 0.1, 3);

	@Config(type = Config.Type.GENERIC, restriction = @Restriction(conflict = @Condition(value = ModIds.pistorder, versionPredicates = "<=1.6.0")), category = Config.Category.FEATURES)
	public static final TweakerMoreConfigBoolean PISTORDER_TEXT_SHADOW = newConfigBoolean("pistorderTextShadow", true);

	@Config(type = Config.Type.HOTKEY, category = Config.Category.FEATURES)
	public static final TweakerMoreConfigHotkey REFRESH_INVENTORY = newConfigHotKey("refreshInventory", "", KEYBIND_SETTINGS_ANY);

	@Config(type = Config.Type.TWEAK, category = Config.Category.FEATURES)
	public static final TweakerMoreConfigBooleanHotkeyed SAFE_AFK = newConfigBooleanHotkeyed("safeAfk");

	@Config(type = Config.Type.GENERIC, category = Config.Category.FEATURES)
	public static final TweakerMoreConfigDouble SAFE_AFK_HEALTH_THRESHOLD = newConfigDouble("safeAfkHealthThreshold", 10, 0, 100);

	@Config(
			type = Config.Type.TWEAK,
			restriction = @Restriction(require = @Condition(ModIds.litematica)),
			category = Config.Category.FEATURES
	)
	public static final TweakerMoreConfigBooleanHotkeyed SCHEMATIC_PRO_PLACE = newConfigBooleanHotkeyed("schematicProPlace");

	@Config(
			type = Config.Type.TWEAK,
			restriction = @Restriction(require = @Condition(ModIds.litematica)),
			category = Config.Category.FEATURES
	)
	public static final TweakerMoreConfigBooleanHotkeyed SCHEMATIC_BLOCK_PLACEMENT_RESTRICTION = newConfigBooleanHotkeyed("schematicBlockPlacementRestriction");

	@Config(type = Config.Type.LIST, category = Config.Category.FEATURES)
	public static final TweakerMoreConfigOptionList SCHEMATIC_BLOCK_PLACEMENT_RESTRICTION_HINT = newConfigOptionList("schematicBlockPlacementRestrictionHint", SchematicBlockPlacementRestrictionHintType.DEFAULT);

	@Config(type = Config.Type.LIST, category = Config.Category.FEATURES)
	public static final TweakerMoreConfigStringList SCHEMATIC_BLOCK_PLACEMENT_RESTRICTION_ITEM_WHITELIST = newConfigStringList("schematicBlockPlacementRestrictionItemWhitelist", ImmutableList.of());

	@Config(type = Config.Type.GENERIC, category = Config.Category.FEATURES)
	public static final TweakerMoreConfigInteger SCHEMATIC_BLOCK_PLACEMENT_RESTRICTION_MARGIN = newConfigInteger("schematicBlockPlacementRestrictionMargin", 2, 0, 16);

	@Config(type = Config.Type.TWEAK, category = Config.Category.FEATURES)
	public static final TweakerMoreConfigBooleanHotkeyed SCHEMATIC_BLOCK_PLACEMENT_RESTRICTION_CHECK_FACING = newConfigBooleanHotkeyed("schematicBlockPlacementRestrictionCheckFacing", true, "");

	@Config(type = Config.Type.TWEAK, category = Config.Category.FEATURES)
	public static final TweakerMoreConfigBooleanHotkeyed SCHEMATIC_BLOCK_PLACEMENT_RESTRICTION_CHECK_SLAB = newConfigBooleanHotkeyed("schematicBlockPlacementRestrictionCheckSlab", true, "");

	@Config(type = Config.Type.GENERIC, category = Config.Category.FEATURES)
	public static final TweakerMoreConfigBoolean SCHEMATIC_BLOCK_PLACEMENT_RESTRICTION_STRICT = newConfigBoolean("schematicBlockPlacementRestrictionStrict", true);

	@Config(type = Config.Type.LIST, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigOptionListHotkeyed SERVER_MSPT_METRICS_STATISTIC_TYPE = newConfigOptionListHotkeyed("serverMsptMetricsStatisticType", ServerMsptMetricsStatisticType.DEFAULT);

	@Config(type = Config.Type.GENERIC, restriction = @Restriction(require = @Condition(value = ModIds.minecraft, versionPredicates = ">=1.16")), category = Config.Category.FEATURES)
	public static final TweakerMoreConfigBoolean SPECTATOR_TELEPORT_COMMAND = newConfigBoolean("spectatorTeleportCommand", false);

	@Config(type = Config.Type.GENERIC, restriction = @Restriction(require = @Condition(value = ModIds.minecraft, versionPredicates = ">=1.16")), category = Config.Category.FEATURES)
	public static final TweakerMoreConfigString SPECTATOR_TELEPORT_COMMAND_PREFIX = newConfigString("spectatorTeleportCommandPrefix", "stp");

	@Config(type = Config.Type.GENERIC, category = Config.Category.FEATURES)
	public static final TweakerMoreConfigBoolean VILLAGER_OFFER_USES_DISPLAY = newConfigBoolean("villagerOfferUsesDisplay", false);

	////////////////////
	//    MC Tweaks   //
	////////////////////

	@Config(type = Config.Type.TWEAK, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigBooleanHotkeyed BARRIER_PARTICLE_ALWAYS_VISIBLE = newConfigBooleanHotkeyed("barrierParticleAlwaysVisible");

	@Config(type = Config.Type.TWEAK, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigBooleanHotkeyed BLOCK_EVENT_THROTTLER = newConfigBooleanHotkeyed("blockEventThrottler");

	@Config(type = Config.Type.LIST, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigStringList BLOCK_EVENT_THROTTLER_TARGET_BLOCKS = newConfigStringList("blockEventThrottlerTargetBlocks", ImmutableList.of(RegistryUtil.getBlockId(Blocks.PISTON), RegistryUtil.getBlockId(Blocks.STICKY_PISTON)));

	@Config(type = Config.Type.GENERIC, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigInteger BLOCK_EVENT_THROTTLER_THRESHOLD = newConfigInteger("blockEventThrottlerThreshold", 200, 0, 10000);

	@Config(type = Config.Type.GENERIC, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigDouble BLOCK_EVENT_THROTTLER_WHITELIST_RANGE = newConfigDouble("blockEventThrottlerWhitelistRange", 8, 0, 256);

	@Config(type = Config.Type.GENERIC, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigInteger BOSS_BAR_MAX_ENTRY = newConfigInteger("bossBarMaxEntry", -1, -1, 20);

	@Config(type = Config.Type.GENERIC, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigDouble BOSS_BAR_SCALE = newConfigDouble("bossBarScale", 1, 0.001, 2);

	@Config(
			type = Config.Type.GENERIC,
			restriction = @Restriction(conflict = {
					@Condition(ModIds.cheat_utils),
					@Condition(ModIds.compact_chat),
					@Condition(ModIds.more_chat_history),
					@Condition(ModIds.parachute),
					@Condition(ModIds.raise_chat_limit),
					@Condition(ModIds.wheres_my_chat_history)
			}),
			category = Config.Category.MC_TWEAKS
	)
	public static final TweakerMoreConfigInteger CHAT_MESSAGE_LIMIT = newConfigInteger("chatMessageLimit", 100, 100, 10000);

	@Config(type = Config.Type.GENERIC, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigBooleanHotkeyed CLIENT_ENTITY_TARGETING_SUPPORT_ALL = newConfigBooleanHotkeyed("clientEntityTargetingSelectAll");

	@Config(type = Config.Type.GENERIC, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigInteger CONNECTION_SIMULATED_DELAY = newConfigInteger("connectionSimulatedDelay", 0, 0, 15_000);

	@Config(type = Config.Type.TWEAK, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigBooleanHotkeyed DAYTIME_OVERRIDE = newConfigBooleanHotkeyed("daytimeOverride");

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

	@Config(type = Config.Type.DISABLE, restriction = @Restriction(require = @Condition(value = ModIds.minecraft, versionPredicates = ">=1.19")), category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigBooleanHotkeyed DISABLE_DARKNESS_EFFECT = newConfigBooleanHotkeyed("disableDarknessEffect");

	@Config(type = Config.Type.DISABLE, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigBooleanHotkeyed DISABLE_ENTITY_DEATH_TILTING = newConfigBooleanHotkeyed("disableEntityDeathTilting");

	@Config(type = Config.Type.DISABLE, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigBooleanHotkeyed DISABLE_ENTITY_MODEL_RENDERING = newConfigBooleanHotkeyed("disableEntityModelRendering");

	@Config(type = Config.Type.DISABLE, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigBooleanHotkeyed DISABLE_ENTITY_RENDER_INTERPOLATION = newConfigBooleanHotkeyed("disableEntityRenderInterpolation");

	@Config(type = Config.Type.GENERIC, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigBoolean DISABLE_ENTITY_RENDER_INTERPOLATION_FORCED_SYNC = newConfigBoolean("disableEntityRenderInterpolationForcedSync", false);

	@Config(type = Config.Type.TWEAK, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigBooleanHotkeyed DISABLE_F3_B_ENTITY_FACING_VECTOR = newConfigBooleanHotkeyed("disableF3BEntityFacingVector");

	@Config(type = Config.Type.DISABLE, restriction = @Restriction(require = @Condition(value = ModIds.minecraft, versionPredicates = ">=1.15")), category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigBooleanHotkeyed DISABLE_HONEY_BLOCK_EFFECT = newConfigBooleanHotkeyed("disableHoneyBlockEffect");

	@Config(type = Config.Type.DISABLE, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigBooleanHotkeyed DISABLE_HORIZON_SHADING_RENDERING = newConfigBooleanHotkeyed("disableHorizonShadingRendering");

	@Config(type = Config.Type.DISABLE, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigBooleanHotkeyed DISABLE_LIGHT_UPDATES = newConfigBooleanHotkeyed("disableLightUpdates");

	@Config(type = Config.Type.DISABLE, restriction = @Restriction(require = @Condition(value = ModIds.minecraft, versionPredicates = ">=1.17")), category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigBooleanHotkeyed DISABLE_PISTON_BLOCK_BREAKING_PARTICLE = newConfigBooleanHotkeyed("disablePistonBlockBreakingParticle");

	@Config(type = Config.Type.DISABLE, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigBooleanHotkeyed DISABLE_REDSTONE_PARTICLE = newConfigBooleanHotkeyed("disableRedstoneParticle");

	@Config(type = Config.Type.DISABLE, restriction = @Restriction(conflict = @Condition(value = ModIds.caxton, versionPredicates = "<0.3.0-beta.2")), category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigBooleanHotkeyed DISABLE_SIGN_TEXT_LENGTH_LIMIT = newConfigBooleanHotkeyed("disableSignTextLengthLimit");

	@Config(type = Config.Type.DISABLE, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigBooleanHotkeyed DISABLE_SLIME_BLOCK_BOUNCING = newConfigBooleanHotkeyed("disableSlimeBlockBouncing");

	@Config(type = Config.Type.DISABLE, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigBooleanHotkeyed DISABLE_TILT_VIEW_WHEN_HURT = newConfigBooleanHotkeyed("disableTiltViewWhenHurt");

	@Config(type = Config.Type.DISABLE, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigBooleanHotkeyed DISABLE_VIGNETTE_DARKNESS = newConfigBooleanHotkeyed("disableVignetteDarkness");

	@Config(type = Config.Type.GENERIC, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigDouble F3_B_ENTITY_FACING_VECTOR_LENGTH = newConfigDouble("f3BEntityFacingVectorLength", 2.0, 0, 16);

	@Config(type = Config.Type.GENERIC, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigBoolean F3_I_USE_RELATED_COORDINATE = newConfigBoolean("f3IUseRelatedCoordinate", false);

	@Config(type = Config.Type.GENERIC, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigBoolean F3_I_USE_RELATED_COORDINATE_SHIFT_1 = newConfigBoolean("f3IUseRelatedCoordinateShift1", true);

	@Config(type = Config.Type.TWEAK, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigBooleanHotkeyed FAKE_NIGHT_VISION = newConfigBooleanHotkeyed("fakeNightVision");

	@Config(type = Config.Type.FIX, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigBoolean FIX_CHEST_MIRRORING = newConfigBoolean("fixChestMirroring", false);

	@Config(type = Config.Type.FIX, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigBoolean FIX_HOVER_TEXT_SCALE = newConfigBoolean("fixHoverTextScale", false);

	@Config(type = Config.Type.TWEAK, restriction = @Restriction(require = @Condition(ModIds.replay_mod)), category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigBooleanHotkeyed FLAWLESS_FRAMES = newConfigBooleanHotkeyed("flawlessFrames");

	@Config(type = Config.Type.GENERIC, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigDouble FLY_DRAG = newConfigDouble("flyDrag", 0.09, 0, 1);

	@Config(type = Config.Type.TWEAK, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigBooleanHotkeyed FOV_OVERRIDE_ENABLED = newConfigBooleanHotkeyed("fovOverrideEnabled");

	@Config(type = Config.Type.GENERIC, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigDouble FOV_OVERRIDE_VALUE = newConfigDouble("fovOverrideValue", 70, 0.01, 180 - 0.01);

	@Config(type = Config.Type.GENERIC, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigBoolean ITEM_TOOLTIP_HIDE_UNTIL_MOUSE_MOVE = newConfigBoolean("itemTooltipHideUntilMouseMove", false);

	@Config(type = Config.Type.GENERIC, restriction = @Restriction(require = @Condition(value = ModIds.minecraft, versionPredicates = ">=1.20.2")), category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigBoolean KEEP_MESSAGE_HISTORY_ON_RECONFIGURATION = newConfigBoolean("keepMessageHistoryOnReconfiguration", false);

	@Config(type = Config.Type.GENERIC, restriction = @Restriction(require = @Condition(value = ModIds.minecraft, versionPredicates = ">=1.16")), category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigBoolean LEGACY_F3_N_LOGIC = newConfigBoolean("legacyF3NLogic", false);

	@Config(type = Config.Type.GENERIC, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigInteger MAX_CHAT_HUD_HEIGHT = newConfigInteger("maxChatHudHeight", 160, 160, 1000);

	@Config(type = Config.Type.GENERIC, restriction = @Restriction(require = @Condition(value = ModIds.minecraft, versionPredicates = ">=1.16")), category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigBoolean MULTIPLAYER_FORCED_ENABLED = newConfigBoolean("multiplayerForcedEnabled", false);

	@Config(type = Config.Type.GENERIC, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigDouble NETHER_PORTAL_SOUND_CHANCE = newConfigDouble("netherPortalSoundChance", 0.01D, 0.0D, 0.01D);

	@Config(type = Config.Type.GENERIC, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigInteger PARTICLE_LIMIT = newConfigInteger("particleLimit", 16384, 0, 1000000);

	@Config(type = Config.Type.LIST, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigStringList PLAYER_NAME_TAG_RENDER_STRATEGY_LIST = newConfigStringList("playerNameTagRenderStrategyList", ImmutableList.of());

	@Config(type = Config.Type.LIST, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigOptionList PLAYER_NAME_TAG_RENDER_STRATEGY_TYPE = newConfigOptionList("playerNameTagRenderStrategyType", RestrictionType.NONE);

	@Config(type = Config.Type.GENERIC, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigBoolean PLAYER_SKIN_BLOCKING_LOADING = newConfigBoolean("playerSkinBlockingLoading", false);

	@Config(type = Config.Type.GENERIC, restriction = @Restriction(require = @Condition(value = ModIds.minecraft, versionPredicates = ">=1.19.4")), category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigBoolean POTION_ITEM_SHOULD_HAVE_ENCHANTMENT_GLINT = newConfigBoolean("potionItemShouldHaveEnchantmentGlint", false);

	@Config(type = Config.Type.TWEAK, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigBooleanHotkeyed PRECISE_ITEM_ENTITY_MODEL = newConfigBooleanHotkeyed("preciseItemEntityModel");

	@Config(type = Config.Type.GENERIC, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigBoolean PRECISE_ITEM_ENTITY_MODEL_YAW_SNAP = newConfigBoolean("preciseItemEntityModelYawSnap", false);

	@Config(type = Config.Type.LIST, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigStringList PRIORITIZED_COMMAND_SUGGESTIONS = newConfigStringList("prioritizedCommandSuggestions", ImmutableList.of());

	@Config(type = Config.Type.GENERIC, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigDouble SCOREBOARD_SIDE_BAR_SCALE = newConfigDouble("scoreboardSideBarScale", 1, 0.001, 2);

	@Config(type = Config.Type.GENERIC, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigBoolean SHULKER_BOX_ITEM_CONTENT_HINT = newConfigBoolean("shulkerBoxItemContentHint", false);

	@Config(type = Config.Type.GENERIC, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigDouble SHULKER_BOX_ITEM_CONTENT_HINT_SCALE = newConfigDouble("shulkerBoxItemContentHintScale", 0.5, 0.01, 0.75);

	@Config(type = Config.Type.GENERIC, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigBoolean SHULKER_BOX_ITEM_CONTENT_HINT_SHOW_BAR_ON_MIXED = newConfigBoolean("shulkerBoxItemContentHintShowBarOnMixed", false);

	@Config(type = Config.Type.GENERIC, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigBoolean SHULKER_BOX_TOOLTIP_ENCHANTMENT_HINT = newConfigBoolean("shulkerBoxTooltipEnchantmentHint", false);

	@Config(type = Config.Type.GENERIC, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigBoolean SHULKER_BOX_TOOLTIP_FILL_LEVEL_HINT = newConfigBoolean("shulkerBoxTooltipFillLevelHint", false);

	@Config(type = Config.Type.GENERIC, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigInteger SHULKER_BOX_TOOLTIP_HINT_LENGTH_LIMIT = newConfigInteger("shulkerBoxTooltipHintLengthLimit", 120, 0, 600);

	@Config(type = Config.Type.GENERIC, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigBoolean SHULKER_BOX_TOOLTIP_POTION_INFO_HINT = newConfigBoolean("shulkerBoxTooltipPotionInfoHint", false);

	@Config(type = Config.Type.GENERIC, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigBoolean SIGN_MULTILINE_PASTE_SUPPORT = newConfigBoolean("signMultilinePasteSupport", false);

	@Config(type = Config.Type.GENERIC, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigBoolean SIGN_EDIT_SCREEN_CANCEL_BUTTON = newConfigBoolean("signEditScreenCancelButton", false);

	@Config(type = Config.Type.GENERIC, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigBoolean SIGN_EDIT_SCREEN_ESC_DISCARD = newConfigBoolean("signEditScreenEscDiscard", false);

	@Config(type = Config.Type.GENERIC, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigBoolean SPECTATOR_TELEPORT_MENU_INCLUDE_SPECTATOR = newConfigBoolean("spectatorTeleportMenuIncludeSpectator", false);

	@Config(type = Config.Type.GENERIC, restriction = @Restriction(require = @Condition(value = ModIds.minecraft, versionPredicates = ">=1.19.3")), category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigBoolean STEVE_ALEX_ONLY_DEFAULT_SKINS = newConfigBoolean("steveAlexOnlyDefaultSkins", false);

	@Config(type = Config.Type.GENERIC, restriction = @Restriction(require = @Condition(value = ModIds.minecraft, versionPredicates = ">=1.20.3")), category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigBoolean TICK_FREEZE_AUTO_REPLACE_WITH_UNFREEZE = newConfigBoolean("tickFreezeAutoReplaceWithUnfreeze", false);

	@Config(type = Config.Type.TWEAK, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigBooleanHotkeyed UNLIMITED_BLOCK_ENTITY_RENDER_DISTANCE = newConfigBooleanHotkeyed("unlimitedBlockEntityRenderDistance");

	@Config(type = Config.Type.TWEAK, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigBooleanHotkeyed UNLIMITED_ENTITY_RENDER_DISTANCE = newConfigBooleanHotkeyed("unlimitedEntityRenderDistance");

	@Config(type = Config.Type.TWEAK, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigBooleanHotkeyed WEATHER_OVERRIDE = newConfigBooleanHotkeyed("weatherOverride");

	@Config(type = Config.Type.LIST, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigOptionList WEATHER_OVERRIDE_VALUE = newConfigOptionList("weatherOverrideValue", WeatherOverrideValue.DEFAULT);

	@Config(type = Config.Type.GENERIC, category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigBoolean YEET_SERVER_IP_REVERSED_DNS_LOOKUP = newConfigBoolean("yeetServerIpReversedDnsLookup", false);

	@Config(type = Config.Type.HOTKEY, restriction = @Restriction(conflict = @Condition(ModIds.locked_window_size)), category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigHotkey WINDOW_SIZE_APPLY = newConfigHotKey("windowSizeApply", "");

	@Config(type = Config.Type.GENERIC, restriction = @Restriction(conflict = @Condition(ModIds.locked_window_size)), category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigInteger WINDOW_SIZE_WIDTH = newConfigInteger("windowSizeWidth", 854, 160, 15360);

	@Config(type = Config.Type.GENERIC, restriction = @Restriction(conflict = @Condition(ModIds.locked_window_size)), category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigInteger WINDOW_SIZE_HEIGHT = newConfigInteger("windowSizeHeight", 480, 90, 8640);

	@Config(type = Config.Type.GENERIC, restriction = @Restriction(conflict = @Condition(ModIds.locked_window_size)), category = Config.Category.MC_TWEAKS)
	public static final TweakerMoreConfigBoolean WINDOW_SIZE_PINNED = newConfigBoolean("windowSizePinned", false);

	////////////////////
	//   Mod Tweaks   //
	////////////////////

	@Config(type = Config.Type.GENERIC, category = Config.Category.MOD_TWEAKS)
	public static final TweakerMoreConfigBoolean APPLY_TWEAKERMORE_OPTION_LABEL_GLOBALLY = newConfigBoolean("applyTweakerMoreOptionLabelGlobally", false);

	@Config(
			type = Config.Type.GENERIC,
			restriction = @Restriction(require = {
					@Condition(ModIds.easier_crafting),
					@Condition(ModIds.itemscroller)
			}),
			category = Config.Category.MOD_TWEAKS
	)
	public static final TweakerMoreConfigBoolean ECRAFT_ITEM_SCROLLER_COMPACT = newConfigBoolean("eCraftItemScrollerCompact", false);

	@Config(type = Config.Type.GENERIC, restriction = @Restriction(require = @Condition(ModIds.extra_player_renderer)), category = Config.Category.MOD_TWEAKS)
	public static final TweakerMoreConfigBoolean EPR_HIDE_ON_DEBUG_HUD = newConfigBoolean("eprHideOnDebugHud", false);

	@Config(type = Config.Type.LIST, restriction = @Restriction(require = @Condition(ModIds.tweakeroo)), category = Config.Category.MOD_TWEAKS)
	public static final TweakerMoreConfigOptionList HAND_RESTORE_LIST_TYPE = newConfigOptionList("handRestockListType", UsageRestriction.ListType.NONE);

	@Config(type = Config.Type.LIST, restriction = @Restriction(require = @Condition(ModIds.tweakeroo)), category = Config.Category.MOD_TWEAKS)
	public static final TweakerMoreConfigStringList HAND_RESTORE_WHITELIST = newConfigStringList("handRestockWhiteList", ImmutableList.of(RegistryUtil.getItemId(Items.BUCKET)));

	@Config(type = Config.Type.LIST, restriction = @Restriction(require = @Condition(ModIds.tweakeroo)), category = Config.Category.MOD_TWEAKS)
	public static final TweakerMoreConfigStringList HAND_RESTORE_BLACKLIST = newConfigStringList("handRestockBlackList", ImmutableList.of(RegistryUtil.getItemId(Items.LAVA_BUCKET)));

	public static final ItemRestriction HAND_RESTORE_RESTRICTION = new ItemRestriction();

	@Config(
			type = Config.Type.GENERIC,
			restriction = @Restriction(require = @Condition(ModIds.litematica)),
			category = Config.Category.MOD_TWEAKS
	)
	public static final TweakerMoreConfigBoolean LM_ORIGIN_OVERRIDE_000 = newConfigBoolean("lmOriginOverride000", false);

	@Config(
			type = Config.Type.GENERIC,
			restriction = @Restriction(require = @Condition(ModIds.litematica)),
			category = Config.Category.MOD_TWEAKS
	)
	public static final TweakerMoreConfigString LM_REMOVE_ENTITY_COMMAND = newConfigString("lmRemoveEntityCommand", LitematicaRemoveEntityCommandOverrider.DEFAULT_COMMAND);

	@Config(
			type = Config.Type.GENERIC,
			restriction = @Restriction(require = @Condition(ModIds.litematica)),
			category = Config.Category.MOD_TWEAKS
	)
	public static final TweakerMoreConfigOptionList LM_REMOVE_ENTITY_COMMAND_POLICY = newConfigOptionList("lmRemoveEntityCommandPolicy", LitematicaRemoveEntityCommandPolicy.DEFAULT);

	@Config(
			type = Config.Type.GENERIC,
			restriction = @Restriction(require = @Condition(ModIds.minihud)),
			category = Config.Category.MOD_TWEAKS
	)
	public static final TweakerMoreConfigBoolean MINIHUD_DISABLE_LIGHT_OVERLAY_SPAWN_CHECK = newConfigBoolean("minihudDisableLightOverlaySpawnCheck", false);

	@Config(
			type = Config.Type.GENERIC,
			restriction = @Restriction(require = @Condition(ModIds.minihud)),
			category = Config.Category.MOD_TWEAKS
	)
	public static final TweakerMoreConfigBoolean MINIHUD_HIDE_IF_CHAT_SCREEN_OPENED = newConfigBoolean("minihudHideIfChatScreenOpened", false);

	@Config(type = Config.Type.GENERIC, category = Config.Category.MOD_TWEAKS)
	public static final TweakerMoreConfigBoolean ML_SHULKER_BOX_PREVIEW_SUPPORT_ENDER_CHEST = newConfigBoolean("mlShulkerBoxPreviewSupportEnderChest", false);

	@Config(
			type = Config.Type.LIST,
			restriction = @Restriction(require = @Condition(ModIds.optifine)),
			category = Config.Category.MOD_TWEAKS
	)
	public static final TweakerMoreConfigOptionListHotkeyed OF_SANTA_HAT = newConfigOptionListHotkeyed("ofSantaHat", OptifineExtraModelRenderStrategy.DEFAULT);

	@Config(
			type = Config.Type.LIST,
			restriction = @Restriction(require = @Condition(ModIds.optifine)),
			category = Config.Category.MOD_TWEAKS
	)
	public static final TweakerMoreConfigOptionListHotkeyed OF_WITCH_HAT = newConfigOptionListHotkeyed("ofWitchHat", OptifineExtraModelRenderStrategy.DEFAULT);

	@Config(
			type = Config.Type.GENERIC,
			restriction = @Restriction(require = @Condition(ModIds.optifine)),
			category = Config.Category.MOD_TWEAKS
	)
	public static final TweakerMoreConfigBoolean OF_REMOVE_SIGN_TEXT_RENDER_DISTANCE = newConfigBoolean("ofRemoveSignTextRenderDistance", false);

	@Config(
			type = Config.Type.GENERIC,
			restriction = @Restriction(require = @Condition(ModIds.optifine)),
			category = Config.Category.MOD_TWEAKS
	)
	public static final TweakerMoreConfigBoolean OF_REMOVE_ITEM_FRAME_ITEM_RENDER_DISTANCE = newConfigBoolean("ofRemoveItemFrameItemRenderDistance", false);

	@Config(
			type = Config.Type.GENERIC,
			restriction = @Restriction(require = {
					@Condition(ModIds.optifine),
					@Condition(value = ModIds.minecraft, versionPredicates = ">=1.15")
			}),
			category = Config.Category.MOD_TWEAKS
	)
	public static final TweakerMoreConfigBoolean OF_UNLOCK_F3_FPS_LIMIT = newConfigBoolean("ofUnlockF3FpsLimit", false);

	@Config(
			type = Config.Type.GENERIC,
			restriction = @Restriction(require = @Condition(ModIds.optifine)),
			category = Config.Category.MOD_TWEAKS
	)
	public static final TweakerMoreConfigBoolean OF_USE_VANILLA_BRIGHTNESS_CACHE = newConfigBoolean("ofUseVanillaBrightnessCache", false);

	@Config(
			type = Config.Type.GENERIC,
			restriction = @Restriction(require = @Condition(ModIds.replay_mod)),
			category = Config.Category.MOD_TWEAKS
	)
	public static final TweakerMoreConfigInteger REPLAY_FLY_SPEED_LIMIT_MULTIPLIER = newConfigInteger("replayFlySpeedLimitMultiplier", 1, 1, 30);

	@Config(
			type = Config.Type.GENERIC,
			restriction = @Restriction(require = @Condition(ModIds.replay_mod)),
			category = Config.Category.MOD_TWEAKS
	)
	public static final TweakerMoreConfigBoolean REPLAY_ACCURATE_TIMELINE_TIMESTAMP = newConfigBoolean("replayAccurateTimelineTimestamp", false);

	@Config(
			type = Config.Type.TWEAK,
			restriction = {
					@Restriction(require = @Condition(ModIds.tweakeroo)),
					@Restriction(require = @Condition(ModIds.litematica)),
					@Restriction(require = @Condition(ModIds.minihud))
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
					@Restriction(require = @Condition(ModIds.optifine)),
					@Restriction(require = @Condition(ModIds.iris))
			},
			category = Config.Category.MOD_TWEAKS
	)
	public static final TweakerMoreConfigBoolean SHADER_GAME_TIME_AS_WORLD_TIME = newConfigBoolean("shaderGameTimeAsWorldTime", false);

	@Config(
			type = Config.Type.GENERIC,
			restriction = @Restriction(require = @Condition(ModIds.xaero_worldmap)),
			category = Config.Category.MOD_TWEAKS
	)
	public static final TweakerMoreConfigBoolean XMAP_NO_SESSION_FINALIZATION_WAIT = newConfigBoolean("xmapNoSessionFinalizationWait", false);

	@Config(
			type = Config.Type.GENERIC,
			restriction = {
					@Restriction(require = {@Condition(ModIds.tweakeroo), @Condition(ModIds.xaero_minimap)}),
					@Restriction(require = {@Condition(ModIds.tweakeroo), @Condition(ModIds.xaero_betterpvp)}),
			},
			category = Config.Category.MOD_TWEAKS
	)
	public static final TweakerMoreConfigBoolean XMAP_WAYPOINT_FREECAM_COMPACT = newConfigBoolean("xmapWaypointFreecamCompact", false);

	//////////////////////////
	//        Porting       //
	//////////////////////////

	@Config(
			type = Config.Type.GENERIC,
			restriction = @Restriction(require = {
					@Condition(ModIds.litematica),
					@Condition(value = ModIds.minecraft, versionPredicates = "<1.17")
			}),
			category = Config.Category.PORTING
	)
	public static final TweakerMoreConfigBoolean LM_CUSTOM_SCHEMATIC_BASE_DIRECTORY_ENABLED_PORTING = newConfigBoolean("lmCustomSchematicBaseDirectoryEnabledPorting", false);

	@Config(
			type = Config.Type.GENERIC,
			restriction = @Restriction(require = {
					@Condition(ModIds.litematica),
					@Condition(value = ModIds.minecraft, versionPredicates = "<1.17")
			}),
			category = Config.Category.PORTING
	)
	public static final TweakerMoreConfigString LM_CUSTOM_SCHEMATIC_BASE_DIRECTORY_PORTING = newConfigString( "lmCustomSchematicBaseDirectoryPorting", LitematicaCustomSchematicBaseDirectoryPorting.getDefaultBaseSchematicDirectory().getAbsolutePath());

	@Config(
			type = Config.Type.GENERIC,
			restriction = @Restriction(require = {
					@Condition(ModIds.litematica),
					@Condition(value = ModIds.minecraft, versionPredicates = "<1.16")
			}),
			category = Config.Category.PORTING
	)
	public static final TweakerMoreConfigBoolean LM_PICK_BLOCK_SHULKERS_PORTING = newConfigBoolean("lmPickBlockShulkersPorting", false);

	@Config(
			type = Config.Type.GENERIC,
			restriction = @Restriction(require = @Condition(value = ModIds.minecraft, versionPredicates = "<1.20")),
			category = Config.Category.PORTING
	)
	public static final TweakerMoreConfigBoolean MC_SPECTATOR_ENTER_SINKING_FIX_PORTING = newConfigBoolean("mcSpectatorEnterSinkingFixPorting", false);

	@Config(
			type = Config.Type.FIX,
			restriction = @Restriction(require = {
					@Condition(ModIds.itemscroller),
					@Condition(value = ModIds.minecraft, versionPredicates = "<1.18")
			}),
			category = Config.Category.PORTING
	)
	public static final TweakerMoreConfigBoolean IS_SCROLL_STACKS_FALLBACK_FIX_PORTING = newConfigBoolean("isScrollStacksFallbackFixPorting", false);

	@Config(
			type = Config.Type.DISABLE,
			restriction = @Restriction(require = {
					@Condition(value = ModIds.minecraft, versionPredicates = "<1.17")
			}),
			category = Config.Category.PORTING
	)
	public static final TweakerMoreConfigBooleanHotkeyed TKR_DISABLE_NAUSEA_EFFECT_PORTING = newConfigBooleanHotkeyed("tkrDisableNauseaEffectPorting");

	//////////////////////////
	//  TweakerMore Setting //
	//////////////////////////

	@Config(type = Config.Type.GENERIC, category = Config.Category.SETTING)
	public static final TweakerMoreConfigDouble CONFIG_ORIGINAL_NAME_SCALE = newConfigDouble("configOriginalNameScale", 0.65, 0, 1);

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
		setHotkeyCallback(COPY_ITEM_DATA_TO_CLIPBOARD, ItemDataCopier::copyItemData, false);
		setHotkeyCallback(COPY_SIGN_TEXT_TO_CLIPBOARD, SignTextCopier::copySignText, false);
		setHotkeyCallback(OPEN_TWEAKERMORE_CONFIG_GUI, TweakerMoreConfigGui::openGui, true);
		setHotkeyCallback(PISTORDER_CLEAR_DISPLAY, PistorderRenderer.getInstance()::clearDisplay, false);
		setHotkeyCallback(REFRESH_INVENTORY, InventoryRefresher::refresh, false);
		setHotkeyCallback(WINDOW_SIZE_APPLY, WindowSizeHelper::applyWindowSize, false);

		// value listeners
		ECRAFT_ITEM_SCROLLER_COMPACT.setValueChangeCallback(EasierCraftingRegistrar::onConfigValueChanged);
		HIDE_DISABLE_OPTIONS.setValueChangeCallback(redrawConfigGui);
		LM_REMOVE_ENTITY_COMMAND.setValueChangeCallback(LitematicaRemoveEntityCommandOverrider::onCommandOverrideChanged);
		OF_SANTA_HAT.setValueChangeCallback(OptifinePlayerExtraModelOverrider::onConfigValueChanged);
		OF_WITCH_HAT.setValueChangeCallback(OptifinePlayerExtraModelOverrider::onConfigValueChanged);
		PARTICLE_LIMIT.setValueChangeCallback(ParticleLimitHelper::onConfigValueChanged);
		FLAWLESS_FRAMES.setValueChangeCallback(FlawlessFramesHandler::onConfigValueChanged);

		// debugs
		TWEAKERMORE_DEBUG_MODE.setValueChangeCallback(redrawConfigGui);
		setHotkeyCallback(TWEAKERMORE_DEBUG_RESET_OPTION_STATISTIC, TweakerMoreDebugHelper::resetAllConfigStatistic, false);
		setHotkeyCallback(TWEAKERMORE_DEV_MIXIN_AUDIT, TweakerMoreDebugHelper::forceLoadAllMixins, true);
		setHotkeyCallback(TWEAKERMORE_DEV_PRINT_DOC, DocumentGenerator::onHotKey, true);

		//////////// Event Listeners ////////////

		TickHandler.getInstance().registerClientTickHandler(ServerDataSyncer.getInstance());
		TickHandler.getInstance().registerClientTickHandler(PistorderRenderer.getInstance());
		TweakerMoreRenderEventHandler.register(new AutoContainerProcessorHintRenderer());
		TweakerMoreRenderEventHandler.register(InfoViewRenderer.getInstance());
		TweakerMoreRenderEventHandler.register(PistorderRenderer.getInstance());

		//////////// Misc ////////////

		CONTAINER_PROCESSOR_HINT.setCommentModifier(AutoContainerProcessorHintRenderer::modifyComment);
		SCHEMATIC_PRO_PLACE.setCommentModifier(ProPlaceImpl::modifyComment);
	}

	private static void setHotkeyCallback(TweakerMoreConfigHotkey configHotkey, Runnable runnable, boolean cancelFurtherProcess)
	{
		configHotkey.setCallBack((action, key) -> {
			runnable.run();
			return cancelFurtherProcess;
		});
	}

	static void onConfigLoaded()
	{
		TweakerMoreConfigs.HAND_RESTORE_RESTRICTION.setListType((UsageRestriction.ListType)TweakerMoreConfigs.HAND_RESTORE_LIST_TYPE.getOptionListValue());
		TweakerMoreConfigs.HAND_RESTORE_RESTRICTION.setListContents(TweakerMoreConfigs.HAND_RESTORE_BLACKLIST.getStrings(), TweakerMoreConfigs.HAND_RESTORE_WHITELIST.getStrings());
		TweakerMoreConfigs.AUTO_CLEAN_CONTAINER_RESTRICTION.setListType((UsageRestriction.ListType)TweakerMoreConfigs.AUTO_CLEAN_CONTAINER_LIST_TYPE.getOptionListValue());
		TweakerMoreConfigs.AUTO_CLEAN_CONTAINER_RESTRICTION.setListContents(TweakerMoreConfigs.AUTO_CLEAN_CONTAINER_BLACKLIST.getStrings(), TweakerMoreConfigs.AUTO_CLEAN_CONTAINER_WHITELIST.getStrings());

		SpectatorTeleportCommand.init();
		WindowSizeHelper.onConfigLoaded();
	}

	// Config fields collecting

	private static final List<TweakerMoreOption> OPTIONS = Lists.newArrayList();
	private static final Map<Config.Category, List<TweakerMoreOption>> CATEGORY_TO_OPTION = Maps.newLinkedHashMap();
	private static final Map<Config.Type, List<TweakerMoreOption>> TYPE_TO_OPTION = Maps.newLinkedHashMap();
	private static final Map<IConfigBase, TweakerMoreOption> CONFIG_TO_OPTION = Maps.newLinkedHashMap();
	private static final Map<String, TweakerMoreOption> NAME_TO_OPTION = Maps.newLinkedHashMap();

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

					if ((annotation.type() == Config.Type.TWEAK || annotation.type() == Config.Type.DISABLE) && !(config instanceof IHotkeyTogglable))
					{
						throw new IllegalStateException(String.format(
								"Config %s annotated with type %s should be a IHotkeyTogglable, but found class %s",
								field.getName(), annotation.type(), config.getClass().getName()
						));
					}

					TweakerMoreOption tweakerMoreOption = new TweakerMoreOption(annotation, (TweakerMoreIConfigBase)config);
					OPTIONS.add(tweakerMoreOption);
					CATEGORY_TO_OPTION.computeIfAbsent(tweakerMoreOption.getCategory(), k -> Lists.newArrayList()).add(tweakerMoreOption);
					TYPE_TO_OPTION.computeIfAbsent(tweakerMoreOption.getType(), k -> Lists.newArrayList()).add(tweakerMoreOption);
					CONFIG_TO_OPTION.put(tweakerMoreOption.getConfig(), tweakerMoreOption);
					NAME_TO_OPTION.put(tweakerMoreOption.getConfig().getName(), tweakerMoreOption);
				}
				catch (Exception e)
				{
					TweakerMoreMod.LOGGER.error("TweakerMoreConfigs init failed", e);
					throw new RuntimeException(e);
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

	public static Optional<TweakerMoreOption> getOptionByName(String configName)
	{
		return Optional.ofNullable(NAME_TO_OPTION.get(configName));
	}

	public static boolean hasConfig(IConfigBase iConfigBase)
	{
		return getOptionFromConfig(iConfigBase).isPresent();
	}
}
