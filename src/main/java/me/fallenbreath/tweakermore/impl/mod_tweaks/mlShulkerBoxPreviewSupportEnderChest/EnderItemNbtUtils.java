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

package me.fallenbreath.tweakermore.impl.mod_tweaks.mlShulkerBoxPreviewSupportEnderChest;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

public class EnderItemNbtUtils
{
	public static boolean containsList(CompoundTag nbt, String key)
	{
		//#if MC >= 12105
		//$$ return nbt.getList(key).isPresent();
		//#else
		return nbt.contains(key, 9  /* LIST */);
		//#endif
	}

	/**
	 * NOTES: in mc1.21.5+, no "all list items are nbt" check will be made,
	 * matching the vanilla implementation ${@link net.minecraft.world.entity.player.Player#readCustomDataFromTag}
	 * to read the "EnderItems" list
	 */
	public static ListTag getNbtListOrEmpty(CompoundTag nbt, String key)
	{
		//#if MC >= 12105
		//$$ return nbt.getList(key).orElseGet(NbtList::new);
		//#else
		return nbt.getList(key, 10  /* NBT */);
		//#endif
	}
}
