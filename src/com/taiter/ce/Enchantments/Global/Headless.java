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



import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;

import com.taiter.ce.Enchantments.CEnchantment;



public class Headless extends CEnchantment {

	public Headless(Application app) {
		super(app);		
		triggers.add(Trigger.DAMAGE_GIVEN);
	}

	@Override
	public void effect(Event e, ItemStack item, int level) {
		EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) e;
		final Player player = (Player) event.getDamager();
		final LivingEntity ent = (LivingEntity) event.getEntity();
		
		new BukkitRunnable() {
			@Override
			public void run() {
		
		if(ent.getHealth() <= 0)
			if(!(ent instanceof Player))
				return;
			else {
				ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
				SkullMeta sm    = (SkullMeta) skull.getItemMeta();
				sm.setOwner(ent.getName());
				skull.setItemMeta(sm);
				ent.getWorld().dropItem(ent.getLocation(), skull);
				player.getWorld().playSound(player.getLocation(), Sound.ZOMBIE_WOODBREAK, 0.1f, 0.1f);
			}
			}
		}.runTaskLater(getPlugin(), 1l);
		

	}

	@Override
	public void initConfigEntries() {
		this.resetMaxLevel();
	}
}
