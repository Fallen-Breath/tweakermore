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

package me.fallenbreath.tweakermore.mixins.core.migration;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fi.dy.masa.tweakeroo.config.Configs;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigStorage;
import me.fallenbreath.tweakermore.util.ModIds;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.io.File;

@Restriction(require = @Condition(ModIds.tweakeroo))
@Mixin(Configs.class)
public abstract class ConfigsMixin
{
	/**
	 * TweakerMore v1.x store its config in tweakeroo's config file
	 * So here's the solution for migration to v2.x
	 *
	 * TODO: remove this in the future
	 */
	@SuppressWarnings("InvalidInjectorMethodSignature")
	@Inject(
			method = "loadFromFile",
			at = @At(
					value = "INVOKE_ASSIGN",
					target = "Lcom/google/gson/JsonElement;getAsJsonObject()Lcom/google/gson/JsonObject;",
					shift = At.Shift.AFTER
			),
			locals = LocalCapture.CAPTURE_FAILHARD,
			remap = false
	)
	private static void loadLegacyTweakerMoreOptionsFromTweakeroo(CallbackInfo ci, File configFile, JsonElement element, JsonObject root)
	{
		TweakerMoreConfigStorage.getInstance().loadFromJson(root, false);
	}
}
