package com.github.nguyendevs.ND_EnderDragon.generals;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import com.github.nguyendevs.ND_EnderDragon.NDEnderDragon;
import org.bukkit.*;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.DragonFireball;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.EnderDragon.Phase;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class DragonMethods {
    public static void freeTheEnd(World dragonWorld) {
        final String world = dragonWorld.getName();
        (new BukkitRunnable() {
            public void run() {
                Location location = new Location(Bukkit.getWorld(world), 0.0D, 59.0D, 0.0D);
                int radius = 100;

                for(int x = -radius; x < radius; ++x) {
                    for(int y = -radius; y < radius; ++y) {
                        for(int z = -radius; z < radius; ++z) {
                            Block block = location.getWorld().getBlockAt(x + location.getBlockX(), y + location.getBlockY(), z + location.getBlockZ());
                            if (block.getType() == Material.FIRE) {
                                block.setType(Material.AIR);
                            }
                        }
                    }
                }

            }
        }).runTaskLater(NDEnderDragon.ndenderdragon, 80L);
        (new BukkitRunnable() {
            public void run() {
                Location location = new Location(Bukkit.getWorld(world), 0.0D, 59.0D, 0.0D);
                int radius = 100;
                World world2 = Bukkit.getWorld(world);
                for (EnderCrystal crystal : world2.getEntitiesByClass(EnderCrystal.class)) {
                    double x = crystal.getLocation().getX();
                    double y = crystal.getLocation().getY();
                    double z = crystal.getLocation().getZ();
                    if (x >= -100 && x <= 100 && y >= 0 && y <= 250 && z >= -100 && z <= 100) {
                        crystal.getWorld().createExplosion(crystal.getLocation(), 4.0F);
                        crystal.remove();
                    }
                }
                for(int x = -radius; x < radius; ++x) {
                    for(int y = -radius; y < radius; ++y) {
                        for(int z = -radius; z < radius; ++z) {
                            Block block = location.getWorld().getBlockAt(x + location.getBlockX(), y + location.getBlockY(), z + location.getBlockZ());
                            if (block.getType() == Material.OBSIDIAN) {
                                int randomCrackChance = GeneralMethods.generateInt(5, 1);
                                if (randomCrackChance == 1) {
                                    block.setType(Material.AIR);
                                } else {
                                    int randomCryingChance = GeneralMethods.generateInt(3, 1);
                                    if (randomCryingChance == 1) {
                                        block.setType(Material.CRYING_OBSIDIAN);
                                    }
                                }
                            }
                        }
                    }
                }

            }
        }).runTaskLater(NDEnderDragon.ndenderdragon, 80L);
        (new BukkitRunnable() {
            public void run() {
                Location location = new Location(Bukkit.getWorld(world), 0.0D, 59.0D, 0.0D);
                Iterator var2 = location.getNearbyLivingEntities(500.0D, 500.0D, 500.0D).iterator();

                while(var2.hasNext()) {
                    LivingEntity entity = (LivingEntity)var2.next();
                    if (entity instanceof Enderman) {
                        Enderman enderman = (Enderman)entity;
                        enderman.remove();
                        enderman.getWorld().dropItemNaturally(enderman.getLocation(), new ItemStack(Material.ENDER_PEARL));
                        enderman.getWorld().spawnParticle(Particle.EXPLOSION, enderman.getLocation(), 1);
                        ExperienceOrb experienceOrb = (ExperienceOrb)enderman.getWorld().spawn(enderman.getLocation(), ExperienceOrb.class);
                        experienceOrb.setExperience(20);
                    } else if (entity instanceof EnderCrystal) {
                        EnderCrystal enderCrystal = (EnderCrystal)entity;
                        enderCrystal.remove();
                        enderCrystal.getWorld().spawnParticle(Particle.EXPLOSION, enderCrystal.getLocation(), 1);
                    }
                }

            }
        }).runTaskLater(NDEnderDragon.ndenderdragon, 80L);
        (new BukkitRunnable() {
            public void run() {
                Location location = new Location(Bukkit.getWorld(world), 0.0D, 59.0D, 0.0D);
                int radius = 100;

                for(int x = -radius; x < radius; ++x) {
                    for(int y = -radius; y < radius; ++y) {
                        for(int z = -radius; z < radius; ++z) {
                            Block block = location.getWorld().getBlockAt(x + location.getBlockX(), y + location.getBlockY(), z + location.getBlockZ());
                            if (block.getType() == Material.FIRE) {
                                block.setType(Material.AIR);
                            }
                        }
                    }
                }

            }
        }).runTaskLater(NDEnderDragon.ndenderdragon, 200L);
    }

    public static void breatheFireAttack(final EnderDragon dragon, final int duration, final boolean endFire, final boolean performant) {
        final Location endLocation = getTargetLocation(15, dragon.getWorld());
        final int xDirection = GeneralMethods.generateInt(2, 1);
        final int zDirection = GeneralMethods.generateInt(2, 1);
        boolean canMakeSounds = NDEnderDragon.ndenderdragon.getConfig().getBoolean("dragonCanFireBreathSounds");

        if (canMakeSounds) {
            roar(dragon.getLocation(), duration);
        }

        new BukkitRunnable() {
            int i = 0;

            public void run() {
                if (dragon.isDead() || dragon.getHealth() <= 0.0D || dragon.getPhase() == Phase.DYING || i >= duration) {
                    this.cancel();
                    return;
                }

                i++;
                Location dragonEyeLocation = dragon.getEyeLocation().add(0.0D, -2.0D, 0.0D);
                Location explosionLocation = endLocation.clone();
                DragonMethods.setFire(explosionLocation, 2, 14);

                if (endFire) {
                    explosionLocation.getWorld().spawn(explosionLocation, LightningStrike.class);
                    explosionLocation.getWorld().createExplosion(explosionLocation, 4.0F, true);

                    // Tạo DustOptions (màu tim, kích thước 1.0F)
                    Particle.DustOptions dustOptions = new Particle.DustOptions(Color.PURPLE, 1.0F);
                    GeneralMethods.spawnEndFireParticleAlongLine(
                            dragonEyeLocation, endLocation, Particle.DUST, 75, 350,
                            0.0D, 0.0D, 0.0D, 0.0D,
                            dustOptions, true, null
                    );
                } else {
                    int particleCount = performant ? 20 : 350;
                    int pointsPerLine = performant ? 20 : 75;
                    boolean forceDisplay = !performant;

                    // Tạo DustOptions
                    Particle.DustOptions dustOptions = new Particle.DustOptions(Color.ORANGE, 1.0F);
                    GeneralMethods.spawnFireParticleAlongLine(
                            dragonEyeLocation, endLocation, Particle.DUST, pointsPerLine, particleCount,
                            0.0D, 0.0D, 0.0D, 0.0D,
                            dustOptions, forceDisplay, null
                    );
                }

                double targetMoveDistance = 0.3D;
                endLocation.add(xDirection == 1 ? targetMoveDistance : -targetMoveDistance, 0.0D,
                        zDirection == 1 ? targetMoveDistance : -targetMoveDistance);

                Block highestBlockAt = endLocation.getWorld().getHighestBlockAt(endLocation.getBlockX(), endLocation.getBlockZ());
                endLocation.setY(highestBlockAt.getY());
            }
        }.runTaskTimer(NDEnderDragon.ndenderdragon, 0L, 1L);
    }


    public static Location getTargetLocation(int radius, World dragonWorld) {
        Location location = new Location(Bukkit.getWorld(dragonWorld.getName()), 0.0D, 59.0D, 0.0D);
        int x = GeneralMethods.generateInt(radius, -radius);
        int z = GeneralMethods.generateInt(radius, -radius);
        location.add((double)x, 0.0D, (double)z);
        Block highestBlockAt = location.getWorld().getHighestBlockAt(x, z);
        location.setY((double)highestBlockAt.getY());
        return location;
    }

    public static void shootSound(Location point) {
        Iterator var1 = point.getNearbyLivingEntities(100.0D, 100.0D, 100.0D).iterator();

        while(var1.hasNext()) {
            LivingEntity entity = (LivingEntity)var1.next();
            if (entity instanceof Player) {
                Player player = (Player)entity;
                player.playSound(player.getLocation(), "minecraft:entity.ender_dragon.shoot", 20.0F, 1.0F);
            }
        }

    }

    public static void launchFireballs(EnderDragon dragon) {
        DragonFireball fireball = (DragonFireball)dragon.getWorld().spawn(dragon.getLocation(), DragonFireball.class);
        Location dragonLoc = dragon.getLocation().add(0.0D, -4.0D, 0.0D);
        Location targetLoc = getTargetLocation(15, dragon.getWorld());
        Vector v = targetLoc.toVector().subtract(dragonLoc.toVector());
        fireball.setDirection(v);
        shootSound(dragonLoc);
    }

    public static List<Location> findEndCrystalPillars(Location center, int radius) {
        List<Location> pillarLocations = new ArrayList();

        for(int x = -radius; x < radius; ++x) {
            for(int y = -radius; y < radius; ++y) {
                for(int z = -radius; z < radius; ++z) {
                    Block block = center.getWorld().getBlockAt(x + center.getBlockX(), y + center.getBlockY(), z + center.getBlockZ());
                    if (block.getType() == Material.BEDROCK && block.getLocation().clone().add(0.0D, -1.0D, 0.0D).getBlock().getType() == Material.OBSIDIAN) {
                        boolean foundCrystal = false;
                        Iterator var8 = block.getLocation().getNearbyEntities(10.0D, 10.0D, 10.0D).iterator();

                        while(var8.hasNext()) {
                            Entity entity = (Entity)var8.next();
                            if (entity instanceof EnderCrystal) {
                                EnderCrystal crystal = (EnderCrystal)entity;
                                foundCrystal = true;
                            }
                        }

                        if (!foundCrystal) {
                            pillarLocations.add(block.getLocation());
                        }
                    }
                }
            }
        }

        return pillarLocations;
    }

    public static void launchFireballsAtLocation(EnderDragon dragon, Location targetLocation) {
        DragonFireball fireball = (DragonFireball)dragon.getWorld().spawn(dragon.getLocation(), DragonFireball.class);
        Location dragonLoc = dragon.getLocation().add(0.0D, 6.0D, 0.0D);
        Vector v = targetLocation.toVector().subtract(dragonLoc.toVector());
        fireball.setDirection(v);
        shootSound(dragonLoc);
    }

    public static void roar(final Location point, final int duration) {
        (new BukkitRunnable() {
            int i = 0;

            public void run() {
                if (this.i < duration) {
                    ++this.i;
                    Iterator var1 = point.getNearbyLivingEntities(100.0D, 100.0D, 100.0D).iterator();

                    while(var1.hasNext()) {
                        LivingEntity entity = (LivingEntity)var1.next();
                        if (entity instanceof Player) {
                            Player player = (Player)entity;
                            player.playSound(player.getLocation(), "minecraft:entity.ender_dragon.growl", 20.0F, 1.0F);
                        }
                    }
                } else {
                    this.cancel();
                }

            }
        }).runTaskTimer(NDEnderDragon.ndenderdragon, 0L, 1L);
    }

    public static void setFire(Location point, int radius, int chance) {
        for(int x = -radius; x < radius; ++x) {
            for(int y = -radius; y < radius; ++y) {
                for(int z = -radius; z < radius; ++z) {
                    Block block = point.getWorld().getBlockAt(x + point.getBlockX(), y + point.getBlockY(), z + point.getBlockZ());
                    if (block.isEmpty() && block.getLocation().add(0.0D, -1.0D, 0.0D).getBlock().isSolid() && GeneralMethods.generateInt(chance, 1) == 1) {
                        block.setType(Material.FIRE);
                    }
                }
            }
        }

    }

    public static void createCrystals(World dragonWorld) {
        EnderCrystal crystal1 = (EnderCrystal)dragonWorld.spawn(dragonWorld.getHighestBlockAt(0, 3).getLocation().add(0.5D, 1.0D, 0.5D), EnderCrystal.class);
        EnderCrystal crystal2 = (EnderCrystal)dragonWorld.spawn(dragonWorld.getHighestBlockAt(0, -3).getLocation().add(0.5D, 1.0D, 0.5D), EnderCrystal.class);
        EnderCrystal crystal3 = (EnderCrystal)dragonWorld.spawn(dragonWorld.getHighestBlockAt(3, 0).getLocation().add(0.5D, 1.0D, 0.5D), EnderCrystal.class);
        EnderCrystal crystal4 = (EnderCrystal)dragonWorld.spawn(dragonWorld.getHighestBlockAt(-3, 0).getLocation().add(0.5D, 1.0D, 0.5D), EnderCrystal.class);
        crystal1.setShowingBottom(false);
        crystal2.setShowingBottom(false);
        crystal3.setShowingBottom(false);
        crystal4.setShowingBottom(false);
    }
}