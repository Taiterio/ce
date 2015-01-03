package com.taiter.ce;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import com.taiter.ce.CItems.CItem;
import com.taiter.ce.CItems.Swimsuit;
import com.taiter.ce.Enchantments.CEnchantment;



public class CeCommand {
	
	private Main main;
	private Boolean confirmUpdate = false; 
	
	public CeCommand(Main m) {
		this.main = m;
	}

	public String processCommand(CommandSender sender, String[] args) {

		String Success = ChatColor.GREEN + "[CE] ";
		String Error = ChatColor.RED + "[CE] ";
		String usageError = Error + "Correct Usage: /ce ";
		
		String requiredPermission = "ce.cmd.";
		
		
		if(args.length >= 1) {
			
			String name = args[0].toLowerCase();

			if(name.startsWith("r")) {
				
				requiredPermission += "reload";
				if(!sender.hasPermission(requiredPermission) && !sender.isOp())
					return Error + "You do not have permission to use this command.";

				
				Main.plugin.reloadConfig();
				Main.config = Main.plugin.getConfig();
				
				Main.enchantments.clear();
				Main.items.clear();
				main.initializeListener();
				
				Main.makeLists(true, false);
				
				
				//Get the maximum amount of Enchantments on an Item
		    	Main.maxEnchants = Integer.parseInt(Main.config.getString("Global.Enchantments.MaximumCustomEnchantments"));
		    	
		    	//Set the Loreprefix
		    	Main.lorePrefix = Main.tools.resolveEnchantmentColor();
				
				Main.tools.generateInventories();
				
				return Success + "The Custom Enchantments config has been reloaded successfully.";

			} else if(name.startsWith("u")) {
				if(sender.equals(Bukkit.getConsoleSender())) {
					
					usageError += "update <check/applyupdate>";
					
					if(args.length >= 2) {
						
						String toDo = args[1].toLowerCase();
						
						if(toDo.startsWith("c")) {
							if(!main.hasUpdated) {
								try {
									main.updateCheck();
								} catch (IOException e) {
									Error += "Failed to check for updates.";
									return Error;
								}
								return "";
							} else {
								Error += "You are already using the newest Version.";
								return Error;
							}
							
						} else if(toDo.equals("applyupdate")) {
							if(main.hasUpdate && !main.hasUpdated) {
								if(!confirmUpdate) {
									confirmUpdate = true;
									sender.sendMessage(ChatColor.GOLD + "[CE] Rerun the command to confirm the update (This expires in 5 Minutes).");
									new BukkitRunnable() {
										
										@Override
										public void run() {
											if(confirmUpdate)
												confirmUpdate = false;
										}
										
									}.runTaskLater(main, 6000l);
									return "";
								} else {
									try {
										main.update();
									} catch (IOException e) {}
									return "";
								}
							} else {
								Error += "You are already using the newest Version.";
								return Error;
							}
						}
							
					} else {
						return usageError;
					}
				} else {
					Error += "This command can only be run via Console";
					return Error;
				}
		    } else if(name.startsWith("g")) {
				usageError += "give <Player> <Material> <Enchantment:Level/Item> [Enchantment:Level] ...";
				if(args.length >= 4) {
					
					requiredPermission += "give";
					if(!sender.hasPermission(requiredPermission) && !sender.isOp())
						return Error + "You do not have permission to use this command.";
					
					Player target = null;
					
					for(Player ps : Bukkit.getOnlinePlayers())
						if(ps.getName().equalsIgnoreCase(args[1]))
							target = ps;
					
					if(target == null) {
						Error += "The Player '" + args[1] + "' was not found.";
						return Error;
					}
					
					if(target.getInventory().firstEmpty() < 0) {
						Error += "The Inventory of Player '" + args[1] + "' is full.";
						return Error;
					}
					
					Material mat = null;
					
					
					
					try{
						mat = Material.getMaterial(Integer.parseInt(args[2]));
					}catch(Exception e) {}
					
					if(mat == null) {
					
						try{
							mat = Material.valueOf(args[2].toUpperCase());
						}catch(Exception e) {
							Error += "The Material '" + args[2] + "' was not found.";
							return Error;
						}
					}
					
					String fullString = args[3];
					if(args.length > 4)
						for(int i = 4; i < args.length; i++)
							fullString += " " + args[i];
					
					fullString = fullString.toLowerCase();
					
					CItem custom 			= null;
					List<String> enchants 	= new ArrayList<String>();
					List<String> cEnchants 	= new ArrayList<String>();
					
					
					for(CItem ci : Main.items) {
						if(fullString.contains(ci.getOriginalName().toLowerCase())) {
							custom = ci;
							fullString.replace(ci.getOriginalName().toLowerCase(), "");
						} else if(fullString.contains(ci.getOriginalName().replace(" ", "").toLowerCase())) {
							custom = ci;
							fullString.replace(ci.getOriginalName().replace(" ", "").toLowerCase(), "");
						} else if(fullString.contains(ci.getDisplayName().toLowerCase())) {
							custom = ci;
							fullString.replace(ci.getDisplayName().toLowerCase(), "");
						} else if(fullString.contains(ci.getDisplayName().replace(" ", "").toLowerCase())) {
							custom = ci;
							fullString.replace(ci.getDisplayName().replace(" ", "").toLowerCase(), "");
						}
					}									
					

					
					for(int i = 0; i < fullString.split(" ").length; i++) {
							for(CEnchantment ce : Main.enchantments) {
								int level = 0;
								int index = 0;
								int endIndex = 0;
								String enchName = "";
								if(fullString.contains(ce.getOriginalName().toLowerCase())) {
									enchName = ce.getOriginalName().toLowerCase();
									index 	 = fullString.indexOf(enchName);
									endIndex = index + enchName.length() + 2; // Making a substring from index - endIndex returns the enchantment's name with the level
									
									if(endIndex <= fullString.length())
										enchName = fullString.substring(index, endIndex);
									else {
										endIndex = index + enchName.length() + 1;
										if(endIndex <= fullString.length())
											enchName = fullString.substring(index, endIndex);
									}

									
									if(enchName.endsWith(" "))
										enchName = fullString.substring(index, endIndex-1);
											
									if(enchName.contains(":"))  {
										String[] finalName = enchName.split(":");
										try {
											level = Integer.parseInt(finalName[1]);
										} catch (Exception e) {}
										enchName = finalName[0];
									}
									fullString = fullString.replace(enchName, "");
									cEnchants.add(ce.getDisplayName() + " " + level);
									
								} else if(fullString.contains(ce.getOriginalName().replace(" ", "").toLowerCase())) {

									enchName = ce.getOriginalName().replace(" ", "").toLowerCase();
									index 	 = fullString.indexOf(enchName);
									endIndex = index + enchName.length() + 2; // Making a substring from index - endIndex returns the enchantment's name with the level
									
									if(endIndex <= fullString.length())
										enchName = fullString.substring(index, endIndex);
									else {
										endIndex = index + enchName.length() + 1;
										if(endIndex <= fullString.length())
											enchName = fullString.substring(index, endIndex);
									}
									
									if(enchName.endsWith(" "))
										enchName = fullString.substring(index, endIndex-1);
											
									if(enchName.contains(":"))  {
										String[] finalName = enchName.split(":");
										try {
											level = Integer.parseInt(finalName[1]);
										} catch (Exception e) {}
										enchName = finalName[0];
									}
									fullString = fullString.replace(enchName, "");
									cEnchants.add(ce.getDisplayName() + " " + level);
									
								} else if(fullString.contains(ce.getDisplayName().toLowerCase())) {

									enchName = ce.getDisplayName().toLowerCase();
									index 	 = fullString.indexOf(enchName);
									endIndex = index + enchName.length() + 2; // Making a substring from index - endIndex returns the enchantment's name with the level
									
									if(endIndex <= fullString.length())
										enchName = fullString.substring(index, endIndex);
									else {
										endIndex = index + enchName.length() + 1;
										if(endIndex <= fullString.length())
											enchName = fullString.substring(index, endIndex);
									}

									
									if(enchName.endsWith(" "))
										enchName = fullString.substring(index, endIndex-1);
											
									if(enchName.contains(":"))  {
										String[] finalName = enchName.split(":");
										try {
											level = Integer.parseInt(finalName[1]);
										} catch (Exception e) {}
										enchName = finalName[0];
									}
									fullString = fullString.replace(enchName, "");
									cEnchants.add(ce.getDisplayName() + " " + level);
									
								} else if(fullString.contains(ce.getDisplayName().replace(" ", "").toLowerCase())) {
									
									enchName = ce.getDisplayName().replace(" ", "").toLowerCase();
									index 	 = fullString.indexOf(enchName);
									endIndex = index + enchName.length() + 2; // Making a substring from index - endIndex returns the enchantment's name with the level
									
									if(endIndex <= fullString.length())
										enchName = fullString.substring(index, endIndex);
									else {
										endIndex = index + enchName.length() + 1;
										if(endIndex <= fullString.length())
											enchName = fullString.substring(index, endIndex);
									}
									
									if(enchName.endsWith(" "))
										enchName = fullString.substring(index, endIndex-1);
											
									if(enchName.contains(":"))  {
										String[] finalName = enchName.split(":");
										try {
											level = Integer.parseInt(finalName[1]);
										} catch (Exception ex) {}
										enchName = finalName[0];
									}
									fullString = fullString.replace(enchName, "");
									cEnchants.add(ce.getDisplayName() + " " + level);
									
								}
							}
							for(Enchantment e : Enchantment.values()) {
								
								
								int level = 0;
								int index = 0;
								int endIndex = 0;
								String enchName = e.getName().toLowerCase();
																
								if(fullString.contains(enchName)) {
									
									index 	 = fullString.indexOf(enchName);
									endIndex = index + enchName.length() + 3; // Making a substring from index - endIndex returns the enchantment's name with the level

									if(endIndex <= fullString.length())
										enchName = fullString.substring(index, endIndex);
									else {
										endIndex = index + enchName.length() + 2;
										if(endIndex <= fullString.length())
											enchName = fullString.substring(index, endIndex);
									}
									
									if(enchName.endsWith(" "))
										enchName = fullString.substring(index, endIndex-1);
										
											
									if(enchName.contains(":"))  {
										String[] finalName = enchName.split(":");
										try {
											level = Integer.parseInt(finalName[1]);
										} catch (Exception ex) {}
										enchName = finalName[0];
									}
									fullString = fullString.replace(enchName, "");
									enchants.add(e.getName() + " " + level);
							}
						}
					}
					
					ItemStack newItem = new ItemStack(mat);
					ItemMeta im = newItem.getItemMeta();
					String targetNotification = ChatColor.GOLD + "[CE] ";
					
					if(custom != null) {
						if(target.hasPermission("ce.items." + custom.getOriginalName())) {
						CItem cust = Main.items.get(Main.items.indexOf(custom));
						im.setDisplayName(cust.getDisplayName());
						im.setLore(cust.getDescription());
						newItem.setItemMeta(im);
						if(custom instanceof Swimsuit) {//TODO:REPLACE
							
							int count = 0;
							
							for(ItemStack i : target.getInventory())
								if(i == null || i.getType().equals(Material.AIR))
									count++;
							
							if(count < 4) {
								Error += "The Inventory of Player '" + args[1] + "' is full.";
								return Error;
							}
							
							ItemStack cp = newItem.clone();
							ItemStack le = newItem.clone();
							ItemStack bo = newItem.clone();
							
							String[] parts = ((Swimsuit) custom).parts;
							
							cp.setType(Material.IRON_CHESTPLATE);
							le.setType(Material.IRON_LEGGINGS);
							bo.setType(Material.IRON_BOOTS);
							
							im.setDisplayName(parts[1]);
							cp.setItemMeta(im);
							im.setDisplayName(parts[2]);
							le.setItemMeta(im);
							im.setDisplayName(parts[3]);
							bo.setItemMeta(im);
							
							target.getInventory().addItem(newItem);
							target.getInventory().addItem(cp);
							target.getInventory().addItem(le);
							target.getInventory().addItem(bo);
						}
						Success += "The enchanted Item was given to Player " +  target.getName() +".";
						targetNotification += "You have received an enchanted item from " + sender.getName() + "!";
						} else {
							Error += target.getName() + " does not have the permission to use the item " + custom.getOriginalName() + ".";
							return Error;
						}
					}
					
					if(!enchants.isEmpty()) {
						for(String e : enchants) {
							String[] enchALvl = e.split(" ");
							Enchantment ench = Enchantment.getByName(enchALvl[0]);
							int level = 1;
							try {
								level = Integer.parseInt(enchALvl[1]);
							} catch (Exception ex) {}
							newItem.addUnsafeEnchantment(ench, level);
							im = newItem.getItemMeta();
						}
						if(Success.length() < 10) {
							Success += "The enchanted Item was successfully given to Player " +  target.getName() +".";
							targetNotification += "You have received an enchanted item from " + sender.getName() + "!";
						}
					}
					if(!cEnchants.isEmpty()) {
						List<String> lore = new ArrayList<String>();
						if(im.hasLore())
							lore = im.getLore();
						for(String e : cEnchants) {
							String[] enchALvl = e.split(" ");
							int level = 1;
							try {
								level = Integer.parseInt(enchALvl[enchALvl.length-1]);
							} catch (Exception ex) {}
							String finalEnchName = enchALvl[0];
							if(enchALvl.length > 2)
								for(int i = 1; i < enchALvl.length-1; i++)
									finalEnchName += " " + enchALvl[i];
							lore.add(finalEnchName + " " + Main.tools.intToLevel(level));
						}
						im.setLore(lore);
						if(Success.length() < 10) {
						Success += "The enchanted Item was successfully given to Player " +  target.getName() +".";
						targetNotification += "You have received an enchanted item from " + sender.getName() + "!";
						}
					}
					if(Success.length() > 10) {
						newItem.setItemMeta(im);
						target.getInventory().addItem(newItem);
						target.sendMessage(targetNotification);
						return Success;
					} else
						return Error + "No enchantments or items were found to be applied.";
					
					
				} else
					return usageError;
			}
			if(sender instanceof Player) {

				Player p = (Player) sender;

				if(name.startsWith("l")) {
					usageError += "list <Items/Enchantments>";
					if(args.length >= 2) {
						String toList = args[1].toLowerCase();
						if(toList.startsWith("i") ) {
							p.sendMessage(ChatColor.GOLD + "-------------Item List-------------");
							for(CItem ci : Main.items)
								if( p.isOp() || p.hasPermission("ce.item." + ci.getOriginalName()))
									p.sendMessage("   " + ci.getDisplayName());
							p.sendMessage(ChatColor.GOLD + "-----------------------------------");
							return "";
						} else if(toList.startsWith("e")) {
							p.sendMessage(ChatColor.GOLD + "----------Enchantment List-----------");
							for(CEnchantment ce : Main.enchantments)
								if( p.isOp() || p.hasPermission("ce.ench." + ce.getOriginalName()))
									p.sendMessage("   " + ce.getDisplayName());
							p.sendMessage(ChatColor.GOLD + "------------------------------------");
							return "";
						} else 
							return usageError;
					} else
						return usageError;

				
				} else if(name.startsWith("m")) {
					requiredPermission += "menu";
					if(!sender.hasPermission(requiredPermission) && !sender.isOp())
						return Error + "You do not have permission to use this command.";
					Main.tools.openCEMenu(p);
					return "";
				}
				
				
				ItemStack item = p.getItemInHand();
				

					if(name.startsWith("i") || name.startsWith("e")) {
						usageError += (name.startsWith("e") ? "enchant <Enchantment> <Level>" : "item <Item>");
						if(args.length >= 2) {
							
							requiredPermission += "enchant";
							if(!sender.hasPermission(requiredPermission) && !sender.isOp())
								return Error + "You do not have permission to use this command.";
							
							String customName = args[1];
							
							int level = 0;
							
							if(name.startsWith("e")) {
								if(item.getType().equals(Material.AIR))
									return Error + "You are not holding an item in your hand";
								try{
									level = Integer.parseInt(args[args.length-1]);
								} catch(Exception e) {}
							}
							
							if(level < 0)
								level *= -1;
							
							if(level > 10)
								level = 0;
							
							if(args.length > 2)
								for(int i = 2; i < (level == 0 ? args.length : args.length-1); i++)
									customName +=  " " +  args[i];
							
							
							CBasic custom = null;
							
							for(CEnchantment ce : Main.enchantments)
								if(ce.getOriginalName().equalsIgnoreCase(customName) || ChatColor.stripColor(ce.getDisplayName()).equalsIgnoreCase(customName) || ce.getOriginalName().replace(" ", "").equalsIgnoreCase(customName) || ChatColor.stripColor(ce.getDisplayName()).replace(" ", "").equalsIgnoreCase(customName)) {
									custom = ce;
								}
							for(CItem ci : Main.items)
								if(ci.getOriginalName().equalsIgnoreCase(customName) || ChatColor.stripColor(ci.getDisplayName()).equalsIgnoreCase(customName) || ci.getOriginalName().replace(" ", "").equalsIgnoreCase(customName) || ChatColor.stripColor(ci.getDisplayName()).replace(" ", "").equalsIgnoreCase(customName)){
									custom = ci;
									}
							if(custom == null) {
								Error += "The " + (name.startsWith("i") ? "item":"enchantment") +" '" + customName + "' does not exist.";
								return Error;
							}
							
							if(!p.hasPermission("ce." + (name.startsWith("i") ? "item":"ench") + "." + custom.getOriginalName())) {
								Error += "You do not have permission to use '" + customName + "'.";
								return Error;
							}
							
							List<String> lore = new ArrayList<String>();
							
							ItemMeta im = item.getItemMeta();
							
							if(item.hasItemMeta() && item.getItemMeta().hasLore())
								lore = item.getItemMeta().getLore();
							
							if(custom instanceof CEnchantment) {
								lore.add(custom.getDisplayName() + " " + Main.tools.intToLevel(level));
								im.setLore(lore);
								item.setItemMeta(im);
								p.setItemInHand(item);
								Success += "You have enchanted your item with '" + custom.getDisplayName() + ChatColor.GREEN + "'" + (level != 0 ? " level " + level : "") + "!";
							} else if(custom instanceof CItem){
								Material toSet = item.getType();
								if(toSet == Material.AIR)
									toSet = ((CItem) custom).getMaterial();
								ItemStack newItem = new ItemStack( toSet );
								ItemMeta newIm = newItem.getItemMeta();
								newIm.setDisplayName(custom.getDisplayName());
								newIm.setLore(((CItem) custom).getDescription());
								newItem.setItemMeta(newIm);
								if(custom instanceof Swimsuit) {//TODO:REPLACE
									
									int count = 0;
									
									for(ItemStack i : p.getInventory())
										if(i == null || i.getType().equals(Material.AIR))
											count++;
									
									if(count < 4) {
										Error += "Your inventory is full.";
										return Error;
									}
									
									ItemStack cp = newItem.clone();
									ItemStack le = newItem.clone();
									ItemStack bo = newItem.clone();
									
									String[] parts = ((Swimsuit) custom).parts;
									
									cp.setType(Material.IRON_CHESTPLATE);
									le.setType(Material.IRON_LEGGINGS);
									bo.setType(Material.IRON_BOOTS);
									
									im.setDisplayName(parts[1]);
									cp.setItemMeta(im);
									im.setDisplayName(parts[2]);
									le.setItemMeta(im);
									im.setDisplayName(parts[3]);
									bo.setItemMeta(im);
									
									p.getInventory().addItem(newItem);
									p.getInventory().addItem(cp);
									p.getInventory().addItem(le);
									p.getInventory().addItem(bo);
								} else 
									p.setItemInHand(newItem);
								
								Success += "You have created the item '" + custom.getDisplayName() + ChatColor.GREEN +  "'!";
							}
							return Success;
							
							} else
								return usageError;
							
						

					// Changes item's name/lore, color, etc
					} 
					

					if(item.getType() != Material.AIR) {
					if(name.startsWith("c")) {

						usageError += "change <name/lore> <color/set/add/reset> [New Value]";

						if(args.length >= 4) {
							
							requiredPermission += "change";
							if(!sender.hasPermission(requiredPermission) && !sender.isOp())
								return Error + "You do not have permission to use this command.";

							String toChange = args[1].toLowerCase();
							String option = args[2].toLowerCase();

							if(toChange.startsWith("n")) {
								
									
									ItemMeta im = item.getItemMeta();
									
									if(option.startsWith("s")) {

										String toSet = "";

										for(int i = 3; i < args.length - 1; i++) {
											toSet += args[i] + " ";
										}

										toSet += args[args.length - 1];

										im.setDisplayName(toSet);
										item.setItemMeta(im);
										return Success + "You have successfully set the item's Name!";

									}
									
								if(item.hasItemMeta()) {
									if(im.hasDisplayName()) {

										if(option.startsWith("c")) {

											if(ChatColor.valueOf(args[3].toUpperCase()) != null) {

												im.setDisplayName(ChatColor.valueOf(args[3].toUpperCase()) + "" + ChatColor.stripColor(im.getDisplayName()));
												item.setItemMeta(im);
												return Success + "You have successfully changed the item's Color!";

											}

											return Error + "The Color " + args[3] + " could not be found.";

										} else if(option.startsWith("a")) {

											String toSet = "";

											for(int i = 3; i < args.length - 1; i++) {
												toSet += args[i] + " ";
											}

											toSet += args[args.length - 1];

											im.setDisplayName(im.getDisplayName() + " " + toSet);
											item.setItemMeta(im);
											return Success + "You have successfully changed the item's Name!";

										} else if(option.startsWith("r")) {

											im.setDisplayName(null);
											item.setItemMeta(im);
											return Success + "You have successfully reset the item's Name!";

										}

									} else {

										return Error + "Your item does not have a name to be changed, use '/ce change name set' first.";

									}

								} else {

									return Error + "Your item does not have a name to be changed, use '/ce change name set' first.";

								}

								return usageError;

							} else if(toChange.startsWith("l")) {

								
									ItemMeta im = item.getItemMeta();
									
									if(option.startsWith("s")) {

										List<String> lore = new ArrayList<String>();
										
										String toSet = "";

										for(int i = 3; i < args.length-1; i++)
											toSet += args[i] + " ";
										toSet += args[args.length-1];
										
										lore.add(toSet);

										im.setLore(lore);
										item.setItemMeta(im);
										return Success + "You have successfully set the item's lore!";

									}
									
								if(item.hasItemMeta()) {
									if(item.getItemMeta().hasLore()) {

										List<String> lore = im.getLore();

										if(option.startsWith("c")) {

											if(ChatColor.valueOf(args[3].toUpperCase()) != null) {

												List<String> l = new ArrayList<String>();

												for(String i : lore) {
													l.add(ChatColor.valueOf(args[3].toUpperCase()) + "" + ChatColor.stripColor(i));
												}

												im.setLore(l);
												item.setItemMeta(im);
												return Success + "You have successfully changed the color of the item's lore!";

											}

											return Error + "The Color " + args[3] + " could not be found.";

										} else if(option.startsWith("a")) {

											String toSet = "";

											for(int i = 3; i < args.length - 1; i++) {
												toSet += args[i] + " ";
											}

											toSet += args[args.length - 1];

											lore.add(toSet);
											im.setLore(lore);
											item.setItemMeta(im);
											return Success + "You have successfully added the new line to the lore!";

										} else if(option.startsWith("r")) {

											im.setLore(null);
											item.setItemMeta(im);
											return Success + "You have successfully reset the item's lore!";

										}

									} else {

										return Error + "Your item does not have a lore to be changed, use '/ce change lore set' first.";

									}

								}

							}

						}

						return usageError;

					}

				} else {

					return Error + "You are not holding an item in your hand";

				}

			} else {

				return Error + "This command can only be used by players";

			}

		}

		usageError += "<Reload/List/Enchant/Change/Give/Update>";
		return usageError;

	}

}
