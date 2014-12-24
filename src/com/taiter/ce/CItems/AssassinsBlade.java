package com.taiter.ce.CItems;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;


public class AssassinsBlade extends CItem {

	int InvisibilityDuration;
	int AmbushDmgMultiplier;
	int WeaknessLevel;
	int WeaknessLength;
	
	public AssassinsBlade(String originalName, ChatColor color, String lDescription, long lCooldown, Material mat) {
		super(originalName, color, lDescription, lCooldown, mat);
		this.configEntries.add("InvisibilityDuration: 400");
		this.configEntries.add("AmbushDmgMultiplier: 2");
		this.configEntries.add("WeaknessLevel: 4");
		this.configEntries.add("WeaknessLength: 100");
	}

	@Override
	public boolean effect(Event event, final Player player) {
		  if(event instanceof PlayerInteractEvent)
			  if(!player.hasMetadata("ce.assassin"))
				  if(player.isSneaking())
					  if(((PlayerInteractEvent) event).getAction().toString().startsWith("RIGHT")) {
						  player.setMetadata("ce.assassin", new FixedMetadataValue(main, null));
						  player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, InvisibilityDuration, 0, true), true);
						  player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "You hide in the shadows.");
						  new BukkitRunnable() {
							  @Override
							  public void run() {
								  if(player.hasMetadata("ce.assassin")) {
									  player.removeMetadata("ce.assassin", main);
									  player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "You are no longer hidden!");
								  }
							  }
						  }.runTaskLater(main, InvisibilityDuration);
						  return true;
					  }
					  if(event instanceof EntityDamageByEntityEvent) {
						 EntityDamageByEntityEvent e = ((EntityDamageByEntityEvent) event);
						   if(player.hasMetadata("ce.assassin")) {
							  e.setDamage(e.getDamage() * AmbushDmgMultiplier);
							  player.removeMetadata("ce.assassin", main);
							  player.removePotionEffect(PotionEffectType.INVISIBILITY);
							  player.getWorld().playSound(e.getEntity().getLocation(), Sound.ZOMBIE_METAL, 0.4f, 0.1f);
							  player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, WeaknessLength, WeaknessLevel, false), true);
							  player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "You are no longer hidden!");
						   }
					  }
		 return false;
	}

	@Override
	public void initConfigEntries() {
		InvisibilityDuration = Integer.parseInt(getConfig().getString("Items." + getOriginalName() + ".InvisibilityDuration"));
		AmbushDmgMultiplier = Integer.parseInt(getConfig().getString("Items." + getOriginalName() + ".AmbushDmgMultiplier"));
		WeaknessLevel = Integer.parseInt(getConfig().getString("Items." + getOriginalName() + ".WeaknessLevel"))-1;
		WeaknessLength = Integer.parseInt(getConfig().getString("Items." + getOriginalName() + ".WeaknessLength"));
	
	}

}
