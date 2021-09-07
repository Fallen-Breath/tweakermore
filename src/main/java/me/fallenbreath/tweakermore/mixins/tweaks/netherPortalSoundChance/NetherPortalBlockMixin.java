package me.fallenbreath.tweakermore.mixins.tweaks.netherPortalSoundChance;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.block.NetherPortalBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(value = NetherPortalBlock.class, priority = 10000)
public abstract class NetherPortalBlockMixin
{
	@ModifyConstant(
			method = "randomDisplayTick",
			constant = @Constant(intValue = 100),
			require = 0,
			allow = 1
	)
	private int modifyPlaySoundChance(int vanillaChance)
	{
		double value = TweakerMoreConfigs.NETHER_PORTAL_SOUND_CHANCE.getDoubleValue();
		return value <= 0.0D ? Integer.MAX_VALUE : (int)(1.0D / value);
	}
}
