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



import org.bukkit.GameMode;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import com.taiter.ce.EffectManager;
import com.taiter.ce.Enchantments.CEnchantment;



public class Lifesteal extends CEnchantment {

	public double	heal;

	public Lifesteal(Application app) {
		super(app);		
		configEntries.put("Heal", 2);
		triggers.add(Trigger.DAMAGE_GIVEN);
	}

	@Override
	public void effect(Event e, ItemStack item, int level) {
		EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) e;
		Player damager = null;
		
		if(event.getDamager() instanceof Player)
			damager = (Player) event.getDamager();
		else
			damager = (Player) ((Arrow) event.getDamager()).getShooter();
		
		if(damager.getGameMode().equals(GameMode.CREATIVE))
			return;
		
		double newHeal = ((Damageable) damager).getHealth() + heal + level;

		if(newHeal < ((Damageable) damager).getMaxHealth())
			damager.setHealth(newHeal);
		else
			damager.setHealth(((Damageable) damager).getMaxHealth());
		
		EffectManager.playSound(damager.getLocation(), "ENTITY_EXPERIENCE_ORB_PICKUP", 0.3f, 1f);

		
	}

	@Override
	public void initConfigEntries() {
		heal = Double.parseDouble(getConfig().getString("Enchantments." + getOriginalName() + ".Heal"));		
	}
}
