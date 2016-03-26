package com.taiter.ce.Enchantments.Global;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import com.taiter.ce.EffectManager;
import com.taiter.ce.Enchantments.CEnchantment;



public class Aerial extends CEnchantment {

	float	DamageIncreasePercentage;

	public Aerial(Application app) {
		super(app);		
		configEntries.put("DamageIncreasePercentagePerLevel", 10);
		triggers.add(Trigger.DAMAGE_GIVEN);
	}

	@Override
	public void effect(Event e, ItemStack item, int level) {
		EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) e;
		Player player = (Player) event.getDamager();
		if(player.getVelocity().getY() == -0.0784000015258789) //Constant velocity for not moving in y direction (Gravity)
			return;
		
		event.setDamage(event.getDamage() * (1 + DamageIncreasePercentage * level));

		EffectManager.playSound(player.getLocation(), "ENTITY_BAT_TAKEOFF", 0.1f, 0.1f);
	}

	@Override
	public void initConfigEntries() {
		DamageIncreasePercentage = Float.parseFloat(getConfig().getString("Enchantments." + getOriginalName() + ".DamageIncreasePercentagePerLevel"))/100;		
	}
}
