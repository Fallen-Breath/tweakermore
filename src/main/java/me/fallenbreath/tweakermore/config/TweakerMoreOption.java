package me.fallenbreath.tweakermore.config;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.util.StringUtils;
import me.fallenbreath.tweakermore.util.ModIds;
import me.fallenbreath.tweakermore.util.dependency.ModPredicate;
import me.fallenbreath.tweakermore.util.dependency.ModRestriction;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class TweakerMoreOption
{
	private final Config annotation;
	private final IConfigBase option;
	private final ModRestriction modRestriction;
	private final ModRestriction minecraftRestriction;

	public TweakerMoreOption(Config annotation, IConfigBase option)
	{
		this.annotation = annotation;
		this.option = option;
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

	public IConfigBase getOption()
	{
		return this.option;
	}

	private static Optional<String> getFooter(Collection<ModPredicate> modPredicates, boolean good, String footerTextKey)
	{
		if (modPredicates.size() > 0)
		{
			List<String> elements = Lists.newArrayList();
			for (ModPredicate requirement : modPredicates)
			{
				String element = StringUtils.translate("tweakermore.util.mod." + requirement.modId) + requirement.getVersionPredicatesString();
				if ((good && !requirement.isSatisfied()) || (!good && requirement.isSatisfied()))
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
