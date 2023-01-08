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

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.impl.mod_tweaks.lmOriginOverride000.LitematicaOriginOverrideGlobals;
import me.fallenbreath.tweakermore.util.ModIds;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Restriction(require = @Condition(ModIds.litematica))
@Mixin(targets = "fi.dy.masa.litematica.gui.GuiSchematicLoad$ButtonListener")
public abstract class GuiSchematicLoadButtonListenerMixin
{
	@Inject(
			method = "actionPerformedWithButton",
			at = @At(
					value = "INVOKE",
					target = "Lfi/dy/masa/litematica/data/DataManager;getSchematicPlacementManager()Lfi/dy/masa/litematica/schematic/placement/SchematicPlacementManager;",
					remap = false
			),
			remap = false
	)
	private void lmOriginOverride000_getReadyToSetOriginTo000(CallbackInfo ci)
	{
		LitematicaOriginOverrideGlobals.IS_USER_LOADING_SCHEMATIC.set(true);
	}
}
