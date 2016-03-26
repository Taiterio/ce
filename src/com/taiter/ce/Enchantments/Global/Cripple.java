package com.taiter.ce.Enchantments.Global;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.taiter.ce.EffectManager;
import com.taiter.ce.Enchantments.CEnchantment;



public class Cripple extends CEnchantment {

	int	duration;
	int	strength;

	public Cripple(Application app) {
		super(app);		
		configEntries.put("Duration", 100);
		configEntries.put("Strength", 1);
		triggers.add(Trigger.DAMAGE_GIVEN);
	}

	@Override
	public void effect(Event e, ItemStack item, int level) {
		EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) e;
		LivingEntity target = (LivingEntity) event.getEntity();
		
		if(!target.hasPotionEffect(PotionEffectType.CONFUSION)) {

			EffectManager.playSound(target.getLocation(), "ENTITY_PLAYER_HURT", 1f, 0.4f);
			EffectManager.playSound(target.getLocation(), "BLOCK_ANVIL_LAND", 0.1f, 2f);
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
