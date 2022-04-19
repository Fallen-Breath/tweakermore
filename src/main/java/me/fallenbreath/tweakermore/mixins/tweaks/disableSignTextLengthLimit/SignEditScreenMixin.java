package me.fallenbreath.tweakermore.mixins.tweaks.disableSignTextLengthLimit;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.SignEditScreen;
import net.minecraft.client.util.SelectionManager;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;

//#if MC >= 11700
//$$ import net.minecraft.client.render.VertexConsumer;
//$$ import net.minecraft.client.util.SpriteIdentifier;
//#endif

//#if MC >= 11500
import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.Texts;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
//#endif

@Mixin(SignEditScreen.class)
public abstract class SignEditScreenMixin extends Screen
{
	//#if MC < 11600
	@Shadow private SelectionManager selectionManager;
	//#endif

	@Shadow @Final private SignBlockEntity sign;

	//#if MC >= 11600
	//$$ @Shadow @Final private String[] text;
	//#endif

	//#if MC >= 11700
	//$$ private boolean filtered$TKM;
	//#endif

	protected SignEditScreenMixin(Text title)
	{
		super(title);
	}

	//#if MC >= 11700
	//$$ @Inject(method = "<init>", at = @At("TAIL"))
	//$$ private void recordFilteredParam(SignBlockEntity sign, boolean filtered, CallbackInfo ci)
	//$$ {
	//$$ 	this.filtered$TKM = filtered;
	//$$ }
	//#endif

	//#if MC >= 11600
	//$$ @ModifyConstant(
	//$$ 		method = "method_27611",  // lambda method in <init>
	//$$ 		constant = @Constant(intValue = 90),
	//$$ 		remap = false,
	//$$ 		require = 0
	//$$ )
	//#else
	@ModifyArg(
			method = "init",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/util/SelectionManager;<init>(Lnet/minecraft/client/MinecraftClient;Ljava/util/function/Supplier;Ljava/util/function/Consumer;I)V"
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

	//#if MC >= 11500
	//#if MC < 11600
	@ModifyArg(
			method = "method_23773",  // lambda method in method render
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/util/Texts;wrapLines(Lnet/minecraft/text/Text;ILnet/minecraft/client/font/TextRenderer;ZZ)Ljava/util/List;",
					remap = true
			),
			remap = false
	)
	private int disableSignTextLengthLimitInSignEditScreenRendering(int maxLength)
	{
		if (TweakerMoreConfigs.DISABLE_SIGN_TEXT_LENGTH_LIMIT.getBooleanValue())
		{
			// should be modified into Integer.MAX_VALUE too in the @ModifyArg above
			maxLength = ((SelectionManagerAccessor)this.selectionManager).getMaxLength();
		}
		return maxLength;
	}
	//#endif  // if MC < 11600

	@Inject(
			method = "render",
			at = @At(
					value = "INVOKE",
					//#if MC >= 11600
					//$$ target = "Lnet/minecraft/client/font/TextRenderer;draw(Ljava/lang/String;FFIZLnet/minecraft/util/math/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;ZIIZ)I",
					//#else
					target = "Lnet/minecraft/client/font/TextRenderer;draw(Ljava/lang/String;FFIZLnet/minecraft/client/util/math/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;ZII)I",
					//#endif
					ordinal = 0
			),
			locals = LocalCapture.CAPTURE_FAILHARD
	)
	//#if MC >= 11700
	//$$ private void drawLineOverflowHint(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci, float f, BlockState blockState, boolean bl, boolean bl2, float g, VertexConsumerProvider.Immediate immediate, SpriteIdentifier spriteIdentifier, VertexConsumer vertexConsumer, float h, int i, int j, int k, int l, Matrix4f matrix4f, int lineIdx, String string, float xStart)
	//#elseif MC >= 11600
	//$$ private void drawLineOverflowHint(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci, float f, BlockState blockState, boolean bl, boolean bl2, float g, VertexConsumerProvider.Immediate immediate, float h, int i, int j, int k, int l, Matrix4f matrix4f, int lineIdx, String string, float xStart)
	//#else
	private void drawLineOverflowHint(int mouseX, int mouseY, float delta, CallbackInfo ci, MatrixStack matrixStack, float f, BlockState blockState, boolean bl, boolean bl2, float g, VertexConsumerProvider.Immediate immediate, float h, int i, String strings[], Matrix4f matrix4f, int k, int l, int m, int n, int lineIdx, String string, float xStart)
	//#endif
	{
		if (TweakerMoreConfigs.DISABLE_SIGN_TEXT_LENGTH_LIMIT.getBooleanValue())
		{
			//#if MC >= 11600
			//$$ int textArrayLen = this.text.length;
			//$$ MinecraftClient mc = this.client;
			//#else
			int textArrayLen = this.sign.text.length;
			MinecraftClient mc = this.minecraft;
			//#endif
			if (mc != null && 0 <= lineIdx && lineIdx < textArrayLen)
			{
				Text text = this.sign.getTextOnRow(
						lineIdx
						//#if MC >= 11700
						//$$ , this.filtered$TKM
						//#endif
				);
				List<?> wrapped =
						//#if MC >= 11600
						//$$ mc.textRenderer.wrapLines(text, 90);
						//#else
						Texts.wrapLines(text, 90,mc.textRenderer, false, true);
						//#endif
				boolean overflowed = wrapped.size() > 1;
				if (overflowed)
				{
					assert Formatting.RED.getColorValue() != null;
					mc.textRenderer.draw("!", xStart - 10, lineIdx * 10 - textArrayLen * 5, Formatting.RED.getColorValue(), false, matrix4f, immediate, false, 0, 15728880);
				}
			}
		}
	}
	//#endif  // if MC >= 11500
}
