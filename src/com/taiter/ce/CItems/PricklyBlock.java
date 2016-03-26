package com.taiter.ce.CItems;

/*
* This file is part of Custom Enchantments
* Copyright (C) Taiterio 2015
*
* This program is free software: you can redistribute it and/or modify it
* under the terms of the GNU Lesser General Public License as published by the
* Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful, but WITHOUT
* ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
* FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
* for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/


import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;


public class PricklyBlock extends CItem {

	int Damage;
	int NauseaLevel;
	int NauseaDuration;
	
	public PricklyBlock(String originalName, ChatColor color, String lDescription, long lCooldown, Material mat) {
		super(originalName, color, lDescription, lCooldown, mat);
		this.configEntries.put("Damage", 3);
		this.configEntries.put("NauseaLevel", 2);
		this.configEntries.put("NauseaDuration", 200);
		triggers.add(Trigger.BLOCK_PLACED);
		triggers.add(Trigger.MOVE);
	}

	@Override
	public boolean effect(Event event, Player player) {
		if(event instanceof BlockPlaceEvent) {
			BlockPlaceEvent e = (BlockPlaceEvent) event;
			Block b = e.getBlock();
			b.setMetadata("ce.mine", new FixedMetadataValue(main, getOriginalName()));
			String coord = b.getX() + " " + b.getY() + " " + b.getZ();
			b.getRelative(0,1,0).setMetadata("ce.mine.secondary", new FixedMetadataValue(main, coord));
			b.getRelative(0,-1,0).setMetadata("ce.mine.secondary", new FixedMetadataValue(main, coord));
			b.getRelative(1,0,0).setMetadata("ce.mine.secondary", new FixedMetadataValue(main, coord));
			b.getRelative(0,0,1).setMetadata("ce.mine.secondary", new FixedMetadataValue(main, coord));
			b.getRelative(0,0,-1).setMetadata("ce.mine.secondary", new FixedMetadataValue(main, coord));
			b.getRelative(-1,0,0).setMetadata("ce.mine.secondary", new FixedMetadataValue(main, coord));
		} else if(event instanceof PlayerMoveEvent) {
			if(!player.getGameMode().equals(GameMode.CREATIVE) && !player.hasPotionEffect(PotionEffectType.CONFUSION)) {
				player.damage(Damage);
				player.sendMessage(ChatColor.DARK_GREEN + "A nearbly Block is hurting you!");
				player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, NauseaDuration, NauseaLevel));
			}
		}
		return false;
	}

	@Override
	public void initConfigEntries() {
		Damage = Integer.parseInt(getConfig().getString("Items." + getOriginalName() + ".Damage"));
		NauseaLevel = Integer.parseInt(getConfig().getString("Items." + getOriginalName() + ".NauseaLevel"))-1;
		NauseaDuration = Integer.parseInt(getConfig().getString("Items." + getOriginalName() + ".NauseaDuration"));
	}

}
