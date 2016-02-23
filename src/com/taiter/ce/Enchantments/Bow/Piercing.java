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

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier;
import org.bukkit.inventory.ItemStack;

import com.taiter.ce.Enchantments.CEnchantment;

public class Piercing extends CEnchantment {

    private ItemStack[] emptyArmor = new ItemStack[4];

    public Piercing(Application app) {
        super(app);
        triggers.add(Trigger.SHOOT_BOW);
        triggers.add(Trigger.DAMAGE_GIVEN);
        for (int i = 0; i < 4; i++)
            emptyArmor[i] = new ItemStack(Material.AIR, 0);
    }

    @Override
    public void effect(Event e, ItemStack item, int level) {
        if (e instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) e;
            LivingEntity target = (LivingEntity) event.getEntity();

            int armorCounter = 0;
            for (ItemStack piece : target.getEquipment().getArmorContents())
                if (!piece.getType().equals(Material.AIR))
                    armorCounter++;

            if (armorCounter == 0)
                return;
            
            event.setDamage(DamageModifier.ARMOR, 0); //Completely remove effects of Armor
            target.getWorld().playEffect(target.getLocation(), Effect.ZOMBIE_DESTROY_DOOR, 10);
        }
    }

    @Override
    public void initConfigEntries() {
    }
}
