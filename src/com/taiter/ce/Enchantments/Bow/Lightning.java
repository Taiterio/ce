package com.taiter.ce.Enchantments.Bow;

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

import java.util.Random;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import com.taiter.ce.Enchantments.CEnchantment;

public class Lightning extends CEnchantment {

    int chance;

    public Lightning(Application app) {
        super(app);
        configEntries.put("LightningChance", 75);
        triggers.add(Trigger.SHOOT_BOW);
        triggers.add(Trigger.DAMAGE_GIVEN);
    }

    @Override
    public void effect(Event e, ItemStack item, int level) {
        if (e instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) e;
            LivingEntity target = (LivingEntity) event.getEntity();
            Random random = new Random();
            int temp = level;
            while (temp != 0) {
                if (random.nextInt(100) < chance)
                    target.getWorld().strikeLightning(target.getLocation());
                --temp;
            }
        }
    }

    @Override
    public void initConfigEntries() {
        chance = Integer.parseInt(getConfig().getString("Enchantments." + getOriginalName() + ".LightningChance"));
    }
}
