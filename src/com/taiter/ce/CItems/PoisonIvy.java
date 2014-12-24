package com.taiter.ce.CItems;


import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;



public class PoisonIvy extends CItem {

	int PoisonDuration;
	int PoisonLevel;

	
	public PoisonIvy(String originalName, ChatColor color, String lDescription, long lCooldown, Material mat) {
		super(originalName, color, lDescription, lCooldown, mat);
		this.configEntries.add("PoisonDuration: 100");
		this.configEntries.add("PoisonLevel: 2");
	}

	@Override
	public boolean effect(Event event, Player player) {
		if(event instanceof BlockPlaceEvent) {
			BlockPlaceEvent e = (BlockPlaceEvent) event;
			e.getBlock().setMetadata("ce.mine", new FixedMetadataValue(main, getOriginalName()));
		} else if(event instanceof PlayerMoveEvent) {
			if(!player.hasPotionEffect(PotionEffectType.POISON)) {
				player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, PoisonDuration, PoisonLevel));
				player.sendMessage(ChatColor.DARK_GREEN + "You have touched Poison Ivy!");
			}
		}
		return false;
	}

	@Override
	public void initConfigEntries() {
		PoisonLevel = Integer.parseInt(getConfig().getString("Items." + getOriginalName() + ".PoisonLevel"))-1;
		PoisonDuration = Integer.parseInt(getConfig().getString("Items." + getOriginalName() + ".PoisonDuration"));
	}

}
