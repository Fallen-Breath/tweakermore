package me.fallenbreath.tweakermore.util;

import java.lang.reflect.Field;
import java.util.Optional;
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
}
