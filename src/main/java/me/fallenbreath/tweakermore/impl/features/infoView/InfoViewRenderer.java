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

package me.fallenbreath.tweakermore.impl.features.infoView;

import com.google.common.collect.Lists;
import fi.dy.masa.malilib.event.TickHandler;
import fi.dy.masa.malilib.interfaces.IClientTickHandler;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.impl.features.infoView.beacon.BeaconEffectRenderer;
import me.fallenbreath.tweakermore.impl.features.infoView.cache.InfoViewCachedRenderer;
import me.fallenbreath.tweakermore.impl.features.infoView.commandBlock.CommandBlockContentRenderer;
import me.fallenbreath.tweakermore.impl.features.infoView.comparator.ComparatorLevelRenderer;
import me.fallenbreath.tweakermore.impl.features.infoView.growthSpeed.GrowthSpeedRenderer;
import me.fallenbreath.tweakermore.impl.features.infoView.hopper.HopperCooldownRenderer;
import me.fallenbreath.tweakermore.impl.features.infoView.redstoneDust.RedstoneDustUpdateOrderRenderer;
import me.fallenbreath.tweakermore.impl.features.infoView.respawnBlock.RespawnBlockExplosionViewer;
import me.fallenbreath.tweakermore.impl.features.infoView.structureBlock.StructureBlockContentRenderer;
import me.fallenbreath.tweakermore.util.render.TweakerMoreIRenderer;
import me.fallenbreath.tweakermore.util.render.context.WorldRenderContextImpl;
import net.minecraft.client.MinecraftClient;

import java.util.List;
import java.util.stream.Collectors;

public class InfoViewRenderer implements TweakerMoreIRenderer, IClientTickHandler
{
	private static final InfoViewRenderer INSTANCE = new InfoViewRenderer();
	private static final List<InfoViewer> CONTENT_PREVIEWERS = Lists.newArrayList(
			new BeaconEffectRenderer(),
			new CommandBlockContentRenderer(),
			new ComparatorLevelRenderer(),
			new HopperCooldownRenderer(),
			new GrowthSpeedRenderer(),
			new RedstoneDustUpdateOrderRenderer(),
			new RespawnBlockExplosionViewer(),
			new StructureBlockContentRenderer()
	);

	private final InfoViewCachedRenderer cachedRenderer = new InfoViewCachedRenderer();

	private InfoViewRenderer()
	{
		TickHandler.getInstance().registerClientTickHandler(this);
	}

	public static InfoViewRenderer getInstance()
	{
		return INSTANCE;
	}

	@Override
	public void onRenderWorldLast(WorldRenderContextImpl context)
	{
		if (!TweakerMoreConfigs.INFO_VIEW.getBooleanValue())
		{
			return;
		}

		List<InfoViewer> viewers = CONTENT_PREVIEWERS.stream().
				filter(InfoViewer::isRenderEnabled).
				collect(Collectors.toList());
		if (viewers.isEmpty())
		{
			return;
		}

		viewers.forEach(InfoViewer::onInfoViewStart);
		this.cachedRenderer.render(context, viewers);
		viewers.forEach(InfoViewer::onInfoViewEnd);
	}

	@Override
	public void onClientTick(MinecraftClient client)
	{
		this.cachedRenderer.onClientTick();
		CONTENT_PREVIEWERS.forEach(InfoViewer::onClientTick);
	}
}
