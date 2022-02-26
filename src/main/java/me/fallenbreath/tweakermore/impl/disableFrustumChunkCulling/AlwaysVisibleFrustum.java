package me.fallenbreath.tweakermore.impl.disableFrustumChunkCulling;

import net.minecraft.client.render.Frustum;
import net.minecraft.util.math.Box;

public class AlwaysVisibleFrustum extends Frustum
{
	private final Frustum frustum;

	public AlwaysVisibleFrustum(Frustum frustum)
	{
		super(frustum);
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