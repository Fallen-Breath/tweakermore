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

package me.fallenbreath.tweakermore.util.render.context;

import me.fallenbreath.tweakermore.util.render.RenderUtils;
import me.fallenbreath.tweakermore.util.render.matrix.IMatrixStack;
import me.fallenbreath.tweakermore.util.render.matrix.JomlMatrixStack;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.fog.FogRenderer;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fStack;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;

import java.util.List;

/**
 * For those who needs in-game transformation and guiDrawer drawing (mc1.21.6+) (very hacky)
 * <p>
 * mc1.21.6-: subproject 1.15.2 (main project)
 * mc1.21.6+: subproject 1.21.6        <--------
 */
public class MixedRenderContext implements GuiRenderContext, WorldRenderContext, AutoCloseable
{
	private final DrawContext drawContext;
	private final GuiRenderState guiState;
	private final GuiRenderer guiRenderer;
	private final FogRenderer fogRenderer;
	private final JomlMatrixStack matrixStack;

	public MixedRenderContext()
	{
		MinecraftClient mc = MinecraftClient.getInstance();
		VertexConsumerProvider.Immediate immediate = RenderUtils.getVertexConsumer();
		this.guiState = new GuiRenderState();
		this.drawContext = new DrawContext(mc, this.guiState);
		this.guiRenderer = new GuiRenderer(this.guiState, immediate, List.of());
		((InWorldGuiRendererHook)this.guiRenderer).setInWorldGuiRender$TKM(true);
		this.fogRenderer = new FogRenderer();
		this.matrixStack = new JomlMatrixStack(new Matrix4fStack(16));
	}

	public static MixedRenderContext create()
	{
		return new MixedRenderContext();
	}

	public void applyToMatrix3x2fStack(Matrix3x2fStack matrix3x2fStack)
	{
		Matrix4f matrix4f = new Matrix4f();
		this.matrixStack.getRaw().get(matrix4f);

		// translate (m03 for x, m13 for y)
		float tx = matrix4f.m03();
		float ty = matrix4f.m13();

		// 2x2 transformation (ignore z)
		float m00 = matrix4f.m00(); // x axis
		float m01 = matrix4f.m01();
		float m10 = matrix4f.m10(); // y axis
		float m11 = matrix4f.m11();

		matrix3x2fStack.mul(new Matrix3x2f(
				m00, m01, tx,
				m10, m11, ty
		));
	}

	public void pushMatrixToGuiDrawer()
	{
		Matrix3x2fStack matrix3x2fStack = this.drawContext.getMatrices();
		matrix3x2fStack.pushMatrix();
		this.applyToMatrix3x2fStack(matrix3x2fStack);
	}

	public void popMatrixFromGuiDrawer()
	{
		this.drawContext.getMatrices().popMatrix();
	}

	public void renderGuiElements()
	{
		this.guiRenderer.render(this.fogRenderer.getFogBuffer(FogRenderer.FogType.NONE));
		this.guiRenderer.incrementFrame();
		this.fogRenderer.rotate();
		this.guiState.clear();
	}

	@Override
	public void close()
	{
		this.guiRenderer.close();
		this.fogRenderer.close();
	}

	@Override
	@NotNull
	public DrawContext getGuiDrawer()
	{
		return this.drawContext;
	}

	@Override
	public void pushMatrix()
	{
		this.matrixStack.pushMatrix();
	}

	@Override
	public void popMatrix()
	{
		this.matrixStack.popMatrix();
	}

	@Override
	public void translate(double x, double y, double z)
	{
		this.matrixStack.translate(x, y, z);
	}

	@Override
	public void scale(double x, double y, double z)
	{
		this.matrixStack.scale(x, y, z);
	}

	@Override
	public void translate(double x, double y)
	{
		this.matrixStack.translate(x, y, 0);
	}

	@Override
	public void scale(double x, double y)
	{
		this.matrixStack.scale(x, y, 1);
	}

	@Override
	public void multMatrix(Matrix4f matrix4f)
	{
		this.matrixStack.mul(matrix4f);
	}
}
