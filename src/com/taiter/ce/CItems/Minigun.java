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


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import com.taiter.ce.EffectManager;


public class Minigun extends CItem {

	int ArrowCountPerVolley;
	int ShotsPerSecond;

	
	public Minigun(String originalName, ChatColor color, String lDescription, long lCooldown, Material mat) {
		super(originalName, color, lDescription, lCooldown, mat);
		this.configEntries.put("ArrowCountPerVolley", 20);
		this.configEntries.put("ShotsPerSecond", 20);
		triggers.add(Trigger.SHOOT_BOW);
	}

	@Override
	public boolean effect(Event event, final Player player) {
		final EntityShootBowEvent e = (EntityShootBowEvent) event;
		Arrow oldArrow = (Arrow) e.getProjectile();
		e.setCancelled(true);
		addLock(player);
		
		
		String meta = null;
		if(oldArrow.hasMetadata("ce.bow.enchantment"))
			meta = oldArrow.getMetadata("ce.bow.enchantment").get(0).asString();
		
		
		final int fireTicks = oldArrow.getFireTicks();
		final int knockbackStrength = oldArrow.getKnockbackStrength();
		final boolean critical = oldArrow.isCritical();
		final String metadata = meta;
		
			new BukkitRunnable() {
				
				
				int lArrows = ArrowCountPerVolley;
				ItemStack last = player.getItemInHand();
				
				@Override
				public void run() {
					if (lArrows > 0) {
						if(player.getItemInHand().hasItemMeta() && player.getItemInHand().getItemMeta().equals(last.getItemMeta())) {
							if (player.getGameMode().equals(GameMode.CREATIVE) || player.getInventory().contains(Material.ARROW, 1)) {
								if (!player.getGameMode().equals(GameMode.CREATIVE)) {
									if(last.getDurability() < 380) {
										
										last.setDurability((short) (last.getDurability() + 1));
										last = player.getItemInHand();
										
									} else {
										
										ItemStack brokenItem = new ItemStack(Material.AIR);
										player.setItemInHand(brokenItem);
										player.getWorld().playEffect(player.getLocation(), Effect.ZOMBIE_DESTROY_DOOR, 10);
										EffectManager.playSound(player.getLocation(), "ENTITY_ITEM_BREAK", 0.4f, 0f);
										removeLock(player);
										this.cancel();
										
									}
								
								
									ItemStack arrows = new ItemStack(
										Material.ARROW, 1);
									player.getInventory().removeItem(
										arrows);
								}

								Arrow arrow = player.launchProjectile(Arrow.class);
								arrow.setBounce(false);
								arrow.setVelocity(player.getLocation().getDirection().multiply(5));
								arrow.setShooter(player);
								arrow.setFireTicks(fireTicks); // Set the new arrows on fire if the original one was 
								arrow.setKnockbackStrength(knockbackStrength);
								arrow.setCritical(critical);
								if(metadata != null)
									arrow.setMetadata("ce.bow.enchantment", new FixedMetadataValue(getPlugin(), metadata));
								arrow.setMetadata("ce.minigunarrow", new FixedMetadataValue(main, null));
								player.getWorld().playEffect(player.getLocation(),Effect.BOW_FIRE, 20);
								lArrows--;
								return;
								
							} else {
								
							player.sendMessage(ChatColor.RED + "Out of ammo!");
							player.getWorld().playEffect(player.getLocation(), Effect.CLICK2, 80);
							
							}
						}
					}
					
					removeLock(player);
					this.cancel();
					
				}
			}.runTaskTimer(main, 0l, 20/ShotsPerSecond);
			
			return true;
		
	}

	@Override
	public void initConfigEntries() {
		ArrowCountPerVolley = Integer.parseInt(getConfig().getString("Items." + getOriginalName() + ".ArrowCountPerVolley"));
		ShotsPerSecond = Integer.parseInt(getConfig().getString("Items." + getOriginalName() + ".ShotsPerSecond"));
		if(ShotsPerSecond > 20) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[CE] Warning: The Minigun setting 'ShotsPerSecond' is too high,");
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[CE]          the maximum value is 20, which will be used instead. ");
			ShotsPerSecond = 20;
		}

	}

}
