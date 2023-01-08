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

package me.fallenbreath.tweakermore.mixins.tweaks.mod_tweaks.eCraftItemScrollerCompact;

import fi.dy.masa.itemscroller.recipes.CraftingHandler;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.impl.mod_tweaks.eCraftMassCraftCompact.EasierCraftingRegistrar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Restriction(require = @Condition(type = Condition.Type.MIXIN, value = "me.fallenbreath.tweakermore.mixins.tweaks.mod_tweaks.eCraftItemScrollerCompact.ConfigsMixin"))
@Mixin(CraftingHandler.class)
public abstract class CraftingHandlerMixin
{
	@Inject(method = "clearDefinitions", at = @At("TAIL"), remap = false)
	private static void easierCraftingItemScrolledCompact_markDefinitionsCleared(CallbackInfo ci)
	{
		EasierCraftingRegistrar.markDefinitionCleared();
	}
}
