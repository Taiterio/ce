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
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;



public class PoisonIvy extends CItem {

	int PoisonDuration;
	int PoisonLevel;

	
	public PoisonIvy(String originalName, ChatColor color, String lDescription, long lCooldown, Material mat) {
		super(originalName, color, lDescription, lCooldown, mat);
		this.configEntries.put("PoisonDuration", 100);
		this.configEntries.put("PoisonLevel", 2);
		triggers.add(Trigger.BLOCK_PLACED);
		triggers.add(Trigger.MOVE);
	}

	@Override
	public boolean effect(Event event, Player player) {
		if(event instanceof BlockPlaceEvent) {
			BlockPlaceEvent e = (BlockPlaceEvent) event;
			e.getBlock().setMetadata("ce.mine", new FixedMetadataValue(main, getOriginalName()));
		} else if(event instanceof PlayerMoveEvent) {
			if(!player.hasPotionEffect(PotionEffectType.POISON)) {
				player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, PoisonDuration, PoisonLevel));
				player.sendMessage(ChatColor.DARK_GREEN + "You have touched Poison Ivy!");
			}
		}
		return false;
	}

	@Override
	public void initConfigEntries() {
		PoisonLevel = Integer.parseInt(getConfig().getString("Items." + getOriginalName() + ".PoisonLevel"))-1;
		PoisonDuration = Integer.parseInt(getConfig().getString("Items." + getOriginalName() + ".PoisonDuration"));
	}

}
