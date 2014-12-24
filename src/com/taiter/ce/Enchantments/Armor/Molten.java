package com.taiter.ce.Enchantments.Armor;


import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import com.taiter.ce.Enchantments.CEnchantment;



public class Molten extends CEnchantment {
	
	int duration;

	public Molten(String originalName, Application app, Cause cause, int enchantProbability, int occurrenceChance) {
		super(originalName,  app,  cause, enchantProbability, occurrenceChance);
		configEntries.add("Duration: 20");
	}

	@Override
	public void effect(Event e, ItemStack item, int level) {
		EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) e;
		LivingEntity target = (LivingEntity) event.getDamager();
			target.setFireTicks(duration * level);
	}

	@Override
	public void initConfigEntries() {		
		duration = Integer.parseInt(getConfig().getString("Enchantments." + getOriginalName() + ".Duration"));
	}
}
