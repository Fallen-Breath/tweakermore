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

package me.fallenbreath.tweakermore.impl.mod_tweaks.lmAutoRefreshMaterialList;

import fi.dy.masa.malilib.interfaces.IClientTickHandler;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.Nullable;

public class LitematicaAutoRefreshMaterialListHelper implements IClientTickHandler
{
	private static final LitematicaAutoRefreshMaterialListHelper INSTANCE = new LitematicaAutoRefreshMaterialListHelper();

	@Nullable
	private Runnable refreshTask = null;

	public static LitematicaAutoRefreshMaterialListHelper getInstance()
	{
		return INSTANCE;
	}

	public void addRefreshTask(Runnable task)
	{
		this.refreshTask = task;
	}

	@Override
	public void onClientTick(Minecraft mc)
	{
		if (this.refreshTask != null)
		{
			this.refreshTask.run();
		}
		this.refreshTask = null;
	}
}
