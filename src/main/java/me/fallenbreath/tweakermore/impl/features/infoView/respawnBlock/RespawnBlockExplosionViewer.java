package me.fallenbreath.tweakermore.impl.features.infoView.respawnBlock;

import com.google.common.collect.ImmutableList;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.impl.features.infoView.AbstractInfoViewer;
import me.fallenbreath.tweakermore.impl.features.infoView.respawnBlock.handler.BedHandler;
import me.fallenbreath.tweakermore.impl.features.infoView.respawnBlock.handler.BlockHandler;
import me.fallenbreath.tweakermore.impl.features.infoView.respawnBlock.handler.RespawnAnchorHandler;
import me.fallenbreath.tweakermore.util.damage.DamageCalculator;
import me.fallenbreath.tweakermore.util.Messenger;
import me.fallenbreath.tweakermore.util.TemporaryBlockReplacer;
import me.fallenbreath.tweakermore.util.damage.DamageUtil;
import me.fallenbreath.tweakermore.util.render.RenderContext;
import me.fallenbreath.tweakermore.util.render.TextRenderer;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.text.BaseText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class RespawnBlockExplosionViewer extends AbstractInfoViewer
{
	private static final List<BlockHandler> BLOCK_HANDLERS = ImmutableList.of(
			new BedHandler(),
			new RespawnAnchorHandler()
	);

	public RespawnBlockExplosionViewer()
	{
		super(TweakerMoreConfigs.INFO_VIEW_RESPAWN_BLOCK_EXPLOSION, TweakerMoreConfigs.INFO_VIEW_RESPAWN_BLOCK_EXPLOSION_STRATEGY);
	}

	@Nullable
	private static Formatting stagedColor(float value, float[] bounds, Formatting[] formattings)
	{
		if (bounds.length == formattings.length)
		{
			for (int i = 0; i < bounds.length; i++)
			{
				if (value <= bounds[i])
				{
					return formattings[i];
				}
			}
		}
		return null;
	}

	@Override
	public void render(RenderContext context, World world, BlockPos blockPos, BlockState blockState, BlockEntity blockEntity)
	{
		MinecraftClient mc = MinecraftClient.getInstance();
		ClientWorld clientWorld = mc.world;
		if (clientWorld == null || mc.player == null)
		{
			return;
		}
		Optional<BlockHandler> optionalBlockHandler = BLOCK_HANDLERS.stream().
				filter(handler -> handler.worksFor(world, blockPos, blockState)).
				findFirst();
		if (!optionalBlockHandler.isPresent())
		{
			return;
		}

		BlockHandler handler = optionalBlockHandler.get();
		Vec3d explosionCenter = handler.getExplosionCenter(blockPos);

		TemporaryBlockReplacer replacer = new TemporaryBlockReplacer(clientWorld);
		handler.addBlocksToRemove(clientWorld, blockPos, blockState, replacer);
		replacer.removeBlocks();
		DamageCalculator calculator = DamageCalculator.explosion(explosionCenter, handler.getExplosionPower(), mc.player).
				applyDifficulty(world.getDifficulty()).
				applyArmorAndResistanceAndEnchantment();
		replacer.restoreBlocks();

		float amount = calculator.getDamageAmount();
		calculator.applyAbsorption();
		float remainingHealth = calculator.getEntityHealthAfterDeal();

		Formatting amountFmt = stagedColor(
				remainingHealth,
				new float[]{0.0F, mc.player.getMaximumHealth() * 0.2F},
				new Formatting[]{Formatting.RED, Formatting.GOLD}
		);
		Formatting lineFmt = stagedColor(
				amount,
				new float[]{1E-6F, DamageUtil.modifyDamageForDifficulty(1.0F, world.getDifficulty(), calculator.getDamageSource())},
				new Formatting[]{Formatting.DARK_GRAY, Formatting.GRAY}
		);

		Function<Float, BaseText> float2text = hp -> {
			BaseText text = Messenger.s(String.format("%.2f", hp));
			if (amountFmt != null)
			{
				Messenger.formatting(text, amountFmt);
			}
			return text;
		};
		Function<BaseText, BaseText> lineModifier = text -> {
			if (amountFmt == null && lineFmt != null)
			{
				Messenger.formatting(text, lineFmt);
			}
			return text;
		};

		BaseText line1 = Messenger.tr("tweakermore.config.infoViewRespawnBlockExplosion.message.damage", float2text.apply(amount));
		BaseText line2 = Messenger.c("-> ", float2text.apply(remainingHealth), "HP");
		TextRenderer.create().at(explosionCenter).
				addLine(lineModifier.apply(line1)).
				addLine(lineModifier.apply(line2)).
				bgColor(0x1F000000).
				seeThrough().shadow().
				render();
	}

	@Override
	public boolean shouldRenderFor(World world, BlockPos blockPos, BlockState blockState)
	{
		for (BlockHandler handler : BLOCK_HANDLERS)
		{
			if (handler.worksFor(world, blockPos, blockState))
			{
				return true;
			}
		}
		return false;
	}
}
