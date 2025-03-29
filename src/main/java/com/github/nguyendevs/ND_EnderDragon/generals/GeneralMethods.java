package com.github.nguyendevs.ND_EnderDragon.generals;

import com.destroystokyo.paper.profile.ProfileProperty;
import com.destroystokyo.paper.profile.PlayerProfile;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.UUID;
import java.util.function.Predicate;

public class GeneralMethods {
    public static int generateInt(int max, int min) {
        return min + (int)(Math.random() * (double)(max - min + 1));
    }

    public static double generateDouble(double max, double min) {
        return min + Math.random() * (max - min + 1.0D);
    }


    public static void spawnParticleAlongLine(Location start, Location end, Particle particle, int pointsPerLine, int particleCount, double offsetX, double offsetY, double offsetZ, double extra, @Nullable Double data, boolean forceDisplay, @Nullable Predicate<Location> operationPerPoint) {
        double d = start.distance(end) / (double)pointsPerLine;

        for(int i = 0; i < pointsPerLine; ++i) {
            Location l = start.clone();
            Vector direction = end.toVector().subtract(start.toVector()).normalize();
            Vector v = direction.multiply((double)i * d);
            l.add(v.getX(), v.getY(), v.getZ());
            start.getWorld().spawnParticle(particle, l, particleCount, offsetX, offsetY, offsetZ, extra, data, forceDisplay);
        }

    }

    public static void spawnFireParticleAlongLine(Location start, Location end, Particle particle, int pointsPerLine, int particleCount, double offsetX, double offsetY, double offsetZ, double extra, @Nullable Particle.DustOptions data, boolean forceDisplay, @Nullable Predicate<Location> operationPerPoint) {
        double d = start.distance(end) / (double)pointsPerLine;
        Particle.DustOptions dust = new Particle.DustOptions(Color.fromRGB(255, 120, 10), 2.0F);

        for(int i = 0; i < pointsPerLine; ++i) {
            Location l = start.clone();
            Vector direction = end.toVector().subtract(start.toVector()).normalize();
            Vector v = direction.multiply((double)i * d);
            l.add(v.getX(), v.getY(), v.getZ());
            start.getWorld().spawnParticle(particle, l, particleCount, offsetX, offsetY, offsetZ, extra, dust, forceDisplay);
        }

    }

    public static void spawnEndFireParticleAlongLine(Location start, Location end, Particle particle, int pointsPerLine, int particleCount, double offsetX, double offsetY, double offsetZ, double extra, @Nullable Particle.DustOptions data, boolean forceDisplay, @Nullable Predicate<Location> operationPerPoint) {
        double d = start.distance(end) / (double)pointsPerLine;
        Particle.DustOptions dust = new Particle.DustOptions(Color.fromRGB(214, 94, 247), 2.0F);

        for(int i = 0; i < pointsPerLine; ++i) {
            Location l = start.clone();
            Vector direction = end.toVector().subtract(start.toVector()).normalize();
            Vector v = direction.multiply((double)i * d);
            l.add(v.getX(), v.getY(), v.getZ());
            start.getWorld().spawnParticle(particle, l, particleCount, offsetX, offsetY, offsetZ, extra, dust, forceDisplay);
        }

    }

    public static ItemStack createBasicPlayerHead(String url, String name) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta headMeta = (SkullMeta)head.getItemMeta();
        if (!url.isEmpty()) {
            headMeta.setPlayerProfile(Bukkit.createProfile(UUID.randomUUID(), (String)null));
            PlayerProfile profile = headMeta.getPlayerProfile();
            profile.getProperties().add(new ProfileProperty("textures", url));
            headMeta.setPlayerProfile(profile);
        }

        if (!name.isEmpty()) {
            headMeta.setDisplayName(name);
        }

        head.setItemMeta(headMeta);
        return head;
    }

    public static boolean foundPlayer(World world) {
        boolean returnBool = false;
        boolean foundPlayer = false;
        Iterator var3 = world.getLivingEntities().iterator();

        while(var3.hasNext()) {
            LivingEntity entity = (LivingEntity)var3.next();
            if (!foundPlayer && entity instanceof Player) {
                foundPlayer = true;
                returnBool = true;
            }
        }

        return returnBool;
    }
}
