package me.fallenbreath.tweakermore.impl.features.tweakmSchematicProPlace;

import com.google.common.collect.Lists;
import fi.dy.masa.litematica.config.Configs;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.config.options.TweakerMoreConfigBooleanHotkeyed;
import me.fallenbreath.tweakermore.util.StringUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

public class ProPlaceImpl
{
	private static final List<TweakerMoreConfigBooleanHotkeyed> PRO_PLACE_CONFIGS = Lists.newArrayList(
			TweakerMoreConfigs.TWEAKM_AUTO_PICK_SCHEMATIC_BLOCK,
			TweakerMoreConfigs.TWEAKM_SCHEMATIC_BLOCK_PLACEMENT_RESTRICTION
	);

	public static String modifyComment(String comment)
	{
		String lines = StringUtil.configsToListLines(PRO_PLACE_CONFIGS);
		return comment.replaceFirst("##CONFIGS##", lines);
	}

	public static void handleBlockPlacement(ItemPlacementContext ctx, CallbackInfoReturnable<ActionResult> cir)
	{
		if (Configs.Generic.EASY_PLACE_MODE.getBooleanValue())
		{
			return;
		}

		MinecraftClient mc = MinecraftClient.getInstance();

		boolean proPlace = TweakerMoreConfigs.TWEAKM_SCHEMATIC_PRO_PLACE.getBooleanValue();
		boolean autoPick = proPlace || TweakerMoreConfigs.TWEAKM_AUTO_PICK_SCHEMATIC_BLOCK.getBooleanValue();
		boolean restrict = proPlace || TweakerMoreConfigs.TWEAKM_SCHEMATIC_BLOCK_PLACEMENT_RESTRICTION.getBooleanValue();

		if (autoPick)
		{
			SchematicBlockPicker.doSchematicWorldPickBlock(mc, ctx.getBlockPos(), ctx.getHand());
		}

		if (restrict)
		{
			if (!PlacementRestrictor.canDoBlockPlacement(mc, ctx.getBlockPos()))
			{
				cir.setReturnValue(ActionResult.PASS);
			}
		}
	}
}
