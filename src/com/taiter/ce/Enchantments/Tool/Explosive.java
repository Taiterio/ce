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

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import com.taiter.ce.Tools;
import com.taiter.ce.Enchantments.CEnchantment;

public class Explosive extends CEnchantment {

    int Radius;
    boolean LargerRadius;
    boolean DropItems;

    public Explosive(Application app) {
        super(app);
        configEntries.put("Radius", 3);
        configEntries.put("LargerRadius", true);
        configEntries.put("DropItems", true);
        triggers.add(Trigger.BLOCK_BROKEN);
    }

    @Override
    public void effect(Event e, ItemStack item, int level) {
        BlockBreakEvent event = (BlockBreakEvent) e;
        Player player = event.getPlayer();

        if (!isUsable(player.getItemInHand().getType().toString(), event.getBlock().getType().toString()))
            return;

        List<Location> locations = new ArrayList<Location>();

        int locRad = Radius;
        if (LargerRadius && Tools.random.nextInt(100) < level * 5)
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
            Block b = loc.getBlock();
            String bMat = b.getType().toString();

            if (isUsable(iMat, bMat))
                if (!loc.getBlock().getDrops(item).isEmpty())
                    if (Tools.checkWorldGuard(loc, player, "BUILD", false))
                        if (DropItems)
                            loc.getBlock().breakNaturally(item);
                        else
                            for (ItemStack i : loc.getBlock().getDrops(item)) {
                                player.getInventory().addItem(i);
                                loc.getBlock().setType(Material.AIR);
                            }
        }

    }

    // Checks if the Material of the block (bMat) is intended to be mined by the
    // item's Material (iMat)
    private boolean isUsable(String iMat, String bMat) {
        if ((iMat.endsWith("PICKAXE") && (bMat.contains("ORE") || (!bMat.contains("STAIRS") && bMat.contains("STONE")) || bMat.equals("STAINED_CLAY") || bMat.equals("NETHERRACK")))
                || (iMat.endsWith("SPADE") && (bMat.contains("SAND") || bMat.equals("DIRT") || bMat.equals("SNOW_BLOCK") || bMat.equals("SNOW") || bMat.equals("MYCEL") || bMat.equals("CLAY")
                || bMat.equals("GRAVEL") || bMat.equals("GRASS")))
                || (iMat.endsWith("_AXE") && bMat.contains("LOG") || bMat.contains("PLANKS")) || (iMat.endsWith("HOE") && (bMat.equals("CROPS") || bMat.equals("POTATO") || bMat.equals("CARROT"))))
            return true;
        return false;
    }

    @Override
    public void initConfigEntries() {
        Radius = Integer.parseInt(getConfig().getString("Enchantments." + getOriginalName() + ".Radius"));
        if (Radius % 2 == 0)
            Radius += 1;
        LargerRadius = Boolean.parseBoolean(getConfig().getString("Enchantments." + getOriginalName() + ".LargerRadius"));
        DropItems = Boolean.parseBoolean(getConfig().getString("Enchantments." + getOriginalName() + ".DropItems"));
    }

}
