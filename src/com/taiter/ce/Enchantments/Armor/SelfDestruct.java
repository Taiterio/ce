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



import org.bukkit.entity.EntityType;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.Event;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import com.taiter.ce.Main;
import com.taiter.ce.Tools;
import com.taiter.ce.Enchantments.CEnchantment;



public class SelfDestruct extends CEnchantment {

	int delay;

	public SelfDestruct(Application app) {
		super(app);		
		triggers.add(Trigger.DEATH);
		this.configEntries.put("ExplosionDelay", 40);
	}

	@Override
	public void effect(Event e, ItemStack item, int level) {
		PlayerDeathEvent event = (PlayerDeathEvent) e;
		for(int i = level; i >= 0; i--) {
		TNTPrimed tnt = (TNTPrimed) event.getEntity().getWorld().spawnEntity(event.getEntity().getLocation(), EntityType.PRIMED_TNT);
		tnt.setFuseTicks(delay);
		tnt.setVelocity(new Vector(Tools.random.nextDouble()*1.5 - 1, Tools.random.nextDouble() * 1.5, Tools.random.nextDouble()*1.5 - 1));
		if(!Main.createExplosions)
			tnt.setMetadata("ce.explosive", new FixedMetadataValue(getPlugin(), null));
		}
	}

	@Override
	public void initConfigEntries() {
		delay = Integer.parseInt(getConfig().getString("Enchantments." + getOriginalName() + ".ExplosionDelay"));		
	}
}
