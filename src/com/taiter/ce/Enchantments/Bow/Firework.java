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



import java.util.Random;

import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import com.taiter.ce.Tools;
import com.taiter.ce.Enchantments.CEnchantment;



public class Firework extends CEnchantment {

	int		duration;
	long	delay;

	public Firework(Application app) {
		super(app);		
		configEntries.put("FireworkAmount", 30);
		configEntries.put("Delay", 5);		
		triggers.add(Trigger.SHOOT_BOW);
		triggers.add(Trigger.DAMAGE_GIVEN);
	}

	@Override
	public void effect(Event e, ItemStack item, final int level) {
		if(e instanceof EntityShootBowEvent) {
		final EntityShootBowEvent event = (EntityShootBowEvent) e;
		new BukkitRunnable() {

			int	fireworkLivingTime	= duration + level;

			@Override
			public void run() {
				if(fireworkLivingTime > 0) {
					Location loc = event.getProjectile().getLocation();
					if(event.getProjectile() != null && !event.getProjectile().isDead()) {
						Tools.shootFirework(loc, new Random());

						fireworkLivingTime--;
						return;
					}
				}
					this.cancel();
				
			}

		}.runTaskTimer(getPlugin(), 0l, delay);
		} else if(e instanceof EntityDamageByEntityEvent){
			EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) e;
			event.getEntity().setMetadata("ce.Firework", new FixedMetadataValue(main, null));
			event.getEntity().getWorld().createExplosion(event.getEntity().getLocation(), 0);
		}
		
	}
	
	@Override
	public void initConfigEntries() {
		duration = Integer.parseInt(getConfig().getString("Enchantments." + getOriginalName() + ".FireworkAmount"));
		delay = Long.parseLong(getConfig().getString("Enchantments." + getOriginalName() + ".Delay"));
	}
}
