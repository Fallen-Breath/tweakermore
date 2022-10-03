package me.fallenbreath.tweakermore.mixins.tweaks.mod_tweaks.ofUseVanillaBrightnessCache;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.util.ModIds;
import net.minecraft.client.render.block.BlockModelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Restriction(require = @Condition(ModIds.optifine))
@Mixin(BlockModelRenderer.class)
public interface BlockModelRendererAccessor
{
	@Accessor
	static ThreadLocal<BlockModelRenderer.BrightnessCache> getBrightnessCache()
	{
		throw new RuntimeException();
	}
}
