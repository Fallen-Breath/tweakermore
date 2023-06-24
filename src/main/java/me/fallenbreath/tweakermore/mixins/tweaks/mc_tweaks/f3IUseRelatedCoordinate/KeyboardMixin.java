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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.f3IUseRelatedCoordinate;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.client.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(Keyboard.class)
public abstract class KeyboardMixin
{
	@ModifyArgs(
			method = "copyBlock",
			at = @At(
					value = "INVOKE",
					target = "Ljava/lang/String;format(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;",
					remap = false
			)
	)
	private void f3IUseRelatedCoordinate_rebuildSetblockCommand(Args args)
	{
		if (TweakerMoreConfigs.F3_I_USE_RELATED_COORDINATE.getBooleanValue())
		{
			Object[] arr = args.get(2);
			if (arr.length == 4)
			{
				String deltaY = TweakerMoreConfigs.F3_I_USE_RELATED_COORDINATE_SHIFT_1.getBooleanValue() ? "~1" : "~";
				StringBuilder block = (StringBuilder)arr[3];

				// "/setblock %d %d %d %s"
				String command = String.format("/setblock ~ %s ~ %s", deltaY, block);

				args.set(1, "%s");
				args.set(2, new Object[]{command});
			}
		}
	}

	@ModifyArgs(
			method = "copyEntity",
			at = @At(
					value = "INVOKE",
					target = "Ljava/lang/String;format(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;",
					remap = false
			),
			require = 2
	)
	private void f3IUseRelatedCoordinate_rebuildSummonCommand(Args args)
	{
		if (TweakerMoreConfigs.F3_I_USE_RELATED_COORDINATE.getBooleanValue())
		{
			Object[] arr = args.get(2);
			if (arr.length != 4 && arr.length != 5)
			{
				return;
			}

			String deltaY = TweakerMoreConfigs.F3_I_USE_RELATED_COORDINATE_SHIFT_1.getBooleanValue() ? "~1" : "~";
			String id = (String)arr[0];
			String command;
			if (arr.length == 5)  // "/summon %s %.2f %.2f %.2f %s"
			{
				String nbt = (String)arr[4];
				command = String.format("/summon %s ~ %s ~ %s", id, deltaY, nbt);
			}
			else  // "/summon %s %.2f %.2f %.2f"
			{
				command = String.format("/summon %s ~ %s ~", id, deltaY);
			}

			args.set(1, "%s");
			args.set(2, new Object[]{command});
		}
	}
}
