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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.disableSignTextLengthLimit;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.util.ModIds;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Group;
import org.spongepowered.asm.mixin.injection.ModifyArg;

//#if MC >= 12104
//$$ import net.minecraft.client.renderer.blockentity.AbstractSignRenderer;
//#else
import net.minecraft.client.renderer.blockentity.SignRenderer;
//#endif

//#if MC < 11500
//$$ import com.llamalad7.mixinextras.sugar.Local;
//$$ import me.fallenbreath.tweakermore.impl.mc_tweaks.disableSignTextLengthLimit.SignOverflowHintDrawer;
//$$ import net.minecraft.world.level.block.entity.SignBlockEntity;
//$$ import net.minecraft.client.gui.Font;
//$$ import org.spongepowered.asm.mixin.injection.Inject;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//#endif

@Restriction(conflict = {
		@Condition(value = ModIds.caxton, versionPredicates = "<0.3.0-beta.2")
		//#if MC < 11500
		//$$ , @Condition(ModIds.optifine)
		//#endif
})
@Mixin(
		//#if MC >= 12104
		//$$ AbstractSignRenderer.class
		//#else
		SignRenderer.class
		//#endif
)
public abstract class SignBlockEntityRendererMixin
{
	@SuppressWarnings("UnresolvedMixinReference")
	@Group(min = 1, max = 1)
	@ModifyArg(
			method = {
					// lambda method in method
					//   render (MC < 11903)
					//   renderText (MC >= 11903)

					// TODO: update optifine name in mc1.19.3+
					//#if MC >= 12104
					//$$ "method_65819",  // vanilla
					//#elseif MC >= 11903
					//$$ "method_45799",  // vanilla
					//#elseif MC >= 11700
					//$$ "method_32159",  // vanilla
					//$$ "lambda$render$2"  // after being polluted by optifine
					//#else
					"method_3583",  // vanilla
					"lambda$render$0"  // after being polluted by optifine
					//#endif
			},
			at = @At(
					value = "INVOKE",
					//#if MC >= 11600
					//$$ target = "Lnet/minecraft/client/gui/Font;split(Lnet/minecraft/network/chat/FormattedText;I)Ljava/util/List;",
					//#else
					target = "Lnet/minecraft/client/gui/components/ComponentRenderUtils;wrapComponents(Lnet/minecraft/network/chat/Component;ILnet/minecraft/client/gui/Font;ZZ)Ljava/util/List;",
					//#endif
					remap = true
			),
			remap = false
	)
	//#if MC >= 11700
	//$$ private int
	//#else
	private static int
	//#endif
	disableSignTextLengthLimit(int maxLength)
	{
		if (TweakerMoreConfigs.DISABLE_SIGN_TEXT_LENGTH_LIMIT.getBooleanValue())
		{
			maxLength = Integer.MAX_VALUE;
		}
		return maxLength;
	}

	//#if MC < 11500
	//$$ @Inject(
	//$$ 		method = "render(Lnet/minecraft/world/level/block/entity/SignBlockEntity;DDDFI)V",
	//$$ 		at = @At(
	//$$ 				value = "INVOKE",
	//$$ 				target = "Lnet/minecraft/client/gui/Font;draw(Ljava/lang/String;FFI)I",
	//$$ 				ordinal = 0
	//$$ 		)
	//$$ )
	//$$ private void drawLineOverflowHint(
	//$$ 		CallbackInfo ci,
	//$$ 		@Local(argsOnly = true) SignBlockEntity signBlockEntity,
	//$$ 		@Local Font textRenderer,
	//$$ 		@Local(ordinal = 1) int lineIdx,
	//$$ 		@Local String lineContent
	//$$ )
	//$$ {
	//$$ 	SignOverflowHintDrawer.drawLineOverflowHint(signBlockEntity, textRenderer, lineIdx, lineContent);
	//$$ }
	//#endif
}
