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


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.taiter.ce.EffectManager;
import com.taiter.ce.Tools;


public class PiranhaTrap extends CItem {

	int BleedDuration;
	int FishAmount;
	int FishDuration;
	
	public PiranhaTrap(String originalName, ChatColor color, String lDescription, long lCooldown, Material mat) {
		super(originalName, color, lDescription, lCooldown, mat);
		this.configEntries.put("BleedDuration", 120);
		this.configEntries.put("FishAmount", 20);
		this.configEntries.put("FishDuration", 200);
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
				if(!player.hasMetadata("ce.bleed")) {
					Tools.applyBleed(player, BleedDuration);
					player.sendMessage(ChatColor.RED + "You are bitten by piranhas and start bleeding!");
				} else {
					player.removeMetadata("ce.bleed", main);
					Tools.applyBleed(player, BleedDuration * 2);
					player.sendMessage(ChatColor.RED + "You are bitten by piranhas and your bleeding intensifies!");
				}
				
				World w = player.getWorld();;
				w.playEffect(player.getLocation(), Effect.CLICK1, 5);
				
				
				final List<Item> fishList = new ArrayList<Item>();

				final Random rand = new Random();
				Location lo = player.getLocation().getBlock().getRelative(0,1,0).getLocation();
				
				for(int i = 0; i < FishAmount; i++) {
					ItemStack tempFish = new ItemStack(Material.RAW_FISH, 1);
					ItemMeta im = tempFish.getItemMeta();
					im.setDisplayName(i + "");
					tempFish.setItemMeta(im);
					Item tempItem = w.dropItem(lo, tempFish);
					tempItem.setPickupDelay(50000);
					tempItem.setVelocity(new Vector(0.02 * (rand.nextInt(2) == 1 ? -1 : 1), 0.4, 0.02 * (rand.nextInt(2) == 1 ? -1 : 1)));
					fishList.add(tempItem);
				}
				
				String[] s = player.getMetadata("ce.mine").get(0).asString().split(" ");
				Location loc = new Location(player.getWorld(), Integer.parseInt(s[0]), Integer.parseInt(s[1]), Integer.parseInt(s[2]));
				Block b = loc.getBlock();
				b.removeMetadata("ce.mine", main);
				b.getRelative(0,1,0).removeMetadata("ce.mine.secondary", main);
				player.removeMetadata("ce.mine", main);
				if(!b.getType().equals(Material.AIR)) {
					b.setType(Material.AIR);


				new BukkitRunnable() {
					int maxTime = FishDuration;

					@Override
					public void run() {
						if (maxTime >= 0) {
							for(Item fish : fishList) {
								EffectManager.playSound(fish.getLocation(), "BLOCK_WATER_AMBIENT", 0.1f, 0.5f);
								Vector vel = new Vector(0.02 * (rand.nextInt(2) == 1 ? -1 : 1), 0.4, 0.02 * (rand.nextInt(2) == 1 ? -1 : 1));
								fish.setVelocity(vel);
							}
							maxTime--;
						} else {
							for(Item fish : fishList) {
								fish.remove();
							}
							this.cancel();
						}
					}
				}.runTaskTimer(main, 0l, 20l);
				}
			}
		return false;
	}

	@Override
	public void initConfigEntries() {
		BleedDuration = Integer.parseInt(getConfig().getString("Items." + getOriginalName() + ".BleedDuration")) ;
		FishAmount = Integer.parseInt(getConfig().getString("Items." + getOriginalName() + ".FishAmount"));
		FishDuration = Integer.parseInt(getConfig().getString("Items." + getOriginalName() + ".FishDuration"));
	}

}
