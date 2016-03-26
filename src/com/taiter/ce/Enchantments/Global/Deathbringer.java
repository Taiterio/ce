package com.taiter.ce.Enchantments.Global;

import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import com.taiter.ce.EffectManager;
import com.taiter.ce.Enchantments.CEnchantment;



public class Deathbringer extends CEnchantment {

	int	strength;

	public Deathbringer(Application app) {
		super(app);		
		configEntries.put("TrueDamagePerHit", 2);
		triggers.add(Trigger.DAMAGE_GIVEN);
	}

	@Override
	public void effect(Event e, ItemStack item, int level) {
		EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) e;
		Player player = (Player) event.getDamager();
		Entity ent = event.getEntity();
		
		double trueDamage = strength + (level/10); 
		double currentHealth = ((Damageable) ent).getHealth();
		
		if(currentHealth > trueDamage) {
			((Damageable) ent).setHealth(currentHealth-trueDamage);
			EffectManager.playSound(player.getLocation(), "ENTITY_ENDERDRAGON_GROWL", 0.1f, 0.1f);
		}
		

	}

	@Override
	public void initConfigEntries() {
		strength = Integer.parseInt(getConfig().getString("Enchantments." + getOriginalName() + ".TrueDamagePerHit"));		
	}
}
