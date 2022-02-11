package me.fallenbreath.tweakermore.config;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.util.StringUtils;
import me.fallenbreath.tweakermore.util.dependency.Condition;
import me.fallenbreath.tweakermore.util.dependency.ModPredicate;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TweakerMoreOption
{
	private final Config annotation;
	private final IConfigBase option;
	private final List<ModPredicate> modRequirements;
	private final List<ModPredicate> modConflictions;
	private final boolean enable;

	public TweakerMoreOption(Config annotation, IConfigBase option)
	{
		this.annotation = annotation;
		this.option = option;
		this.modRequirements = this.generateRequirement(this.annotation.strategy().enableWhen());
		this.modConflictions = this.generateRequirement(this.annotation.strategy().disableWhen());
		this.enable = this.modRequirements.stream().allMatch(ModPredicate::satisfies) && this.modConflictions.stream().noneMatch(ModPredicate::satisfies);
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
		return this.enable;
	}

	public IConfigBase getOption()
	{
		return this.option;
	}

	private List<ModPredicate> generateRequirement(Condition[] conditions)
	{
		return Arrays.stream(conditions).filter(c -> c.type() == Condition.Type.MOD).map(ModPredicate::of).collect(Collectors.toList());
	}

	private Optional<String> getFooter(Collection<ModPredicate> modPredicates, boolean good, String footerTextKey)
	{
		if (modPredicates.size() > 0)
		{
			List<String> elements = Lists.newArrayList();
			for (ModPredicate requirement : modPredicates)
			{
				String element = StringUtils.translate("tweakermore.util.mod." + requirement.modId) + requirement.getVersionPredicatesString();
				if ((good && !requirement.satisfies()) || (!good && requirement.satisfies()))
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
		this.getFooter(this.modRequirements, true, "tweakermore.gui.mod_relation_footer.requirement").ifPresent(result::add);
		this.getFooter(this.modConflictions, false, "tweakermore.gui.mod_relation_footer.confliction").ifPresent(result::add);
		return result;
	}
}
