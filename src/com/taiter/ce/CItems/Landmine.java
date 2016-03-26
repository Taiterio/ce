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
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.metadata.FixedMetadataValue;

import com.taiter.ce.Main;
import com.taiter.ce.Tools;



public class Landmine extends CItem {

	int ExplosionStrength;
	
	public Landmine(String originalName, ChatColor color, String lDescription, long lCooldown, Material mat) {
		super(originalName, color, lDescription, lCooldown, mat);
		this.configEntries.put("ExplosionStrength", 5);
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
		} else if(event instanceof PlayerMoveEvent) {
			
			World w = player.getWorld();;
			w.playEffect(player.getLocation(), Effect.CLICK1, 5);
			
			String[] s = player.getMetadata("ce.mine").get(0).asString().split(" ");
			Location loc = new Location(player.getWorld(), Integer.parseInt(s[0]), Integer.parseInt(s[1]), Integer.parseInt(s[2]));
			Block b = loc.getBlock();
			b.removeMetadata("ce.mine", main);
			b.getRelative(0,1,0).removeMetadata("ce.mine.secondary", main);
			player.removeMetadata("ce.mine", main);
			
			if(!b.getType().equals(Material.AIR) && !b.getType().equals(Material.WATER) && !b.getType().equals(Material.LAVA) && Tools.checkWorldGuard(loc, player, "PVP", false)) { 
				b.setType(Material.AIR);
				if(Main.createExplosions)
					w.createExplosion(loc, ExplosionStrength);
			}
		}
		return false;
	}

	@Override
	public void initConfigEntries() {
		ExplosionStrength = Integer.parseInt(getConfig().getString("Items." + getOriginalName() + ".ExplosionStrength"));
	}

}
