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

import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.taiter.ce.Tools;



public class Landmine extends CItem {

	int ExplosionStrength;
	
	public Landmine(String originalName, ChatColor color, String lDescription, long lCooldown, Material mat) {
		super(originalName, color, lDescription, lCooldown, mat);
		this.configEntries.add("ExplosionStrength: 5");
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
			
			String[] s = player.getMetadata("ce.mine").get(0).asString().split(" ");
			Location loc = new Location(player.getWorld(), Integer.parseInt(s[0]), Integer.parseInt(s[1]), Integer.parseInt(s[2]));
			Block b = loc.getBlock();
			b.removeMetadata("ce.mine", main);
			b.getRelative(0,1,0).removeMetadata("ce.mine.secondary", main);
			player.removeMetadata("ce.mine", main);
			
			if(!b.getType().equals(Material.AIR) && !b.getType().equals(Material.WATER) && !b.getType().equals(Material.LAVA) && Tools.checkWorldGuard(loc, player, DefaultFlag.PVP)) { 
				b.setType(Material.AIR);
				w.createExplosion(loc, ExplosionStrength);
			}
		}
		return false;
	}

	@Override
	public void initConfigEntries() {
		ExplosionStrength = Integer.parseInt(getConfig().getString("Items." + getOriginalName() + ".ExplosionStrength"));
	}

}
