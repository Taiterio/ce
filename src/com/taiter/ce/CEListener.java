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
import java.util.HashSet;
import java.util.List;

import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.Bukkit;
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
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
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

import com.taiter.ce.CItems.CItem;
import com.taiter.ce.Enchantments.CEnchantment;
import com.taiter.ce.Enchantments.CEnchantment.Application;


public class CEListener implements Listener {
	
	
	HashSet<CBasic> move 		    = new HashSet<CBasic>();
	HashSet<CBasic> interact 	    = new HashSet<CBasic>();
	HashSet<CBasic> interactE 	    = new HashSet<CBasic>();
	HashSet<CBasic> interactR 	    = new HashSet<CBasic>();
	HashSet<CBasic> interactL 	    = new HashSet<CBasic>();
	HashSet<CBasic> damageTaken	    = new HashSet<CBasic>();
	HashSet<CBasic> damageGiven     = new HashSet<CBasic>();
	HashSet<CBasic> damageNature    = new HashSet<CBasic>();
	HashSet<CBasic> shootBow        = new HashSet<CBasic>();
	HashSet<CBasic> projectileThrow = new HashSet<CBasic>();
	HashSet<CBasic> projectileHit   = new HashSet<CBasic>();
	HashSet<CBasic> death 		    = new HashSet<CBasic>();
	HashSet<CBasic> blockPlaced	    = new HashSet<CBasic>();
	HashSet<CBasic> blockBroken     = new HashSet<CBasic>();
	HashSet<CBasic> equipItem       = new HashSet<CBasic>();

	
	
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
				p.openInventory(Tools.getPreviousInventory(topInv.getTitle()));
				return;
			}
			
			//Opens the clicked Enchantments inventory and loads the permissions if needed
			if(topInv.getTitle().equals(Tools.prefix + "Enchantments")) {
				p.closeInventory();
				p.openInventory(Tools.getEnchantmentMenu(p, clickedItem.getItemMeta().getDisplayName()));
				return;
			}
			
			//Opens the item inventory and loads the permissions if needed
			if(topInv.getTitle().equals(Tools.prefix + "Main Menu") && event.getRawSlot() == 4) {
				p.closeInventory();
				p.openInventory(Tools.getItemMenu(p));
				return;
			}

			
			//These are the specific menus, clicking one of them will lead to the enchanting menu, which needs to be 'notified' of the enchantment to give and it's cost
			if(topInv.getTitle().equals(Tools.prefix + "Global") || topInv.getTitle().equals(Tools.prefix + "Bow") || topInv.getTitle().equals(Tools.prefix + "Armor") || topInv.getTitle().equals(Tools.prefix + "Helmet") || topInv.getTitle().equals(Tools.prefix + "Boots") || topInv.getTitle().equals(Tools.prefix + "Tool")) 
				if(p.isOp() || Tools.checkPermission(Tools.getEnchantmentByDisplayname(clickedItem.getItemMeta().getDisplayName()), p)) {
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
			if(topInv.getTitle().equals(Tools.prefix + "Items")) 
				if(p.isOp() || Tools.checkPermission(Tools.getItemByDisplayname(clickedItem.getItemMeta().getDisplayName()), p)) {
					
					
					Inventory approveMenu = Main.CEApproveMenu;
					approveMenu.setItem(0, clickedItem);
					p.closeInventory();
					p.openInventory(approveMenu);
					return;
				} else {
					p.sendMessage(ChatColor.RED + "[CE] You do not have permission to buy this Item.");
					return;
				}
			
			
			if(topInv.getTitle().equals(Tools.prefix + "Enchanting") || topInv.getTitle().equals(Tools.prefix + "Item Creation")) {
				double cost = 0;
				ItemStack item = clickedItem;
				ItemMeta im = item.getItemMeta();
				String type = "";
				String successString = "";
				Boolean itemSet = false; //TODO: Solve this by adding item-types
				//Swimsuit swimsuit = (Swimsuit) Main.items.get(9);

				if(topInv.getTitle().equals(Tools.prefix + "Enchanting")) {
					if(event.getRawSlot() > topInv.getSize() && event.isLeftClick()) {
					CEnchantment ce = Tools.getEnchantmentByDisplayname(topInv.getContents()[0].getItemMeta().getDisplayName());
					cost = ce.getCost();
					type = "enchantment " + ce.getDisplayName();
					if(!ce.getApplication().equals(Application.GLOBAL) && !Tools.isApplicationCorrect(ce.getApplication(), clickedItem.getType())) {
						String appS = ce.getApplication().toString().toLowerCase();
						p.sendMessage(ChatColor.RED + "[CE] This enchantment can only be applied to " + ChatColor.GRAY + (appS.endsWith("s") ? appS : appS + "s") + ChatColor.RED + ".");
						return;
					}
					List<String> lore = new ArrayList<String>();
					if(clickedItem.getItemMeta().hasLore()){
						lore = clickedItem.getItemMeta().getLore();
						int number = 0;
						for(String s : lore) {
							CEnchantment c = Tools.getEnchantmentByDisplayname(s);
							if(c != null && Tools.checkForEnchantment(s, c)) {
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
					
				} else if(topInv.getTitle().equals(Tools.prefix + "Item Creation")) {
					item = topInv.getContents()[0];
					cost = Tools.getItemByDisplayname(item.getItemMeta().getDisplayName()).getCost();
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
							Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[CE] An economy error has occured:");
							Bukkit.getConsoleSender().sendMessage(ChatColor.RED + 	ecr.errorMessage);
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
			p.openInventory(Tools.getNextInventory(clickedItem.getItemMeta().getDisplayName()));
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
			CEventHandler.handleEvent((Player) damaged, e, damageTaken);
		if(damager instanceof Player) 
			CEventHandler.handleEvent((Player) damager, e, damageGiven); 
		else if(damager instanceof Arrow)
			if(damager.hasMetadata("ce.bow.item") || damager.hasMetadata("ce.bow.enchantment"))
				CEventHandler.handleBows((Player) ((Projectile) damager).getShooter(), e);
		
		
	}

	
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void EntityDamageEvent(EntityDamageEvent e) {
		
		Entity 		damaged = e.getEntity();
		
		if(damaged instanceof Player) {
			
			CEventHandler.handleEvent((Player) damaged, e, damageNature);
			
			if(damaged.hasMetadata("ce.springs")) {
				e.setCancelled(true);
				Vector vel = damaged.getVelocity();
				vel.setY( (vel.getY() * -0.75 ) > 1 ? vel.getY() * -0.75 : 0);
				damaged.setVelocity(vel);
			}
			
				
		}
		
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void EntityExplodeEvent(EntityExplodeEvent e) {
		
		if(e.getEntity() != null && e.getEntity().hasMetadata("ce.explosive")) {
			e.getEntity().remove();
			e.setCancelled(true);
		}
		
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void EntityShootBowEvent(EntityShootBowEvent e) {

		Entity 		shooter 	= e.getEntity();
		
		if(shooter instanceof Player)
			CEventHandler.handleEvent((Player) shooter, e, shootBow);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void ProjectileHitEvent(ProjectileHitEvent e) {

		ProjectileSource shooter = e.getEntity().getShooter();
		
		if(shooter instanceof Player) {
			if(e.getEntity().hasMetadata("ce.projectile.item")) {
				CItem ci = Tools.getItemByOriginalname(e.getEntity().getMetadata("ce.projectile.item").get(0).asString());
				if(ci != null)
					ci.effect(e, (Player) shooter);
			}
		}
	}

	
	//PLAYER: org.bukkit.event.player
	
//	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
//	public void PlayerDeathEvent(PlayerDeathEvent e) {
//		
//		CEventHandler.handleEvent(e.getEntity(), e, deathEnchantments);
//		
//	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void ProjectileLaunchEvent(ProjectileLaunchEvent e) {
	
		ProjectileSource shooter = e.getEntity().getShooter();
		
		if(shooter instanceof Player)
			CEventHandler.handleEvent((Player) shooter, e, projectileThrow);
		
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void SignChangeEvent(SignChangeEvent e) {
		if(e.getLine(0).equals("[CustomEnchant]") && !e.getPlayer().isOp())
			e.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void PlayerInteractEvent(PlayerInteractEvent e) {
				
		CEventHandler.handleEvent(e.getPlayer(), e, interact);
		
		if(e.getAction().toString().startsWith("LEFT"))
			CEventHandler.handleEvent(e.getPlayer(), e, interactL);
		else if(e.getAction().toString().startsWith("RIGHT"))
			CEventHandler.handleEvent(e.getPlayer(), e, interactR);
		
		if(e.getClickedBlock() != null && e.getClickedBlock() instanceof Sign) 
			if(((Sign) e.getClickedBlock()).getLine(0).equals("[CustomEnchant]")) {
				Player p = e.getPlayer();
				if(!Main.hasEconomy) {
					p.performCommand("/ce menu");
				} else if(p.getItemInHand().getType() != Material.AIR){
				Sign sign = ((Sign) e.getClickedBlock());
				CEnchantment ce = Tools.getEnchantmentByDisplayname(sign.getLine(1));
				if(ce == null)
					Tools.getEnchantmentByOriginalname(sign.getLine(1));
				if(ce == null)
					for(CEnchantment ceT : Main.enchantments)
						if(Tools.checkForEnchantment(sign.getLine(1), ceT))
							ce = ceT;
				if(ce == null)
					return;
				
				ItemStack inHand = p.getItemInHand();
				if(!Tools.isApplicable(inHand, ce)) {
					p.sendMessage(ChatColor.RED + "[CE] This enchantment can not be applied to this item.");
					return;
				}
				
				int cost = 0;
				try {
					cost = Integer.parseInt(sign.getLine(3).replaceAll("\\D+",""));
				} catch(NumberFormatException ex) {
					return;
				}
				
				if(inHand.hasItemMeta() && inHand.getItemMeta().hasLore()) {
					List<String> lore = inHand.getItemMeta().getLore();
					for(int i = 0; i < lore.size(); i++)
						if(Tools.checkForEnchantment(lore.get(i), ce)) {
							int newLevel = Tools.getLevel(lore.get(i))+1;
							if(newLevel <= ce.getEnchantmentMaxLevel()) {
								if(Main.econ.getBalance(p.getName()) >= cost) {
									EconomyResponse ecr = Main.econ.withdrawPlayer(p.getName(), cost);
									if(ecr.transactionSuccess())
										p.sendMessage(ChatColor.GREEN + "[CE] Upgraded enchantment " + ce.getDisplayName() + ChatColor.RESET + "" + ChatColor.GREEN  + " for " + cost + " " + ((cost == 1) ? Main.econ.currencyNameSingular() : Main.econ.currencyNamePlural()));
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
								lore.set(i, ce.getDisplayName() + " " + Tools.intToLevel(newLevel));
								ItemMeta im = inHand.getItemMeta();
								im.setLore(lore);
								inHand.setItemMeta(im);
								return;
							} else {
								p.sendMessage(ChatColor.RED + "[CE] You already have the maximum level of this enchantment!");
								return;
							}
						}	
					}
				
				List<String> lore = new ArrayList<String>();
				ItemMeta im = inHand.getItemMeta();
				
				if(inHand.getItemMeta().hasLore())
					lore = im.getLore();
				
				if(Main.econ.getBalance(p.getName()) >= cost) {
					EconomyResponse ecr = Main.econ.withdrawPlayer(p.getName(), cost);
					if(ecr.transactionSuccess())
						p.sendMessage(ChatColor.GREEN + "[CE] Bought enchantment " + ce.getDisplayName() + ChatColor.RESET + "" + ChatColor.GREEN  + " for " + cost + " " + ((cost == 1) ? Main.econ.currencyNameSingular() : Main.econ.currencyNamePlural()));
					else {
						Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[CE] An economy error has occured:");
						Bukkit.getConsoleSender().sendMessage(ChatColor.RED + 	ecr.errorMessage);
						p.closeInventory();
						return;
					}
				} else {
					p.sendMessage(ChatColor.RED + "[CE] You do not have enough money to buy this!");
					p.closeInventory();
					return;
				}
				
				lore.add(ce.getDisplayName() + " I");
				im.setLore(lore);
				inHand.setItemMeta(im);
				return;
				
				
				}
			}
		
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void PlayerInteractEntityEvent(PlayerInteractEntityEvent e) {
				
		CEventHandler.handleEvent(e.getPlayer(), e, interactE);
		
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void PlayerMoveEvent(PlayerMoveEvent e) {
		
		
		
		Location from 	= e.getFrom();
		Location to 	= e.getTo();


				
		if(from.getBlockX() != to.getBlockX() || from.getBlockY() != to.getBlockY() || from.getBlockZ() != to.getBlockZ() ) {
				
		CEventHandler.handleEvent(e.getPlayer(), e, move);
		CEventHandler.handleMines(e.getPlayer(), e);
		
		}
		
	}
	
	
	
	//BLOCKS: org.bukkit.event.block
	
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void BlockPlaceEvent(BlockPlaceEvent e) {
		
		CEventHandler.handleEvent(e.getPlayer(), e, blockPlaced);
		
	}
	
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void BlockBreakEvent(BlockBreakEvent e) {
		
		if(e.getBlock().hasMetadata("ce.Ice"))
			e.setCancelled(true);
		
		CEventHandler.handleEvent(e.getPlayer(), e, blockBroken);
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
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void BlockFromToEvent(BlockFromToEvent e) {
		
		if(e.getBlock().hasMetadata("ce.Ice"))
			e.setCancelled(true);
		
	}
	
	
	
	//ENCHANTMENT: org.bukkit.event.enchantment
	
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void EnchantItemEvent(EnchantItemEvent e) {
		
		if(Tools.random.nextInt(100) < (Integer.parseInt(Main.config.getString("Global.Enchantments.CEnchantingProbability")))) {
			CEventHandler.handleEnchanting(e);
		}
		
			
	}
	
	
	
	//WORLD: org.bukkit.event.world
	
	
	
	
	
	

}
