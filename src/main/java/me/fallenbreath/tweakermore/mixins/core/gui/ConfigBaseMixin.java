package me.fallenbreath.tweakermore.mixins.core.gui;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.options.ConfigBase;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.util.StringUtils;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.util.FabricUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ConfigBase.class)
public abstract class ConfigBaseMixin
{
	@Inject(method = "getComment", at = @At("TAIL"), cancellable = true, remap = false)
	private void appendModRequirementHeader(CallbackInfoReturnable<String> cir)
	{
		TweakerMoreConfigs.getOptionFromConfig((IConfigBase)this).ifPresent(tweakerMoreOption -> {
			String[] modIds = tweakerMoreOption.getRequiredModIds();
			if (modIds.length > 0)
			{
				List<String> modNames = Lists.newArrayList();
				for (String modId : modIds)
				{
					String modName = StringUtils.translate("tweakermore.gui.mod_requirement.mod." + modId);
					if (!FabricUtil.isModLoaded(modId))
					{
						modName = GuiBase.TXT_RED + GuiBase.TXT_ITALIC + modId + GuiBase.TXT_GRAY + GuiBase.TXT_ITALIC;
					}
					modNames.add(modName);
				}
				String footer = StringUtils.translate("tweakermore.gui.mod_requirement.footer", Joiner.on(", ").join(modNames));
				footer = GuiBase.TXT_GRAY + GuiBase.TXT_ITALIC + footer + GuiBase.TXT_RST;
				cir.setReturnValue(cir.getReturnValue() + "\n" + footer);
			}
		});
	}
}
