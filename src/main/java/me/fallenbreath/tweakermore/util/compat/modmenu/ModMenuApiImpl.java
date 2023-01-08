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

package me.fallenbreath.tweakermore.util.compat.modmenu;

//#if MC >= 11600
//$$ import com.terraformersmc.modmenu.api.ConfigScreenFactory;
//$$ import com.terraformersmc.modmenu.api.ModMenuApi;
//#elseif MC >= 11500
import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;
//#else
//$$ import io.github.prospector.modmenu.api.ModMenuApi;
//$$ import me.fallenbreath.tweakermore.TweakerMoreMod;
//$$ import net.minecraft.client.gui.screen.Screen;
//$$ import java.util.function.Function;
//#endif

import me.fallenbreath.tweakermore.gui.TweakerMoreConfigGui;

//#if MC < 11500
//$$ @SuppressWarnings("deprecation")
//#endif
public class ModMenuApiImpl implements ModMenuApi
{
	//#if MC < 11500
	//$$ public String getModId()
	//$$ {
	//$$ 	return TweakerMoreMod.MOD_ID;
	//$$ }
	//#endif

	@Override
	//#if MC >= 11500
	public ConfigScreenFactory<?> getModConfigScreenFactory()
	//#else
	//$$ public Function<Screen, ? extends Screen> getConfigScreenFactory()
	//#endif
	{
		return (screen) -> {
			TweakerMoreConfigGui gui = new TweakerMoreConfigGui();
			gui.setParent(screen);
			return gui;
		};
	}
}
