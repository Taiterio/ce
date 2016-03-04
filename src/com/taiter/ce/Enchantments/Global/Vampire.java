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
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import com.taiter.ce.Enchantments.CEnchantment;



public class Vampire extends CEnchantment {

	int	damageHealFraction;
	int	cooldown;

	public Vampire(Application app) {
		super(app);		
		configEntries.add("DamageHealFraction: 2");
		configEntries.add("Cooldown: 100");
		triggers.add(Trigger.DAMAGE_GIVEN);
		this.resetMaxLevel();
	}

	@Override
	public void effect(Event e, ItemStack item, int level) {
		EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) e;
		Player damager = (Player) event.getDamager();
		if (!getHasCooldown(damager)) {	
			double heal = (((Damageable) damager).getHealth() + (event.getDamage() / damageHealFraction));
			if ( heal < ((Damageable) damager).getMaxHealth()) 
				damager.setHealth(heal);
			 else 
				damager.setHealth(((Damageable) damager).getMaxHealth());
			int food = (int) (damager.getFoodLevel() + (event.getDamage() / damageHealFraction));
			if ( food < 20) 
				damager.setFoodLevel(food);
			 else 
				damager.setFoodLevel(20);
			damager.getWorld().playSound(damager.getLocation(), Sound.ENTITY_PLAYER_BURP, 0.4f, 1f);
			generateCooldown(damager, cooldown);
		}
	}

	@Override
	public void initConfigEntries() {
		damageHealFraction = Integer.parseInt(getConfig().getString("Enchantments." + getOriginalName() + ".DamageHealFraction"));	
		cooldown = Integer.parseInt(getConfig().getString("Enchantments." + getOriginalName() + ".Cooldown"));	
	}
}
