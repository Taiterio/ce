package com.taiter.ce.Enchantments.Global;


import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.taiter.ce.Enchantments.CEnchantment;



public class Cripple extends CEnchantment {

	int	duration;
	int	strength;

	public Cripple(String originalName, Application app, Cause cause, int enchantProbability, int occurrenceChance) {
		super(originalName, app,  cause, enchantProbability, occurrenceChance);
		configEntries.add("Duration: 100");
		configEntries.add("Strength: 1");
		}

	@Override
	public void effect(Event e, ItemStack item, int level) {
		EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) e;
		LivingEntity target = (LivingEntity) event.getEntity();
		
		if(!target.hasPotionEffect(PotionEffectType.CONFUSION)) {

			target.getWorld().playSound(target.getLocation(), Sound.HURT_FLESH, 1f, 0.1f);
			target.getWorld().playSound(target.getLocation(), Sound.ANVIL_LAND, 0.1f, 10f);
			target.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, duration * level, 0));
			target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, duration * level, strength + level));

		}
	}

	@Override
	public void initConfigEntries() {
		duration = Integer.parseInt(getConfig().getString("Enchantments." + getOriginalName() + ".Duration"));
		strength = Integer.parseInt(getConfig().getString("Enchantments." + getOriginalName() + ".Strength"))-1;
	}
}
