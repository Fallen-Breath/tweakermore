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

import me.fallenbreath.tweakermore.TweakerMoreMod;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class ReflectionUtils
{
	private static final Map<String, Optional<Class<?>>> classCache = new ConcurrentHashMap<>();

	public static Optional<Class<?>> getClass(String className)
	{
		return classCache.computeIfAbsent(className, k -> {
			try
			{
				return Optional.of(Class.forName(className));
			}
			catch (ClassNotFoundException e)
			{
				TweakerMoreMod.LOGGER.debug("ReflectionUtils.getClass '{}' not found", className);
				return Optional.empty();
			}
		});
	}

	@SuppressWarnings("unchecked")
	public static <T> ValueWrapper<T> getStaticField(Class<?> clazz, String fieldName)
	{
		try
		{
			Field field = clazz.getDeclaredField(fieldName);
			field.setAccessible(true);
			return ValueWrapper.of((T)field.get(null));
		}
		catch (Exception e)
		{
			return ValueWrapper.error(e);
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
			return ValueWrapper.notFound();
		}
		catch (Exception error)
		{
			return ValueWrapper.error(error);
		}
	}

	public static class ValueWrapper<T>
	{
		private final T value;
		private final boolean existed;
		@Nullable
		private final Throwable error;

		private ValueWrapper(T value, boolean existed, @Nullable Throwable error)
		{
			this.value = value;
			this.existed = existed;
			this.error = error;
		}

		public static <T> ValueWrapper<T> of(T value)
		{
			return new ValueWrapper<>(value, true, null);
		}

		public static <T> ValueWrapper<T> notFound()
		{
			return new ValueWrapper<>(null, false, null);
		}

		public static <T> ValueWrapper<T> error(Throwable error)
		{
			return new ValueWrapper<>(null, false, error);
		}

		public boolean isPresent()
		{
			return this.existed;
		}

		@Nullable
		public Throwable getError()
		{
			return this.error;
		}

		public T get()
		{
			if (!this.isPresent())
			{
				throw new RuntimeException("value not exists");
			}
			return this.value;
		}

		public void ifPresent(Consumer<T> action)
		{
			if (this.existed)
			{
				action.accept(this.value);
			}
		}

		@Override
		public String toString()
		{
			return "ValueWrapper[value=" + this.value + ",existed=" + this.existed + ",error=" + this.error + "]";
		}
	}
}
