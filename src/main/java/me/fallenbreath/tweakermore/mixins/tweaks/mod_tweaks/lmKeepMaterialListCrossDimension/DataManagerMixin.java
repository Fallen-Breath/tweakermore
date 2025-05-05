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

package me.fallenbreath.tweakermore.mixins.tweaks.mod_tweaks.lmKeepMaterialListCrossDimension;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import fi.dy.masa.litematica.data.DataManager;
import fi.dy.masa.litematica.materials.MaterialListBase;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.util.ModIds;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Restriction(require = @Condition(ModIds.litematica))
@Mixin(DataManager.class)
public abstract class DataManagerMixin
{
	@Shadow(remap = false) @Nullable
	private MaterialListBase materialList;

	@Inject(
			method = "loadPerDimensionData",
			at = @At("HEAD"),
			remap = false
	)
	private void lmKeepMaterialListCrossDimension_saveMaterialList(CallbackInfo ci, @Share("") LocalRef<MaterialListBase> lv)
	{
		if (TweakerMoreConfigs.LM_KEEP_MATERIAL_LIST_CROSS_DIMENSION.getBooleanValue())
		{
			lv.set(this.materialList);
		}
	}

	@Inject(
			method = "loadPerDimensionData",
			at = @At("TAIL"),
			remap = false
	)
	private void lmKeepMaterialListCrossDimension_restoreMaterialList(CallbackInfo ci, @Share("") LocalRef<MaterialListBase> lv)
	{
		if (TweakerMoreConfigs.LM_KEEP_MATERIAL_LIST_CROSS_DIMENSION.getBooleanValue())
		{
			if (this.materialList == null && lv.get() != null)
			{
				this.materialList = lv.get();
			}
		}
	}
}
