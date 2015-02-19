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


import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;


public class FireworkBattery extends CItem {
	
	public FireworkBattery(String originalName, ChatColor color, String lDescription, long lCooldown, Material mat) {
		super(originalName, color, lDescription, lCooldown, mat);
		triggers.add(Trigger.BLOCK_PLACED);

	}

	@Override
	public boolean effect(Event event, final Player player) {
			BlockPlaceEvent e = (BlockPlaceEvent) event;
			Location loc = e.getBlock().getLocation();
			final Location startLoc = loc.clone();
			loc.setY(loc.getY()+1);
			loc.setX(loc.getX()+0.5);
			loc.setZ(loc.getZ()+0.5);
			final Location l = loc;
			final World w = l.getWorld();
			final Material m = e.getBlock().getType();
			new BukkitRunnable() {
				int fireworks = 400;
				@Override
				public void run() {
					if(startLoc.getBlock().getType().equals(m) && fireworks > 0) {
						w.createExplosion(l, 0f);
						final Firework fw = (Firework) w.spawnEntity(l, EntityType.FIREWORK);
						FireworkMeta fm = fw.getFireworkMeta();
						Random r = new Random();
						int type = r.nextInt(4) + 1;
						int colors = r.nextInt(3) + 1;
						Type ft = null;
						Color c1 = null;
						Color c2 = null;
						switch (type) {
							case 1:
								ft = Type.BALL;
								break;
							case 2:
								ft = Type.BALL_LARGE;
								break;
							case 3:
								ft = Type.BURST;
								break;
							case 4:
								ft = Type.STAR;
								break;
						}
						switch (colors) {
							case 1:
								c1 = Color.RED;
								c2 = Color.ORANGE;
								break;
							case 2:
								c1 = Color.BLUE;
								c2 = Color.TEAL;
								break;
							case 3:
								c1 = Color.GREEN;
								c2 = Color.LIME;
								break;
						}
						FireworkEffect effect =  FireworkEffect.builder().flicker(true).withColor(c1).withFade(c2).trail(true).with(ft).trail(true).build();
						fm.addEffect(effect);
						fm.setPower(r.nextInt(2) + 2);
						fw.setFireworkMeta(fm);
						Vector v = fw.getVelocity();
						fw.setVelocity(new Vector(v.getX() + (r.nextDouble() - r.nextDouble())*0.01, v.getY(), v.getZ() + (r.nextDouble() - r.nextDouble())*0.01));

						fireworks--;
					}
				}
		}.runTaskTimer(getPlugin(), 20l, 10l);
			
		return false;
	}

	@Override
	public void initConfigEntries() {
	}

}
