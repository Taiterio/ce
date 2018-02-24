package com.taiter.ce.Enchantments.Tool;

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

import com.taiter.ce.Enchantments.CEnchantment;
import com.taiter.ce.Tools;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class Explosive extends CEnchantment {

    int radius;
    boolean largerRadius;
    boolean dropItems;
    private Set<UUID> uuidSet = new HashSet<UUID>();

    public Explosive(Application app) {
        super(app);
        configEntries.put("radius", 3);
        configEntries.put("largerRadius", true);
        configEntries.put("dropItems", true);
        triggers.add(Trigger.BLOCK_BROKEN);
    }

    @Override
    public void effect(Event e, ItemStack item, int level) {
        BlockBreakEvent event = (BlockBreakEvent) e;
        Player player = event.getPlayer();
        if (uuidSet.contains(player.getUniqueId())) return;
        uuidSet.add(player.getUniqueId());
        if (!isUsable(player.getItemInHand().getType().toString(), event.getBlock().getType().toString()))
            return;

        List<Location> locations = new ArrayList<Location>();

        int locRad = radius;
        if (largerRadius && Tools.random.nextInt(100) < level * 5)
            locRad += 2;
        int r = locRad - 1;
        int start = r / 2;

        Location sL = event.getBlock().getLocation();

        player.getWorld().createExplosion(sL, 0f); // Create a fake explosion

        sL.setX(sL.getX() - start);
        sL.setY(sL.getY() - start);
        sL.setZ(sL.getZ() - start);

        for (int x = 0; x < locRad; x++)
            for (int y = 0; y < locRad; y++)
                for (int z = 0; z < locRad; z++)
                    if ((!(x == 0 && y == 0 && z == 0)) && (!(x == r && y == 0 && z == 0)) && (!(x == 0 && y == r && z == 0)) && (!(x == 0 && y == 0 && z == r)) && (!(x == r && y == r && z == 0))
                            && (!(x == 0 && y == r && z == r)) && (!(x == r && y == 0 && z == r)) && (!(x == r && y == r && z == r)))
                        locations.add(new Location(sL.getWorld(), sL.getX() + x, sL.getY() + y, sL.getZ() + z));

        for (Location loc : locations) {
            String iMat = item.getType().toString();
            Block block = loc.getBlock();
            String bMat = block.getType().toString();
            if (isUsable(iMat, bMat)) {
                BlockBreakEvent blockBreakEvent = new BlockBreakEvent(block, player);
                Bukkit.getServer().getPluginManager().callEvent(blockBreakEvent);
                if (blockBreakEvent.isCancelled()) return;
                if (!block.getDrops(item).isEmpty())
                    if (dropItems && blockBreakEvent.isDropItems())
                        block.breakNaturally(item);
                    else
                        for (ItemStack i : block.getDrops(item)) {
                            player.getInventory().addItem(i);
                            block.setType(Material.AIR);
                        }
            }
        }

        uuidSet.remove(player.getUniqueId());

    }

    // Checks if the Material of the block (bMat) is intended to be mined by the
    // item's Material (iMat)
    private boolean isUsable(String iMat, String bMat) {
        return (iMat.endsWith("PICKAXE") && (bMat.contains("ORE") || (!bMat.contains("STAIRS") && bMat.contains("STONE")) || bMat.equals("STAINED_CLAY") || bMat.equals("NETHERRACK")))
                || (iMat.endsWith("SPADE") && (bMat.contains("SAND") || bMat.equals("DIRT") || bMat.equals("SNOW_BLOCK") || bMat.equals("SNOW") || bMat.equals("MYCEL") || bMat.equals("CLAY")
                || bMat.equals("GRAVEL") || bMat.equals("GRASS")))
                || (iMat.endsWith("_AXE") && bMat.contains("LOG") || bMat.contains("PLANKS")) || (iMat.endsWith("HOE") && (bMat.equals("CROPS") || bMat.equals("POTATO") || bMat.equals("CARROT")));
    }

    @Override
    public void initConfigEntries() {
        radius = Integer.parseInt(getConfig().getString("Enchantments." + getOriginalName() + ".radius"));
        if (radius % 2 == 0)
            radius += 1;
        largerRadius = Boolean.parseBoolean(getConfig().getString("Enchantments." + getOriginalName() + ".largerRadius"));
        dropItems = Boolean.parseBoolean(getConfig().getString("Enchantments." + getOriginalName() + ".dropItems"));
    }

}
