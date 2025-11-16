/*
 * This file is part of the TweakerMore project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2023  Fallen_Breath and contributors
 *
 * TweakerMore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * TweakerMore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with TweakerMore.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.fallenbreath.tweakermore.impl.features.infoView.respawnBlock;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.impl.features.infoView.CommonScannerInfoViewer;
import me.fallenbreath.tweakermore.impl.features.infoView.cache.RenderVisitorWorldView;
import me.fallenbreath.tweakermore.impl.features.infoView.respawnBlock.handler.AbstractBlockHandler;
import me.fallenbreath.tweakermore.impl.features.infoView.respawnBlock.handler.BedHandler;
import me.fallenbreath.tweakermore.impl.features.infoView.respawnBlock.handler.RespawnAnchorHandler;
import me.fallenbreath.tweakermore.util.Messenger;
import me.fallenbreath.tweakermore.util.TemporaryBlockReplacer;
import me.fallenbreath.tweakermore.util.damage.DamageCalculator;
import me.fallenbreath.tweakermore.util.damage.DamageUtil;
import me.fallenbreath.tweakermore.util.render.TextRenderer;
import me.fallenbreath.tweakermore.util.render.context.WorldRenderContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.BaseComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class RespawnBlockExplosionViewer extends CommonScannerInfoViewer
{
	private static final List<BlockHandlerProvider> BLOCK_HANDLER_FACTORIES = ImmutableList.of(
			BedHandler::new,
			RespawnAnchorHandler::new
	);

	private final LongOpenHashSet renderedKeys = new LongOpenHashSet();
	private final Map<Vec3, DamageCache> damageCache = Maps.newHashMap();

	public RespawnBlockExplosionViewer()
	{
		super(
				TweakerMoreConfigs.INFO_VIEW_RESPAWN_BLOCK_EXPLOSION,
				TweakerMoreConfigs.INFO_VIEW_RESPAWN_BLOCK_EXPLOSION_RENDER_STRATEGY,
				TweakerMoreConfigs.INFO_VIEW_RESPAWN_BLOCK_EXPLOSION_TARGET_STRATEGY
		);
	}

	@Nullable
	private static ChatFormatting stagedColor(float value, float[] bounds, ChatFormatting[] formattings)
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
	public boolean shouldRenderFor(RenderVisitorWorldView world, BlockPos pos)
	{
		for (BlockHandlerProvider factory : BLOCK_HANDLER_FACTORIES)
		{
			if (factory.construct(world, pos, world.getBlockState(pos)).isValid())
			{
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean requireBlockEntitySyncing(RenderVisitorWorldView world, BlockPos pos)
	{
		return false;
	}

	@Override
	protected void render(WorldRenderContext context, RenderVisitorWorldView world, BlockPos pos, boolean isCrossHairPos)
	{
		Minecraft mc = Minecraft.getInstance();
		ClientLevel clientWorld = mc.level;
		if (clientWorld == null || mc.player == null)
		{
			return;
		}
		Optional<AbstractBlockHandler> optionalBlockHandler = BLOCK_HANDLER_FACTORIES.stream().
				map(fac -> fac.construct(world, pos, world.getBlockState(pos))).
				filter(AbstractBlockHandler::isValid).
				findFirst();
		if (!optionalBlockHandler.isPresent())
		{
			return;
		}

		AbstractBlockHandler handler = optionalBlockHandler.get();
		if (!this.renderedKeys.add(handler.getDeduplicationKey().asLong()))
		{
			return;
		}

		DamageCache cache = this.damageCache.computeIfAbsent(
				handler.getExplosionCenter(),
				explosionCenter -> {
					TemporaryBlockReplacer replacer = new TemporaryBlockReplacer(clientWorld);
					handler.addBlocksToRemove(replacer);
					replacer.removeBlocks();
					DamageCalculator calculator = DamageCalculator.explosion(explosionCenter, handler.getExplosionPower(), mc.player);
					replacer.restoreBlocks();

					Level bestWorld = world.getBestWorld();
					if (bestWorld instanceof ServerLevel)
					{
						calculator.setServerWorld((ServerLevel)bestWorld);
					}

					calculator.applyDifficulty(bestWorld.getDifficulty());
					float baseAmount = calculator.getDamageAmount();
					calculator.applyArmorAndResistanceAndEnchantment();
					float appliedAmount = calculator.getDamageAmount();
					calculator.applyAbsorption();
					float remainingHealth = calculator.getEntityHealthAfterDeal();

					return new DamageCache(calculator.getDamageSource(), baseAmount, appliedAmount, remainingHealth);
				});

		ChatFormatting amountFmt = stagedColor(
				cache.remainingHealth,
				new float[]{0.0F, mc.player.getMaxHealth() * 0.2F},
				new ChatFormatting[]{ChatFormatting.RED, ChatFormatting.GOLD}
		);
		ChatFormatting lineFmt = stagedColor(
				cache.baseAmount,
				new float[]{1E-6F, DamageUtil.modifyDamageForDifficulty(1.0F, world.getBestWorld().getDifficulty(), cache.damageSource)},
				new ChatFormatting[]{ChatFormatting.DARK_GRAY, ChatFormatting.GRAY}
		);

		Function<Float, BaseComponent> float2text = hp -> {
			BaseComponent text = Messenger.s(String.format("%.2f", hp));
			if (amountFmt != null)
			{
				Messenger.formatting(text, amountFmt);
			}
			return text;
		};
		Function<BaseComponent, BaseComponent> lineModifier = text -> {
			if (amountFmt == null && lineFmt != null)
			{
				Messenger.formatting(text, lineFmt);
			}
			return text;
		};

		BaseComponent line1 = Messenger.tr("tweakermore.impl.infoViewRespawnBlockExplosion.damage", float2text.apply(cache.appliedAmount));
		BaseComponent line2 = Messenger.c("-> ", float2text.apply(cache.remainingHealth), "HP");
		double alpha = TweakerMoreConfigs.INFO_VIEW_RESPAWN_BLOCK_EXPLOSION_TEXT_ALPHA.getDoubleValue();
		if (alpha > 0)
		{
			int textAlphaBits = ((int)Math.round(0xFF * alpha) & 0xFF) << 24;
			int bgAlphaBits = ((int)Math.round(0x1F * alpha) & 0xFF) << 24;
			//noinspection PointlessBitwiseExpression
			TextRenderer.create().
					at(handler.getTextPosition()).
					addLine(lineModifier.apply(line1)).
					addLine(lineModifier.apply(line2)).
					color(0x00FFFFFF | textAlphaBits, 0x00000000 | bgAlphaBits).
					seeThrough().shadow().
					render();
		}
	}

	@Override
	public void onInfoViewStart()
	{
		this.renderedKeys.clear();
	}

	@Override
	public void onClientTick()
	{
		this.damageCache.clear();
	}

	@FunctionalInterface
	private interface BlockHandlerProvider
	{
		AbstractBlockHandler construct(RenderVisitorWorldView world, BlockPos blockPos, BlockState blockState);
	}

	private static class DamageCache
	{
		public final DamageSource damageSource;
		public final float baseAmount;
		public final float appliedAmount;
		public final float remainingHealth;

		public DamageCache(DamageSource damageSource, float baseAmount, float appliedAmount, float remainingHealth)
		{
			this.damageSource = damageSource;
			this.baseAmount = baseAmount;
			this.appliedAmount = appliedAmount;
			this.remainingHealth = remainingHealth;
		}
	}
}
