package me.fallenbreath.tweakermore.config;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.util.StringUtils;
import me.fallenbreath.conditionalmixin.api.value.ModPredicate;
import me.fallenbreath.conditionalmixin.api.value.ModRestriction;
import me.fallenbreath.tweakermore.config.options.TweakerMoreIConfigBase;
import me.fallenbreath.tweakermore.util.ModIds;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

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

	private static Optional<String> getFooter(Collection<ModPredicate> modPredicates, boolean good, String footerTextKey)
	{
		if (modPredicates.size() > 0)
		{
			List<String> elements = Lists.newArrayList();
			for (ModPredicate modPredicate : modPredicates)
			{
				String element = StringUtils.translate("tweakermore.util.mod." + modPredicate.modId) + modPredicate.getVersionPredicatesString();
				if ((good && !modPredicate.isSatisfied()) || (!good && modPredicate.isSatisfied()))
				{
					element = GuiBase.TXT_RED + GuiBase.TXT_ITALIC + element + GuiBase.TXT_GRAY + GuiBase.TXT_ITALIC;
				}
				elements.add(element);
			}
			String footer = StringUtils.translate(footerTextKey, Joiner.on(", ").join(elements));
			footer = GuiBase.TXT_GRAY + GuiBase.TXT_ITALIC + footer + GuiBase.TXT_RST;
			return Optional.of(footer);
		}
		return Optional.empty();
	}

	public List<String> getModRelationsFooter()
	{
		List<String> result = Lists.newArrayList();
		getFooter(this.modRestriction.getRequirements(), true, "tweakermore.gui.mod_relation_footer.requirement").ifPresent(result::add);
		getFooter(this.modRestriction.getConflictions(), false, "tweakermore.gui.mod_relation_footer.confliction").ifPresent(result::add);
		return result;
	}
}
