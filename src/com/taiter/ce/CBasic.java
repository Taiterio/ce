package com.taiter.ce;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;


public abstract class CBasic {
	
	
	
	
	protected Plugin	     main               = Main.plugin;
	protected Tools 		 tools				= Main.tools;
	
	
	protected List<Player>       cooldown 			= new ArrayList<Player>();
	protected String  			 displayName        ;
	protected String  			 originalName       ;
	protected String 			 typeString			;
	
	
	protected List<String> 		 configEntries 		= new ArrayList<String>(Arrays.asList(new String[]{"Enabled: true"}));
	
	public Plugin 				getPlugin()	 						{	return this.main;			}
	public Tools 				getTools()	 						{	return this.tools;			}
	
		
	public String 				getDisplayName() 					{	return this.displayName;	}
	
	public String 				getOriginalName() 					{	return this.originalName;	}

	public FileConfiguration 	getConfig() 						{	return Main.config;			}
		
	public abstract double 		getCost() 							;
	
	public String 				getType() 							{ return this.typeString;		}

}
