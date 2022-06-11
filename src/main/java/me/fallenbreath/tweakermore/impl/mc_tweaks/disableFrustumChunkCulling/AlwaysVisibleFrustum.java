package me.fallenbreath.tweakermore.impl.mc_tweaks.disableFrustumChunkCulling;

//#if MC >= 11500
import net.minecraft.client.render.Frustum;

//#if MC < 11800
import net.minecraft.client.util.math.Matrix4f;
//#endif  // if MC < 11800

//#else  // if MC >= 11500
//$$ import net.minecraft.client.render.VisibleRegion;
//#endif  // if MC >= 11500
import net.minecraft.util.math.Box;

public class AlwaysVisibleFrustum
		//#if MC >= 11500
		extends Frustum
		//#else
		//$$ implements VisibleRegion
		//#endif
{

	//#if MC >= 11500
	private final Frustum frustum;
	//#else
	//$$ private final VisibleRegion frustum;
	//#endif

	public AlwaysVisibleFrustum(
			//#if MC >= 11500
			Frustum frustum
			//#else
			//$$ VisibleRegion frustum
			//#endif
	)
	{
		//#if MC >= 11800
		//$$ super(frustum);
		//#elseif MC >= 11500
		super(new Matrix4f(), new Matrix4f());
		//#endif
		this.frustum = frustum;
	}

	@Override
	public void
			//#if MC >= 11500
			setPosition
			//#else
			//$$ setOrigin
			//#endif
			(double cameraX, double cameraY, double cameraZ)
	{
		this.frustum.
				//#if MC >= 11500
				setPosition
				//#else
				//$$ setOrigin
				//#endif
						(cameraX, cameraY, cameraZ);
	}

	@Override
	//#if MC >= 11500
	public boolean isVisible(Box box)
	//#else
	//$$ public boolean intersects(Box box)
	//#endif
	{
		return true;
	}
}