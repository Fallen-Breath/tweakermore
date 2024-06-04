/*
 * This file is part of the TweakerMore project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2023  Fallen_Breath and contributors
 *
 * TweakerMore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * TweakerMore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with TweakerMore.  If not, see <https://www.gnu.org/licenses/>.
 */

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
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Restriction(require = @Condition(ModIds.litematica))
@Mixin(LitematicaSchematic.class)
public abstract class LitematicaSchematicMixin implements LitematicaSchematic000Origin
{
	@Unique
	private boolean flag000Origin = false;

	@Override
	public void set000Origin$TKM(boolean value)
	{
		this.flag000Origin = value;
	}

	@Override
	public boolean is000Origin$TKM()
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
			((LitematicaSchematic000Origin)schematic).set000Origin$TKM(true);
		}
		return schematic;
	}

	@ModifyVariable(method = "createEmptySchematic", at = @At("TAIL"), remap = false)
	private static LitematicaSchematic lmOriginOverride000_info000Override(LitematicaSchematic schematic)
	{
		if (TweakerMoreConfigs.LM_ORIGIN_OVERRIDE_000.getBooleanValue())
		{
			if (schematic != null && ((LitematicaSchematic000Origin)schematic).is000Origin$TKM())
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
			if (((LitematicaSchematic000Origin)this).is000Origin$TKM())
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
				((LitematicaSchematic000Origin)this).set000Origin$TKM(true);
			}
		}
	}
}
