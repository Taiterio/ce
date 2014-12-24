package com.taiter.ce.Enchantments.Global;


import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import com.taiter.ce.Enchantments.CEnchantment;



public class Deathbringer extends CEnchantment {

	int	strength;

	public Deathbringer(String originalName, Application app, Cause cause, int enchantProbability, int occurrenceChance) {
		super(originalName, app,  cause, enchantProbability, occurrenceChance);
		configEntries.add("DamageAmplifier: 2");
	}

	@Override
	public void effect(Event e, ItemStack item, int level) {
		EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) e;
		Player player = (Player) event.getDamager();

		player.getWorld().playSound(player.getLocation(), Sound.ENDERDRAGON_GROWL, 0.1f, 0.1f);
		event.setDamage(event.getDamage() * strength + (level/2));
		

	}

	@Override
	public void initConfigEntries() {
		strength = Integer.parseInt(getConfig().getString("Enchantments." + getOriginalName() + ".DamageAmplifier"));		
	}
}
