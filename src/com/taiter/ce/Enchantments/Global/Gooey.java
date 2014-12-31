package com.taiter.ce.Enchantments.Global;


import org.bukkit.Effect;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.taiter.ce.Enchantments.CEnchantment;



public class Gooey extends CEnchantment {

	int	strength;

	public Gooey(String originalName, Application app, Cause cause, int enchantProbability, int occurrenceChance) {
		super(originalName,  app,  cause, enchantProbability, occurrenceChance);
		configEntries.add("HeightMultiplier: 3");
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
