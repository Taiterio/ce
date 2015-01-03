package com.taiter.ce.Enchantments.Global;

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

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import com.taiter.ce.Enchantments.CEnchantment;



public class Deepwounds extends CEnchantment {

	int				duration;
	int				rand;

	public Deepwounds(String originalName, Application app, Cause cause, int enchantProbability, int occurrenceChance) {
		super(originalName, app,  cause, enchantProbability, occurrenceChance);
		configEntries.add("Duration: 20");
		configEntries.add("BleedChance: 20");

	}

	@Override
	public void effect(Event e, ItemStack item, int level) {
		EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) e;
		final Player damaged = (Player) event.getEntity();
		final Player damager = (Player) event.getDamager();
		if(!getHasCooldown(damager) && !damaged.hasMetadata("ce.bleed")) {

		Random random = new Random();
		if(random.nextInt(100) < rand) {
			generateCooldown(damager, 140);
			getTools().applyBleed(damaged, duration*level);
		}
		}
	}

	@Override
	public void initConfigEntries() {
		duration = Integer.parseInt(getConfig().getString("Enchantments." + getOriginalName() + ".Duration"));
		rand = Integer.parseInt(getConfig().getString("Enchantments." + getOriginalName() + ".BleedChance"));
	}
}
