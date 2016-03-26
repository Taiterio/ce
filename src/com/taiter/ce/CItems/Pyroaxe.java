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
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.taiter.ce.EffectManager;
import com.taiter.ce.Main;


public class Pyroaxe extends CItem {

	int damageMultiplier;
	
	public Pyroaxe(String originalName, ChatColor color, String lDescription, long lCooldown, Material mat) {
		super(originalName, color, lDescription, lCooldown, mat);
		configEntries.put("DamageMultiplier", 2);
		triggers.add(Trigger.DAMAGE_GIVEN);
	}

	@Override
	public boolean effect(Event event, Player player) {
		
		EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
		Entity entity = e.getEntity();
		

		if(e.getDamager() == player && entity.getFireTicks() > 0) {
			e.setDamage(damageMultiplier * e.getDamage());
			entity.getWorld().playEffect(entity.getLocation(), Effect.ZOMBIE_DESTROY_DOOR, 10);
			EffectManager.playSound(entity.getLocation(), "BLOCK_ANVIL_LAND", 1f, 0.001f);
			return true;
		}
		return false;
	}

	@Override
	public void initConfigEntries() {
		damageMultiplier = Integer.parseInt(Main.config.getString("Items." + getOriginalName() + ".DamageMultiplier"));
	}

}
