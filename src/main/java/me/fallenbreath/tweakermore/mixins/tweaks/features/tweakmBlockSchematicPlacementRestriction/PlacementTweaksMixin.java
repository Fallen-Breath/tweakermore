package me.fallenbreath.tweakermore.mixins.tweaks.features.tweakmBlockSchematicPlacementRestriction;

import fi.dy.masa.litematica.config.Configs;
import fi.dy.masa.litematica.data.DataManager;
import fi.dy.masa.litematica.materials.MaterialCache;
import fi.dy.masa.litematica.util.EntityUtils;
import fi.dy.masa.litematica.util.WorldUtils;
import fi.dy.masa.litematica.world.SchematicWorldHandler;
import fi.dy.masa.malilib.util.LayerRange;
import fi.dy.masa.malilib.util.PositionUtils;
import fi.dy.masa.tweakeroo.tweaks.PlacementTweaks;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.util.ModIds;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 2000 priority to make it inject later than {@link me.fallenbreath.tweakermore.mixins.tweaks.features.tweakmAutoPickSchematicBlock.PlacementTweaksMixin}
 */
@Restriction(require = {@Condition(ModIds.tweakeroo), @Condition(ModIds.litematica)})
@Mixin(value = PlacementTweaks.class, priority = 2000)
public abstract class PlacementTweaksMixin
{
	@Inject(method = "tryPlaceBlock", at = @At("HEAD"), cancellable = true, remap = false)
	private static void tweakmBlockSchematicPlacementRestriction(
			ClientPlayerInteractionManager controller,
			ClientPlayerEntity player,
			ClientWorld world,
			BlockPos posIn,
			Direction sideIn,
			Direction sideRotatedIn,
			float playerYaw,
			Vec3d hitVec,
			Hand hand,
			PositionUtils.HitPart hitPart,
			boolean isFirstClick,
			CallbackInfoReturnable<ActionResult> cir
	)
	{
		MinecraftClient mc = MinecraftClient.getInstance();
		if (mc.player != null)
		{
			if (
					!Configs.Generic.EASY_PLACE_MODE.getBooleanValue() &&
					!Configs.Generic.PLACEMENT_RESTRICTION.getBooleanValue() &&
					TweakerMoreConfigs.TWEAKM_SCHEMATIC_BLOCK_PLACEMENT_RESTRICTION.getBooleanValue()
			)
			{
				BlockHitResult hitResult = new BlockHitResult(hitVec, sideIn, posIn, false);
				ItemPlacementContext ctx = new ItemPlacementContext(new ItemUsageContext(player, hand, hitResult));
				if (!canDoBlockPlacement(mc, ctx))
				{
					cir.setReturnValue(ActionResult.PASS);
				}
			}
		}
	}

	private static boolean canDoBlockPlacement(MinecraftClient mc, ItemPlacementContext ctx)
	{
		final int PROTECT_RANGE = 2;

		BlockPos pos = ctx.getBlockPos();

		// Always permit if it's far from the schematic
		if (!WorldUtils.isPositionWithinRangeOfSchematicRegions(pos, PROTECT_RANGE))
		{
			return true;
		}

		// Always permit if it's far from the render range
		LayerRange layerRange = DataManager.getRenderLayerRange();
		if (
				BlockPos.stream(
						pos.add(-PROTECT_RANGE, -PROTECT_RANGE, -PROTECT_RANGE),
						pos.add(PROTECT_RANGE, PROTECT_RANGE, PROTECT_RANGE)
				).
				noneMatch(layerRange::isPositionWithinRange)
		)
		{
			return true;
		}

		World schematicWorld = SchematicWorldHandler.getSchematicWorld();
		World clientWorld = mc.world;

		if (schematicWorld != null && mc.player != null && clientWorld != null && mc.interactionManager != null)
		{
			BlockState schematicState = schematicWorld.getBlockState(pos);
			ItemStack schematicStack = MaterialCache.getInstance().
					//#if MC >= 11500
					getRequiredBuildItemForState
					//#else
					//$$ getItemForState
					//#endif
					(schematicState, schematicWorld, pos);

			// there's no possible block for this schematic block, cancel
			if (schematicStack.isEmpty())
			{
				return false;
			}

			// check if the player is using the right item stack for block placement
			//noinspection RedundantIfStatement
			if (EntityUtils.getUsedHandForItem(mc.player, schematicStack) == null)
			{
				return false;
			}
		}

		return true;
	}
}
