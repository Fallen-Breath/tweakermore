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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.disableSignTextLengthLimit;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.util.ModIds;
import net.minecraft.client.gui.font.TextFieldHelper;
import org.spongepowered.asm.mixin.Mixin;

//#if MC < 11600
import org.spongepowered.asm.mixin.gen.Accessor;
//#endif

@Restriction(conflict = @Condition(value = ModIds.caxton, versionPredicates = "<0.3.0-beta.2"))
@Mixin(TextFieldHelper.class)
public interface SelectionManagerAccessor
{
	// un-used since mc 1.16
	//#if MC < 11600
	@Accessor("maxWidth")
	int getMaxLength();
	//#endif
}
