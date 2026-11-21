/*
 * This file is part of the TweakerMore project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026  Fallen_Breath and contributors
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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.signEditingRequireHotkey;

import com.llamalad7.mixinextras.sugar.Local;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.config.options.TweakerMoreConfigHotkeyWithSwitch;
import me.fallenbreath.tweakermore.impl.mc_tweaks.signEditingRequireHotkey.SignEditRejectionTracker;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//#if MC >= 12005 && MC < 12102
//$$ import net.minecraft.world.ItemInteractionResult;
//#else
import net.minecraft.world.InteractionResult;
//#endif
//#if MC >= 26.3
//$$ import net.minecraft.world.level.block.entity.SignTextSlot;
//#endif

/**
 * mc >= 1.20.1: subproject 1.20.1  <--------
 * mc <  1.20.1: subproject 1.15.2 (main project)
 */
@Mixin(SignBlock.class)
public abstract class SignBlockMixin
{
	@Inject(
			//#if MC >= 1.20.5
			//$$ method = "useItemOn",
			//#else
			method = "use",
			//#endif
			at = @At("RETURN")
	)
	private void signEditingRequireHotkey_recordClick(
			CallbackInfoReturnable<?> cir,
			@Local(argsOnly = true) Player player,
			@Local(argsOnly = true) BlockPos pos
	)
	{
		TweakerMoreConfigHotkeyWithSwitch option = TweakerMoreConfigs.SIGN_EDITING_REQUIRE_HOTKEY;
		// Vanilla returns CONSUME on the client for an unwaxed sign without a sign applicator.
		if (
				player instanceof LocalPlayer && player.mayBuild() &&
				option.getEnableState() && !option.isKeybindHeld() &&
				//#if MC >= 12005 && MC < 12102
				//$$ cir.getReturnValue() == ItemInteractionResult.CONSUME &&
				//#else
				cir.getReturnValue() == InteractionResult.CONSUME &&
				//#endif
				player.level().getBlockEntity(pos) instanceof SignBlockEntity sign
		)
		{
			//#if MC >= 26.3
			//$$ boolean front = sign.getSlotPlayerIsFacing(player) == SignTextSlot.FRONT;
			//#else
			boolean front = sign.isFacingFrontText(player);
			//#endif
			SignEditRejectionTracker.INSTANCE.record(pos, front);
		}
	}
}
