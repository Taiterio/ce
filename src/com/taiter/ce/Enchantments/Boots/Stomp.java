package com.taiter.ce.Enchantments.Boots;


import java.util.List;

import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;

import com.taiter.ce.Enchantments.CEnchantment;



public class Stomp extends CEnchantment {

	int	damageReductionFraction;
	int damageApplicationFraction;

	public Stomp(String originalName, Application app, Cause cause, int enchantProbability, int occurrenceChance) {
		super(originalName,  app,  cause, enchantProbability, occurrenceChance);
		configEntries.add("DamageReductionFraction: 4");
		configEntries.add("DamageApplicationFraction: 2");
	}

	@Override
	public void effect(Event e, ItemStack item, int level) {
		if(e instanceof EntityDamageEvent) {
			EntityDamageEvent event = (EntityDamageEvent) e;
			if (event.getCause() == DamageCause.FALL) {
				if(event.getEntity() instanceof Player) {
					if (event.getDamage() > 0) {
						Player player = (Player) event.getEntity();
						List<Entity> entities = player.getNearbyEntities(0, 0, 0);
						if (!entities.isEmpty()) {
							for (Entity ent : entities) {
								if (ent instanceof LivingEntity) {
									double damage = event.getDamage()/damageApplicationFraction;
									if( ((Damageable) ent).getHealth() - damage > 0)
										((LivingEntity) ent).damage(damage, player);
									else  {
										ent.setLastDamageCause(event);
										((Damageable) ent).setHealth(0);
									}
									
								}
							}
							player.getWorld().playEffect(player.getLocation(),
									Effect.ZOMBIE_DESTROY_DOOR, 5);
							player.getWorld().playSound(player.getLocation(),
									Sound.EXPLODE, 1f, 4f);
							
							double damage = event.getDamage()/damageReductionFraction;
							if(((Damageable) player).getHealth() - damage > 0)
								((LivingEntity) player).damage(damage, player);
							else {
								player.setLastDamageCause(event);
								player.setHealth(0);
							}
							event.setCancelled(true);
						}
					}
				}
			}
		}
	}

	@Override
	public void initConfigEntries() {
		damageReductionFraction 	= Integer.parseInt(getConfig().getString("Enchantments." + getOriginalName() + ".DamageReductionFraction"));
		damageApplicationFraction 	= Integer.parseInt(getConfig().getString("Enchantments." + getOriginalName() + ".DamageApplicationFraction"));
	}
}
