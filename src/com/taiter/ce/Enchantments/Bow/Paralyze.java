package com.taiter.ce.Enchantments.Bow;

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



public class Paralyze extends CEnchantment {

	int	duration;
	int	cooldown;
	int	strength;


	public Paralyze(Application app) {
		super(app);		
		configEntries.put("Duration", 60);
		configEntries.put("Strength", 5);
		configEntries.put("Cooldown", 200);
		triggers.add(Trigger.SHOOT_BOW);
		triggers.add(Trigger.DAMAGE_GIVEN);
	}

	@Override
	public void effect(Event e, ItemStack item, int level) {
		if(e instanceof EntityDamageByEntityEvent) {
		EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) e;
		LivingEntity target = (LivingEntity) event.getEntity();
		target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, duration + (level-1)*20, strength + level-1), true);
		target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, duration + (level-1)*20, 1), true);
		target.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, duration + (level-1)*20, strength + level-1), true);
		}
	}

	@Override
	public void initConfigEntries() {
		duration = Integer.parseInt(getConfig().getString("Enchantments." + getOriginalName() + ".Duration"));
		cooldown = Integer.parseInt(getConfig().getString("Enchantments." + getOriginalName() + ".Cooldown"));
		strength = Integer.parseInt(getConfig().getString("Enchantments." + getOriginalName() + ".Strength"))-1;
	}
}
