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



import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import com.taiter.ce.Enchantments.CEnchantment;



public class Charge extends CEnchantment {

	float	DamageIncreasePercentage;

	public Charge(Application app) {
		super(app);		
		configEntries.add("DamageIncreasePercentagePerLevel: 10");
		triggers.add(Trigger.DAMAGE_GIVEN);
	}

	@Override
	public void effect(Event e, ItemStack item, int level) {
		EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) e;
		Player player = (Player) event.getDamager();
		
		if(!player.isSprinting())
			return;
		
		event.setDamage(event.getDamage() * (1 + DamageIncreasePercentage * level)); 

		player.getWorld().playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.1f, 0.1f);
	}

	@Override
	public void initConfigEntries() {
		DamageIncreasePercentage = Float.parseFloat(getConfig().getString("Enchantments." + getOriginalName() + ".DamageIncreasePercentagePerLevel"))/100;		
	}
}
