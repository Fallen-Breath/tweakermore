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

import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.world.entity.Entity;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

//#if MC >= 12100
//$$ import net.minecraft.world.flag.FeatureFlagSet;
//#endif

//#if MC >= 11700
//$$ import net.minecraft.world.level.entity.EntityTypeTest;
//#else
import net.minecraft.world.entity.EntityType;
//#endif

@Mixin(EntitySelector.class)
public interface EntitySelectorAccessor
{
	//#if MC >= 12100
	//$$ @Accessor("contextFreePredicates")
	//$$ List<Predicate<Entity>> getPredicates();
	//#else
	@Accessor("predicate")
	Predicate<Entity> getBasePredicate();
	//#endif

	@Accessor("range")
	MinMaxBounds.Floats getDistance();

	@Accessor("position")
	Function<Vec3, Vec3> getPositionOffset();

	@Accessor("order")
	BiConsumer<Vec3, List<? extends Entity>> getSorter();

	@Nullable
	@Accessor("aabb")
	AABB getBox();

	@Accessor("currentEntity")
	boolean getSenderOnly();

	@Nullable
	@Accessor("playerName")
	String getPlayerName();

	@Nullable
	@Accessor("entityUUID")
	UUID getUuid();

	@Nullable
	@Accessor("type")
	//#if MC >= 11700
	//$$ EntityTypeTest<Entity, ?> getEntityFilter();
	//#else
	EntityType<?> getType();
	//#endif

	@Invoker("getPredicate")
	Predicate<Entity> invokeGetPositionPredicate(
			Vec3 pos
			//#if MC >= 12100
			//$$ , @Nullable AABB box, @Nullable FeatureFlagSet enabledFeatures
			//#endif
	);

	//#if MC >= 12100
	//$$ @Invoker("getAbsoluteAabb")
	//$$ AABB invokeGetOffsetBox(Vec3 offset);
	//#endif
}
