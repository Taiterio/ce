package com.taiter.ce.Enchantments.Global;


import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import com.taiter.ce.Enchantments.CEnchantment;



public class Autorepair extends CEnchantment {
	int	healAmount;
	boolean healFully;

	public Autorepair(String originalName, Application app, Cause cause, int enchantProbability, int occurrenceChance) {
		super(originalName,  app,  cause, enchantProbability, occurrenceChance);		
		configEntries.add("HealAmount: 1");
		configEntries.add("HealFully: false");
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
	}
}
