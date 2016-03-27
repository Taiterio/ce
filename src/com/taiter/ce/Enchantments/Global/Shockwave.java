package com.taiter.ce.Enchantments.Global;

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

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.taiter.ce.Tools;
import com.taiter.ce.Enchantments.CEnchantment;

public class Shockwave extends CEnchantment {

    int cooldown;
    List<Material> ForbiddenMaterials;

    public Shockwave(Application app) {
        super(app);
        configEntries.put("Cooldown", 200);
        configEntries.put("ForbiddenMaterials",
                "BEDROCK, WATER, STATIONARY_WATER, LAVA, STATIONARY_LAVA, CACTUS, CAKE_BLOCK, CROPS, TORCH, ENDER_PORTAL, PISTON_MOVING_PIECE, MELON_STEM, NETHER_WARTS, MOB_SPAWNER, CHEST, SIGN, WALL_SIGN, SIGN_POST, ITEM_FRAME");
        triggers.add(Trigger.DAMAGE_GIVEN);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void effect(Event e, ItemStack item, final int level) {

        final EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) e;
        Player damager = (Player) event.getDamager();

        new BukkitRunnable() {
            @Override
            public void run() {
                event.getEntity().setVelocity(new Vector(0, 1 + (level / 4), 0));
            }
        }.runTaskLater(getPlugin(), 1l);

        Location loc = damager.getLocation();
        loc.setY(damager.getLocation().getY() - 1);
        List<Location> list = Tools.getCone(loc);
        this.generateCooldown(damager, cooldown);
        damager.getWorld().playEffect(damager.getLocation(), Effect.ZOMBIE_DESTROY_DOOR, 10);
        for (final Location l : list) {
            final org.bukkit.block.Block block = l.getBlock();
            Material blockMat = block.getType();
            if (!ForbiddenMaterials.contains(blockMat) && checkSurrounding(block)) {

                if (!Tools.checkWorldGuard(l, damager, "PVP", false))
                    return;
                final Material mat = blockMat;
                final byte matData = block.getData();
                final FallingBlock b = l.getWorld().spawnFallingBlock(l, mat, block.getData());
                b.setDropItem(false);
                b.setVelocity(new Vector(0, (0.5 + 0.1 * (list.indexOf(l))) + (level / 4), 0));
                block.setType(Material.AIR);
                new BukkitRunnable() {
                    Location finLoc = l;

                    @Override
                    public void run() {
                        if (!b.isDead()) {
                            finLoc = b.getLocation();
                        } else {
                            //if(finLoc.getBlock().getLocation() != l) 
                            finLoc.getBlock().setType(Material.AIR);
                            block.setType(mat);
                            block.setData(matData);
                            this.cancel();
                        }
                    }
                }.runTaskTimer(main, 0l, 5l);
            }
        }

    }

    @Override
    public void initConfigEntries() {
        cooldown = Integer.parseInt(getConfig().getString("Enchantments." + getOriginalName() + ".Cooldown"));
        makeList();
    }

    private boolean checkSurrounding(org.bukkit.block.Block block) {

        if (!block.getRelative(0, 1, 0).getType().isSolid())
            return false;
        if (!block.getRelative(1, 0, 0).getType().isSolid())
            return false;
        if (!block.getRelative(-1, 0, 0).getType().isSolid())
            return false;
        if (!block.getRelative(0, 0, 1).getType().isSolid())
            return false;
        if (!block.getRelative(0, 0, -1).getType().isSolid())
            return false;
        return true;
    }

    @SuppressWarnings("deprecation")
    private void makeList() {
        ForbiddenMaterials = new ArrayList<Material>();
        String mS = getConfig().getString("Enchantments." + getOriginalName() + ".ForbiddenMaterials");
        mS = mS.replace(" ", "");

        String[] s = mS.split(",");

        for (int i = 0; i < s.length; i++)
            try {
                ForbiddenMaterials.add(Material.getMaterial(Integer.parseInt(s[i])));
            } catch (NumberFormatException ex) {
                ForbiddenMaterials.add(Material.getMaterial(s[i].toUpperCase()));
            }
        if (ForbiddenMaterials.contains(Material.AIR))
            ForbiddenMaterials.remove(Material.AIR);
    }

}
