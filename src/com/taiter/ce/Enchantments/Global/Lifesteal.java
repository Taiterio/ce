package com.taiter.ce.Enchantments.Global;


import org.bukkit.GameMode;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import com.taiter.ce.Enchantments.CEnchantment;



public class Lifesteal extends CEnchantment {

	public double	heal;

	public Lifesteal(String originalName, Application app, Cause cause, int enchantProbability, int occurrenceChance) {
		super(originalName,  app,  cause, enchantProbability, occurrenceChance);
		configEntries.add("Heal: 2");
	}

	@Override
	public void effect(Event e, ItemStack item, int level) {
		EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) e;
		Player damager = (Player) event.getDamager();
		if(damager.getGameMode().equals(GameMode.CREATIVE))
			return;
		
		double newHeal = ((Damageable) damager).getHealth() + heal + level;

		if(newHeal < ((Damageable) damager).getMaxHealth())
			damager.setHealth(((Damageable) damager).getHealth() + newHeal);
		else
			damager.setHealth(((Damageable) damager).getMaxHealth());

		
	}

	@Override
	public void initConfigEntries() {
		heal = Double.parseDouble(getConfig().getString("Enchantments." + getOriginalName() + ".Heal"));		
	}
}
