package com.taiter.ce.CItems;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;


public class Medikit extends CItem {

	int HealAmount;
	
	public Medikit(String originalName, ChatColor color, String lDescription, long lCooldown, Material mat) {
		super(originalName, color, lDescription, lCooldown, mat);
		this.configEntries.add("HealAmount: 10");
	}

	@Override
	public boolean effect(Event event, Player player) {
		if(event instanceof PlayerInteractEvent) {
			PlayerInteractEvent e = ((PlayerInteractEvent) event);
			if(e.getAction().toString().startsWith("RIGHT")) {
				double maxHealth = ((Damageable) player).getMaxHealth();
				if(((Damageable) player).getHealth() != maxHealth) {
					if(((Damageable) player).getHealth() + HealAmount >= maxHealth)
						player.setHealth(maxHealth);
					else
						player.setHealth(((Damageable) player).getHealth() + HealAmount);
					return true;
				} else
					player.sendMessage(ChatColor.RED + "You do not have any wounds to apply the Medikit to!");
			}
		} else if(event instanceof PlayerInteractEntityEvent || event instanceof EntityDamageByEntityEvent) {
			Player toHeal = null;
			
			if(event instanceof PlayerInteractEntityEvent) {
				toHeal = ((PlayerInteractEntityEvent) event).getPlayer();
			} else if(event instanceof EntityDamageByEntityEvent) {
				EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
				if(e.getEntity() instanceof Player) {
					e.setDamage((double) 0);
					toHeal = (Player) e.getEntity();
				}
			}
			
			if(toHeal != null) {
				double maxHealth = ((Damageable) toHeal).getMaxHealth();
				if(((Damageable) toHeal).getHealth() != maxHealth) {
					if(((Damageable) toHeal).getHealth() + HealAmount >= maxHealth)
						toHeal.setHealth(maxHealth);
					else
						toHeal.setHealth(((Damageable) toHeal).getHealth() + HealAmount);
					player.sendMessage(ChatColor.GREEN + "You have healed " + toHeal.getName() + " using the Medikit!");
					toHeal.sendMessage(ChatColor.GREEN + player.getName() + " has used a Medikit to heal you!");
					return true;
				} else
					player.sendMessage(ChatColor.RED + "Your target does not have any wounds to apply the Medikit to!");
			  
			}
		}
		return false;
	}

	@Override
	public void initConfigEntries() {
		HealAmount = Integer.parseInt(getConfig().getString("Items." + getOriginalName() + ".HealAmount"));
	}

}
