package me.fallenbreath.tweakermore.util.dependency;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import me.fallenbreath.tweakermore.util.FabricUtil;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.Version;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.service.MixinService;
import org.spongepowered.asm.util.Annotations;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public abstract class DependencyMixinConfigPlugin implements IMixinConfigPlugin
{
	private final Map<String, Boolean> dependencyMemory = Maps.newHashMap();

	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName)
	{
		return this.checkDependency(mixinClassName);
	}

	private boolean checkDependency(String mixinClassName)
	{
		return this.dependencyMemory.computeIfAbsent(mixinClassName, key ->
		{
			AnnotationNode dependency = getDependencyAnnotation(mixinClassName);
			if (dependency != null)
			{
				List<AnnotationNode> enableConditions = Annotations.getValue(dependency, "enableWhen", true);
				for (Result result : this.checkConditions(enableConditions))
				{
					if (!result.success)
					{
						this.onDependencyCheckFailed(mixinClassName, result.reason);
						return false;
					}
				}
				List<AnnotationNode> disableConditions = Annotations.getValue(dependency, "disableWhen", true);
				for (Result result : this.checkConditions(disableConditions))
				{
					if (result.success)
					{
						this.onDependencyCheckFailed(mixinClassName, result.reason);
						return false;
					}
				}
			}
			return true;
		});
	}

	@Nullable
	private AnnotationNode getDependencyAnnotation(String className)
	{
		try
		{
			ClassNode classNode = MixinService.getService().getBytecodeProvider().getClassNode(className);
			return Annotations.getVisible(classNode, Strategy.class);
		}
		catch (ClassNotFoundException | IOException e)
		{
			return null;
		}
	}

	private List<Result> checkConditions(List<AnnotationNode> conditions)
	{
		List<Result> results = Lists.newArrayList();
		for (AnnotationNode condition : conditions)
		{
			Condition.Type type = Annotations.getValue(condition, "type", Condition.Type.class, Condition.Type.MOD);
			switch (type)
			{
				case MOD:
					String modId = Annotations.getValue(condition, "value");
					Objects.requireNonNull(modId);
					Optional<ModContainer> modContainer = FabricLoader.getInstance().getModContainer(modId);
					if (!modContainer.isPresent())
					{
						results.add(new Result(false, String.format("required mod %s not found", modId)));
						continue;
					}
					Version modVersion = modContainer.get().getMetadata().getVersion();
					List<String> versionPredicates = Lists.newArrayList(Annotations.getValue(condition, "versionPredicates", Lists.newArrayList()));
					if (!FabricUtil.doesVersionFitsPredicate(modVersion, versionPredicates))
					{
						results.add(new Result(false, String.format("mod %s@%s does not matches version predicates %s", modId, modVersion.getFriendlyString(), versionPredicates)));
						continue;
					}
					results.add(new Result(true, String.format("conflicted/unsupported mod %s@%s found", modId, modVersion.getFriendlyString())));
					break;

				case MIXIN:
					String className = Annotations.getValue(condition, "value");
					if (!this.checkDependency(className))
					{
						results.add(new Result(false, String.format("required mixin class %s disabled", className)));
						continue;
					}
					results.add(new Result(true, String.format("conflicted mixin class %s found", className)));
					break;
			}
		}
		return results;
	}

	protected void onDependencyCheckFailed(String mixinClassName, String reason)
	{
	}

	private static class Result
	{
		public final boolean success;
		public final String reason;

		private Result(boolean success, String reason)
		{
			this.success = success;
			this.reason = reason;
		}
	}
}
