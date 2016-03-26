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
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;


public class Medikit extends CItem {

	int HealAmount;
	
	public Medikit(String originalName, ChatColor color, String lDescription, long lCooldown, Material mat) {
		super(originalName, color, lDescription, lCooldown, mat);
		this.configEntries.put("HealAmount", 10);
		triggers.add(Trigger.DAMAGE_GIVEN);
		triggers.add(Trigger.INTERACT_ENTITY);
		triggers.add(Trigger.INTERACT_RIGHT);
	}

	@Override
	public boolean effect(Event event, Player player) {
		if(event instanceof PlayerInteractEvent) {
				double maxHealth = ((Damageable) player).getMaxHealth();
				if(((Damageable) player).getHealth() != maxHealth) {
					if(((Damageable) player).getHealth() + HealAmount >= maxHealth)
						player.setHealth(maxHealth);
					else
						player.setHealth(((Damageable) player).getHealth() + HealAmount);
					return true;
				} else
					player.sendMessage(ChatColor.RED + "You do not have any wounds to apply the Medikit to!");
		} else if(event instanceof PlayerInteractEntityEvent || event instanceof EntityDamageByEntityEvent) {
			Player toHeal = null;
			
			if(event instanceof PlayerInteractEntityEvent) {
				toHeal = ((PlayerInteractEntityEvent) event).getPlayer();
			} else if(event instanceof EntityDamageByEntityEvent) {
				EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
				if(e.getDamager() == player && e.getEntity() instanceof Player) {
					e.setDamage(0);
					toHeal = (Player) e.getEntity();
				}
			}
			
			if(toHeal != null) {
				double maxHealth = ((Damageable) toHeal).getMaxHealth();
				if(((Damageable) toHeal).getHealth() != maxHealth) {
					if(((Damageable) toHeal).getHealth() + HealAmount >= maxHealth)
						toHeal.setHealth(maxHealth);
					else
						toHeal.setHealth(((Damageable) toHeal).getHealth() + HealAmount);
					player.sendMessage(ChatColor.GREEN + "You have healed " + toHeal.getName() + " using the Medikit!");
					toHeal.sendMessage(ChatColor.GREEN + player.getName() + " has used a Medikit to heal you!");
					return true;
				} else
					player.sendMessage(ChatColor.RED + "Your target does not have any wounds to apply the Medikit to!");
			  
			}
		}
		return false;
	}

	@Override
	public void initConfigEntries() {
		HealAmount = Integer.parseInt(getConfig().getString("Items." + getOriginalName() + ".HealAmount"));
	}

}
