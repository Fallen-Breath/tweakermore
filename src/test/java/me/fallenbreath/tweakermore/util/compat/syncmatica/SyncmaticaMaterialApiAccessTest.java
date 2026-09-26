/*
 * This file is part of the TweakerMore project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026  Fallen_Breath and contributors
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

package me.fallenbreath.tweakermore.util.compat.syncmatica;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class SyncmaticaMaterialApiAccessTest
{
	@Test
	public void decodesMaterialRequirementRecordShape() throws ReflectiveOperationException
	{
		List<ClaimedMaterialRequirement> requirements = SyncmaticaMaterialApiAccess.decodeRequirements(Arrays.asList(
				new ApiRequirement("minecraft:stone", "", 64),
				new ApiRequirement("minecraft:oak_planks", "future-variant", 12)
		));

		assertEquals(2, requirements.size());
		assertEquals("minecraft:stone", requirements.get(0).getItemId());
		assertEquals("", requirements.get(0).getVariant());
		assertEquals(64, requirements.get(0).getMissingAmount());
		assertEquals("minecraft:oak_planks", requirements.get(1).getItemId());
		assertEquals("future-variant", requirements.get(1).getVariant());
		assertEquals(12, requirements.get(1).getMissingAmount());
	}

	public static class ApiRequirement
	{
		private final String itemId;
		private final String variant;
		private final int missingAmount;

		public ApiRequirement(String itemId, String variant, int missingAmount)
		{
			this.itemId = itemId;
			this.variant = variant;
			this.missingAmount = missingAmount;
		}

		public String itemId()
		{
			return this.itemId;
		}

		public String variant()
		{
			return this.variant;
		}

		public int missingAmount()
		{
			return this.missingAmount;
		}
	}
}
