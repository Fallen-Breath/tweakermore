package me.fallenbreath.tweakermore.mixins.tweaks.bossBarMaxEntry;

import com.google.common.collect.Iterators;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.client.gui.hud.BossBarHud;
import net.minecraft.client.gui.hud.ClientBossBar;
import net.minecraft.client.util.Window;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Iterator;

@Mixin(BossBarHud.class)
public abstract class BossBarHudMixin
{
	@ModifyVariable(
			method = "render",
			at = @At(
					value = "INVOKE_ASSIGN",
					target = "Ljava/util/Collection;iterator()Ljava/util/Iterator;",
					remap = false
			)
	)
	private Iterator<ClientBossBar> tweakerMore_bossBarMaxEntry_checkLimitation(Iterator<ClientBossBar> iterator)
	{
		int limitation = TweakerMoreConfigs.BOSS_BAR_MAX_ENTRY.getIntegerValue();
		if (limitation >= 0)
		{
			iterator = Iterators.limit(iterator, limitation);
		}
		return iterator;
	}

	@Redirect(
			method = "render",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/util/Window;getScaledHeight()I"
			),
			require = 0
	)
	private int tweakerMore_bossBarMaxEntry_skipVanillaCheck(Window window)
	{
		if (TweakerMoreConfigs.BOSS_BAR_MAX_ENTRY.getIntegerValue() >= 0)
		{
			return Integer.MAX_VALUE;
		}
		return window.getScaledHeight();
	}
}
