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

package me.fallenbreath.tweakermore.mixins.tweaks.mod_tweaks.lmAutoRefreshMaterialList;

import fi.dy.masa.litematica.data.DataManager;
import fi.dy.masa.litematica.materials.MaterialListBase;
import fi.dy.masa.litematica.schematic.placement.SchematicPlacement;
import fi.dy.masa.litematica.util.BlockInfoListType;
import fi.dy.masa.litematica.util.SchematicWorldRefresher;
import fi.dy.masa.litematica.world.SchematicWorldHandler;
import fi.dy.masa.litematica.world.WorldSchematic;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.impl.mod_tweaks.lmAutoRefreshMaterialList.LitematicaAutoRefreshMaterialListHelper;
import me.fallenbreath.tweakermore.util.ModIds;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Restriction(require = @Condition(ModIds.litematica))
@Mixin(SchematicWorldRefresher.class)
public abstract class SchematicWorldRefresherMixin
{
	@Inject(
			method = {
					"updateBetweenX",
					"updateBetweenY",
					"updateBetweenZ"
			},
			at = @At("HEAD"),
			remap = false
	)
	private void lmAutoRefreshMaterialList_triggerRefresh(CallbackInfo ci)
	{
		if (TweakerMoreConfigs.LM_AUTO_REFRESH_MATERIAL_LIST.getBooleanValue())
		{
			WorldSchematic world = SchematicWorldHandler.getSchematicWorld();
			MaterialListBase materialList = DataManager.getMaterialList();
			if (world != null && materialList != null && materialList.getMaterialListType() == BlockInfoListType.RENDER_LAYERS)
			{
				LitematicaAutoRefreshMaterialListHelper.getInstance().addRefreshTask(materialList::reCreateMaterialList);
			}
		}
	}
}
