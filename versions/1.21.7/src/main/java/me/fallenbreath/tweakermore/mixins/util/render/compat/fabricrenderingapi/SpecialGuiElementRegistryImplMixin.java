/*
 * This file is part of the TweakerMore project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2025  Fallen_Breath and contributors
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

package me.fallenbreath.tweakermore.mixins.util.render.compat.fabricrenderingapi;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.util.ModIds;
import me.fallenbreath.tweakermore.util.render.context.InWorldGuiDrawer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fabric Rendering API does not expect that GuiRenderer was instantiated by a not-vanilla mod XD
 * <p>
 * See also: <a href="https://github.com/FabricMC/fabric/blob/05ccac950a2ea7ef8b4a4e08c955b96543f1ac59/fabric-rendering-v1/src/client/java/net/fabricmc/fabric/impl/client/rendering/SpecialGuiElementRegistryImpl.java#L40-L47">fabric api source code</a>
 */
@SuppressWarnings("UnresolvedMixinReference")
@Restriction(require = @Condition(value = ModIds.fabric_api_rendering_v1, versionPredicates = ">=0.127.0"))
@Pseudo
@Mixin(targets = "net.fabricmc.fabric.impl.client.rendering.SpecialGuiElementRegistryImpl")
public abstract class SpecialGuiElementRegistryImplMixin
{
	@Inject(method = "onReady", at = @At("HEAD"), remap = false, cancellable = true)
	private static void cancelRegistrationHandlerForInWorldGuiDrawer(CallbackInfo ci)
	{
		if (InWorldGuiDrawer.initializing)
		{
			ci.cancel();
		}
	}
}
