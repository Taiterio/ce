package com.taiter.ce.Enchantments.Armor;


import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.taiter.ce.Enchantments.CEnchantment;



public class Hardened extends CEnchantment {
	
	int duration;
	int strength;

	public Hardened(String originalName, Application app, Cause cause, int enchantProbability, int occurrenceChance) {
		super(originalName,  app,  cause, enchantProbability, occurrenceChance);
		configEntries.add("Duration: 60");
		configEntries.add("Strength: 1");
	}

	@Override
	public void effect(Event e, ItemStack item, int level) {
		EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) e;
		LivingEntity target = (LivingEntity) event.getDamager();
			target.addPotionEffect(
					new PotionEffect(
							PotionEffectType.WEAKNESS, 
							duration * level,
							strength + level));

	}

	@Override
	public void initConfigEntries() {
	duration = Integer.parseInt(getConfig().getString("Enchantments." + getOriginalName() + ".Duration"));
	strength = Integer.parseInt(getConfig().getString("Enchantments." + getOriginalName() + ".Strength"))-1;
	}
}
