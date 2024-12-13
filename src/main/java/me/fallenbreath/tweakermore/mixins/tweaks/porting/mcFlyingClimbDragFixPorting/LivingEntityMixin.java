/*
 * This file is part of the TweakerMore project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2024  Fallen_Breath and contributors
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

package me.fallenbreath.tweakermore.mixins.tweaks.porting.mcFlyingClimbDragFixPorting;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.util.ModIds;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Restriction(require = @Condition(value = ModIds.minecraft, versionPredicates = "<1.21.4"))
@Mixin(LivingEntity.class)
public class LivingEntityMixin
{
    @Inject(method = "isClimbing", at=@At("RETURN"), cancellable = true)
    public void isClimbing(CallbackInfoReturnable<Boolean> cir)
    {
        LivingEntity self = (LivingEntity) (Object) this;
        if (TweakerMoreConfigs.MC_FLYING_CLIMB_DRAG_FIX_PORTING.getBooleanValue() && cir.getReturnValue() && self instanceof PlayerEntity)
        {
            PlayerEntity player = (PlayerEntity) self;
            //#if MC >= 11700
            //$$ if (player.getAbilities().flying)
            //#else
            if (player.abilities.flying)
            //#endif
            {
                cir.setReturnValue(false);
            }
        }
    }
}
