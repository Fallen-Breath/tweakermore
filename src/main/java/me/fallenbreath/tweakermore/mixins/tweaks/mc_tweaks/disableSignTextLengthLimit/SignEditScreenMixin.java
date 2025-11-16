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
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.util.ModIds;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.gui.components.ComponentRenderUtils;
import com.mojang.math.Matrix4f;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

//#if MC >= 11904
//$$ import net.minecraft.client.font.TextRenderer;
//#endif

//#if MC >= 11903
//$$ import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
//$$ import net.minecraft.client.gui.screen.ingame.AbstractSignEditScreen;
//$$ import org.joml.Vector3f;
//#else
import net.minecraft.client.gui.screens.inventory.SignEditScreen;
//#endif

//#if MC >= 11700
//$$ import net.minecraft.client.render.VertexConsumer;
//$$ import net.minecraft.client.util.SpriteIdentifier;
//$$ import org.spongepowered.asm.mixin.Unique;
//#endif

/**
 * The implementation for mc [1.15.2, 1.20)
 * See subproject 1.14.4 or 1.20 for implementation for other version range
 */
@Restriction(conflict = @Condition(value = ModIds.caxton, versionPredicates = "<0.3.0-beta.2"))
@Mixin(
		//#if MC >= 11903
		//$$ AbstractSignEditScreen.class
		//#else
		SignEditScreen.class
		//#endif
)
public abstract class SignEditScreenMixin extends Screen
{
	//#if MC < 11600
	@Shadow private TextFieldHelper signField;
	//#endif

	@Shadow @Final private SignBlockEntity
			//#if MC >= 11903
			//$$ blockEntity;
			//#else
			sign;
			//#endif

	//#if MC >= 11600
	//$$ @Shadow @Final private String[] text;
	//#endif

	//#if MC >= 11700
	//$$ @Unique private boolean filtered$TKM;
	//#endif

	protected SignEditScreenMixin(Component title)
	{
		super(title);
	}

	//#if MC >= 11700
	//$$ @Inject(
	//$$ 		//#if MC >= 12000
	//$$ 		//$$ method = "<init>(Lnet/minecraft/block/entity/SignBlockEntity;ZZLnet/minecraft/text/Text;)V",
	//$$ 		//#elseif MC >= 11903
	//$$ 		//$$ method = "<init>(Lnet/minecraft/block/entity/SignBlockEntity;ZLnet/minecraft/text/Text;)V",
	//$$ 		//#else
	//$$ 		method = "<init>",
	//$$ 		//#endif
	//$$ 		at = @At("TAIL")
	//$$ )
	//$$ private void recordFilteredParam(
	//$$ 		SignBlockEntity sign,
	//$$ 		//#if MC >= 12000
	//$$ 		//$$ boolean front,
	//$$ 		//#endif
	//$$ 		boolean filtered,
	//$$ 		//#if MC >= 11903
	//$$ 		//$$ Text title,
	//$$ 		//#endif
	//$$ 		CallbackInfo ci
	//$$ )
	//$$ {
	//$$ 	this.filtered$TKM = filtered;
	//$$ }
	//#endif

	//#if MC >= 11903
	//$$ @ModifyExpressionValue(
	//$$ 		method = "method_45658",  // lambda method in init
	//$$ 		at = @At(
	//$$ 				value = "INVOKE",
	//$$ 				target = "Lnet/minecraft/block/entity/SignBlockEntity;getMaxTextWidth()I",
	//$$ 				remap = true
	//$$ 		),
	//$$ 		remap = false
	//$$ )
	//#elseif MC >= 11600
	//$$ @ModifyConstant(
	//$$ 		method = "method_27611",  // lambda method in init
	//$$ 		constant = @Constant(intValue = 90),
	//$$ 		remap = false,
	//$$ 		require = 0
	//$$ )
	//#else
	@ModifyArg(
			method = "init",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/font/TextFieldHelper;<init>(Lnet/minecraft/client/Minecraft;Ljava/util/function/Supplier;Ljava/util/function/Consumer;I)V"
			)
	)
	//#endif
	private int disableSignTextLengthLimitInSignEditor(int maxLength)
	{
		if (TweakerMoreConfigs.DISABLE_SIGN_TEXT_LENGTH_LIMIT.getBooleanValue())
		{
			maxLength = Integer.MAX_VALUE;
		}
		return maxLength;
	}

	//#if MC < 11600
	@ModifyArg(
			method = "method_23773",  // lambda method in method render
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/components/ComponentRenderUtils;wrapComponents(Lnet/minecraft/network/chat/Component;ILnet/minecraft/client/gui/Font;ZZ)Ljava/util/List;",
					remap = true
			),
			remap = false
	)
	private int disableSignTextLengthLimitInSignEditScreenRendering(int maxLength)
	{
		if (TweakerMoreConfigs.DISABLE_SIGN_TEXT_LENGTH_LIMIT.getBooleanValue())
		{
			// should be modified into Integer.MAX_VALUE too in the @ModifyArg above
			maxLength = ((SelectionManagerAccessor)this.signField).getMaxLength();
		}
		return maxLength;
	}
	//#endif  // if MC < 11600

	@Inject(
			//#if MC >= 11903
			//$$ method = "renderSignText",
			//#else
			method = "render",
			//#endif
			at = @At(
					value = "INVOKE",
					//#if MC >= 11904
					//$$ target = "Lnet/minecraft/client/font/TextRenderer;draw(Ljava/lang/String;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/font/TextRenderer$TextLayerType;IIZ)I",
					//#elseif MC >= 11600
					//$$ target = "Lnet/minecraft/client/font/TextRenderer;draw(Ljava/lang/String;FFIZLnet/minecraft/util/math/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;ZIIZ)I",
					//#else
					target = "Lnet/minecraft/client/gui/Font;drawInBatch(Ljava/lang/String;FFIZLcom/mojang/math/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource;ZII)I",
					//#endif
					ordinal = 0
			),
			locals = LocalCapture.CAPTURE_FAILHARD
	)
	//#if MC >= 11903
	//$$ private void drawLineOverflowHint(MatrixStack matrices, VertexConsumerProvider.Immediate immediate, CallbackInfo ci, Vector3f vector3f, int i, boolean bl, int j, int k, int l, int m, Matrix4f matrix4f, int lineIdx, String string, float xStart)
	//#elseif MC >= 11700
	//$$ private void drawLineOverflowHint(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci, float f, BlockState blockState, boolean bl, boolean bl2, float g, VertexConsumerProvider.Immediate immediate, SpriteIdentifier spriteIdentifier, VertexConsumer vertexConsumer, float h, int i, int j, int k, int l, Matrix4f matrix4f, int lineIdx, String string, float xStart)
	//#elseif MC >= 11600
	//$$ private void drawLineOverflowHint(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci, float f, BlockState blockState, boolean bl, boolean bl2, float g, VertexConsumerProvider.Immediate immediate, float h, int i, int j, int k, int l, Matrix4f matrix4f, int lineIdx, String string, float xStart)
	//#else
	private void drawLineOverflowHint(int mouseX, int mouseY, float delta, CallbackInfo ci, PoseStack matrixStack, float f, BlockState blockState, boolean bl, boolean bl2, float g, MultiBufferSource.BufferSource immediate, float h, int i, String strings[], Matrix4f matrix4f, int k, int l, int m, int n, int lineIdx, String string, float xStart)
	//#endif
	{
		if (TweakerMoreConfigs.DISABLE_SIGN_TEXT_LENGTH_LIMIT.getBooleanValue())
		{
			SignBlockEntity sign =
					//#if MC >= 11903
					//$$ this.blockEntity;
					//#else
					this.sign;
					//#endif

			//#if MC >= 11600
			//$$ int textArrayLen = this.text.length;
			//$$ MinecraftClient mc = this.client;
			//#else
			int textArrayLen = sign.messages.length;
			Minecraft mc = this.minecraft;
			//#endif

			if (mc != null && 0 <= lineIdx && lineIdx < textArrayLen)
			{
				Component text = sign.getMessage(
						lineIdx
						//#if MC >= 11700
						//$$ , this.filtered$TKM
						//#endif
				);
				int maxWidth =
						//#if MC >= 11903
						//$$ this.blockEntity.getMaxTextWidth();
						//#else
						90;
						//#endif

				List<?> wrapped =
						//#if MC >= 11600
						//$$ mc.textRenderer.wrapLines(text, maxWidth);
						//#else
						ComponentRenderUtils.wrapComponents(text, maxWidth, mc.font, false, true);
						//#endif
				boolean overflowed = wrapped.size() > 1;
				if (overflowed)
				{
					assert ChatFormatting.RED.getColor() != null;
					mc.font.drawInBatch(
							"!", xStart - 10, lineIdx * 10 - textArrayLen * 5, ChatFormatting.RED.getColor(), false, matrix4f, immediate,
							//#if MC >= 11904
							//$$ TextRenderer.TextLayerType.NORMAL,
							//#else
							false,
							//#endif
							0, 0xF000F0
					);
				}
			}
		}
	}
}
