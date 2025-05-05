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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.disableInventoryScreenPlayerModelRendering;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HorseScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

//#if MC >= 12000
//$$ import net.minecraft.client.gui.DrawContext;
//#elseif MC >= 11904
//$$ import net.minecraft.client.util.math.MatrixStack;
//#endif

@Mixin(value = {
		InventoryScreen.class,
		CreativeInventoryScreen.class,
		HorseScreen.class,
})
public abstract class InventoryScreenMixin
{
	@WrapWithCondition(
			method = "drawBackground",
			at = @At(
					value = "INVOKE",
					//#if MC >= 12002
					//$$ target = "Lnet/minecraft/client/gui/screen/ingame/InventoryScreen;drawEntity(Lnet/minecraft/client/gui/DrawContext;IIIIIFFFLnet/minecraft/entity/LivingEntity;)V"
					//#elseif MC >= 12000
					//$$ target = "Lnet/minecraft/client/gui/screen/ingame/InventoryScreen;drawEntity(Lnet/minecraft/client/gui/DrawContext;IIIFFLnet/minecraft/entity/LivingEntity;)V"
					//#elseif MC >= 11904
					//$$ target = "Lnet/minecraft/client/gui/screen/ingame/InventoryScreen;drawEntity(Lnet/minecraft/client/util/math/MatrixStack;IIIFFLnet/minecraft/entity/LivingEntity;)V"
					//#else
					target = "Lnet/minecraft/client/gui/screen/ingame/InventoryScreen;drawEntity(IIIFFLnet/minecraft/entity/LivingEntity;)V"
					//#endif
			)
	)
	private static boolean disableInventoryScreenPlayerModelRendering_cancel(
			//#if MC >= 12000
			//$$ DrawContext context,
			//#elseif MC >= 11904
			//$$ MatrixStack matrixStack,
			//#endif

			//#if MC >= 12002
			//$$ int x1, int y1, int x2, int y2, int size, float f,
			//#else
			int x, int y, int size,
			//#endif
			float mouseX, float mouseY, LivingEntity entity
	)
	{
		return !TweakerMoreConfigs.DISABLE_INVENTORY_SCREEN_PLAYER_MODEL_RENDERING.getBooleanValue();
	}
}
