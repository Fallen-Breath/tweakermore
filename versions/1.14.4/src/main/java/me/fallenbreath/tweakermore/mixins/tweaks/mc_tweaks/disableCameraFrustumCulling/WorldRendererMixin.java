package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.disableCameraFrustumCulling;

import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Only used in mc1.15+
 */
@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin
{
}
