package me.fallenbreath.tweakermore.mixins.tweaks.disableSignTextLengthLimit;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.SignEditScreen;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(SignEditScreen.class)
public abstract class SignEditScreenMixin extends Screen
{
	@Shadow @Final private SignBlockEntity sign;

	@Shadow @Final private String[] text;

	private boolean filtered$TKM;

	protected SignEditScreenMixin(Text title)
	{
		super(title);
	}

	@Inject(method = "<init>", at = @At("TAIL"))
	private void recordFilteredParam(SignBlockEntity sign, boolean filtered, CallbackInfo ci)
	{
		this.filtered$TKM = filtered;
	}

	@ModifyConstant(
			method = "method_27611",  // lambda method in <init>
			constant = @Constant(intValue = 90),
			remap = false,
			require = 0
	)
	private int disableSignTextLengthLimitInSignEditor(int maxLength)
	{
		if (TweakerMoreConfigs.DISABLE_SIGN_TEXT_LENGTH_LIMIT.getBooleanValue())
		{
			maxLength = Integer.MAX_VALUE;
		}
		return maxLength;
	}

	@Inject(
			method = "render",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/font/TextRenderer;draw(Ljava/lang/String;FFIZLnet/minecraft/util/math/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;ZIIZ)I",
					ordinal = 0
			),
			locals = LocalCapture.CAPTURE_FAILHARD
	)
	private void drawLineOverflowHint(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci, float f, BlockState blockState, boolean bl, boolean bl2, float g, VertexConsumerProvider.Immediate immediate, SpriteIdentifier spriteIdentifier, VertexConsumer vertexConsumer, float h, int i, int j, int k, int l, Matrix4f matrix4f, int lineIdx, String string, float xStart)
	{
		if (TweakerMoreConfigs.DISABLE_SIGN_TEXT_LENGTH_LIMIT.getBooleanValue())
		{
			Text text = this.sign.getTextOnRow(lineIdx, this.filtered$TKM);
			if (0 <= lineIdx && lineIdx < this.text.length)
			{
				assert this.client != null;
				boolean overflowed = this.client.textRenderer.wrapLines(text, 90).size() > 1;
				if (overflowed)
				{
					assert Formatting.RED.getColorValue() != null;
					this.client.textRenderer.draw("!", xStart - 10, lineIdx * 10 - this.text.length * 5, Formatting.RED.getColorValue(), false, matrix4f, immediate, false, 0, 15728880);
				}
			}
		}
	}
}
