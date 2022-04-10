package me.fallenbreath.tweakermore.mixins.tweaks.serverDataSyncer.litematica;

import fi.dy.masa.litematica.selection.AreaSelection;
import fi.dy.masa.litematica.selection.Box;
import fi.dy.masa.litematica.util.PositionUtils;
import fi.dy.masa.litematica.util.SchematicUtils;
import fi.dy.masa.malilib.gui.Message;
import fi.dy.masa.malilib.util.InfoUtils;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.impl.serverDataSyncer.ServerDataSyncer;
import me.fallenbreath.tweakermore.util.ModIds;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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
				showMessage$TKM(Message.MessageType.INFO, "schematic_data_sync_start", area.getName());

				List<Box> validBoxes = PositionUtils.getValidBoxes(area);
				AtomicInteger beCounter = new AtomicInteger(0);
				AtomicInteger entityCounter = new AtomicInteger(0);

				for (int i = 0; i < validBoxes.size(); i++)
				{
					Box box = validBoxes.get(i);
					final boolean isLast = i == validBoxes.size() - 1;
					ServerDataSyncer.getInstance().syncEverythingWithin(
							box.getPos1(),
							box.getPos2(),
							(blockEntityAmount, entityAmount) -> {
								beCounter.addAndGet(blockEntityAmount);
								entityCounter.addAndGet(entityAmount);
								if (isLast)
								{
									showMessage$TKM(Message.MessageType.SUCCESS, "schematic_data_synced", beCounter.get(), entityCounter.get());
								}
							}
					);
				}
			}
		}
		return area;
	}

	@Unique
	private static void showMessage$TKM(Message.MessageType type, String textName, Object... args)
	{
		// delay a bit so the text can be shown on the top of the current gui
		MinecraftClient.getInstance().send(() -> {
			InfoUtils.showGuiOrInGameMessage(type, 3000, "tweakermore.config.serverDataSyncer." + textName, args);
		});
	}
}
