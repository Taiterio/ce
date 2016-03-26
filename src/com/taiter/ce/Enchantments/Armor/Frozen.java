package com.taiter.ce.Enchantments.Armor;

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



import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.taiter.ce.Enchantments.CEnchantment;



public class Frozen extends CEnchantment {
	
	int duration;
	int strength;

	public Frozen(Application app) {
		super(app);		
		configEntries.put("Duration", 60);
		configEntries.put("Strength", 1);
		triggers.add(Trigger.DAMAGE_TAKEN);
	}

	@Override
	public void effect(Event e, ItemStack item, int level) {
		EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) e;
		LivingEntity target = (LivingEntity) event.getDamager();
		target.addPotionEffect(
				new PotionEffect(
						PotionEffectType.SLOW, 
						duration * level,
						strength + level));
	}

	@Override
	public void initConfigEntries() {
		duration = Integer.parseInt(getConfig().getString("Enchantments." + getOriginalName() + ".Duration"));
		strength = Integer.parseInt(getConfig().getString("Enchantments." + getOriginalName() + ".Strength"))-1;
	}
}
