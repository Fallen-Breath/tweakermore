package me.fallenbreath.tweakermore.util.compat.modmenu;

//#if MC >= 11600
//$$ import com.terraformersmc.modmenu.api.ConfigScreenFactory;
//$$ import com.terraformersmc.modmenu.api.ModMenuApi;
//#elseif MC >= 11500
import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;
//#else
//$$ import io.github.prospector.modmenu.api.ModMenuApi;
//$$ import me.fallenbreath.tweakermore.TweakerMoreMod;
//$$ import net.minecraft.client.gui.screen.Screen;
//$$ import java.util.function.Function;
//#endif

import me.fallenbreath.tweakermore.gui.TweakerMoreConfigGui;

//#if MC < 11500
//$$ @SuppressWarnings("deprecation")
//#endif
public class ModMenuApiImpl implements ModMenuApi
{
	//#if MC < 11500
	//$$ public String getModId()
	//$$ {
	//$$ 	return TweakerMoreMod.MOD_ID;
	//$$ }
	//#endif

	@Override
	//#if MC >= 11500
	public ConfigScreenFactory<?> getModConfigScreenFactory()
	//#else
	//$$ public Function<Screen, ? extends Screen> getConfigScreenFactory()
	//#endif
	{
		return (screen) -> {
			TweakerMoreConfigGui gui = new TweakerMoreConfigGui();
			gui.setParent(screen);
			return gui;
		};
	}
}
