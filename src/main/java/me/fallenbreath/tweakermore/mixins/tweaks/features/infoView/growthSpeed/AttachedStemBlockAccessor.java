/*
 * This file is part of the TweakerMore project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2024  Fallen_Breath and contributors
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

package me.fallenbreath.tweakermore.mixins.tweaks.features.infoView.growthSpeed;

import net.minecraft.world.level.block.AttachedStemBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

//#if MC >= 12004
//$$ import net.minecraft.world.level.block.Block;
//$$ import net.minecraft.resources.ResourceKey;
//#else
import net.minecraft.world.level.block.StemGrownBlock;
//#endif

@Mixin(AttachedStemBlock.class)
public interface AttachedStemBlockAccessor
{
	@Accessor("fruit")
	//#if MC >= 12004
	//$$ ResourceKey<Block> getStemBlock();
	//#else
	StemGrownBlock getGourdBlock();
	//#endif
}
