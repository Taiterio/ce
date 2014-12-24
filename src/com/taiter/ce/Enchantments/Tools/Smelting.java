package com.taiter.ce.Enchantments.Tools;


import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import com.taiter.ce.Enchantments.CEnchantment;



public class Smelting extends CEnchantment {

	public Smelting(String originalName, Application app, Cause cause, int enchantProbability, int occurrenceChance) {
		super(originalName, app,  cause, enchantProbability, occurrenceChance);
	}

	@Override
	public void effect(Event e, ItemStack item, int level) {
		BlockBreakEvent event = (BlockBreakEvent) e;
		Player player = event.getPlayer();
						
			ItemStack itemToDrop = null;
			Material drop = null;
			MaterialData md = null;
			
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
			else if(m == Material.DIAMOND_ORE)
				drop = Material.DIAMOND;
			else if(m == Material.WOOD) {
				drop = Material.COAL;
				md = new MaterialData(Material.COAL, (byte) 1);
			}
			else if(m == Material.SAND)
				drop = Material.GLASS;
			else if(m == Material.CLAY)
				drop = Material.BRICK;
			
			if(drop != null) {
				itemToDrop = new ItemStack(drop, event.getBlock().getDrops(player.getItemInHand()).size()); //Prevents unallowed tool usage (Wooden Pickaxe -> Diamond Ore)
				if(md != null)
					itemToDrop.setData(md);
				event.setCancelled(true);
				player.getWorld().dropItemNaturally(b.getLocation(), itemToDrop);
				player.getWorld().playEffect(b.getLocation(), Effect.MOBSPAWNER_FLAMES, 12);
				b.setType(Material.AIR);
			}
		
	}
	
	@Override
	public void initConfigEntries() {
	}
	
}
