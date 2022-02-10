package me.fallenbreath.tweakermore.util.mixin;

import com.google.common.collect.Maps;
import me.fallenbreath.tweakermore.util.FabricUtil;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.service.MixinService;
import org.spongepowered.asm.util.Annotations;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public abstract class DependencyMixinConfigPlugin implements IMixinConfigPlugin
{
	private final Map<String, Boolean> dependencyMemory = Maps.newHashMap();
	private String failedReason;

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
				if (!enableConditions.isEmpty() && !this.checkCondition(enableConditions))
				{
					this.onDependencyCheckFailed(mixinClassName, this.failedReason);
					return false;
				}
				List<AnnotationNode> disableConditions = Annotations.getValue(dependency, "disableWhen", true);
				if (!disableConditions.isEmpty() && this.checkCondition(disableConditions))
				{
					this.onDependencyCheckFailed(mixinClassName, this.failedReason);
					return false;
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
			return Annotations.getVisible(classNode, ModRequire.class);
		}
		catch (ClassNotFoundException | IOException e)
		{
			return null;
		}
	}

	private boolean checkCondition(List<AnnotationNode> conditions)
	{
		for (AnnotationNode condition : conditions)
		{
			Condition.Type type = Annotations.getValue(condition, "type", Condition.Type.class, Condition.Type.MOD);
			switch (type)
			{
				case MOD:
					String modId = Annotations.getValue(condition, "value");
					if (!FabricUtil.isModLoaded(modId))
					{
						this.failedReason = String.format("mod requirement %s not found", modId);
						return false;
					}
					this.failedReason = String.format("conflicted mod %s found", modId);
					break;

				case MIXIN:
					String className = Annotations.getValue(condition, "value");
					if (!this.checkDependency(className))
					{
						this.failedReason = String.format("required mixin class %s disabled", className);
						return false;
					}
					this.failedReason = String.format("conflicted mixin class %s found", className);
					break;
			}
		}
		return true;
	}

	protected void onDependencyCheckFailed(String mixinClassName, String reason)
	{
	}
}
