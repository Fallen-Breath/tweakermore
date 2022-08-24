package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.playerSkinBlockingLoading;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.impl.mc_tweaks.playerSkinBlockingLoading.TaskSynchronizer;
import me.fallenbreath.tweakermore.util.ModIds;
import net.minecraft.client.texture.PlayerSkinProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

//#if MC >= 11500
import org.spongepowered.asm.mixin.injection.ModifyVariable;
//#else
//$$ import org.spongepowered.asm.mixin.injection.ModifyArg;
//#endif

/**
 * Custom skin loader mod @Overwrite / @Inject cancel onto the loadSkin method
 * So don't apply mixin when that mod is loaded
 */
@Restriction(conflict = @Condition(ModIds.custom_skin_loader))
@Mixin(PlayerSkinProvider.class)
public abstract class PlayerSkinProviderMixin
{
	//#if MC >= 11500
	@ModifyVariable(
	//#else
	//$$ @ModifyArg(
	//#endif
			method = "loadSkin(Lcom/mojang/authlib/GameProfile;Lnet/minecraft/client/texture/PlayerSkinProvider$SkinTextureAvailableCallback;Z)V",
			at = @At(
					value = "INVOKE",
					//#if MC >= 11800
					//$$ target = "Lnet/minecraft/util/Util;getMainWorkerExecutor()Ljava/util/concurrent/ExecutorService;"
					//#elseif MC >= 11500
					target = "Lnet/minecraft/util/Util;getServerWorkerExecutor()Ljava/util/concurrent/Executor;"
					//#else
					//$$ target = "Ljava/util/concurrent/ExecutorService;submit(Ljava/lang/Runnable;)Ljava/util/concurrent/Future;"
					//#endif
			)
	)
	private Runnable playerSkinBlockingLoading_blockingProfileFetching(Runnable runnable)
	{
		if (TweakerMoreConfigs.PLAYER_SKIN_BLOCKING_LOADING.getBooleanValue())
		{
			runnable = TaskSynchronizer.createSyncedTask(runnable);
		}
		return runnable;
	}
}
