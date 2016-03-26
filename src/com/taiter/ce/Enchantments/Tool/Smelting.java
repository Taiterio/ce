package com.taiter.ce.Enchantments.Tool;

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
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import com.taiter.ce.Enchantments.CEnchantment;



public class Smelting extends CEnchantment {

	public Smelting(Application app) {
		super(app);		
		triggers.add(Trigger.BLOCK_BROKEN);
		this.resetMaxLevel();
	}

	@Override
	public void effect(Event e, ItemStack item, int level) {
		BlockBreakEvent event = (BlockBreakEvent) e;
		Player player = event.getPlayer();
						
		if(!event.getBlock().getDrops(item).isEmpty()) {
		
			ItemStack itemToDrop = null;
			Material drop = null;
			short dur = 0;
			
			Block b = event.getBlock();
			
			Material m = b.getType();
			
			if(m == Material.STONE)
				drop = m;
			else if(m == Material.COBBLESTONE)
				drop = Material.STONE;
			else if(m == Material.IRON_ORE)
				drop = Material.IRON_INGOT;
			else if(m == Material.GOLD_ORE)
				drop = Material.GOLD_INGOT;
			else if(m.toString().contains("LOG")) {
				drop = Material.COAL;
				dur = 1;
			}
			else if(m == Material.SAND)
				drop = Material.GLASS;
			else if(m == Material.CLAY)
				drop = Material.BRICK;
			
			if(drop != null) {
				itemToDrop = new ItemStack(drop, event.getBlock().getDrops(player.getItemInHand()).size()); //Prevents unallowed tool usage (Wooden Pickaxe -> Diamond Ore)
				itemToDrop.setDurability(dur);
				event.setCancelled(true);
				player.getWorld().dropItemNaturally(b.getLocation(), itemToDrop);
				player.getWorld().playEffect(b.getLocation(), Effect.MOBSPAWNER_FLAMES, 12);
				b.setType(Material.AIR);
			}
			
		}
		
	}
	
	@Override
	public void initConfigEntries() {
	}
	
}
