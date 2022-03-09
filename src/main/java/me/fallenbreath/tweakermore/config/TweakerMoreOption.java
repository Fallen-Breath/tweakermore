package me.fallenbreath.tweakermore.config;

import com.google.common.collect.Lists;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.util.StringUtils;
import me.fallenbreath.tweakermore.config.options.TweakerMoreIConfigBase;
import me.fallenbreath.tweakermore.util.ModIds;
import me.fallenbreath.tweakermore.util.condition.ModPredicate;
import me.fallenbreath.tweakermore.util.condition.ModRestriction;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TweakerMoreOption
{
	private final Config annotation;
	private final TweakerMoreIConfigBase config;
	private final List<ModRestriction> modRestrictions;
	private final List<ModRestriction> minecraftRestrictions;

	public TweakerMoreOption(Config annotation, TweakerMoreIConfigBase config)
	{
		this.annotation = annotation;
		this.config = config;
		this.modRestrictions = Arrays.stream(annotation.restriction()).map(ModRestriction::of).collect(Collectors.toList());
		this.minecraftRestrictions = Arrays.stream(annotation.restriction()).map(r -> ModRestriction.of(r, c -> ModIds.minecraft.equals(c.value()))).collect(Collectors.toList());
	}

	public Config.Type getType()
	{
		return this.annotation.value();
	}

	public Config.Category getCategory()
	{
		return this.annotation.category();
	}

	public List<ModRestriction> getModRestrictions()
	{
		return this.modRestrictions;
	}

	public boolean isEnabled()
	{
		return this.modRestrictions.isEmpty() || this.modRestrictions.stream().anyMatch(ModRestriction::isSatisfied);
	}

	public boolean worksForCurrentMCVersion()
	{
		return this.minecraftRestrictions.isEmpty() || this.minecraftRestrictions.stream().anyMatch(ModRestriction::isSatisfied);
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

	private static List<String> getFooter(Collection<ModPredicate> modPredicates, boolean nice, boolean good, String footerTextKey)
	{
		if (modPredicates.size() > 0)
		{
			List<String> lines = Lists.newArrayList();
			lines.add((nice ? GuiBase.TXT_GRAY : GuiBase.TXT_RED) + GuiBase.TXT_ITALIC + StringUtils.translate(footerTextKey) + GuiBase.TXT_RST);
			for (ModPredicate modPredicate : modPredicates)
			{
				String element = String.format("%s (%s) %s", StringUtils.translate("tweakermore.util.mod." + modPredicate.modId), modPredicate.modId, modPredicate.getVersionPredicatesString());
				String color;
				if ((good && modPredicate.isSatisfied()) || (!good && !modPredicate.isSatisfied()))  // nice
				{
					color = GuiBase.TXT_GRAY;
				}
				else  // oh no the restriction check might fail due to this
				{
					color = GuiBase.TXT_RED;
				}
				String lineItem = color + GuiBase.TXT_ITALIC + element + GuiBase.TXT_GRAY + GuiBase.TXT_ITALIC;
				lines.add(GuiBase.TXT_DARK_GRAY + GuiBase.TXT_ITALIC + "- " + lineItem);
			}
			return lines;
		}
		return Collections.emptyList();
	}

	public List<String> getModRelationsFooter()
	{
		List<String> result = Lists.newArrayList();
		boolean first = true;
		for (ModRestriction modRestriction : this.modRestrictions)
		{
			if (!first)
			{
				result.add(GuiBase.TXT_DARK_GRAY + GuiBase.TXT_ITALIC + String.format("--- %s ---", StringUtils.translate("tweakermore.gui.mod_relation_footer.or")));
			}
			first = false;
			result.addAll(getFooter(modRestriction.getRequirements(), modRestriction.isRequirementsSatisfied(), true, "tweakermore.gui.mod_relation_footer.requirement"));
			result.addAll(getFooter(modRestriction.getConflictions(), modRestriction.isNoConfliction(), false, "tweakermore.gui.mod_relation_footer.confliction"));
		}
		return result;
	}
}
