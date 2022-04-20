## Tweaks

### autoFillContainerThreshold

The minimum occupied slot amount for the item to trigger tweak tweakmAutoFillContainer

e.g. if you always have 2 slots of firework rocket you can set the value to 3,

 then your rockets will never be used to fill the container

- Category: Tweaks
- Type: integer
- Default value: `2`
- Minimum value: `1`
- Maximum value: `36`


### bossBarMaxEntry

Overwrites the maximum amount of boss bar to be displayed at the same time

It will also skip the vanilla windowHeight/3 limit check

Set it to -1 to disabled (use vanilla logic)

- Category: Tweaks
- Type: integer
- Default value: `-1`
- Minimum value: `-1`
- Maximum value: `20`

![bossBarMaxEntry](assets/bossBarMaxEntry-en_us.png)


### bossBarScale

Scale the boss bar hud with given factor

- Category: Tweaks
- Type: double
- Default value: `1.0`
- Minimum value: `0.001`
- Maximum value: `2.0`


### chatMessageLimit

Modify the maximum number of history messages stored in the chat hud

- Category: Tweaks
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

- Category: Tweaks
- Type: integer
- Default value: `0`
- Minimum value: `0`
- Maximum value: `15000`


### copySignTextToClipBoard

Copy texts in the sign you are pointing to into the clipboard

- Category: Tweaks
- Type: hotkey
- Default value: *no hotkey*

![copySignTextToClipBoard](assets/copySignTextToClipBoard-en_us.png)


### daytimeOverrideValue

The client-side daytime value to be overrided by tweak tweakmDaytimeOverride

- Category: Tweaks
- Type: integer
- Default value: `0`
- Minimum value: `0`
- Maximum value: `24000`


### disableCameraFrustumCulling

Disable rendering culling using camera's frustum, i.e. stuffs outside the player's fov will also be rendered

Affects both block and entity rendering

A use case is to ensure the integrity of shadows when using shaders

Has significant impact on framerate

- Category: Tweaks
- Type: togglable hotkey
- Default value: *no hotkey*, `false`


### disableLightUpdates

Yeets client-side light updates

- Category: Tweaks
- Type: togglable hotkey
- Default value: *no hotkey*, `false`


### disablePistonBlockBreakingParticle

Remove block breaking particles when pistons break blocks

- Category: Tweaks
- Type: togglable hotkey
- Default value: *no hotkey*, `false`
- Mod restrictions:
  - Required mods:
    - Minecraft (`minecraft`) ` >=1.17`


### disableRedstoneParticle

Disable all redstone particles

i.e. particles from redstone dust, redstone torch, redstone repeater etc.

- Category: Tweaks
- Type: togglable hotkey
- Default value: *no hotkey*, `false`


### disableSignTextLengthLimit

Disable the text length limit based on character width when editing or rendering a sign block

A red exclamation mark will be shown on the left side when the limit is exceeded during the editing of the sign

- Category: Tweaks
- Type: togglable hotkey
- Default value: *no hotkey*, `false`

![disableSignTextLengthLimit](assets/disableSignTextLengthLimit.png)


### fixChestMirroring

Fixed chest type is not properly transformed when chest block is mirrored

Affects stuffs that use vanilla block mirror logic, e.g. vanilla structure placement, litematica mod schematic placement

- Category: Tweaks
- Type: boolean
- Default value: `false`


### handRestockBlackList

The items that will NOT trigger tweakHandRestock

- Category: Tweaks
- Type: string list
- Default value: `[minecraft:lava_bucket]`
- Mod restrictions:
  - Required mods:
    - Tweakeroo (`tweakeroo`)


### handRestockListType

The item restriction type for tweakHandRestock

- Category: Tweaks
- Type: option list
- Default value: `none`
- Available options: `None`, `Black List`
- Mod restrictions:
  - Required mods:
    - Tweakeroo (`tweakeroo`)


### handRestockWhiteList

The items that will trigger tweakHandRestock

- Category: Tweaks
- Type: string list
- Default value: `[minecraft:bucket]`
- Mod restrictions:
  - Required mods:
    - Tweakeroo (`tweakeroo`)


### legacyF3NLogic

Modify the logic of hotkey F3 + N back to 1.15 and before

1.15- logic cheat sheet: creative -> spectator, other -> creative

- Category: Tweaks
- Type: boolean
- Default value: `false`
- Mod restrictions:
  - Required mods:
    - Minecraft (`minecraft`) ` >=1.16`


### maxChatHudHeight

The maximum height of the chat hud

- Category: Tweaks
- Type: integer
- Default value: `160`
- Minimum value: `160`
- Maximum value: `1000`


### netherPortalSoundChance

The chance for a nether portal block to play sound

Set it to 0.001 or 0.0001 for less noisy portal

- Category: Tweaks
- Type: double
- Default value: `0.01`
- Minimum value: `0.0`
- Maximum value: `0.01`


### prioritizedCommandSuggestions

Command suggestions in this list will be more forward in the command completion list

You can put those command completions you always prefer to choose in this list

so they will always be at the head of the command completion list

- Category: Tweaks
- Type: string list
- Default value: `[]`

![prioritizedCommandSuggestions](assets/prioritizedCommandSuggestions.png)


### redstoneDustUpdateOrderTextAlpha

The alpha value of the floating text used in redstone dust update order displaying

See also: option tweakmShowRedstoneDustUpdateOrder

- Category: Tweaks
- Type: double
- Default value: `0.6`
- Minimum value: `0.0`
- Maximum value: `1.0`


### refreshInventory

Request a player inventory refresh to the server

It's done by simulating an invalid inventory dragging operation,

so the server think that the inventory is out of sync then do resync

- Category: Tweaks
- Type: hotkey
- Default value: *no hotkey*


### safeAfkHealthThreshold

The threshold of the health of the player which triggers the safe afk disconnecting feature

Disconnect when player takes damage and its health becomes less than the given value

- Category: Tweaks
- Type: double
- Default value: `10.0`
- Minimum value: `0.0`
- Maximum value: `100.0`


### scoreboardSideBarScale

Scale the scoreboard side bar hud with given factor

- Category: Tweaks
- Type: double
- Default value: `1.0`
- Minimum value: `0.001`
- Maximum value: `2.0`


### shulkerTooltipEnchantmentHint

Display enchantments of items in the tooltip of shulker box items

- Category: Tweaks
- Type: boolean
- Default value: `false`

![shulkerTooltipEnchantmentHint](assets/shulkerTooltipEnchantmentHint-en_us.png)


### shulkerTooltipFillLevelHint

Display content fill level in first line of the tooltip of shulker box items

- Category: Tweaks
- Type: boolean
- Default value: `false`

![shulkerTooltipFillLevelHint](assets/shulkerTooltipFillLevelHint-en_us.png)


### tweakmAutoCleanContainer

Automatically drops everything in the opened container

and then close the container

- Category: Tweaks
- Type: togglable hotkey
- Default value: *no hotkey*, `false`
- Mod restrictions:
  - Required mods:
    - Item Scroller (`itemscroller`)


### tweakmAutoCollectMaterialListItem

Automatically collect missing items in litematica mod material list in the opened container to player inventory

and then close the container

- Category: Tweaks
- Type: togglable hotkey
- Default value: *no hotkey*, `false`
- Mod restrictions:
  - Required mods:
    - Litematica (`litematica`)
    - Item Scroller (`itemscroller`)


### tweakmAutoFillContainer

Automatically fill the opened container with the most numerous item stack in your inventory

iff the item stack is the only most numerous one

and then close the container

- Category: Tweaks
- Type: togglable hotkey
- Default value: *no hotkey*, `false`
- Mod restrictions:
  - Required mods:
    - Item Scroller (`itemscroller`)


### tweakmAutoPickSchematicBlock

Pick block from schematic automatically before block placement

It's logic is the same as the pickBlock tweaks in litematica mod, you need to enable pickBlockEnabled option in litematica

Does not work with easy place mode

- Category: Tweaks
- Type: togglable hotkey
- Default value: *no hotkey*, `false`
- Mod restrictions:
  - Required mods:
    - Tweakeroo (`tweakeroo`)
    - Litematica (`litematica`)


### tweakmDaytimeOverride

Override the time of day of the client world

Does not affect server-side daytime

- Category: Tweaks
- Type: togglable hotkey
- Default value: *no hotkey*, `false`


### tweakmFlawlessFrames

Forced client to render all changes in loaded chunk for each frames

using the logic from replay mod which is used in its rendering tasks

WARNING: Expect client lag spike with enabled

- Category: Tweaks
- Type: togglable hotkey
- Default value: *no hotkey*, `false`
- Mod restrictions:
  - Required mods:
    - Replay mod (`replaymod`)


### tweakmSafeAfk

Disconnect when receive damage

Health threshold to disconnect can be set via safeAfkHealthThreshold

- Category: Tweaks
- Type: togglable hotkey
- Default value: *no hotkey*, `false`


### tweakmShowRedstoneDustUpdateOrder

When looking at a redstone dust block, show the block update order of it

- Category: Tweaks
- Type: togglable hotkey
- Default value: *no hotkey*, `false`


### tweakmUnlimitedEntityRenderDistance

Set client-side entity render distance to unlimited

Still requires the server to send entity packets via EntityTracker properly

- Category: Tweaks
- Type: togglable hotkey
- Default value: *no hotkey*, `false`


### villagerOfferUsesDisplay

Display villager offer use and limit amount on villager offer list

Hover on the arrow of the offer to see it

- Category: Tweaks
- Type: boolean
- Default value: `false`

![villagerOfferUsesDisplay](assets/villagerOfferUsesDisplay.png)


## Mods Tweaks

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
- Type: togglable hotkey
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
- Type: togglable hotkey
- Default value: *no hotkey*, `false`
- Mod restrictions:
  - Required mods:
    - Minecraft (`minecraft`) ` <1.17`


## Setting

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
- Type: togglable hotkey
- Default value: *no hotkey*, `false`


