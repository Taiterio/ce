package com.taiter.ce.CItems;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;


public class PricklyBlock extends CItem {

	int Damage;
	int NauseaLevel;
	int NauseaDuration;
	
	public PricklyBlock(String originalName, ChatColor color, String lDescription, long lCooldown, Material mat) {
		super(originalName, color, lDescription, lCooldown, mat);
		this.configEntries.add("Damage: 3");
		this.configEntries.add("NauseaLevel: 2");
		this.configEntries.add("NauseaDuration: 200");
	}

	@Override
	public boolean effect(Event event, Player player) {
		if(event instanceof BlockPlaceEvent) {
			BlockPlaceEvent e = (BlockPlaceEvent) event;
			Block b = e.getBlock();
			b.setMetadata("ce.mine", new FixedMetadataValue(main, getOriginalName()));
			String coord = b.getX() + " " + b.getY() + " " + b.getZ();
			b.getRelative(0,1,0).setMetadata("ce.mine.secondary", new FixedMetadataValue(main, coord));
			b.getRelative(0,-1,0).setMetadata("ce.mine.secondary", new FixedMetadataValue(main, coord));
			b.getRelative(1,0,0).setMetadata("ce.mine.secondary", new FixedMetadataValue(main, coord));
			b.getRelative(0,0,1).setMetadata("ce.mine.secondary", new FixedMetadataValue(main, coord));
			b.getRelative(0,0,-1).setMetadata("ce.mine.secondary", new FixedMetadataValue(main, coord));
			b.getRelative(-1,0,0).setMetadata("ce.mine.secondary", new FixedMetadataValue(main, coord));
		} else if(event instanceof PlayerMoveEvent) {
			if(!player.getGameMode().equals(GameMode.CREATIVE) && !player.hasPotionEffect(PotionEffectType.CONFUSION)) {
				player.damage(Damage);
				player.sendMessage(ChatColor.DARK_GREEN + "A nearbly Block is hurting you!");
				player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, NauseaDuration, NauseaLevel));
			}
		}
		return false;
	}

	@Override
	public void initConfigEntries() {
		Damage = Integer.parseInt(getConfig().getString("Items." + getOriginalName() + ".Damage"));
		NauseaLevel = Integer.parseInt(getConfig().getString("Items." + getOriginalName() + ".NauseaLevel"))-1;
		NauseaDuration = Integer.parseInt(getConfig().getString("Items." + getOriginalName() + ".NauseaDuration"));
	}

}
