package com.github.nguyendevs.ND_EnderDragon.dragonrevamp.moves;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import com.github.nguyendevs.ND_EnderDragon.NDEnderDragon;
import com.github.nguyendevs.ND_EnderDragon.generals.DragonMethods;
import com.github.nguyendevs.ND_EnderDragon.generals.EndWorldsData;
import com.github.nguyendevs.ND_EnderDragon.generals.GeneralMethods;
import com.github.nguyendevs.ND_EnderDragon.generals.PresetChatMessages;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
//import me.wiggle.NDEnderDragon.ndenderdragon;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ShulkerBullet;
import org.bukkit.entity.EnderDragon.Phase;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class DragonAttacks implements Listener {
    public static List<EnderDragon> dragons = new ArrayList();
    public static List<EnderDragon> enragedDragons = new ArrayList();
    public static List<EnderDragon> isHoldingPlayer = new ArrayList();
    public static List<EnderDragon> isOnCooldown = new ArrayList();

    @EventHandler
    public void dragonTick(ServerTickEndEvent event) {
        if (dragons != null && !dragons.isEmpty()) {
            Iterator var2 = dragons.iterator();

            while(true) {
                EnderDragon dragon;
                do {
                    if (!var2.hasNext()) {
                        return;
                    }

                    dragon = (EnderDragon)var2.next();
                } while(dragon.isDead());

                if (!isOnCooldown.contains(dragon)) {
                    if (!(dragon.getHealth() < dragon.getMaxHealth() / 2.0D) && !enragedDragons.contains(dragon)) {
                        this.rollNormalAttacks(dragon);
                    } else {
                        this.rollEnragedAttacks(dragon);
                    }
                }

                boolean canAttack = NDEnderDragon.ndenderdragon.getConfig().getBoolean("dragonCanPickUp");
                if (canAttack) {
                    this.pickupPlayers(dragon);
                }
            }
        }
    }

    private void rollNormalAttacks(EnderDragon dragon) {
        int randomAttack = GeneralMethods.generateInt(4, 1);
        if (randomAttack == 1) {
            this.determineFireBreathType(dragon);
        } else if (randomAttack == 2) {
            this.endFireballAttack(dragon);
        } else if (randomAttack == 3) {
            this.respawnCrystal(dragon);
        }

    }

    private void rollEnragedAttacks(EnderDragon dragon) {
        int randomAttack = GeneralMethods.generateInt(6, 1);
        if (randomAttack == 1) {
            this.determineFireBreathType(dragon);
        } else if (randomAttack == 2) {
            this.endFireballAttack(dragon);
        } else if (randomAttack == 3) {
            this.rallyEndermen(dragon);
        } else if (randomAttack == 4) {
            this.lightningAttack(dragon);
        } else if (randomAttack == 5) {
            this.respawnCrystal(dragon);
        } else if (randomAttack == 6) {
            this.spawnShulkerBullets(dragon);
        }

    }

    private void spawnShulkerBullets(final EnderDragon dragon) {
        boolean canSpawnShulkerBullets = NDEnderDragon.ndenderdragon.getConfig().getBoolean("dragonCanSpawnShulkerBullets");
        if (canSpawnShulkerBullets) {
            isOnCooldown.add(dragon);
            Iterator var3 = dragon.getWorld().getHighestBlockAt(0, 0).getLocation().getNearbyLivingEntities(100.0D, 100.0D, 100.0D).iterator();

            while(true) {
                LivingEntity entity;
                do {
                    if (!var3.hasNext()) {
                        int cooldown = GeneralMethods.generateInt(60, 40);
                        if (enragedDragons.contains(dragon)) {
                            cooldown = GeneralMethods.generateInt(40, 20);
                        }

                        (new BukkitRunnable() {
                            public void run() {
                                DragonAttacks.isOnCooldown.remove(dragon);
                            }
                        }).runTaskLater(NDEnderDragon.ndenderdragon, (long)cooldown);
                        boolean announceAttack = NDEnderDragon.ndenderdragon.getConfig().getBoolean("dragonAttacksAnnounced");
                        if (announceAttack) {
                            String dragonName = NDEnderDragon.ndenderdragon.getConfig().getString("dragonName");
                            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',
                                    NDEnderDragon.ndenderdragon.getConfig().getString("messages.prefix") + " " + dragonName + String.valueOf(ChatColor.WHITE) + " đã tạo ra " + String.valueOf(ChatColor.YELLOW) + "một loạt đạn Shulker" + String.valueOf(ChatColor.WHITE) + "!"));
                        }

                        return;
                    }

                    entity = (LivingEntity)var3.next();
                } while(!(entity instanceof Player));

                Player player = (Player)entity;

                for(int i = 0; i < GeneralMethods.generateInt(6, 3); ++i) {
                    int range = 7;
                    Location spawnLocation = player.getLocation().clone().add((double)GeneralMethods.generateInt(range, -range), 0.0D, (double)GeneralMethods.generateInt(range, -range));
                    ShulkerBullet bullet = (ShulkerBullet)player.getWorld().spawn(spawnLocation, ShulkerBullet.class);
                    bullet.setTarget(player);
                }
            }
        }
    }

    private void respawnCrystal(final EnderDragon dragon) {
        boolean canSpawnCrystals = NDEnderDragon.ndenderdragon.getConfig().getBoolean("dragonCanSpawnCrystal");
        if (canSpawnCrystals) {
            int chance = NDEnderDragon.ndenderdragon.getConfig().getInt("dragonSpawnCrystalChance");

            // Tạo số ngẫu nhiên từ 1 đến 100
            int randomChance = GeneralMethods.generateInt(100, 1); //GeneralMethods.generateInt(max, min) có sẵn

            // Chỉ chạy logic nếu randomChance <= chance
            if (randomChance <= chance) {
                int maxCrystals = NDEnderDragon.ndenderdragon.getConfig().getInt("maxEndCrystalPerSpawn");
                if (enragedDragons.contains(dragon)) {
                    maxCrystals = NDEnderDragon.ndenderdragon.getConfig().getInt("maxEndCrystalPerSpawnOminous");
                }

                if (dragon.getDragonBattle() != null && dragon.getDragonBattle().getHealingCrystals().size() < maxCrystals) {
                    List<Location> pillarLocations = DragonMethods.findEndCrystalPillars(dragon.getWorld().getHighestBlockAt(0, 0).getLocation(), 100);
                    if (pillarLocations.size() > 0) {
                        Collections.shuffle(pillarLocations);
                        boolean announceAttack = NDEnderDragon.ndenderdragon.getConfig().getBoolean("dragonAttacksAnnounced");
                        if (announceAttack) {
                            String dragonName = NDEnderDragon.ndenderdragon.getConfig().getString("dragonName");
                            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',
                                    NDEnderDragon.ndenderdragon.getConfig().getString("messages.prefix") + " " + dragonName + String.valueOf(ChatColor.WHITE) + " đã hồi sinh một " + String.valueOf(ChatColor.YELLOW) + "Pha lê End" + String.valueOf(ChatColor.WHITE) + "!"));
                        }

                        isOnCooldown.add(dragon);
                        EnderCrystal crystal = (EnderCrystal)dragon.getWorld().spawn(((Location)pillarLocations.get(0)).clone().add(0.5D, 2.0D, 0.5D), EnderCrystal.class);
                        crystal.setCustomName(PresetChatMessages.fullCrystalHealthBar);
                        crystal.setCustomNameVisible(true);
                        crystal.setShowingBottom(false);
                        int cooldown = GeneralMethods.generateInt(100, 60);
                        if (enragedDragons.contains(dragon)) {
                            cooldown = GeneralMethods.generateInt(80, 40);
                        }

                        (new BukkitRunnable() {
                            public void run() {
                                DragonAttacks.isOnCooldown.remove(dragon);
                            }
                        }).runTaskLater(NDEnderDragon.ndenderdragon, (long)cooldown);
                    }
                }
            }
        }
    }

    private void pickupPlayers(final EnderDragon dragon) {
        boolean canPickUp = NDEnderDragon.ndenderdragon.getConfig().getBoolean("dragonCanPickUp");
        if (canPickUp) {
            int chance = NDEnderDragon.ndenderdragon.getConfig().getInt("dragonPickUpChance");
            int randomChance = GeneralMethods.generateInt(chance, 1);
            if (randomChance == 1 && (dragon.getPhase() == Phase.STRAFING || dragon.getPhase() == Phase.CHARGE_PLAYER || dragon.getPhase() == Phase.CIRCLING || dragon.getPhase() == Phase.LEAVE_PORTAL) && !isHoldingPlayer.contains(dragon)) {
                boolean found = false;
                int grabRadius = 6;
                for (LivingEntity findEntity : dragon.getLocation().getNearbyLivingEntities(grabRadius, grabRadius, grabRadius)) {
                    if (findEntity instanceof Player) {
                        final Player player = (Player) findEntity;
                        if (player.getGameMode() != GameMode.CREATIVE && player.getGameMode() != GameMode.SPECTATOR && !found) {
                            found = true;
                            final boolean announceAttack = NDEnderDragon.ndenderdragon.getConfig().getBoolean("dragonAttacksAnnounced");
                            if (announceAttack) {
                                String dragonName = NDEnderDragon.ndenderdragon.getConfig().getString("dragonName");
                                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',
                                        NDEnderDragon.ndenderdragon.getConfig().getString("messages.prefix") + " " + dragonName + ChatColor.WHITE + " đã cạp " + ChatColor.YELLOW + player.getDisplayName() + ChatColor.WHITE + "!"));
                            }

                            isHoldingPlayer.add(dragon);
                            isOnCooldown.add(dragon);

                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    int randomDropHeight = GeneralMethods.generateInt(87, 77);
                                    if (dragon.getLocation().getY() <= randomDropHeight && !dragon.isDead() && dragon.getPhase() != Phase.DYING) {
                                        Location dragonLocation = dragon.getEyeLocation().add(0.0D, -7.0D, 0.0D);
                                        player.teleport(dragonLocation);
                                        player.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 2, 0, false, false));
                                        player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 2, 0, false, false));
                                        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 2, 5, false, false));
                                    } else {
                                        this.cancel();
                                        DragonAttacks.isHoldingPlayer.remove(dragon);
                                        if (announceAttack) {
                                            String dragonName = NDEnderDragon.ndenderdragon.getConfig().getString("dragonName");
                                            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',
                                                    NDEnderDragon.ndenderdragon.getConfig().getString("messages.prefix") + " " + dragonName + ChatColor.WHITE + " đã thả " + ChatColor.YELLOW + player.getDisplayName() + ChatColor.WHITE + "!"));
                                        }

                                        new BukkitRunnable() {
                                            @Override
                                            public void run() {
                                                DragonAttacks.isOnCooldown.remove(dragon);
                                            }
                                        }.runTaskLater(NDEnderDragon.ndenderdragon, 100L);
                                    }
                                }
                            }.runTaskTimer(NDEnderDragon.ndenderdragon, 0L, 1L);
                        }
                    }
                }
            }
        }
    }
    private void endFireballAttack(final EnderDragon dragon) {
        if (GeneralMethods.foundPlayer(dragon.getWorld()) && (dragon.getPhase() == Phase.CHARGE_PLAYER || dragon.getPhase() == Phase.CIRCLING || dragon.getPhase() == Phase.STRAFING)) {
            boolean canAttack = NDEnderDragon.ndenderdragon.getConfig().getBoolean("dragonCanFireball");
            if (canAttack) {
                isOnCooldown.add(dragon);
                int amount = GeneralMethods.generateInt(7, 3);
                if (dragon.getHealth() < dragon.getMaxHealth() / 2.0D) {
                    amount = GeneralMethods.generateInt(9, 4);
                }

                boolean announceAttack = NDEnderDragon.ndenderdragon.getConfig().getBoolean("dragonAttacksAnnounced");
                if (announceAttack) {
                    String dragonName = NDEnderDragon.ndenderdragon.getConfig().getString("dragonName");
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',
                            NDEnderDragon.ndenderdragon.getConfig().getString("messages.prefix") + " " + dragonName + ChatColor.WHITE + " đã phóng " + ChatColor.YELLOW + "cầu lửa" + ChatColor.WHITE + "!"));
                }

                int finalAmount = amount;
                new BukkitRunnable() {
                    int i = 0;

                    @Override
                    public void run() {
                        if (i < finalAmount) {
                            i++;
                            if (dragon.isDead() || dragon.getHealth() <= 0.0D || dragon.getPhase() == Phase.DYING) {
                                this.cancel();
                            }
                            DragonMethods.launchFireballs(dragon);
                        } else {
                            this.cancel();
                            int cooldown = DragonAttacks.enragedDragons.contains(dragon) ? 20 : 60;

                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    DragonAttacks.isOnCooldown.remove(dragon);
                                }
                            }.runTaskLater(NDEnderDragon.ndenderdragon, cooldown);
                        }
                    }
                }.runTaskTimer(NDEnderDragon.ndenderdragon, 0L, 8L);
            }
        }
    }
    private void rallyEndermen(final EnderDragon dragon) {
        boolean canAttack = NDEnderDragon.ndenderdragon.getConfig().getBoolean("dragonCanRallyEndermen");
        if (canAttack) {
            Location location = new Location(Bukkit.getWorld(dragon.getWorld().getName()), 0.0D, 59.0D, 0.0D);
            Iterator var4 = location.getNearbyLivingEntities(100.0D, 100.0D, 100.0D).iterator();

            label45:
            while(true) {
                LivingEntity findPlayers;
                do {
                    if (!var4.hasNext()) {
                        isOnCooldown.add(dragon);
                        int cooldown = 60;
                        if (enragedDragons.contains(dragon)) {
                            cooldown = 20;
                        }

                        (new BukkitRunnable() {
                            public void run() {
                                DragonAttacks.isOnCooldown.remove(dragon);
                            }
                        }).runTaskLater(NDEnderDragon.ndenderdragon, (long)cooldown);
                        Iterator var13 = location.getNearbyLivingEntities(100.0D, 100.0D, 100.0D).iterator();

                        while(var13.hasNext()) {
                            LivingEntity entity = (LivingEntity)var13.next();
                            if (entity instanceof Player) {
                                Player player = (Player)entity;
                                player.playSound(player.getLocation(), "minecraft:entity.ender_dragon.growl", 20.0F, 1.0F);
                            }
                        }
                        break label45;
                    }

                    findPlayers = (LivingEntity)var4.next();
                } while(!(findPlayers instanceof Player));

                Player player = (Player)findPlayers;
                this.spawnAssistingEndermen(dragon, player);
                Iterator var7 = player.getLocation().getNearbyLivingEntities(8.0D, 8.0D, 8.0D).iterator();

                while(var7.hasNext()) {
                    LivingEntity findEndermen = (LivingEntity)var7.next();
                    if (findEndermen instanceof Enderman) {
                        Enderman enderman = (Enderman)findEndermen;
                        enderman.setTarget(player);
                        boolean announceAttack = NDEnderDragon.ndenderdragon.getConfig().getBoolean("dragonAttacksAnnounced");
                        if (announceAttack) {
                            String dragonName = NDEnderDragon.ndenderdragon.getConfig().getString("dragonName");
                            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',
                                    NDEnderDragon.ndenderdragon.getConfig().getString("messages.prefix") + " " + dragonName + String.valueOf(ChatColor.WHITE) + " đã thao túng &5Enderman&f tấn công " + String.valueOf(ChatColor.YELLOW) + player.getDisplayName() + String.valueOf(ChatColor.WHITE) + "!"));
                        }
                    }
                }
            }
        }

    }

    private void lightningAttack(final EnderDragon dragon) {
        boolean canAttack = NDEnderDragon.ndenderdragon.getConfig().getBoolean("dragonCanSpawnLightning");
        if (canAttack) {
            isOnCooldown.add(dragon);

            // Tổng số sét cần sinh (có thể điều chỉnh)
            final int totalStrikes = GeneralMethods.generateInt(9, 3); // Từ 3 đến 9 sét
            final int strikesPerSecond = 3; // 3 sét mỗi giây
            final int ticksPerStrike = 20 / strikesPerSecond; // ~6-7 ticks mỗi sét (20 ticks = 1 giây)

            boolean announceAttack = NDEnderDragon.ndenderdragon.getConfig().getBoolean("dragonAttacksAnnounced");
            if (announceAttack) {
                String dragonName = NDEnderDragon.ndenderdragon.getConfig().getString("dragonName");
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',
                        NDEnderDragon.ndenderdragon.getConfig().getString("messages.prefix") + " " + dragonName + String.valueOf(ChatColor.WHITE) + " đã triệu hồi " + String.valueOf(ChatColor.YELLOW) + "từ lôi" + String.valueOf(ChatColor.WHITE) + "!"));
            }

            // Sinh sét theo thời gian với tần suất 3 lần mỗi giây
            new BukkitRunnable() {
                int strikesSpawned = 0;

                @Override
                public void run() {
                    if (strikesSpawned >= totalStrikes || dragon.isDead() || dragon.getHealth() <= 0.0D || dragon.getPhase() == Phase.DYING) {
                        this.cancel();
                        return;
                    }

                    // Sinh 1 sét tại vị trí ngẫu nhiên
                    if (!dragon.isDead() && dragon.getHealth() > 0.0D && dragon.getPhase() != Phase.DYING) {
                        dragon.getWorld().spawn(DragonMethods.getTargetLocation(20, dragon.getWorld()), LightningStrike.class);
                    }
                    strikesSpawned++;
                }
            }.runTaskTimer(NDEnderDragon.ndenderdragon, 0L, ticksPerStrike); // Chạy mỗi ~6-7 ticks

            // Cooldown
            int cooldown = GeneralMethods.generateInt(80, 40);
            if (enragedDragons.contains(dragon)) {
                cooldown = GeneralMethods.generateInt(60, 20);
            }

            new BukkitRunnable() {
                public void run() {
                    DragonAttacks.isOnCooldown.remove(dragon);
                }
            }.runTaskLater(NDEnderDragon.ndenderdragon, (long)cooldown);
        }
    }

    private void determineFireBreathType(EnderDragon dragon) {
        boolean canAttack = NDEnderDragon.ndenderdragon.getConfig().getBoolean("dragonCanFireBreath");
        if (canAttack) {
            if (!(dragon.getHealth() < dragon.getMaxHealth() / 2.0D) && !enragedDragons.contains(dragon)) {
                this.fireBreathAttack(dragon);
            } else {
                this.endFireBreathAttack(dragon);
            }
        }

    }

    private void endFireBreathAttack(final EnderDragon dragon) {
        if (GeneralMethods.foundPlayer(dragon.getWorld()) && dragon.getLocation().getY() > 74.0D) {
            int upperDuration = NDEnderDragon.ndenderdragon.getConfig().getInt("dragonFireBreathDurationUpper");
            int lowerDuration = NDEnderDragon.ndenderdragon.getConfig().getInt("dragonFireBreathDurationLower");
            int duration = GeneralMethods.generateInt(upperDuration, lowerDuration);
            isOnCooldown.add(dragon);
            boolean announceAttack = NDEnderDragon.ndenderdragon.getConfig().getBoolean("dragonAttacksAnnounced");
            if (announceAttack) {
                String dragonName = NDEnderDragon.ndenderdragon.getConfig().getString("dragonName");
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',
                        NDEnderDragon.ndenderdragon.getConfig().getString("messages.prefix") + " " + dragonName + String.valueOf(ChatColor.WHITE) + " đang " + String.valueOf(ChatColor.YELLOW) + "khạc lửa Ender" + String.valueOf(ChatColor.WHITE) + "!"));
            }

            DragonMethods.breatheFireAttack(dragon, duration, true, false);
            int cooldown = GeneralMethods.generateInt(80, 40);
            if (enragedDragons.contains(dragon)) {
                cooldown = GeneralMethods.generateInt(60, 20);
            }

            (new BukkitRunnable() {
                public void run() {
                    DragonAttacks.isOnCooldown.remove(dragon);
                }
            }).runTaskLater(NDEnderDragon.ndenderdragon, (long)cooldown);
        }

    }

    private void fireBreathAttack(final EnderDragon dragon) {
        if (GeneralMethods.foundPlayer(dragon.getWorld()) && dragon.getLocation().getY() > 74.0D) {
            int upperDuration = NDEnderDragon.ndenderdragon.getConfig().getInt("dragonFireBreathDurationUpper");
            int lowerDuration = NDEnderDragon.ndenderdragon.getConfig().getInt("dragonFireBreathDurationLower");
            int duration = GeneralMethods.generateInt(upperDuration, lowerDuration);
            boolean performanceMode = NDEnderDragon.ndenderdragon.getConfig().getBoolean("performanceMode");
            if (performanceMode) {
                DragonMethods.breatheFireAttack(dragon, duration, false, true);
            } else {
                DragonMethods.breatheFireAttack(dragon, duration, false, false);
            }

            boolean announceAttack = NDEnderDragon.ndenderdragon.getConfig().getBoolean("dragonAttacksAnnounced");
            if (announceAttack) {
                String dragonName = NDEnderDragon.ndenderdragon.getConfig().getString("dragonName");

                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',
                        NDEnderDragon.ndenderdragon.getConfig().getString("messages.prefix") + " " + dragonName + String.valueOf(ChatColor.WHITE) + " đang " + String.valueOf(ChatColor.YELLOW) + "khạc lửa" + String.valueOf(ChatColor.WHITE) + "!"));
            }

            isOnCooldown.add(dragon);
            int cooldown = GeneralMethods.generateInt(80, 40);
            if (enragedDragons.contains(dragon)) {
                cooldown = GeneralMethods.generateInt(60, 20);
            }

            (new BukkitRunnable() {
                public void run() {
                    DragonAttacks.isOnCooldown.remove(dragon);
                }
            }).runTaskLater(NDEnderDragon.ndenderdragon, (long)cooldown);
        }

    }

    @EventHandler
    public void dragonRoarAttack(EntityDamageEvent event) {
        Entity var3 = event.getEntity();
        if (var3 instanceof EnderDragon) {
            final EnderDragon enderDragon = (EnderDragon)var3;
            if (!dragons.contains(enderDragon)) {
                Iterator var7 = EndWorldsData.endWorldsList.iterator();

                while(var7.hasNext()) {
                    String world = (String)var7.next();
                    if (world.equalsIgnoreCase(enderDragon.getWorld().getName())) {
                        dragons.add(enderDragon);
                        //player.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 2, 0, false, false));

                    }
                }
            }

            if (enderDragon.getPhase() != Phase.DYING && enderDragon.getPhase() != Phase.CIRCLING && enderDragon.getPhase() != Phase.STRAFING && enderDragon.getPhase() != Phase.LEAVE_PORTAL && enderDragon.getPhase() != Phase.CHARGE_PLAYER && enderDragon.getPhase() != Phase.FLY_TO_PORTAL) {
                boolean canAttack = NDEnderDragon.ndenderdragon.getConfig().getBoolean("dragonCanRoar");
                if (canAttack) {
                    int chance = NDEnderDragon.ndenderdragon.getConfig().getInt("dragonRoarChance");
                    if (GeneralMethods.generateInt(chance, 1) == 1 && !isOnCooldown.contains(enderDragon)) {
                        isOnCooldown.add(enderDragon);
                        DragonMethods.roar(enderDragon.getLocation(), 40);
                        Iterator var5 = enderDragon.getLocation().getNearbyLivingEntities(9.0D, 9.0D, 9.0D).iterator();

                        while(var5.hasNext()) {
                            LivingEntity entity = (LivingEntity)var5.next();
                            if (!(entity instanceof EnderDragon)) {
                                entity.damage(4.0D);
                                entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 200, 1, false, false));
                                entity.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 200, 0, false, false));
                                entity.setVelocity(new Vector(GeneralMethods.generateInt(20, -20), 15, GeneralMethods.generateInt(20, -20)));
                            }
                        }

                        for(int i = 0; i < 20; ++i) {
                            GeneralMethods.spawnParticleAlongLine(enderDragon.getLocation(), enderDragon.getLocation().add((double)GeneralMethods.generateInt(20, -20), (double)GeneralMethods.generateInt(20, 10), (double)GeneralMethods.generateInt(20, -20)), Particle.CLOUD, 10, 100, 0.0D, 0.0D, 0.0D, 0.0D, (Double)null, true, (Predicate)null);
                        }

                        (new BukkitRunnable() {
                            public void run() {
                                DragonAttacks.isOnCooldown.remove(enderDragon);
                            }
                        }).runTaskLater(NDEnderDragon.ndenderdragon, 80L);
                    }
                }
            }
        }

    }

    private void spawnAssistingEndermen(EnderDragon dragon, Player player) {
        int amount = GeneralMethods.generateInt(2, 0);

        for(int i = 0; i < amount; ++i) {
            Enderman enderman = (Enderman)dragon.getWorld().spawn(dragon.getWorld().getHighestBlockAt(0, 0).getLocation(), Enderman.class);
            enderman.setTarget(player);
        }

    }
}
