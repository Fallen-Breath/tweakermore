package me.fallenbreath.tweakermore.mixins.tweaks.serverDataSyncer.litematica;

import fi.dy.masa.litematica.selection.AreaSelection;
import fi.dy.masa.litematica.util.SchematicUtils;
import fi.dy.masa.malilib.gui.Message;
import fi.dy.masa.malilib.util.InfoUtils;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.impl.serverDataSyncer.AreaSelectionUtil;
import me.fallenbreath.tweakermore.impl.serverDataSyncer.ServerDataSyncer;
import me.fallenbreath.tweakermore.impl.serverDataSyncer.TargetPair;
import me.fallenbreath.tweakermore.util.ModIds;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;

@Restriction(require = @Condition(ModIds.litematica))
@Mixin(SchematicUtils.class)
public abstract class SchematicUtilsMixin
{
	@ModifyVariable(
			method = "saveSchematic",
			at = @At(
					value = "INVOKE_ASSIGN",
					target = "Lfi/dy/masa/litematica/selection/SelectionManager;getCurrentSelection()Lfi/dy/masa/litematica/selection/AreaSelection;",
					remap = false
			),
			remap = false
	)
	private static AreaSelection serverDataSyncer4Schematic(AreaSelection area)
	{
		if (TweakerMoreConfigs.SERVER_DATA_SYNCER.getBooleanValue())
		{
			MinecraftClient mc = MinecraftClient.getInstance();
			if (area != null && !mc.isIntegratedServerRunning() && ServerDataSyncer.hasEnoughPermission())
			{
				MinecraftClient.getInstance().send(() -> {
					showMessage$TKM(Message.MessageType.INFO, "start", area.getName());

					TargetPair pair = AreaSelectionUtil.extractBlockEntitiesAndEntities(area);
					final int beTotal = pair.getBlockEntityAmount();
					final int eTotal = pair.getEntityAmount();

					AtomicLong lastUpdateTime = new AtomicLong(Util.getMeasuringTimeMs());
					CompletableFuture<Void> future = ServerDataSyncer.getInstance().syncEverything(pair, (be, e) -> {
						long currentTime = Util.getMeasuringTimeMs();
						if (currentTime - lastUpdateTime.get() > 500)
						{
							lastUpdateTime.set(currentTime);
							String percent = String.format("%.1f%%", 100.0D * (be + e) / (beTotal + eTotal));
							showMessage$TKM(Message.MessageType.INFO, "progress", be, beTotal, e, eTotal, percent);
						}
					});
					future.thenRun(() -> showMessage$TKM(Message.MessageType.SUCCESS, "synced", beTotal, eTotal));
				});
			}
		}
		return area;
	}

	@Unique
	private static void showMessage$TKM(Message.MessageType type, String textName, Object... args)
	{
		InfoUtils.showGuiOrInGameMessage(type, 3000, "tweakermore.config.serverDataSyncer.schematic_sync." + textName, args);
	}
}
