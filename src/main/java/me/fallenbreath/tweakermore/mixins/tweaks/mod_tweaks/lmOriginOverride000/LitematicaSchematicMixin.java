package me.fallenbreath.tweakermore.mixins.tweaks.mod_tweaks.lmOriginOverride000;

import fi.dy.masa.litematica.schematic.LitematicaSchematic;
import fi.dy.masa.malilib.gui.Message;
import fi.dy.masa.malilib.util.InfoUtils;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.impl.mod_tweaks.lmOriginOverride000.LitematicaOriginOverrideGlobals;
import me.fallenbreath.tweakermore.impl.mod_tweaks.lmOriginOverride000.LitematicaSchematic000Origin;
import me.fallenbreath.tweakermore.util.ModIds;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Restriction(require = @Condition(ModIds.litematica))
@Mixin(LitematicaSchematic.class)
public abstract class LitematicaSchematicMixin implements LitematicaSchematic000Origin
{
	private boolean flag000Origin = false;

	@Override
	public void set000Origin(boolean value)
	{
		this.flag000Origin = value;
	}

	@Override
	public boolean is000Origin()
	{
		return this.flag000Origin;
	}

	@ModifyVariable(
			method = "createEmptySchematic",
			at = @At(
					value = "INVOKE",
					target = "Lfi/dy/masa/litematica/schematic/LitematicaSchematic;setSubRegionPositions(Ljava/util/List;Lnet/minecraft/util/math/BlockPos;)V"
			)
	)
	private static LitematicaSchematic lmOriginOverride000_mark000Override(LitematicaSchematic schematic)
	{
		if (TweakerMoreConfigs.LM_ORIGIN_OVERRIDE_000.getBooleanValue())
		{
			((LitematicaSchematic000Origin)schematic).set000Origin(true);
		}
		return schematic;
	}

	@ModifyVariable(method = "createEmptySchematic", at = @At("TAIL"), remap = false)
	private static LitematicaSchematic lmOriginOverride000_info000Override(LitematicaSchematic schematic)
	{
		if (TweakerMoreConfigs.LM_ORIGIN_OVERRIDE_000.getBooleanValue())
		{
			if (schematic != null && ((LitematicaSchematic000Origin)schematic).is000Origin())
			{
				InfoUtils.showGuiMessage(Message.MessageType.INFO, "tweakermore.impl.lmOriginOverride000.marked_override", schematic.getMetadata().getName());
			}
		}
		return schematic;
	}

	@ModifyArg(
			method = "createEmptySchematic",
			at = @At(
					value = "INVOKE",
					target = "Lfi/dy/masa/litematica/schematic/LitematicaSchematic;setSubRegionPositions(Ljava/util/List;Lnet/minecraft/util/math/BlockPos;)V"
			)
	)
	private static BlockPos lmOriginOverride000_modifyOnSchematicCreated(BlockPos origin)
	{
		if (TweakerMoreConfigs.LM_ORIGIN_OVERRIDE_000.getBooleanValue())
		{
			origin = new BlockPos(0, 0, 0);
		}
		return origin;
	}

	@ModifyVariable(method = "writeToNBT", at = @At("TAIL"))
	private CompoundTag lmOriginOverride000_save000Flag(CompoundTag nbt)
	{
		if (TweakerMoreConfigs.LM_ORIGIN_OVERRIDE_000.getBooleanValue())
		{
			if (((LitematicaSchematic000Origin)this).is000Origin())
			{
				nbt.putBoolean(LitematicaOriginOverrideGlobals.ORIGIN_OVERRIDE_FLAG, true);
			}
		}
		return nbt;
	}

	@Inject(method = "readFromNBT", at = @At("HEAD"))
	private void lmOriginOverride000_load000Flag(CompoundTag nbt, CallbackInfoReturnable<Boolean> cir)
	{
		if (TweakerMoreConfigs.LM_ORIGIN_OVERRIDE_000.getBooleanValue())
		{
			if (nbt.getBoolean(LitematicaOriginOverrideGlobals.ORIGIN_OVERRIDE_FLAG))
			{
				((LitematicaSchematic000Origin)this).set000Origin(true);
			}
		}
	}
}
