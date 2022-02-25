package me.fallenbreath.tweakermore.config;

import com.google.common.collect.Lists;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.util.StringUtils;
import me.fallenbreath.conditionalmixin.util.ModPredicate;
import me.fallenbreath.conditionalmixin.util.ModRestriction;
import me.fallenbreath.tweakermore.config.options.TweakerMoreIConfigBase;
import me.fallenbreath.tweakermore.util.ModIds;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class TweakerMoreOption
{
	private final Config annotation;
	private final TweakerMoreIConfigBase config;
	private final ModRestriction modRestriction;
	private final ModRestriction minecraftRestriction;

	public TweakerMoreOption(Config annotation, TweakerMoreIConfigBase config)
	{
		this.annotation = annotation;
		this.config = config;
		this.modRestriction = ModRestriction.of(annotation.restriction());
		this.minecraftRestriction = ModRestriction.of(annotation.restriction(), c -> ModIds.minecraft.equals(c.value()));
	}

	public Config.Type getType()
	{
		return this.annotation.value();
	}

	public Config.Category getCategory()
	{
		return this.annotation.category();
	}

	public ModRestriction getModRestriction()
	{
		return this.modRestriction;
	}

	public boolean isEnabled()
	{
		return this.modRestriction.isSatisfied();
	}

	public boolean worksForCurrentMCVersion()
	{
		return this.minecraftRestriction.isSatisfied();
	}

	public boolean isDebug()
	{
		return this.annotation.debug();
	}

	public boolean isDevOnly()
	{
		return this.annotation.devOnly();
	}

	public TweakerMoreIConfigBase getConfig()
	{
		return this.config;
	}

	private static List<String> getFooter(Collection<ModPredicate> modPredicates, boolean good, String footerTextKey)
	{
		if (modPredicates.size() > 0)
		{
			List<String> lines = Lists.newArrayList();
			lines.add(GuiBase.TXT_GRAY + GuiBase.TXT_ITALIC + StringUtils.translate(footerTextKey) + GuiBase.TXT_RST);
			for (ModPredicate modPredicate : modPredicates)
			{
				String element = String.format("%s (%s) %s", StringUtils.translate("tweakermore.util.mod." + modPredicate.modId), modPredicate.modId, modPredicate.getVersionPredicatesString());
				String lineItem = GuiBase.TXT_GRAY + GuiBase.TXT_ITALIC + element;
				if ((good && !modPredicate.isSatisfied()) || (!good && modPredicate.isSatisfied()))
				{
					lineItem = GuiBase.TXT_RED + GuiBase.TXT_ITALIC + lineItem + GuiBase.TXT_GRAY + GuiBase.TXT_ITALIC;
				}
				lines.add(GuiBase.TXT_DARK_GRAY + GuiBase.TXT_ITALIC + "- " + lineItem);
			}
			return lines;
		}
		return Collections.emptyList();
	}

	public List<String> getModRelationsFooter()
	{
		List<String> result = Lists.newArrayList();
		result.addAll(getFooter(this.modRestriction.getRequirements(), true, "tweakermore.gui.mod_relation_footer.requirement"));
		result.addAll(getFooter(this.modRestriction.getConflictions(), false, "tweakermore.gui.mod_relation_footer.confliction"));
		return result;
	}
}
