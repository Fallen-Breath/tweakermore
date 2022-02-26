package me.fallenbreath.tweakermore.impl.disableFrustumChunkCulling;

import net.minecraft.client.render.Frustum;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Matrix4f;

public class AlwaysVisibleFrustum extends Frustum
{
	private final Frustum frustum;

	public AlwaysVisibleFrustum(Frustum frustum)
	{
		super(new Matrix4f(), new Matrix4f());
		this.frustum = frustum;
	}

	public void setPosition(double cameraX, double cameraY, double cameraZ)
	{
		this.frustum.setPosition(cameraX, cameraY, cameraZ);
	}

	@Override
	public boolean isVisible(Box box)
	{
		return true;
	}
}