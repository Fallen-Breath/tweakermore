## Features

New features provided by TweakerMore

### autoFillContainerThreshold

The minimum occupied slot amount for the item to trigger tweak tweakmAutoFillContainer

e.g. if you always have 2 slots of firework rocket you can set the value to 3,

 then your rockets will never be used to fill the container

- Category: Features
- Type: integer
- Default value: `2`
- Minimum value: `1`
- Maximum value: `36`


### copySignTextToClipBoard

Copy texts in the sign you are pointing to into the clipboard

- Category: Features
- Type: hotkey
- Default value: *no hotkey*

![copySignTextToClipBoard](assets/copySignTextToClipBoard-en_us.png)


### creativePickBlockWithState

When performing creative pick block action (middle click) with hotkey pressed,

store the target block's block state into the nbt named "BlockStateTag" of the picked item.

So the block state can be restored when you place blocks with the picked item

- Category: Features
- Type: hotkey with switch
- Default value: `LEFT_ALT`


### redstoneDustUpdateOrderTextAlpha

The alpha value of the floating text used in redstone dust update order displaying

See also: option tweakmShowRedstoneDustUpdateOrder

- Category: Features
- Type: double
- Default value: `0.6`
- Minimum value: `0.0`
- Maximum value: `1.0`


### refreshInventory

Request a player inventory refresh to the server

It's done by simulating an invalid inventory dragging operation,

so the server think that the inventory is out of sync then do resync

- Category: Features
- Type: hotkey
- Default value: *no hotkey*


### safeAfkHealthThreshold

The threshold of the health of the player which triggers the safe afk disconnecting feature

Disconnect when player takes damage and its health becomes less than the given value

- Category: Features
- Type: double
- Default value: `10.0`
- Minimum value: `0.0`
- Maximum value: `100.0`


### tweakmAutoCleanContainer

Automatically drops everything in the opened container

and then close the container

- Category: Features
- Type: hotkey togglable boolean
- Default value: *no hotkey*, `false`
- Mod restrictions:
  - Required mods:
    - Item Scroller (`itemscroller`)


### tweakmAutoCleanContainerBlackList

Items that will NOT be thrown out from the container with tweakmAutoCleanContainer

- Category: Features
- Type: string list
- Default value: `[minecraft:shulker_box]`
- Mod restrictions:
  - Required mods:
    - Item Scroller (`itemscroller`)


### tweakmAutoCleanContainerListType

The item restriction type for tweakmAutoCleanContainer

- Category: Features
- Type: option list
- Default value: `None`
- Available options: `None`, `Black List`, `White List`
- Mod restrictions:
  - Required mods:
    - Item Scroller (`itemscroller`)


### tweakmAutoCleanContainerWhiteList

Items that will be thrown out from the container with tweakmAutoCleanContainer

- Category: Features
- Type: string list
- Default value: `[minecraft:shulker_box]`
- Mod restrictions:
  - Required mods:
    - Item Scroller (`itemscroller`)


### tweakmAutoCollectMaterialListItem

Automatically collect missing items in litematica mod material list in the opened container to player inventory

and then close the container

- Category: Features
- Type: hotkey togglable boolean
- Default value: *no hotkey*, `false`
- Mod restrictions:
  - Required mods:
    - Litematica (`litematica`)
    - Item Scroller (`itemscroller`)


### tweakmAutoFillContainer

Automatically fill the opened container with the most numerous item stack in your inventory

iff the item stack is the only most numerous one

and then close the container

- Category: Features
- Type: hotkey togglable boolean
- Default value: *no hotkey*, `false`
- Mod restrictions:
  - Required mods:
    - Item Scroller (`itemscroller`)


### tweakmAutoPickSchematicBlock

Pick block from schematic automatically before block placement

It's logic is the same as the pickBlock tweaks in litematica mod, you need to enable pickBlockEnabled option in litematica

Does not work with easy place mode

- Category: Features
- Type: hotkey togglable boolean
- Default value: *no hotkey*, `false`
- Mod restrictions:
  - Required mods:
    - Tweakeroo (`tweakeroo`)
    - Litematica (`litematica`)


### tweakmAutoPutBackExistedItem

Automatically put back all items which also exist in the container from your inventory into the container

and then close the container

- Category: Features
- Type: hotkey togglable boolean
- Default value: *no hotkey*, `false`
- Mod restrictions:
  - Required mods:
    - Item Scroller (`itemscroller`)


### tweakmAutoVillagerTradeFavorites

Automatically trigger item scroller's villagerTradeFavorites feature when a merchant screen is opened

and then close the merchant screen

- Category: Features
- Type: hotkey togglable boolean
- Default value: *no hotkey*, `false`
- Mod restrictions:
  - Required mods:
    - Item Scroller (`itemscroller`)
    - Minecraft (`minecraft`) ` >=1.16`


### tweakmContainerProcessorHint

Displays the enabling status of auto container processing related features, including:

- tweakmAutoCleanContainer

- tweakmAutoFillContainer

- tweakmAutoPutBackExistedItem

- tweakmAutoCollectMaterialListItem

- Category: Features
- Type: hotkey togglable boolean
- Default value: *no hotkey*, `false`

![tweakmContainerProcessorHint](assets/tweakmContainerProcessorHint-en_us.png)


### tweakmContainerProcessorHintPos

The displayed position of the auto container processor enable status hint

- Category: Features
- Type: option list
- Default value: `Top Right`
- Available options: `Top Left`, `Top Right`, `Bottom Left`, `Bottom Right`, `Center`


### tweakmContainerProcessorHintScale

The font scale of the auto container processor enable status hint

- Category: Features
- Type: double
- Default value: `1.0`
- Minimum value: `0.25`
- Maximum value: `4.0`


### tweakmSafeAfk

Disconnect when receive damage

Health threshold to disconnect can be set via safeAfkHealthThreshold

- Category: Features
- Type: hotkey togglable boolean
- Default value: *no hotkey*, `false`


### tweakmSchematicBlockPlacementRestriction

Similar to litematica's placementRestriction option, it cancels block placement when the placement doesn't match current schematic

Unlike litematica's implementation, it uses a simple and accurate constraint strategy,

it works nicely with tweakeroo's placement tweaks

Does not work with litematica's or easy place mode

- Category: Features
- Type: hotkey togglable boolean
- Default value: *no hotkey*, `false`
- Mod restrictions:
  - Required mods:
    - Tweakeroo (`tweakeroo`)
    - Litematica (`litematica`)


### tweakmSchematicBlockPlacementRestrictionHint

Switch for displaying hint message when option tweakmSchematicBlockPlacementRestriction cancels a placement

- Category: Features
- Type: option list
- Default value: `All`
- Available options: `All`, `Wrong item only`, `Never`


### tweakmSchematicBlockPlacementRestrictionMargin

The no-block-placement protection margin length from schematic boxes

e.g. with default value 2, block placements within 2 blocks range of any schematic boxes will be cancelled

Used in option tweakmSchematicBlockPlacementRestriction

- Category: Features
- Type: integer
- Default value: `2`
- Minimum value: `0`
- Maximum value: `16`


### tweakmSchematicProPlace

A shortcut option to enable/disable the following options at the same time:

- tweakmAutoPickSchematicBlock

- tweakmSchematicBlockPlacementRestriction

With these options enabled, you can have a not-cheaty version of "easy place" from litematica,

which help you auto select target item and cancel wrong block placements

Has all the functions of easy place except for the floating block placing

Compatible with various block placement tweaks from tweakeroo, doesn't work with easy place mode

- Category: Features
- Type: hotkey togglable boolean
- Default value: *no hotkey*, `false`
- Mod restrictions:
  - Required mods:
    - Tweakeroo (`tweakeroo`)
    - Litematica (`litematica`)


### tweakmShowRedstoneDustUpdateOrder

When looking at a redstone dust block, show the block update order of it

- Category: Features
- Type: hotkey togglable boolean
- Default value: *no hotkey*, `false`

![tweakmShowRedstoneDustUpdateOrder](assets/tweakmShowRedstoneDustUpdateOrder.png)


### villagerOfferUsesDisplay

Display villager offer use and limit amount on villager offer list

Hover on the arrow of the offer to see it

- Category: Features
- Type: boolean
- Default value: `false`

![villagerOfferUsesDisplay](assets/villagerOfferUsesDisplay.png)


## MC Tweaks

Tweaks on Minecraft

### bossBarMaxEntry

Overwrites the maximum amount of boss bar to be displayed at the same time

It will also skip the vanilla windowHeight/3 limit check

Set it to -1 to disabled (use vanilla logic)

- Category: MC Tweaks
- Type: integer
- Default value: `-1`
- Minimum value: `-1`
- Maximum value: `20`

![bossBarMaxEntry](assets/bossBarMaxEntry-en_us.png)


### bossBarScale

Scale the boss bar hud with given factor

- Category: MC Tweaks
- Type: double
- Default value: `1.0`
- Minimum value: `0.001`
- Maximum value: `2.0`


### chatMessageLimit

Modify the maximum number of history messages stored in the chat hud

- Category: MC Tweaks
- Type: integer
- Default value: `100`
- Minimum value: `100`
- Maximum value: `10000`
- Mod restrictions:
  - Conflicted mods:
    - CompactChat (`compactchat`)
    - MoreChatHistory (`morechathistory`)
    - Parachute (`parachute`)
    - Raise Chat Limit (`raise-chat-limit`)
    - Where's My Chat History (`wmch`)


### connectionSimulatedDelay

Client network delay simulator. Enabled when the value is greater than 0

Adds given delay (in milliseconds) before any packet processing

Basically it stably adds your ping to the server with the given value

- Category: MC Tweaks
- Type: integer
- Default value: `0`
- Minimum value: `0`
- Maximum value: `15000`


### daytimeOverrideValue

The client-side daytime value to be overridden by tweak tweakmDaytimeOverride

- Category: MC Tweaks
- Type: integer
- Default value: `0`
- Minimum value: `0`
- Maximum value: `24000`


### disableBeaconBeamRendering

Prevents rendering beacon beams

So beacon beams can never suddenly come out when recording / rendering

- Category: MC Tweaks
- Type: hotkey togglable boolean
- Default value: *no hotkey*, `false`


### disableCameraFrustumCulling

Disable rendering culling using camera's frustum, i.e. stuffs outside the player's fov will also be rendered

Affects both block and entity rendering

A use case is to ensure the integrity of shadows when using shaders

Has significant impact on framerate

- Category: MC Tweaks
- Type: hotkey togglable boolean
- Default value: *no hotkey*, `false`


### disableCameraSubmersionFog

Disable fog effect caused by camera being submerged in block or fluid, including water, lava and powder snow

It will use the render distance fog instead, just like the camera is in the air

- Category: MC Tweaks
- Type: hotkey togglable boolean
- Default value: *no hotkey*, `false`

![disableCameraSubmersionFog](assets/disableCameraSubmersionFog.png)


### disableCreativeFlyClimbingCheck

Prevent creative flying player from entering "ladder climbing" state,

so player's flying movement will not be affected by ladder things

- Category: MC Tweaks
- Type: hotkey togglable boolean
- Default value: *no hotkey*, `false`


### disableEntityRenderInterpolation

Disable the entity animation interpolation during rendering

- Category: MC Tweaks
- Type: hotkey togglable boolean
- Default value: *no hotkey*, `false`


### disableLightUpdates

Yeets client-side light updates

- Category: MC Tweaks
- Type: hotkey togglable boolean
- Default value: *no hotkey*, `false`


### disablePistonBlockBreakingParticle

Remove block breaking particles when pistons break blocks

- Category: MC Tweaks
- Type: hotkey togglable boolean
- Default value: *no hotkey*, `false`
- Mod restrictions:
  - Required mods:
    - Minecraft (`minecraft`) ` >=1.17`


### disableRedstoneParticle

Disable all redstone particles

i.e. particles from redstone dust, redstone torch, redstone repeater etc.

- Category: MC Tweaks
- Type: hotkey togglable boolean
- Default value: *no hotkey*, `false`


### disableSignTextLengthLimit

Disable the text length limit based on character width when editing or rendering a sign block

A red exclamation mark will be shown on the left side when the limit is exceeded during the editing of the sign

- Category: MC Tweaks
- Type: hotkey togglable boolean
- Default value: *no hotkey*, `false`

![disableSignTextLengthLimit](assets/disableSignTextLengthLimit.png)


### fixChestMirroring

Fixed chest type is not properly transformed when chest block is mirrored

Affects stuffs that use vanilla block mirror logic, e.g. vanilla structure placement, litematica mod schematic placement

- Category: MC Tweaks
- Type: boolean
- Default value: `false`


### flyDrag

Overwrite the drag factor when flying in creative or spectator mode

Overwrites fabric-carpet's rule creativeFlyDrag when the value is modified

- Category: MC Tweaks
- Type: double
- Default value: `0.09`
- Minimum value: `0.0`
- Maximum value: `1.0`


### legacyF3NLogic

Modify the logic of hotkey F3 + N back to 1.15 and before

1.15- logic cheat sheet: creative -> spectator, other -> creative

- Category: MC Tweaks
- Type: boolean
- Default value: `false`
- Mod restrictions:
  - Required mods:
    - Minecraft (`minecraft`) ` >=1.16`


### maxChatHudHeight

The maximum height of the chat hud

- Category: MC Tweaks
- Type: integer
- Default value: `160`
- Minimum value: `160`
- Maximum value: `1000`


### netherPortalSoundChance

The chance for a nether portal block to play sound

Set it to 0.001 or 0.0001 for less noisy portal

- Category: MC Tweaks
- Type: double
- Default value: `0.01`
- Minimum value: `0.0`
- Maximum value: `0.01`


### prioritizedCommandSuggestions

Command suggestions in this list will be more forward in the command completion list

You can put those command completions you always prefer to choose in this list

so they will always be at the head of the command completion list

- Category: MC Tweaks
- Type: string list
- Default value: `[]`

![prioritizedCommandSuggestions](assets/prioritizedCommandSuggestions.png)


### scoreboardSideBarScale

Scale the scoreboard side bar hud with given factor

- Category: MC Tweaks
- Type: double
- Default value: `1.0`
- Minimum value: `0.001`
- Maximum value: `2.0`


### shulkerTooltipEnchantmentHint

Display enchantments of items in the tooltip of shulker box items

- Category: MC Tweaks
- Type: boolean
- Default value: `false`

![shulkerTooltipEnchantmentHint](assets/shulkerTooltipEnchantmentHint-en_us.png)


### shulkerTooltipFillLevelHint

Display content fill level in first line of the tooltip of shulker box items

- Category: MC Tweaks
- Type: boolean
- Default value: `false`

![shulkerTooltipFillLevelHint](assets/shulkerTooltipFillLevelHint-en_us.png)


### shulkerTooltipHintLengthLimit

The text length limit used during displaying extra information of items in the tooltip of shulker box items

When exceed, remaining information will be folded

- Category: MC Tweaks
- Type: integer
- Default value: `120`
- Minimum value: `0`
- Maximum value: `600`


### shulkerTooltipPotionInfoHint

Display potion information of items in the tooltip of shulker box items

- Category: MC Tweaks
- Type: boolean
- Default value: `false`

![shulkerTooltipPotionInfoHint](assets/shulkerTooltipPotionInfoHint-en_us.png)


### spectatorTeleportMenuIncludeSpectator

Allow spectator players in the player list to be listed in the spectator teleport menu

Spectators will be listed at the end of the menu with gray and italic name

- Category: MC Tweaks
- Type: boolean
- Default value: `false`


### tweakmDaytimeOverride

Override the time of day in the client world

Does not affect server-side daytime

- Category: MC Tweaks
- Type: hotkey togglable boolean
- Default value: *no hotkey*, `false`


### tweakmFakeNightVision

Always use night vision for game rendering, regardless of whether the player actually has night vision

- Category: MC Tweaks
- Type: hotkey togglable boolean
- Default value: *no hotkey*, `false`


### tweakmFlawlessFrames

Forced client to render all changes in loaded chunk for each frames

using the logic from replay mod which is used in its rendering tasks

WARNING: Expect client lag spike with enabled

- Category: MC Tweaks
- Type: hotkey togglable boolean
- Default value: *no hotkey*, `false`
- Mod restrictions:
  - Required mods:
    - Replay mod (`replaymod`)


### tweakmUnlimitedEntityRenderDistance

Set client-side entity render distance to unlimited

Still requires the server to send entity packets via EntityTracker properly

- Category: MC Tweaks
- Type: hotkey togglable boolean
- Default value: *no hotkey*, `false`


### tweakmWeatherOverride

Override the weather of the world in the client world

Does not affect server-side weather

- Category: MC Tweaks
- Type: hotkey togglable boolean
- Default value: *no hotkey*, `false`


### weatherOverrideValue

The client-side weather value to be overridden by tweak tweakmWeatherOverride

- Category: MC Tweaks
- Type: option list
- Default value: `Clear sky`
- Available options: `Clear sky`, `Rain`, `Thunderstorm`


## Mods Tweaks

Tweaks on mods

### applyTweakerMoreOptionLabelGlobally

Apply the TweakerMore style translated text + original text label to all config GUIs using malilib

- Category: Mods Tweaks
- Type: boolean
- Default value: `false`


### eCraftItemScrollerCompact

Fixed some item scroller functionality doesn't work with easier crafting mod

e.g. broken mass craft

- Category: Mods Tweaks
- Type: boolean
- Default value: `false`
- Mod restrictions:
  - Required mods:
    - EasierCrafting (`easiercrafting`)
    - Item Scroller (`itemscroller`)

![eCraftItemScrollerCompact](assets/eCraftItemScrollerCompact.png)


### handRestockBlackList

The items that will NOT trigger tweakHandRestock

- Category: Mods Tweaks
- Type: string list
- Default value: `[minecraft:lava_bucket]`
- Mod restrictions:
  - Required mods:
    - Tweakeroo (`tweakeroo`)


### handRestockListType

The item restriction type for tweakHandRestock

- Category: Mods Tweaks
- Type: option list
- Default value: `None`
- Available options: `None`, `Black List`, `White List`
- Mod restrictions:
  - Required mods:
    - Tweakeroo (`tweakeroo`)


### handRestockWhiteList

The items that will trigger tweakHandRestock

- Category: Mods Tweaks
- Type: string list
- Default value: `[minecraft:bucket]`
- Mod restrictions:
  - Required mods:
    - Tweakeroo (`tweakeroo`)


### minihudDisableLightOverlaySpawnCheck

Mini HUD light level overlay are rendered only on spawn-able block

This option removes the spawn-able check,

so light overlay will be rendered at every non-solid block above a non-air non-fluid block

- Category: Mods Tweaks
- Type: boolean
- Default value: `false`
- Mod restrictions:
  - Required mods:
    - MiniHUD (`minihud`)

![minihudDisableLightOverlaySpawnCheck](assets/minihudDisableLightOverlaySpawnCheck.png)


### ofRemoveItemFrameItemRenderDistance

Remove the item frame item render distance limit from Optifine

Back to vanilla behavior where the displayed item of item frame is always rendered together with the item frame

- Category: Mods Tweaks
- Type: boolean
- Default value: `false`
- Mod restrictions:
  - Required mods:
    - Optifine (`optifabric`)


### ofRemoveSignTextRenderDistance

Remove the sign text render distance limit from Optifine

Back to vanilla behavior where sign text is always rendered together with the sign block

- Category: Mods Tweaks
- Type: boolean
- Default value: `false`
- Mod restrictions:
  - Required mods:
    - Optifine (`optifabric`)


### ofUnlockF3FpsLimit

Removed 10 FPS render limit for F3 debug hud

- Category: Mods Tweaks
- Type: boolean
- Default value: `false`
- Mod restrictions:
  - Required mods:
    - Optifine (`optifabric`)
    - Minecraft (`minecraft`) ` >=1.15`


### replayAccurateTimelineTimestamp

Display timestamps accurate to milliseconds on the timeline in replay mod

- Category: Mods Tweaks
- Type: boolean
- Default value: `false`
- Mod restrictions:
  - Required mods:
    - Replay mod (`replaymod`)

![replayAccurateTimelineTimestamp](assets/replayAccurateTimelineTimestamp.png)


### replayFlySpeedLimitMultiplier

Multiple the camera fly speed upper limit in replay mod with given value

So the maximum fly speed can be increased

- Category: Mods Tweaks
- Type: integer
- Default value: `1`
- Minimum value: `1`
- Maximum value: `30`
- Mod restrictions:
  - Required mods:
    - Replay mod (`replaymod`)


### serverDataSyncer

Sync entity & block entity datas from the server using vanilla's tag query protocol

Sync when following actions happen:

- Tweakeroo inventory preview

- Litematica schematic saving

- MiniHUD bee hive bee amount display

- Litematica block info overlay

Requires player having OP permission (permission level 2) for it to work

- Category: Mods Tweaks
- Type: hotkey togglable boolean
- Default value: *no hotkey*, `false`
- Mod restrictions:
  - Required mods:
    - Tweakeroo (`tweakeroo`)

  *or*

  - Required mods:
    - Litematica (`litematica`)

  *or*

  - Required mods:
    - MiniHUD (`minihud`)


### serverDataSyncerQueryInterval

The query interval of serverDataSyncer for each batch of queries in game tick

With default value 1 it will send a batch of query every game tick

- Category: Mods Tweaks
- Type: integer
- Default value: `1`
- Minimum value: `1`
- Maximum value: `100`


### serverDataSyncerQueryLimit

Maximum amount of queries sent with serverDataSyncer per game tick

Queries exceeding the limit will be delayed

- Category: Mods Tweaks
- Type: integer
- Default value: `512`
- Minimum value: `1`
- Maximum value: `8192`


### shaderGameTimeAsWorldTime

Use game time instead of day time as uniform value "worldTime" for Optifine/Iris shaders

So with gamerule doDaylightCycle false this timer variable still increases

- Category: Mods Tweaks
- Type: boolean
- Default value: `false`
- Mod restrictions:
  - Required mods:
    - Optifine (`optifabric`)

  *or*

  - Required mods:
    - Iris (`iris`)


### xmapNoSessionFinalizationWait

Skip the session finalization operation of xaero's worldmap when quit a world

So no lag when quitting server or single player

Might have some side effects though

- Category: Mods Tweaks
- Type: boolean
- Default value: `false`
- Mod restrictions:
  - Required mods:
    - Xaero's World Map (`xaeroworldmap`)


## Porting

Stuffs porting from mods from other Minecraft versions

### isScrollStacksFallbackFixPorting

Fixed SCROLL_STACKS_FALLBACK of item-scroller ignores the last stack

The same as masa's fixed in itemscoller commit 0984fe7

- Category: Porting
- Type: boolean
- Default value: `false`
- Mod restrictions:
  - Required mods:
    - Item Scroller (`itemscroller`)
    - Minecraft (`minecraft`) ` <1.18`


### lmPickBlockShulkersPorting

Backports option pickBlockShulkers from Litematica 1.16+

- Category: Porting
- Type: boolean
- Default value: `false`
- Mod restrictions:
  - Required mods:
    - Litematica (`litematica`)
    - Minecraft (`minecraft`) ` <1.16`


### tkrDisableNauseaEffectPorting

Backports option disableNauseaEffect from Tweakeroo 1.17+

Also fixes the nether portal overlay fails to be displayed with this option on

- Category: Porting
- Type: hotkey togglable boolean
- Default value: *no hotkey*, `false`
- Mod restrictions:
  - Required mods:
    - Minecraft (`minecraft`) ` <1.17`


## Setting

Settings of TweakMore itself

### hideDisabledOptions

Hide options which are disabled due to mod relations unsatisfied in the config GUI

- Category: Setting
- Type: boolean
- Default value: `false`


### openTweakerMoreConfigGui

Open the config GUI of TweakerMore

- Category: Setting
- Type: hotkey
- Default value: `K,C`


### preserveConfigUnknownEntries

Preserve unknown config entries in TweakerMore's config file

If set to false, unknown entries will be removed on config being written

- Category: Setting
- Type: boolean
- Default value: `true`


### tweakerMoreDebugMode

Debug mode of TweakerMore

When enabled, debug parameter options and options not supported by the current Minecraft version will be displayed

and debugging related functions will be enabled

- Category: Setting
- Type: hotkey togglable boolean
- Default value: *no hotkey*, `false`


