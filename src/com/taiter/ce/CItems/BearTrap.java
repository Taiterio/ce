package com.taiter.ce.CItems;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;


public class BearTrap extends CItem {

	int BleedDuration;
	int SlowLevel;
	int SlowDuration;
	int WeaknessLevel;
	int WeaknessDuration;
	
	public BearTrap(String originalName, ChatColor color, String lDescription, long lCooldown, Material mat) {
		super(originalName, color, lDescription, lCooldown, mat);
		this.configEntries.add("BleedDuration: 100");
		this.configEntries.add("SlowLevel: 2");
		this.configEntries.add("SlowDuration: 100");
		this.configEntries.add("WeaknessLevel: 4");
		this.configEntries.add("WeaknessDuration: 100");
	}

	@Override
	public boolean effect(Event event, Player player) {
		if(event instanceof BlockPlaceEvent) {
			BlockPlaceEvent e = (BlockPlaceEvent) event;
			Block b = e.getBlock();
			b.setMetadata("ce.mine", new FixedMetadataValue(main, getOriginalName()));
			String coord = b.getX() + " " + b.getY() + " " + b.getZ();
			b.getRelative(0,1,0).setMetadata("ce.mine.secondary", new FixedMetadataValue(main, coord));
		} else if(event instanceof PlayerMoveEvent) {
			
			World w = player.getWorld();;
			w.playEffect(player.getLocation(), Effect.CLICK1, 5);
			w.playEffect(player.getLocation(), Effect.ZOMBIE_CHEW_IRON_DOOR, 10);
			
			
			

			
			String[] s = player.getMetadata("ce.mine").get(0).asString().split(" ");
			Location loc = new Location(player.getWorld(), Integer.parseInt(s[0]), Integer.parseInt(s[1]), Integer.parseInt(s[2]));
			Block b = loc.getBlock();
			b.removeMetadata("ce.mine", main);
			b.getRelative(0,1,0).removeMetadata("ce.mine.secondary", main);
			player.removeMetadata("ce.mine", main);
			
			if(!b.getType().equals(Material.AIR)) {
				b.setType(Material.AIR);

				if(player.hasMetadata("ce.bleed")) 
					player.removeMetadata("ce.bleed", main);
				if(player.hasMetadata("ce.bleed") && player.hasPotionEffect(PotionEffectType.SLOW) && player.hasPotionEffect(PotionEffectType.WEAKNESS)) {
					player.sendMessage(ChatColor.RED + "You walked into another Bear Trap! Are you kidding me?!");
				} else {
					player.sendMessage(ChatColor.RED + "You triggered a trap, leaving you vulnerable!");
				}
				getTools().applyBleed(player, BleedDuration);
				
				player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, SlowDuration, SlowLevel));
				player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, WeaknessDuration, WeaknessLevel));

			}
			
		}
		return false;
	}

	@Override
	public void initConfigEntries() {
		BleedDuration = Integer.parseInt(getConfig().getString("Items." + getOriginalName() + ".BleedDuration"));
		SlowLevel = Integer.parseInt(getConfig().getString("Items." + getOriginalName() + ".SlowLevel"))-1;
		SlowDuration = Integer.parseInt(getConfig().getString("Items." + getOriginalName() + ".SlowDuration"));
		WeaknessLevel = Integer.parseInt(getConfig().getString("Items." + getOriginalName() + ".WeaknessLevel"))-1;
		WeaknessDuration = Integer.parseInt(getConfig().getString("Items." + getOriginalName() + ".WeaknessDuration"));
	
		
	}

}
