package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.disableCameraFrustumCulling;

import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Only used in mc1.14.4
 */
@Mixin(GameRenderer.class)
public abstract class GameRendererMixin
{
}