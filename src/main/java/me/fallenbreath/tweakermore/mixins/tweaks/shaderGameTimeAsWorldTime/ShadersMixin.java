package me.fallenbreath.tweakermore.mixins.tweaks.shaderGameTimeAsWorldTime;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.util.ModIds;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Group;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;

@SuppressWarnings("UnresolvedMixinReference")
@Restriction(require = @Condition(ModIds.optifine))
@Pseudo
@Mixin(targets = "net.optifine.shaders.Shaders")
public abstract class ShadersMixin
{
	@Group(min = 1, max = 1)
	@ModifyArg(
			method = {
					"useProgram",  // optifine < HD U G7 (1.16.4)
					"setProgramUniforms",  // optifine >= HD U G7 (1.16.4)
			},
			slice = @Slice(
					from = @At(
							value = "FIELD",
							target = "Lnet/optifine/shaders/Shaders;uniform_worldTime:Lnet/optifine/shaders/uniform/ShaderUniform1i;",
							remap = false
					)
			),
			at = @At(
					value = "INVOKE",
					target = "Lnet/optifine/shaders/Shaders;setProgramUniform1i(Lnet/optifine/shaders/uniform/ShaderUniform1i;I)V",
					ordinal = 0,
					remap = false
			),
			remap = false
	)
	private static int ofShaderGameTimeAsWorldTime(int worldTime)
	{
		if (TweakerMoreConfigs.SHADER_GAME_TIME_AS_WORLD_TIME.getBooleanValue())
		{
			ClientWorld clientWorld = MinecraftClient.getInstance().world;
			if (clientWorld != null)
			{
				worldTime = (int)(clientWorld.getTime() % 24000L);
			}
		}
		return worldTime;
	}
}
