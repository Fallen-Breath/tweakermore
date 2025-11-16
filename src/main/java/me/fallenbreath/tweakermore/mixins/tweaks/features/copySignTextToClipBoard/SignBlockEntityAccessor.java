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

package me.fallenbreath.tweakermore.mixins.tweaks.features.copySignTextToClipBoard;

import net.minecraft.world.level.block.entity.SignBlockEntity;
import org.spongepowered.asm.mixin.Mixin;

//#if 11600 <= MC && MC < 12000
//$$ import net.minecraft.text.Text;
//$$ import org.spongepowered.asm.mixin.gen.Accessor;
//#endif

// used in [1.16, 1.20)
@Mixin(SignBlockEntity.class)
public interface SignBlockEntityAccessor
{
	//#if 11600 <= MC && MC < 12000
	//$$ @Accessor
	//$$ Text[] getTexts();
	//#endif
}