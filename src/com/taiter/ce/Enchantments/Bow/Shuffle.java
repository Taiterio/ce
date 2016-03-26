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



import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import com.taiter.ce.EffectManager;
import com.taiter.ce.Enchantments.CEnchantment;



public class Shuffle extends CEnchantment {

	public Shuffle(Application app) {
		super(app);		
		triggers.add(Trigger.SHOOT_BOW);
		triggers.add(Trigger.DAMAGE_GIVEN);
		this.resetMaxLevel();
	}

	@Override
	public void effect(Event e, ItemStack item, final int level) {
		if(e instanceof EntityDamageByEntityEvent) {
		EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) e;
		Entity target = event.getEntity();
		Player p = (Player) ((Projectile) event.getDamager()).getShooter();
		
		if(target.getEntityId() == p.getEntityId())
			return;
		
		Location pLoc = p.getLocation();
		Location tLoc = target.getLocation();
		
		target.teleport(pLoc);
		p.teleport(tLoc);
		
		EffectManager.playSound(tLoc, "ENTITY_ENDERMAN_TELEPORT", 0.4f, 2f);
		EffectManager.playSound(pLoc, "ENTITY_ENDERMAN_TELEPORT", 0.4f, 2f);

		
		for(int i = 10; i>0; i--) {
			p.getWorld().playEffect(tLoc, Effect.ENDER_SIGNAL, 10);
			p.getWorld().playEffect(pLoc, Effect.ENDER_SIGNAL, 10);
		}
		
		if(target instanceof Player) {
			p.sendMessage(ChatColor.DARK_PURPLE + "You have switched positions with " + target.getName() + "!");
			target.sendMessage(ChatColor.DARK_PURPLE + "You have switched positions with " + p.getName() + "!");
		}
		
		
		}
	}

	@Override
	public void initConfigEntries() {
	}

}
