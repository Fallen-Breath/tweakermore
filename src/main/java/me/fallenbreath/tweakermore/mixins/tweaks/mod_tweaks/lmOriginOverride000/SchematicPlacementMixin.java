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

package me.fallenbreath.tweakermore.mixins.tweaks.mod_tweaks.lmOriginOverride000;

import fi.dy.masa.litematica.schematic.LitematicaSchematic;
import fi.dy.masa.litematica.schematic.placement.SchematicPlacement;
import fi.dy.masa.malilib.gui.Message;
import fi.dy.masa.malilib.util.InfoUtils;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.impl.mod_tweaks.lmOriginOverride000.LitematicaOriginOverrideGlobals;
import me.fallenbreath.tweakermore.impl.mod_tweaks.lmOriginOverride000.LitematicaSchematic000Origin;
import me.fallenbreath.tweakermore.util.ModIds;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Restriction(require = @Condition(ModIds.litematica))
@Mixin(SchematicPlacement.class)
public abstract class SchematicPlacementMixin
{
	@Shadow(remap = false)
	private LitematicaSchematic schematic;

	@Shadow(remap = false)
	private BlockPos origin;

	@Inject(method = "<init>", at = @At("TAIL"))
	private void lmOriginOverride000_tweakPlacementOriginTo000(CallbackInfo ci)
	{
		if (TweakerMoreConfigs.LM_ORIGIN_OVERRIDE_000.getBooleanValue())
		{
			if (((LitematicaSchematic000Origin)this.schematic).is000Origin$TKM() && LitematicaOriginOverrideGlobals.IS_USER_LOADING_SCHEMATIC.get())
			{
				InfoUtils.showGuiMessage(Message.MessageType.SUCCESS, "tweakermore.impl.lmOriginOverride000.placement_tweaked", this.schematic.getMetadata().getName());
				this.origin = LitematicaOriginOverrideGlobals.POS_000;

				LitematicaOriginOverrideGlobals.IS_USER_LOADING_SCHEMATIC.remove();
			}
		}
	}
}
