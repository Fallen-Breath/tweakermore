package me.fallenbreath.tweakermore.util;

import me.fallenbreath.tweakermore.TweakerMoreMod;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class RegistryUtil
{
	public static Identifier id(String path)
	{
		return new Identifier(TweakerMoreMod.MOD_ID, path);
	}

	public static String getItemId(Item item)
	{
		return Registry.ITEM.getId(item).toString();
	}
}
