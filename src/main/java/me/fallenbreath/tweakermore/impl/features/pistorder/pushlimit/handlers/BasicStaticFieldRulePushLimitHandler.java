/*
 * This file is part of the Pistorder project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2023  Fallen_Breath and contributors
 *
 * Pistorder is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Pistorder is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Pistorder.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.fallenbreath.tweakermore.impl.features.pistorder.pushlimit.handlers;

import net.fabricmc.loader.api.FabricLoader;

import java.lang.reflect.Field;

public abstract class BasicStaticFieldRulePushLimitHandler implements PushLimitHandler
{
	private final String className;
	private final String fieldName;
	private final boolean modLoaded;
	private Field ruleField;

	public BasicStaticFieldRulePushLimitHandler(String className, String fieldName)
	{
		this.className = className;
		this.fieldName = fieldName;
		this.modLoaded = FabricLoader.getInstance().isModLoaded(this.getModId());
		this.ruleField = null;
	}

	private Field getRuleField() throws ClassNotFoundException, NoSuchFieldException
	{
		if (this.ruleField != null)
		{
			return this.ruleField;
		}

		Class<?> clazz = Class.forName(this.className);
		Field field = clazz.getField(this.fieldName);
		field.setAccessible(true);

		this.ruleField = field;
		return field;
	}

	@Override
	public void setPushLimit(int pushLimit) throws PushLimitOperateException
	{
		if (!this.modLoaded)
		{
			throw new PushLimitOperateException();
		}
		try
		{
			this.getRuleField().setInt(null, pushLimit);
		}
		catch (Exception e)
		{
			throw new PushLimitOperateException(e);
		}
	}

	@Override
	public int getPushLimit() throws PushLimitOperateException
	{
		if (!this.modLoaded)
		{
			throw new PushLimitOperateException();
		}
		try
		{
			return this.getRuleField().getInt(null);
		}
		catch (Exception e)
		{
			throw new PushLimitOperateException(e);
		}
	}
}
