package com.taiter.ce.Enchantments.Bow;


import java.util.Random;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import com.taiter.ce.Main;
import com.taiter.ce.Enchantments.CEnchantment;



public class Lightning extends CEnchantment {

	int	chance;

	public Lightning(String originalName, Application app, Cause cause, int enchantProbability, int occurrenceChance) {
		super(originalName, app,  cause, enchantProbability, occurrenceChance);
		configEntries.add("LightningChance: 75");
		}

	@Override
	public void effect(Event e, ItemStack item, int level) {
		if(e instanceof EntityShootBowEvent) {
			((EntityShootBowEvent) e).getProjectile().setMetadata("ce.lightningArrow", new FixedMetadataValue(Main.plugin, level));
		} else if(e instanceof EntityDamageByEntityEvent) {
		EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) e;
		LivingEntity target = (LivingEntity) event.getEntity();
		Random random = new Random();
		int temp = level;
		while(temp != 0) {
		if(random.nextInt(100) < chance)
			target.getWorld().strikeLightning(target.getLocation());
		--temp;
		}
		}
	}

	@Override
	public void initConfigEntries() {
		chance = Integer.parseInt(getConfig().getString("Enchantments." + getOriginalName() + ".LightningChance"));
	}
}
