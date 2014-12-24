package com.taiter.ce.CItems;




import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import com.taiter.ce.CBasic;
import com.taiter.ce.Main;

public abstract class CItem extends CBasic {	
	
	private boolean				hasRetriedConfig = false;
	
	Material	 		 		itemMaterial;
	List<String>				description;
	
	public long			 		cooldownTime;
	public ChatColor			itemColor;

	
	public Material 			getMaterial() 						{	return this.itemMaterial;	}
	public List<String>			getDescription() 					{	return this.description;	}
	@Override
	public double				getCost() 							{	return Double.parseDouble(Main.config.getString("Items." + getOriginalName() + ".Cost"));}
	
	
	public CItem(String originalName, ChatColor color, String lDescription, long lCooldown, Material mat) {
		this.typeString = "Items";
		this.itemMaterial = mat;
		this.originalName = originalName; 
		this.description =  new ArrayList<String>(Arrays.asList(lDescription.split(";")));
		this.configEntries.add("DisplayName: " + originalName);
		this.configEntries.add("Color: " + color.name());
		this.configEntries.add("Description: " + lDescription);
		this.configEntries.add("Cooldown: " + lCooldown);
		this.configEntries.add("Cost: 0");
		
	}
	
	public boolean getHasCooldown(Player p) {
		if(cooldown.contains(p))
			return true;
		return false;
	}
	public void generateCooldown(final Player p, long cooldownT) {
	  if(cooldownT != 0) {
		new BukkitRunnable() {
			@Override
			public void run() {
				cooldown.remove(p);
			}
		}.runTaskLater(main, cooldownT);
	  }
	}
	
	public void addLock(Player p) {
		p.setMetadata("ce." + getOriginalName() + ".lock", new FixedMetadataValue(Main.plugin, null));
	}
	
	public void removeLock(Player p) {
		p.removeMetadata("ce." + getOriginalName() + ".lock", main);
	}
	
	public abstract boolean effect(Event event, Player owner); // The boolean represents whether the cooldown is to be applied or not
	public abstract void initConfigEntries();
	
	public void finalizeItem() {
		
		if(!getConfig().contains("Items." + getOriginalName()))
			getTools().writeConfigEntry(this);

		try {
		this.itemColor 	 = ChatColor.valueOf(Main.config.getString("Items." + getOriginalName() + ".Color").toUpperCase());
		} catch(Exception e) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[CE] The color of the Custom Item '" + getOriginalName() + "' is invalid, please check the official Bukkit list of ChatColors.");
			this.itemColor = ChatColor.AQUA;
		}
		this.displayName = itemColor + Main.config.getString("Items." + getOriginalName() + ".DisplayName");
		this.description = new ArrayList<String>(Arrays.asList(Main.config.getString("Items." + getOriginalName() + ".Description").split(";"))); 
		for(String s : description)
			description.set(description.indexOf(s), ChatColor.GRAY + "" + ChatColor.ITALIC + s);
		//If the item has a special line, this whitespace is required
		this.description.add("");
		this.cooldownTime= Main.config.getLong("Items." + getOriginalName() + ".Cooldown");
		try {
		initConfigEntries();
		} catch(Exception e) {
			if(!hasRetriedConfig) {
				getTools().writeConfigEntry(this);
				hasRetriedConfig = true;
				finalizeItem();
			} else {
				Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[CE] WARNING: Could not configurate the CE-Item '" + getOriginalName() + "',");
				Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[CE] 		 please check the config for any errors, the item is now disabled. ");
				Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[CE] 		 Explicit error: " + e.getMessage());
				Main.items.remove(this);
			}
		}
		
		if(this.description.get(description.size()-1).length() > 0)
			this.description.add("");

		
	}
	
}
