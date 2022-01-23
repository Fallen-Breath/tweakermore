package me.fallenbreath.tweakermore.mixins.tweaks.tweakmPickBlockLastContinuously;

import fi.dy.masa.litematica.config.Configs;
import fi.dy.masa.litematica.data.DataManager;
import fi.dy.masa.litematica.materials.MaterialCache;
import fi.dy.masa.litematica.tool.ToolMode;
import fi.dy.masa.litematica.util.EntityUtils;
import fi.dy.masa.litematica.util.InventoryUtils;
import fi.dy.masa.litematica.util.ItemUtils;
import fi.dy.masa.litematica.world.SchematicWorldHandler;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.util.LayerRange;
import fi.dy.masa.malilib.util.PositionUtils;
import fi.dy.masa.tweakeroo.tweaks.PlacementTweaks;
import me.fallenbreath.tweakermore.config.TweakerMoreToggles;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerInventory;
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

@Mixin(PlacementTweaks.class)
public abstract class PlacementTweaksMixin
{
	@Inject(method = "tryPlaceBlock", at = @At("HEAD"), remap = false)
	private static void tweakmPickBlockLastContinuously(
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
			if (DataManager.getToolMode() != ToolMode.REBUILD && !Configs.Generic.EASY_PLACE_MODE.getBooleanValue())
			{
				if (TweakerMoreToggles.TWEAKM_AUTO_PICK_SCHEMATIC_BLOCK.getBooleanValue() && EntityUtils.shouldPickBlock(mc.player))
				{
					BlockHitResult hitResult = new BlockHitResult(hitVec, sideIn, posIn, false);
					ItemPlacementContext ctx = new ItemPlacementContext(new ItemUsageContext(player, hand, hitResult));
					doSchematicWorldPickBlock(mc, ctx.getBlockPos(), hand);
				}
			}
		}
	}

	/**
	 * Stolen from {@link fi.dy.masa.litematica.util.WorldUtils#doSchematicWorldPickBlock}
	 */
	private static void doSchematicWorldPickBlock(MinecraftClient mc, BlockPos pos, Hand hand)
	{
		World schematicWorld = SchematicWorldHandler.getSchematicWorld();
		World clientWorld = mc.world;
		if (schematicWorld != null && mc.player != null && clientWorld != null && mc.interactionManager != null)
		{
			LayerRange layerRange = DataManager.getRenderLayerRange();
			if (!layerRange.isPositionWithinRange(pos))
			{
				return;
			}
			BlockState state = schematicWorld.getBlockState(pos);
			ItemStack stack = MaterialCache.getInstance().getRequiredBuildItemForState(state, schematicWorld, pos);
//			System.err.println(pos + " " + state + " " + stack);
			if (!stack.isEmpty())
			{
				PlayerInventory inv = mc.player.inventory;
				stack = stack.copy();
				if (mc.player.abilities.creativeMode)
				{
					BlockEntity te = schematicWorld.getBlockEntity(pos);
					if (GuiBase.isCtrlDown() && te != null && clientWorld.isAir(pos))
					{
						ItemUtils.storeTEInStack(stack, te);
					}

					InventoryUtils.setPickedItemToHand(stack, mc);
					mc.interactionManager.clickCreativeStack(mc.player.getStackInHand(Hand.MAIN_HAND), 36 + inv.selectedSlot);
				}
				else
				{
					int slot = inv.getSlotWithStack(stack);
					boolean shouldPick = inv.selectedSlot != slot;
					boolean canPick = slot != -1;
					if (shouldPick && canPick)
					{
						InventoryUtils.setPickedItemToHand(stack, mc);
					}
				}

				// so hand restore works fine
				PlacementTweaks.cacheStackInHand(hand);
			}
		}
	}
}
