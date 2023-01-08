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

package me.fallenbreath.tweakermore.util;

import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ReflectionUtil
{
	public static Optional<Class<?>> getClass(String className)
	{
		try
		{
			return Optional.of(Class.forName(className));
		}
		catch (ClassNotFoundException e)
		{
			return Optional.empty();
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> ValueWrapper<T> getField(Class<?> clazz, String fieldName)
	{
		try
		{
			Field field = clazz.getDeclaredField(fieldName);
			field.setAccessible(true);
			return ValueWrapper.of((T)field.get(null));
		}
		catch (Exception e)
		{
			return ValueWrapper.empty();
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> Supplier<T> getStaticFieldGetter(Class<?> clazz, String fieldName)
	{
		try
		{
			Field field = clazz.getDeclaredField(fieldName);
			field.setAccessible(true);
			return () -> {
				try
				{
					return (T)field.get(null);
				}
				catch (Exception e)
				{
					throw new RuntimeException("cannot get field value " + fieldName);
				}
			};
		}
		catch (Exception e)
		{
			throw new RuntimeException("cannot access " + fieldName);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> ValueWrapper<T> invoke(Class<?> clazz, String methodName, @Nullable Object object, Object... args)
	{
		try
		{
			for (Method method : clazz.getDeclaredMethods())
			{
				if (method.getName().equals(methodName) && method.getParameterCount() == args.length)
				{
					method.setAccessible(true);
					return ValueWrapper.of((T)method.invoke(object, args));
				}
			}
		}
		catch (Exception ignored)
		{
		}
		return ValueWrapper.empty();
	}

	public static class ValueWrapper<T>
	{
		@Nullable
		private final T value;
		private final boolean existed;

		private ValueWrapper(@Nullable T value, boolean existed)
		{
			this.value = value;
			this.existed = existed;
		}

		public static <T> ValueWrapper<T> of(@Nullable T value)
		{
			return new ValueWrapper<>(value, true);
		}

		public static <T> ValueWrapper<T> empty()
		{
			return new ValueWrapper<>(null, false);
		}

		public boolean isPresent()
		{
			return this.existed;
		}

		@Nullable
		public T get()
		{
			if (!this.isPresent())
			{
				throw new RuntimeException("value not exists");
			}
			return this.value;
		}

		public void ifPresent(Consumer<@Nullable T> action)
		{
			if (this.existed)
			{
				action.accept(this.value);
			}
		}
	}
}
