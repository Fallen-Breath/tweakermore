package me.fallenbreath.tweakermore.mixins.tweaks.mod_tweaks.eprHideOnDebugHud;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.impl.mod_tweaks.eprHideOnDebugHud.EprHideOnDebugHudImpl;
import me.fallenbreath.tweakermore.util.ModIds;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("UnresolvedMixinReference")
@Restriction(require = @Condition(value = ModIds.extra_player_renderer, versionPredicates = "<2.0.0"))
@Pseudo
@Mixin(targets = "github.io.lucunji.explayerenderer.client.render.PlayerHUD")
public abstract class PlayerHUDRenderer_v1Mixin
{
	@Inject(method = "render", at = @At("HEAD"), remap = false, cancellable = true)
	private void eprHideOnDebugHud(CallbackInfo ci)
	{
		EprHideOnDebugHudImpl.applyHide(ci);
	}
}
