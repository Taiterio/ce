package com.taiter.ce;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

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

public class ReflectionHelper {

    private static String cbVersion = "org.bukkit.craftbukkit.";
    private static String nmsVersion = "net.minecraft.server.";

    private static Constructor<?> effectPacketConstructor;

    static {
        String pkg = Bukkit.getServer().getClass().getPackage().getName();
        String version = pkg.substring(pkg.lastIndexOf(".") + 1);
        cbVersion += version + ".";
        nmsVersion += version + ".";

        //Grab all needed reflection methods/constructors
        try {
            effectPacketConstructor = getNMSClass("PacketPlayOutWorldParticles").getConstructor(getNMSClass("EnumParticle"), boolean.class, float.class, float.class, float.class, float.class,
                    float.class, float.class, float.class, int.class, int[].class);
        } catch (NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
        }

        new EffectManager();
    }

    public static Constructor<?> getEffectPacketConstructor() {
        return effectPacketConstructor;
    }

    public static Object loadEnumParticleValues() throws NoSuchMethodException, SecurityException {
        return getNMSClass("EnumParticle").getEnumConstants();
    }

    public static Class<?> wrapperClassToPrimitive(Class<?> cl) {
        if (cl == Boolean.class)
            return boolean.class;
        if (cl == Short.class)
            return short.class;
        if (cl == Byte.class)
            return byte.class;
        if (cl == Integer.class)
            return int.class;
        if (cl == Long.class)
            return long.class;
        if (cl == Float.class)
            return float.class;
        if (cl == Double.class)
            return double.class;
        if (cl == Character.class)
            return char.class;
        if (cl == Void.class)
            return void.class;
        return cl;
    }

    public static Class<?>[] toParamTypes(Object... params) {
        Class<?>[] classes = new Class<?>[params.length];
        for (int i = 0; i < params.length; i++)
            classes[i] = wrapperClassToPrimitive(params[i].getClass());
        return classes;
    }

    public static Object getEntityHandle(Entity e) {
        return callMethod(e, "getHandle");
    }

    public static Object getWorldHandle(World w) {
        return callMethod(w, "getHandle");
    }

    public static Object getPlayerConnection(Player p) {
        return getPlayerConnection(getEntityHandle(p));
    }

    public static Object getPlayerConnection(Object handle) {
        return getDeclaredField(handle, "playerConnection");
    }

    public static void sendPacket(Player p, Object packet) {
        Object pc = getPlayerConnection(p);
        try {
            pc.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(pc, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Object getPacket(String name, Object... params) {
        return callDeclaredConstructor(getNMSClass(name), params);
    }

    public static void sendJsonMessage(Player p, String json) {
        //Used for sending formatted text chat, unused for now
        Object msg = callDeclaredMethod(getNMSClass("ChatSerializer"), "a", json);
        sendPacket(p, getPacket("PacketPlayOutChat", msg, true));
    }

    public static Class<?> getClass(String name) {
        try {
            return Class.forName(name);
        } catch (Exception e) {
            return null;
        }
    }

    public static Class<?> getNMSClass(String name) {
        return getClass(nmsVersion + name);
    }

    public static Class<?> getCBClass(String name) {
        return getClass(cbVersion + name);
    }

    public static Object callDeclaredMethod(Object object, String method) {
        return callDeclaredMethod(object, method, new Object[0]);
    }

    public static Object callDeclaredMethod(Object object, String method, Object... params) {
        try {
            Method m = object.getClass().getDeclaredMethod(method, toParamTypes(params));
            m.setAccessible(true);
            return m.invoke(object, params);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object callMethod(Object object, String method) {
        return callMethod(object, method, new Object[0]);
    }

    public static Object callMethod(Object object, String method, Object... params) {
        try {
            Method m = object.getClass().getMethod(method, toParamTypes(params));
            m.setAccessible(true);
            return m.invoke(object, params);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object callDeclaredConstructor(Class<?> clazz, Object... params) {
        try {
            Constructor<?> con = clazz.getDeclaredConstructor(toParamTypes(params));
            con.setAccessible(true);
            return con.newInstance(params);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object callConstructor(Class<?> clazz, Object... params) {
        try {
            Constructor<?> con = clazz.getConstructor(toParamTypes(params));
            con.setAccessible(true);
            return con.newInstance(params);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object getDeclaredField(Object object, String field) {
        try {
            Field f = object.getClass().getDeclaredField(field);
            f.setAccessible(true);
            return f.get(object);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object getField(Object object, String field) {
        try {
            Field f = object.getClass().getField(field);
            f.setAccessible(true);
            return f.get(object);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void setDeclaredField(Object object, String field, Object value) {
        try {
            Field f = object.getClass().getDeclaredField(field);
            f.setAccessible(true);
            f.set(object, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setField(Object object, String field, Object value) {
        try {
            Field f = object.getClass().getField(field);
            f.setAccessible(true);
            f.set(object, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
