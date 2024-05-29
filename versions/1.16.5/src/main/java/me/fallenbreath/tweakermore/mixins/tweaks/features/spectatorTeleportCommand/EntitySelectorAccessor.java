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

package me.fallenbreath.tweakermore.mixins.tweaks.features.spectatorTeleportCommand;

import net.minecraft.command.EntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.predicate.NumberRange;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

//#if MC >= 11700
//$$ import net.minecraft.util.TypeFilter;
//#else
import net.minecraft.entity.EntityType;
//#endif

@Mixin(EntitySelector.class)
public interface EntitySelectorAccessor
{
	@Accessor
	Predicate<Entity> getBasePredicate();

	@Accessor
	NumberRange.FloatRange getDistance();

	@Accessor
	Function<Vec3d, Vec3d> getPositionOffset();

	@Accessor
	BiConsumer<Vec3d, List<? extends Entity>> getSorter();

	@Nullable
	@Accessor
	Box getBox();

	@Accessor
	boolean getSenderOnly();

	@Nullable
	@Accessor
	String getPlayerName();

	@Nullable
	@Accessor
	UUID getUuid();

	@Nullable
	@Accessor("type")
	//#if MC >= 11700
	//$$ TypeFilter<Entity, ?> getEntityFilter();
	//#else
	EntityType<?> getType();
	//#endif
}
