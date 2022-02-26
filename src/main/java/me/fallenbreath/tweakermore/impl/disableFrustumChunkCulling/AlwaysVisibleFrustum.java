package me.fallenbreath.tweakermore.impl.disableFrustumChunkCulling;

import net.minecraft.client.render.VisibleRegion;
import net.minecraft.util.math.Box;

public class AlwaysVisibleFrustum implements VisibleRegion
{
	private final VisibleRegion frustum;

	public AlwaysVisibleFrustum(VisibleRegion frustum)
	{
		this.frustum = frustum;
	}

	@Override
	public void setOrigin(double cameraX, double cameraY, double cameraZ)
	{
		this.frustum.setOrigin(cameraX, cameraY, cameraZ);
	}

	@Override
	public boolean intersects(Box boundingBox)
	{
		return true;
	}
}