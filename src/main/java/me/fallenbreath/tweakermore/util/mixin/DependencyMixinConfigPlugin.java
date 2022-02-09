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

	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName)
	{
		return this.checkDependency(mixinClassName);
	}

	private boolean checkDependency(String mixinClassName)
	{
		return this.dependencyMemory.computeIfAbsent(mixinClassName, key ->
		{
			AnnotationNode modRequire = getDependencyAnnotation(mixinClassName);
			if (modRequire != null)
			{
				List<String> modIds = Annotations.getValue(modRequire, "value");
				for (String modId : modIds)
				{
					if (!FabricUtil.isModLoaded(modId))
					{
						this.onDependencyCheckFailed(mixinClassName, modIds, modId);
						return false;
					}
				}
				return true;
			}
			return true;
		});
	}

	protected void onDependencyCheckFailed(String mixinClassName, List<String> requiredModIds, String failedModId)
	{
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
}
