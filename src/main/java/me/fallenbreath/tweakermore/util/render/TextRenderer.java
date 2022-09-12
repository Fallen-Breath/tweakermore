package me.fallenbreath.tweakermore.util.render;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

//#if MC >= 11700
//$$ import com.mojang.blaze3d.systems.RenderSystem;
//#endif

//#if MC >= 11600
//$$ import net.minecraft.client.util.math.MatrixStack;
//$$ import net.minecraft.text.OrderedText;
//#endif

//#if MC >= 11500
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.Rotation3;
//#else
//$$ import net.minecraft.client.render.entity.EntityRenderDispatcher;
//#endif

public class TextRenderer
{
	//#if MC >= 11600
	//$$ private OrderedText text;
	//#else
	private String text;
	//#endif

	private Vec3d pos;
	private double fontSize;
	private int color;
	private int backgroundColor;
	private boolean shadow;
	private boolean seeThrough;

	private TextRenderer()
	{
		this.fontSize = 0.02F;
		this.color = 0xFFFFFFFF;
		this.backgroundColor = 0x00000000;
		this.shadow = false;
		this.seeThrough = false;
	}

	public static TextRenderer create()
	{
		return new TextRenderer();
	}

	/*
	 * ============================
	 *         Render Impl
	 * ============================
	 */

	/**
	 * Reference: {@link DebugRenderer#drawString(String, double, double, double, int, float, boolean, float, boolean)}
	 * Note:
	 * - shadow=true + seeThrough=false might result in weird rendering
	 * - 1.14 doesn't support shadow = true
	 */
	public void render()
	{
		MinecraftClient client = MinecraftClient.getInstance();
		Camera camera = client.gameRenderer.getCamera();
		if (camera.isReady() && client.getEntityRenderManager().gameOptions != null && client.player != null)
		{
			double x = this.pos.x;
			double y = this.pos.y;
			double z = this.pos.z;
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

			renderContext.scale(this.fontSize, -this.fontSize, this.fontSize);

			//#if MC < 11500
			//$$ EntityRenderDispatcher entityRenderDispatcher = client.getEntityRenderManager();
			//$$ GlStateManager.rotatef(-entityRenderDispatcher.cameraYaw, 0.0F, 1.0F, 0.0F);
			//$$ GlStateManager.rotatef(-entityRenderDispatcher.cameraPitch, 1.0F, 0.0F, 0.0F);
			//#endif

			//#if MC < 11700
			renderContext.disableLighting();
			//#endif

			if (this.seeThrough)
			{
				renderContext.disableDepthTest();
			}
			else
			{
				renderContext.enableDepthTest();
			}
			renderContext.enableTexture();
			renderContext.depthMask(true);
			renderContext.scale(-1.0, 1.0, 1.0);

			float textX = RenderUtil.getRenderWidth(this.text) * 0.5F;
			float textY = RenderUtil.TEXT_HEIGHT * 0.5F;
			renderContext.translate(-textX, -textY, 0);

			//#if MC >= 11700
			//$$ RenderSystem.applyModelViewMatrix();
			//#else
			renderContext.enableAlphaTest();
			//#endif

			// enable transparent-able text rendering
			renderContext.enableBlend();
			renderContext.blendFunc(
					//#if MC >= 11500
					GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA
					//#else
					//$$ GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA
					//#endif
			);

			//#if MC >= 11500
			int backgroundColor = this.backgroundColor;
			while (true)
			{
				VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
				Matrix4f matrix4f = Rotation3.identity().getMatrix();
				client.textRenderer.draw(this.text, 0, 0, this.color, this.shadow, matrix4f, immediate, this.seeThrough, backgroundColor, 0xF000F0);
				immediate.draw();

				// draw twice when having background
				if (backgroundColor == 0)
				{
					break;
				}
				else
				{
					backgroundColor = 0;
				}
			}
			//#else
			//$$ client.textRenderer.draw(this.text, 0, 0, this.color);
			//#endif

			//#if MC < 11600
			renderContext.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			//#endif

			renderContext.enableDepthTest();
			renderContext.popMatrix();
		}
	}

	/**
	 * ============================
	 *        Field Setters
	 * ============================
	 */

	//#if MC >= 11600
	//$$ public TextRenderer text(OrderedText text)
	//$$ {
	//$$ 	this.text = text;
	//$$ 	return this;
	//$$ }
	//#endif

	public TextRenderer text(String text)
	{
		//#if MC >= 11600
		//$$ return this.text(TextUtil.orderedText(text));
		//#else
		this.text = text;
		return this;
		//#endif
	}

	public TextRenderer text(Text text)
	{
		//#if MC >= 11600
		//$$ return this.text(text.asOrderedText());
		//#else
		return this.text(text.asFormattedString());
		//#endif
	}

	public TextRenderer at(Vec3d vec3d)
	{
		this.pos = vec3d;
		return this;
	}

	public TextRenderer at(double x, double y, double z)
	{
		return this.at(new Vec3d(x, y, z));
	}

	public TextRenderer atCenter(BlockPos blockPos)
	{
		return this.at(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5);
	}

	public TextRenderer fontSize(double fontSize)
	{
		this.fontSize = fontSize;
		return this;
	}

	public TextRenderer color(int color)
	{
		this.color = color;
		return this;
	}

	public TextRenderer bgColor(int backgroundColor)
	{
		this.backgroundColor = backgroundColor;
		return this;
	}

	public TextRenderer color(int color, int backgroundColor)
	{
		this.color(color);
		this.bgColor(backgroundColor);
		return this;
	}

	public TextRenderer shadow(boolean shadow)
	{
		this.shadow = shadow;
		return this;
	}

	public TextRenderer shadow()
	{
		return this.shadow(true);
	}

	public TextRenderer seeThrough(boolean seeThrough)
	{
		this.seeThrough = seeThrough;
		return this;
	}

	public TextRenderer seeThrough()
	{
		return this.seeThrough(true);
	}
}
