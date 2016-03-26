package com.taiter.ce.Enchantments.Global;

import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import com.taiter.ce.EffectManager;
import com.taiter.ce.Enchantments.CEnchantment;



public class Vampire extends CEnchantment {

	int	damageHealFraction;
	int	cooldown;

	public Vampire(Application app) {
		super(app);		
		configEntries.put("DamageHealFraction", 2);
		configEntries.put("Cooldown", 100);
		triggers.add(Trigger.DAMAGE_GIVEN);
		this.resetMaxLevel();
	}

	@Override
	public void effect(Event e, ItemStack item, int level) {
		EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) e;
		Player damager = (Player) event.getDamager();
		if (!getHasCooldown(damager)) {	
			double heal = (((Damageable) damager).getHealth() + (event.getDamage() / damageHealFraction));
			if ( heal < ((Damageable) damager).getMaxHealth()) 
				damager.setHealth(heal);
			 else 
				damager.setHealth(((Damageable) damager).getMaxHealth());
			int food = (int) (damager.getFoodLevel() + (event.getDamage() / damageHealFraction));
			if ( food < 20) 
				damager.setFoodLevel(food);
			 else 
				damager.setFoodLevel(20);
			EffectManager.playSound(damager.getLocation(), "ENTITY_PLAYER_BURP", 0.4f, 1f);
			generateCooldown(damager, cooldown);
		}
	}

	@Override
	public void initConfigEntries() {
		damageHealFraction = Integer.parseInt(getConfig().getString("Enchantments." + getOriginalName() + ".DamageHealFraction"));	
		cooldown = Integer.parseInt(getConfig().getString("Enchantments." + getOriginalName() + ".Cooldown"));	
	}
}
