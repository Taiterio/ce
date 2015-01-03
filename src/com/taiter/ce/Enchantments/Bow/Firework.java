package com.taiter.ce.Enchantments.Bow;


import java.util.Random;

import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.taiter.ce.Enchantments.CEnchantment;



public class Firework extends CEnchantment {

	int		duration;
	long	delay;

	public Firework(String originalName, Application app, Cause cause, int enchantProbability, int occurrenceChance) {
		super(originalName, app,  cause, enchantProbability, occurrenceChance);
		configEntries.add("FireworkAmount: 30");
		configEntries.add("Delay: 5");
	}

	@Override
	public void effect(Event e, ItemStack item, final int level) {
		if(e instanceof EntityShootBowEvent) {
		final EntityShootBowEvent event = (EntityShootBowEvent) e;
		new BukkitRunnable() {

			int	fireworkLivingTime	= duration + level;

			@Override
			public void run() {
				if(fireworkLivingTime > 0) {
					Location loc = event.getProjectile().getLocation();
					if(event.getProjectile() != null && !event.getProjectile().isDead()) {
						getTools().shootFirework(loc, new Random());

						fireworkLivingTime--;
						return;
					}
				}
					this.cancel();
				
			}

		}.runTaskTimer(getPlugin(), 0l, delay);
		} else if(e instanceof EntityDamageByEntityEvent){
			EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) e;
			event.getEntity().getWorld().createExplosion(event.getEntity().getLocation(), 2);
		}
		
	}
	
	@Override
	public void initConfigEntries() {
		duration = Integer.parseInt(getConfig().getString("Enchantments." + getOriginalName() + ".FireworkAmount"));
		delay = Long.parseLong(getConfig().getString("Enchantments." + getOriginalName() + ".Delay"));
	}
}
