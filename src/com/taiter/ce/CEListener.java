package com.taiter.ce;

/*
* This file is part of Custom Enchantments
* Copyright (C) Taiterio 2015
*
* This program is free software: you can redistribute it and/or modify it
* under the terms of the GNU Lesser General Public License as published by the
* Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful, but WITHOUT
* ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
* FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
* for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/


import java.util.ArrayList;
import java.util.List;

import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

import com.taiter.ce.Enchantments.CEnchantment;
import com.taiter.ce.Enchantments.CEnchantment.Application;


public class CEListener implements Listener {
	
	
	List<CEnchantment> moveEnchantments 		= new ArrayList<CEnchantment>();
	List<CEnchantment> clickEnchantments 		= new ArrayList<CEnchantment>();
	List<CEnchantment> interactEnchantments 	= new ArrayList<CEnchantment>();
	List<CEnchantment> damageTakenEnchantments	= new ArrayList<CEnchantment>();
	List<CEnchantment> damageGivenEnchantments 	= new ArrayList<CEnchantment>();
	List<CEnchantment> bowEnchantments			= new ArrayList<CEnchantment>();
	List<CEnchantment> deathEnchantments 		= new ArrayList<CEnchantment>();
	List<CEnchantment> blockPlaceEnchantments	= new ArrayList<CEnchantment>();
	List<CEnchantment> blockBreakEnchantments 	= new ArrayList<CEnchantment>();
	
	
	/*
	 * Almost all priorities are set to Monitor, the highest priority possible, 
	 * which allows CE to check if an event has been cancelled by any kind of plugin beforehand.
	 * 
	 * This is due to the fact that CE rarely cancels Events itself; 
	 * with the addition of all events ignoring cancelled events this becomes apparent.
	 * 
	 * If it is needed for an effect to cancel an event however, the priority HIGHEST is used, 
	 * as cancelling the event this late fits best for not interfering with other plugins too much.
	 * 
	 */
	
	//Inventory menu
	//PREVENTION of taking items out
	@EventHandler(priority = EventPriority.HIGHEST)
	public void inventoryMenuPrevention(InventoryDragEvent event) {
		if(event.getView().getTopInventory().getTitle().startsWith("CE")) 
			event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void inventoryMenuPrevention(InventoryCreativeEvent event) {
		if(event.getView().getTopInventory().getTitle().startsWith("CE")) 
			event.setCancelled(true);
	}
	//PREVENTION
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void inventoryMenu(final InventoryClickEvent event) {
		if(event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR || event.getRawSlot() < -1) {
			return;
		}
		if(event.getView().getTopInventory().getTitle().startsWith("CE")) {
			Inventory topInv = event.getView().getTopInventory();
			final Player p = (Player) event.getWhoClicked();
			ItemStack clickedItem = event.getCurrentItem();
			event.setResult(Result.ALLOW);

			event.setCancelled(true);
			
			//This is the back-button, located in the very last spot of each inventory
			if((event.getRawSlot() == topInv.getSize()-1) && event.getCurrentItem().getType() != Material.AIR) { 
				p.closeInventory();
				p.openInventory(Main.tools.getPreviousInventory(topInv.getTitle()));
				return;
			}
			
			//Opens the clicked Enchantments inventory and loads the permissions if needed
			if(topInv.getTitle().equals(Main.tools.prefix + "Enchantments")) {
				p.closeInventory();
				p.openInventory(Main.tools.getEnchantmentMenu(p, clickedItem.getItemMeta().getDisplayName()));
				return;
			}
			
			//Opens the item inventory and loads the permissions if needed
			if(topInv.getTitle().equals(Main.tools.prefix + "Main Menu") && event.getRawSlot() == 4) {
				p.closeInventory();
				p.openInventory(Main.tools.getItemMenu(p));
				return;
			}

			
			//These are the specific menus, clicking one of them will lead to the enchanting menu, which needs to be 'notified' of the enchantment to give and it's cost
			if(topInv.getTitle().equals(Main.tools.prefix + "Global") || topInv.getTitle().equals(Main.tools.prefix + "Bow") || topInv.getTitle().equals(Main.tools.prefix + "Armor") || topInv.getTitle().equals(Main.tools.prefix + "Helmet") || topInv.getTitle().equals(Main.tools.prefix + "Boots") || topInv.getTitle().equals(Main.tools.prefix + "Tool")) 
				if(p.isOp() || p.hasPermission("ce.ench." + (Main.tools.getEnchantmentByDisplayname(clickedItem.getItemMeta().getDisplayName()).getOriginalName()) )) {
					Inventory enchantingMenu = Main.CEEnchantingMenu;
					enchantingMenu.setItem(0, clickedItem);
					p.closeInventory();
					p.openInventory(enchantingMenu);
					return;
				} else {
					p.sendMessage(ChatColor.RED + "[CE] You do not have permission to buy this Enchantment.");
					return;
				}
			
			//This opens the Item Creation Menu
			if(topInv.getTitle().equals(Main.tools.prefix + "Items")) 
				if(p.isOp() || p.hasPermission("ce.item." + (Main.tools.getItemByDisplayname(clickedItem.getItemMeta().getDisplayName()).getOriginalName()) )) {
					
					
					Inventory approveMenu = Main.CEApproveMenu;
					approveMenu.setItem(0, clickedItem);
					p.closeInventory();
					p.openInventory(approveMenu);
					return;
				} else {
					p.sendMessage(ChatColor.RED + "[CE] You do not have permission to buy this Item.");
					return;
				}
			
			
			if(topInv.getTitle().equals(Main.tools.prefix + "Enchanting") || topInv.getTitle().equals(Main.tools.prefix + "Item Creation")) {
				double cost = 0;
				ItemStack item = clickedItem;
				ItemMeta im = item.getItemMeta();
				String type = "";
				String successString = "";
				Boolean itemSet = false; //TODO: Solve this by adding item-types
				//Swimsuit swimsuit = (Swimsuit) Main.items.get(9);

				if(topInv.getTitle().equals(Main.tools.prefix + "Enchanting")) {
					if(event.getRawSlot() > topInv.getSize() && event.isLeftClick()) {
					CEnchantment ce = Main.tools.getEnchantmentByDisplayname(topInv.getContents()[0].getItemMeta().getDisplayName());
					cost = ce.getCost();
					type = "enchantment " + ce.getDisplayName();
					if(!ce.getApplication().equals(Application.GLOBAL) && !Main.tools.isApplicationCorrect(ce.getApplication(), clickedItem.getType())) {
						String appS = ce.getApplication().toString().toLowerCase();
						p.sendMessage(ChatColor.RED + "[CE] This enchantment can only be applied to " + ChatColor.GRAY + (appS.endsWith("s") ? appS : appS + "s") + ChatColor.RED + ".");
						return;
					}
					List<String> lore = new ArrayList<String>();
					if(clickedItem.getItemMeta().hasLore()){
						lore = clickedItem.getItemMeta().getLore();
						int number = 0;
						for(String s : lore) {
							CEnchantment c = Main.tools.getEnchantmentByDisplayname(s);
							if(c != null && c.getClass() == ce.getClass()) {
								p.sendMessage(ChatColor.RED + "[CE] This item already has this enchantment!");
								return;
							}
							if(c != null)
								number++;
						}
						if(Main.maxEnchants > 0 && number >= Main.maxEnchants) {
							p.sendMessage(ChatColor.RED + "[CE] This item has already reached the maximum of " + ChatColor.GRAY + Main.maxEnchants + ChatColor.RED + " enchantments.");
							return;
						}
					}
					
					lore.add(ce.getDisplayName());
					im.setLore(lore);
					item.setItemMeta(im);
					} else
						return;
					
				} else if(topInv.getTitle().equals(Main.tools.prefix + "Item Creation")) {
					item = topInv.getContents()[0];
//					if(item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equals(swimsuit.getDisplayName())) { //TODO: Always keep the position of the Swimsuit updated
//						
//						
//						itemSet = true; 
//						
//						int count = 0;
//						
//						for(ItemStack i : event.getView().getBottomInventory())
//							if(i == null || i.getType().equals(Material.AIR))
//								count++;
//						
//						if(count < 4) {
//							p.sendMessage(ChatColor.RED + "[CE] Your Inventory is full!");
//							return;
//						}
//						
//						
//					} else {
					
						if(event.getView().getBottomInventory().firstEmpty() < 0) {
							p.sendMessage(ChatColor.RED + "[CE] Your Inventory is full!");
							return;
						}
					
//					}
					
					ItemMeta itemIm = item.getItemMeta();
					List<String> lore = itemIm.getLore();
					lore.remove(lore.size() - 1 );
					itemIm.setLore(lore);
					item.setItemMeta(itemIm);
					type = "item " + itemIm.getDisplayName();
				} 
				
				//TODO: Fix the cost for items
				//      rewrite the whole section to check which enchantment or item is selected
				
				successString = (ChatColor.GREEN + "[CE] You have created the " + type + ChatColor.GREEN + ".");
				
				if(!p.isOp() && cost != 0) {
					if(Main.econ.getBalance(p.getName()) >= cost) {
						EconomyResponse ecr = Main.econ.withdrawPlayer(p.getName(), cost);
						if(ecr.transactionSuccess())
							successString = (ChatColor.GREEN + "[CE] Purchased " + type + ChatColor.GREEN + " for " + cost + " " + ((cost == 1) ? Main.econ.currencyNameSingular() : Main.econ.currencyNamePlural()));
						else {
							p.sendMessage(ChatColor.RED + "[CE] An economy error has occured:");
							p.sendMessage(ChatColor.RED + 	ecr.errorMessage);
							p.closeInventory();
							return;
						}
					} else {
						p.sendMessage(ChatColor.RED + "[CE] You do not have enough money to buy this!");
						p.closeInventory();
						return;
					}
				}
				if(item != null) {
					if(!itemSet)
						if(type.startsWith("item") && event.getRawSlot() < topInv.getSize())
							p.setItemInHand(item);
						else
							p.getInventory().setItem(event.getSlot(), item);
//					else {
//						ItemStack cp = item.clone();
//						ItemStack le = item.clone();
//						ItemStack bo = item.clone();
//						
//						String[] parts = swimsuit.parts;
//						
//						cp.setType(Material.IRON_CHESTPLATE);
//						le.setType(Material.IRON_LEGGINGS);
//						bo.setType(Material.IRON_BOOTS);
//						
//						im.setDisplayName(parts[1]);
//						cp.setItemMeta(im);
//						im.setDisplayName(parts[2]);
//						le.setItemMeta(im);
//						im.setDisplayName(parts[3]);
//						bo.setItemMeta(im);
//						
//						p.getInventory().addItem(item);
//						p.getInventory().addItem(cp);
//						p.getInventory().addItem(le);
//						p.getInventory().addItem(bo);
//
//					}
				}
				p.closeInventory();
				p.sendMessage(successString);
				return;
			}
			
			p.closeInventory();
			try {
			p.openInventory(Main.tools.getNextInventory(clickedItem.getItemMeta().getDisplayName()));
			} catch (Exception e) {
				p.sendMessage(ChatColor.RED + "[CE] This feature is disabled.");
			}
			
		
			
		}
	}
	
	
	//ENTITIES: org.bukkit.event.entity
	
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void antiArrowSpam(ProjectileHitEvent event) { // Destroys the Arrows of the Minigun
		if (event.getEntityType().equals(EntityType.ARROW)) {
		Arrow arrow = (Arrow) event.getEntity();
		ProjectileSource shooter = arrow.getShooter();
			if (shooter instanceof Player) 
				if (arrow.hasMetadata("ce.minigunarrow")) 
					if (((Player) shooter).getGameMode().equals(GameMode.CREATIVE)) 
					arrow.remove();
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void EntityDamageByEntityEvent(EntityDamageByEntityEvent e) {
		
		Entity 		damager = e.getDamager();
		Entity 		damaged = e.getEntity();
		
		if(damaged instanceof Player)
			Main.tools.handleEvent((Player) damaged, e, damageTakenEnchantments);
		if(damager instanceof Player) 
			Main.tools.handleEvent((Player) damager, e, damageGivenEnchantments); 
		else if(damager instanceof Arrow)
			if(damager.hasMetadata("ce.bow.item") || damager.hasMetadata("ce.bow.enchantment"))
				Main.tools.handleBows((Player) ((Projectile) damager).getShooter(), e);
		
		
	}
	
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void EntityDamageEvent(EntityDamageEvent e) {
		
		Entity 		damaged = e.getEntity();
		
		if(damaged instanceof Player) {
			
			Main.tools.handleEvent((Player) damaged, e, damageTakenEnchantments);
			
			if(damaged.hasMetadata("ce.springs")) {
				e.setCancelled(true);
				Vector vel = damaged.getVelocity();
				vel.setY( (vel.getY() * -0.75 ) > 1 ? vel.getY() * -0.75 : 0);
				damaged.setVelocity(vel);
			}
			
				
		}
		
	}
	
	
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void EntityShootBowEvent(EntityShootBowEvent e) {
		
		Entity 		shooter 	= e.getEntity();
		
		if(shooter instanceof Player)
			Main.tools.handleEvent((Player) shooter, e, bowEnchantments);
		
	}
	
	
	
	//PLAYER: org.bukkit.event.player
	
//	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
//	public void PlayerDeathEvent(PlayerDeathEvent e) {
//		
//		Main.tools.handleEvent(e.getEntity(), e, deathEnchantments);
//		
//	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void PlayerInteractEvent(PlayerInteractEvent e) {
				
		Main.tools.handleEvent(e.getPlayer(), e, clickEnchantments);
		
		if(e.getClickedBlock() != null && e.getClickedBlock() instanceof Sign) 
			if(((Sign) e.getClickedBlock()).getLine(0).equals("[CustomEnchant]"))
				Main.tools.openCEMenu(e.getPlayer());
		
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void PlayerInteractEntityEvent(PlayerInteractEntityEvent e) {
				
		Main.tools.handleEvent(e.getPlayer(), e, interactEnchantments);
		
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void PlayerMoveEvent(PlayerMoveEvent e) {
		
		Location from 	= e.getFrom();
		Location to 	= e.getTo();
				
		if(from.getBlockX() != to.getBlockX() || from.getBlockY() != to.getBlockY() || from.getBlockZ() != to.getBlockZ() ) {
				
		Main.tools.handleEvent(e.getPlayer(), e, moveEnchantments);
		Main.tools.handleMines(e.getPlayer(), e);
		
		}
		
	}
	
	
	
	//BLOCKS: org.bukkit.event.block
	
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void BlockPlaceEvent(BlockPlaceEvent e) {
		
		Main.tools.handleEvent(e.getPlayer(), e, blockPlaceEnchantments);
		
	}
	
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void BlockBreakEvent(BlockBreakEvent e) {
		
		Main.tools.handleEvent(e.getPlayer(), e, blockBreakEnchantments);
		if(e.getBlock().hasMetadata("ce.mine")) {
			Block b = e.getBlock();
			b.removeMetadata("ce.mine", Main.plugin);
			Block[] blocks = {
					b.getRelative(0,1,0),
					b.getRelative(1,0,0),
					b.getRelative(-1,0,0),
					b.getRelative(0,0,1),
					b.getRelative(0,0,-1)
					};
			
			for(Block block : blocks) {
				if(block.hasMetadata("ce.mine.secondary")) {
					String[] s = block.getMetadata("ce.mine.secondary").get(0).asString().split(" ");
					Location loc = new Location(e.getPlayer().getWorld(), Integer.parseInt(s[0]), Integer.parseInt(s[1]), Integer.parseInt(s[2]));
					Location blockLoc = b.getLocation();
					if(loc.getBlockX() == blockLoc.getBlockX() && loc.getBlockY() == blockLoc.getBlockY() && loc.getBlockZ() == blockLoc.getBlockZ())
						block.removeMetadata("ce.mine.secondary", Main.plugin);
				}
			}
		}
		
	}
	
	
	
	//ENCHANTMENT: org.bukkit.event.enchantment
	
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void EnchantItemEvent(EnchantItemEvent e) {
		
		if(Main.tools.random.nextInt(100) < (Integer.parseInt(Main.config.getString("Global.Enchantments.CEnchantingProbability")))) {
			Main.tools.handleEnchanting(e, Main.tools.random);
		}
		
			
	}
	
	
	
	//WORLD: org.bukkit.event.world
	
	
	
	
	
	

}
