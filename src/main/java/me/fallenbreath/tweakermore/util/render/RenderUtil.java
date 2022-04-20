package me.fallenbreath.tweakermore.util.render;

//#if MC >= 11600
//$$ import com.mojang.blaze3d.systems.RenderSystem;
//$$ import net.minecraft.client.util.math.MatrixStack;
//#endif

//#if MC >= 11500
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.Rotation3;
//#else
//$$ import com.mojang.blaze3d.platform.GlStateManager;
//$$ import net.minecraft.client.render.entity.EntityRenderDispatcher;
//#endif

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.math.Matrix4f;

public class RenderUtil
{
	/**
	 * Reference: {@link DebugRenderer#drawString(String, double, double, double, int, float, boolean, float, boolean)}
	 * Note:
	 * - shadow=true + seeThrough=false might result in weird rendering
	 * - 1.14 doesn't support shadow = true
	 */
	public static void drawString(String text, double x, double y, double z, float fontSize, int color, boolean shadow, boolean seeThrough)
	{
		MinecraftClient client = MinecraftClient.getInstance();
		Camera camera = client.gameRenderer.getCamera();
		if (camera.isReady() && client.getEntityRenderManager().gameOptions != null && client.player != null)
		{
			double camX = camera.getPos().x;
			double camY = camera.getPos().y;
			double camZ = camera.getPos().z;

			RenderContext renderContext = new RenderContext(
					//#if MC >= 11700
					//$$ RenderSystem.getModelViewStack()
					//#elseif MC >= 11600
					//$$ new MatrixStack()
					//#endif
			);

			renderContext.pushMatrix();
			renderContext.translate((float)(x - camX), (float)(y - camY), (float)(z - camZ));

			//#if MC >= 11500
			renderContext.multMatrix(new Matrix4f(camera.getRotation()));
			//#endif

			renderContext.scale(fontSize, -fontSize, fontSize);

			//#if MC < 11500
			//$$ EntityRenderDispatcher entityRenderDispatcher = client.getEntityRenderManager();
			//$$ GlStateManager.rotatef(-entityRenderDispatcher.cameraYaw, 0.0F, 1.0F, 0.0F);
			//$$ GlStateManager.rotatef(-entityRenderDispatcher.cameraPitch, 1.0F, 0.0F, 0.0F);
			//#endif

			renderContext.enableTexture();
			if (seeThrough)
			{
				renderContext.disableDepthTest();
			}
			else
			{
				renderContext.enableDepthTest();
			}
			renderContext.depthMask(true);
			renderContext.scale(-1.0, 1.0, 1.0);

			//#if MC >= 11700
			//$$ RenderSystem.applyModelViewMatrix();
			//#else
			renderContext.enableAlphaTest();
			//#endif

			float renderX = -client.textRenderer.getStringWidth(text) * 0.5F;
			float renderY = client.textRenderer.getStringBoundedHeight(text, Integer.MAX_VALUE) * -0.5F;

			//#if MC >= 11500
			VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
			Matrix4f matrix4f = Rotation3.identity().getMatrix();
			client.textRenderer.draw(text, renderX, renderY, color, shadow, matrix4f, immediate, seeThrough, 0, 0xF000F0);
			immediate.draw();
			//#else
			//$$ client.textRenderer.draw(text, renderX, renderY, color);
			//#endif

			//#if MC < 11600
			renderContext.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			//#endif

			renderContext.enableDepthTest();
			renderContext.popMatrix();
		}
	}

	public static Scaler createScaler(int anchorX, int anchorY, double factor)
	{
		return new Scaler(anchorX, anchorY, factor);
	}

	public static class Scaler
	{
		private final int anchorX;
		private final int anchorY;
		private final double factor;

		private RenderContext renderContext;

		private Scaler(int anchorX, int anchorY, double factor)
		{
			this.anchorX = anchorX;
			this.anchorY = anchorY;
			if (factor <= 0)
			{
				throw new IllegalArgumentException("factor should be greater than 0, but " + factor + " found");
			}
			this.factor = factor;
		}

		public void apply(
				//#if MC >= 11600
				//$$ MatrixStack matrixStack
				//#endif
		)
		{
			this.renderContext = new RenderContext(
					//#if MC >= 11600
					//$$ matrixStack
					//#endif
			);
			this.renderContext.pushMatrix();
			this.renderContext.translate(-anchorX * factor, -anchorY * factor, 0);
			this.renderContext.scale(factor, factor, 1);
			this.renderContext.translate(anchorX / factor, anchorY / factor, 0);
		}

		public void restore()
		{
			if (this.renderContext == null)
			{
				throw new RuntimeException("RenderUtil.Scaler: Calling restore before calling apply");
			}
			this.renderContext.popMatrix();
		}
	}
}
