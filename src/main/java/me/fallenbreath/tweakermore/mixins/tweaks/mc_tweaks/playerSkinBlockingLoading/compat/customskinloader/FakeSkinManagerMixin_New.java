package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.playerSkinBlockingLoading.compat.customskinloader;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.impl.mc_tweaks.playerSkinBlockingLoading.TaskSynchronizer;
import me.fallenbreath.tweakermore.util.ModIds;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * The way to submit Runnable in {@link customskinloader.fake.FakeSkinManager#loadProfileTextures}
 *   >=14.14-SNAPSHOT-336: {@code CustomSkinLoader.loadProfileTextures()}    <--------
 *   >14.11 <14.14-SNAPSHOT-336: {@code THREAD_POOL.execute()}
 *   <=14.11: {@code THREAD_POOL.submit()}
 */
@Restriction(require = @Condition(value = ModIds.custom_skin_loader, versionPredicates = ">=14.14-SNAPSHOT-336"))
@Pseudo
@Mixin(targets = "customskinloader.fake.FakeSkinManager")
public abstract class FakeSkinManagerMixin_New
{
	@SuppressWarnings("UnresolvedMixinReference")
	@ModifyArg(
			method = "loadProfileTextures",
			at = @At(
					value = "INVOKE",
					target = "Lcustomskinloader/CustomSkinLoader;loadProfileTextures(Ljava/lang/Runnable;)V"
			),
			remap = false
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