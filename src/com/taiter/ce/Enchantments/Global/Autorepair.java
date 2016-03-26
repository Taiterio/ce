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



import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.taiter.ce.Main;
import com.taiter.ce.Enchantments.CEnchantment;



public class Autorepair extends CEnchantment {
	int	healAmount;
	boolean healFully;
	int cooldown;
	
	public Autorepair(Application app) {
		super(app);				
		configEntries.put("HealAmount", 1);
		configEntries.put("HealFully", false);
		triggers.add(Trigger.MOVE);
	}

	@Override
	public void effect(Event e, ItemStack item, int level) {
		PlayerMoveEvent event = (PlayerMoveEvent) e;
		Player owner = event.getPlayer();

		
		if(owner != null && owner.isOnline() && !owner.isDead()) {
			if(healFully)
				item.setDurability((short) 0);
			else {
				int newDur = item.getDurability() - ( 1 + (healAmount*level));
				
				if(newDur > 0)
					item.setDurability((short) newDur);
				else
					item.setDurability((short) 0);
			}
		}
	}

	@Override
	public void initConfigEntries() {
		healAmount = Integer.parseInt(getConfig().getString("Enchantments." + getOriginalName() + ".HealAmount"));
		healFully  = Boolean.parseBoolean(getConfig().getString("Enchantments." + getOriginalName() + ".HealFully"));
		if(getConfig().contains("Enchantments." + getOriginalName() + ".Cooldown")) {
		    getConfig().set("Enchantments." + getOriginalName() + ".Cooldown", null);
		    new BukkitRunnable() {
		        @Override
		        public void run() {
		            Main.plugin.saveConfig();
		        }
		    }.runTaskLater(Main.plugin, 60);
		}
	}
}
