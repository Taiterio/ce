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



import org.bukkit.Effect;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.taiter.ce.Enchantments.CEnchantment;



public class Gooey extends CEnchantment {

	int	strength;

	public Gooey(Application app) {
		super(app);		
		configEntries.put("HeightMultiplier", 3);
		triggers.add(Trigger.DAMAGE_GIVEN);
	}

	@Override
	public void effect(Event e, ItemStack item, final int level) {
		final EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) e;

		new BukkitRunnable() {

			@Override
			public void run() {
				event.getEntity().setVelocity(event.getEntity().getVelocity().setY((strength * level * 0.05) + 0.75));
				event.getEntity().getWorld().playEffect(event.getEntity().getLocation(), Effect.SMOKE, 60);
			}
		}.runTaskLater(getPlugin(), 1l);
	}

	@Override
	public void initConfigEntries() {
		strength = Integer.parseInt(getConfig().getString("Enchantments." + getOriginalName() + ".HeightMultiplier"));		
	}
}
