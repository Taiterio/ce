package com.taiter.ce.Enchantments.Boots;

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

import java.util.List;

import org.bukkit.Effect;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;

import com.taiter.ce.EffectManager;
import com.taiter.ce.Enchantments.CEnchantment;

public class Stomp extends CEnchantment {

    int damageReductionFraction;
    int damageApplicationFraction;

    public Stomp(Application app) {
        super(app);
        configEntries.put("DamageReductionFraction", 4);
        configEntries.put("DamageApplicationFraction", 2);
        triggers.add(Trigger.DAMAGE_NATURE);
    }

    @Override
    public void effect(Event e, ItemStack item, int level) {
        if (e instanceof EntityDamageEvent) {
            EntityDamageEvent event = (EntityDamageEvent) e;
            if (event.getCause() == DamageCause.FALL) {
                if (event.getEntity() instanceof Player) {
                    if (event.getDamage() > 0) {
                        Player player = (Player) event.getEntity();
                        List<Entity> entities = player.getNearbyEntities(1, 0, 1);
                        if (!entities.isEmpty()) {
                            for (Entity ent : entities) {
                                if (ent instanceof LivingEntity) {
                                    ((LivingEntity) ent).damage(event.getDamage() / damageApplicationFraction, player);

                                }
                            }
                            player.getWorld().playEffect(player.getLocation(), Effect.ZOMBIE_DESTROY_DOOR, 5);
                            EffectManager.playSound(player.getLocation(), "ENTITY_GENERIC_EXPLODE", 1f, 2f);

                            double damage = event.getDamage() / damageReductionFraction;
                            if (((Damageable) player).getHealth() - damage > 0)
                                ((LivingEntity) player).damage(damage, player);
                            else {
                                player.setLastDamageCause(event);
                                player.setHealth(0);
                            }
                            event.setCancelled(true);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void initConfigEntries() {
        damageReductionFraction = Integer.parseInt(getConfig().getString("Enchantments." + getOriginalName() + ".DamageReductionFraction"));
        damageApplicationFraction = Integer.parseInt(getConfig().getString("Enchantments." + getOriginalName() + ".DamageApplicationFraction"));
    }
}
