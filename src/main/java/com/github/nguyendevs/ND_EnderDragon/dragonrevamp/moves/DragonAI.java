package com.github.nguyendevs.ND_EnderDragon.dragonrevamp.moves;

import com.github.nguyendevs.ND_EnderDragon.NDEnderDragon;
import com.github.nguyendevs.ND_EnderDragon.dragonrevamp.loot.DragonLoot;
import com.github.nguyendevs.ND_EnderDragon.generals.DragonMethods;
import com.github.nguyendevs.ND_EnderDragon.generals.EndWorldsData;
import com.github.nguyendevs.ND_EnderDragon.generals.GeneralMethods;
import com.github.nguyendevs.ND_EnderDragon.generals.LeaderBoard;
import com.github.nguyendevs.ND_EnderDragon.generals.PresetChatMessages;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
// me.wiggle.NDEnderDragon.ndenderdragon;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Chest;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.DragonFireball;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Endermite;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ShulkerBullet;
import org.bukkit.entity.EnderDragon.Phase;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class DragonAI implements Listener {
    private HashMap<EnderCrystal, Integer> enderCrystalHits = new HashMap();
    public ArrayList<Player> heartCooldown = new ArrayList();

    @EventHandler
    public void playerConsumeHeart(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        if (player.getItemInHand().getType() == Material.PLAYER_HEAD && event.getAction().isRightClick()) {
            ItemStack head = player.getItemInHand();
            if (head.hasItemMeta()) {
                ItemMeta headMeta = head.getItemMeta();
                if (headMeta.getDisplayName().equalsIgnoreCase(String.valueOf(ChatColor.DARK_RED) + "Trái tim long chủng")) {
                    event.setCancelled(true);
                    if (!this.heartCooldown.contains(player)) {
                        this.heartCooldown.add(player);
                        player.playSound(player.getLocation(), "minecraft:entity.generic.eat", 20.0F, 1.0F);
                        player.getItemInHand().subtract(1);
                        player.addPotionEffect(new PotionEffect(PotionEffectType.INSTANT_DAMAGE, 1, 5), false);
                        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 6000, 3), false);
                        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 6000, 1), false);
                        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 6000, 3), false);
                        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 6000, 0), false);
                        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 6000, 0), false);
                        player.addPotionEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, 6000, 1), false);
                        (new BukkitRunnable() {
                            public void run() {
                                DragonAI.this.heartCooldown.remove(player);
                            }
                        }).runTaskLater(NDEnderDragon.ndenderdragon, 2L);
                    }
                }
            }
        }

    }

    @EventHandler
    public void endermenTargetDragon(EntityTargetLivingEntityEvent event) {
        Entity var3 = event.getEntity();
        if (var3 instanceof Enderman) {
            Enderman enderman = (Enderman)var3;
            LivingEntity var5 = event.getTarget();
            if (var5 instanceof EnderDragon) {
                EnderDragon enderDragon = (EnderDragon)var5;
                event.setCancelled(true);
            } else {
                var5 = event.getTarget();
                if (var5 instanceof Endermite) {
                    Endermite endermite = (Endermite)var5;
                    if (endermite.getMaxHealth() >= 20.0D) {
                        event.setCancelled(true);
                    }
                }
            }
        }

    }

    @EventHandler
    public void dragonSpawn(EntitySpawnEvent event) {
        Entity var3 = event.getEntity();
        if (var3 instanceof EnderDragon) {
            EnderDragon enderDragon = (EnderDragon) var3;
            int health = NDEnderDragon.ndenderdragon.getConfig().getInt("basedragonHealth");
            boolean canScale = NDEnderDragon.ndenderdragon.getConfig().getBoolean("dragonHealthScales");
            if (canScale) {
                for (Iterator var5 = Bukkit.getOnlinePlayers().iterator(); var5.hasNext(); health += NDEnderDragon.ndenderdragon.getConfig().getInt("scalingdragonHealth")) {
                    Player ignored = (Player) var5.next();
                }
                enderDragon.setMaxHealth((double) health);
                enderDragon.setHealth((double) health);
            }

            String name = NDEnderDragon.ndenderdragon.getConfig().getString("dragonName");
            enderDragon.setCustomName(name);
            boolean destroyLoot = NDEnderDragon.ndenderdragon.getConfig().getBoolean("dragonRespawnDestroysLoot");
            if (destroyLoot) {
                World dragonWorld = enderDragon.getWorld();
                for (int i = 0; i < dragonWorld.getMaxHeight(); ++i) {
                    if (dragonWorld.getBlockAt(0, i, 0).getType() == Material.CHEST) {
                        dragonWorld.getBlockAt(0, i, 0).setType(Material.AIR);
                    }
                }
            }

            // Xử lý phát sáng
            String glowColorStr = NDEnderDragon.ndenderdragon.getConfig().getString("glow_color", "none").toUpperCase();
            if (!glowColorStr.equals("NONE")) {
                ChatColor glowColor;
                if (glowColorStr.equals("RANDOM")) {
                    ChatColor[] glowColors = {
                            ChatColor.AQUA, ChatColor.BLACK, ChatColor.BLUE, ChatColor.DARK_AQUA, ChatColor.DARK_BLUE,
                            ChatColor.DARK_GRAY, ChatColor.DARK_GREEN, ChatColor.DARK_PURPLE, ChatColor.DARK_RED,
                            ChatColor.GOLD, ChatColor.GRAY, ChatColor.GREEN, ChatColor.LIGHT_PURPLE, ChatColor.RED,
                            ChatColor.WHITE, ChatColor.YELLOW
                    };
                    glowColor = glowColors[new Random().nextInt(glowColors.length)];
                } else {
                    glowColor = ChatColor.valueOf(glowColorStr);
                }
                enderDragon.setGlowing(true);
                Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
                Team glowTeam = scoreboard.getTeam("DragonGlow");
                if (glowTeam == null) {
                    glowTeam = scoreboard.registerNewTeam("DragonGlow");
                }
                glowTeam.setColor(glowColor);
                glowTeam.addEntry(enderDragon.getUniqueId().toString());
            }

            Iterator var17 = EndWorldsData.endWorldsList.iterator();
            while (true) {
                do {
                    String world;
                    do {
                        if (!var17.hasNext()) {
                            boolean canBecomeEnraged = NDEnderDragon.ndenderdragon.getConfig().getBoolean("dragonCanBecomeEnraged");
                            if (canBecomeEnraged) {
                                Iterator var20 = enderDragon.getWorld().getHighestBlockAt(0, 0).getLocation().getNearbyLivingEntities(100.0D, 100.0D, 100.0D).iterator();
                                while (var20.hasNext()) {
                                    LivingEntity entity = (LivingEntity) var20.next();
                                    if (entity instanceof Player) {
                                        Player player = (Player) entity;
                                        PotionEffect effect = player.getPotionEffect(PotionEffectType.BAD_OMEN);
                                        if (effect != null) {
                                            DragonAttacks.enragedDragons.add(enderDragon);
                                            enderDragon.setCustomName("Enraged " + enderDragon.getCustomName());
                                            boolean announceAttack = NDEnderDragon.ndenderdragon.getConfig().getBoolean("dragonAttacksAnnounced");
                                            if (announceAttack) {
                                                String dragonName = NDEnderDragon.ndenderdragon.getConfig().getString("dragonName");
                                                Bukkit.broadcastMessage(dragonName + String.valueOf(ChatColor.WHITE) + " đang " + String.valueOf(ChatColor.YELLOW) + "Cuồng loạn" + String.valueOf(ChatColor.WHITE) + "!");
                                            }
                                            return;
                                        }
                                    }
                                }
                            }
                            return;
                        }
                        world = (String) var17.next();
                    } while (!world.equalsIgnoreCase(enderDragon.getWorld().getName()));
                    DragonAttacks.dragons.add(enderDragon);
                } while (enderDragon.getDragonBattle() == null);
                Iterator var9 = enderDragon.getDragonBattle().getHealingCrystals().iterator();
                while (var9.hasNext()) {
                    EnderCrystal crystal = (EnderCrystal) var9.next();
                    crystal.setCustomName(PresetChatMessages.fullCrystalHealthBar);
                    crystal.setCustomNameVisible(true);
                }
            }
        }
    }



    @EventHandler
    public void dragonFireballExplode(ProjectileHitEvent event) {
        if ((event.getEntity() instanceof DragonFireball || event.getEntity() instanceof ShulkerBullet) && event.getHitEntity() != null && event.getHitEntity() instanceof EnderDragon) {
            event.setCancelled(true);
        }

    }

    @EventHandler
    public void dragonExplodeAI(EntityDamageEvent event) {
        Entity var3 = event.getEntity();
        if (var3 instanceof EnderDragon) {
            EnderDragon enderDragon = (EnderDragon)var3;
            if (event.getCause() == DamageCause.BLOCK_EXPLOSION || event.getCause() == DamageCause.ENTITY_EXPLOSION) {
                boolean canAvoid = NDEnderDragon.ndenderdragon.getConfig().getBoolean("dragonAvoidsExplosions");
                if (canAvoid) {
                    event.setDamage(event.getDamage() / 4.0D);
                    enderDragon.setPhase(Phase.STRAFING);
                }
            }
        }

    }

    @EventHandler
    public void endermiteDamageResistance(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Endermite || event.getEntity() instanceof ShulkerBullet) {
            Entity var3 = event.getDamager();
            if (var3 instanceof EnderDragon) {
                EnderDragon enderDragon = (EnderDragon)var3;
                event.setCancelled(true);
            }
        }

    }

    @EventHandler
    public void enderCrystalDamage(EntityDamageEvent event) {
        Entity var3 = event.getEntity();
        if (var3 instanceof EnderCrystal) {
            EnderCrystal enderCrystal = (EnderCrystal)var3;
            if (enderCrystal.getCustomName() != null && (enderCrystal.getCustomName().equalsIgnoreCase(PresetChatMessages.fullCrystalHealthBar) || enderCrystal.getCustomName().equalsIgnoreCase(PresetChatMessages.crystalHealthBar2) || enderCrystal.getCustomName().equalsIgnoreCase(PresetChatMessages.crystalHealthBar1))) {
                event.setCancelled(true);
                if (!enderCrystal.isGlowing()) {
                    if (!this.enderCrystalHits.containsKey(enderCrystal)) {
                        this.enderCrystalHits.put(enderCrystal, 1);
                    } else {
                        this.enderCrystalHits.put(enderCrystal, (Integer)this.enderCrystalHits.get(enderCrystal) + 1);
                    }

                    int health = (Integer)this.enderCrystalHits.get(enderCrystal);
                    if (health == 1) {
                        enderCrystal.setCustomName(PresetChatMessages.crystalHealthBar2);
                    } else if (health == 2) {
                        enderCrystal.setCustomName(PresetChatMessages.crystalHealthBar1);
                    } else if (health == 3) {
                        enderCrystal.setCustomName(PresetChatMessages.crystalHealthBar0);
                    }

                    this.crystalFightBack(enderCrystal);
                }
            }
        }

    }

    private void crystalFightBack(final EnderCrystal crystal) {
        Iterator var2 = crystal.getLocation().getNearbyLivingEntities(4.0D, 4.0D, 4.0D).iterator();

        while(var2.hasNext()) {
            LivingEntity entity = (LivingEntity)var2.next();
            if (entity instanceof Player) {
                Player player = (Player)entity;
                player.playSound(player.getLocation(), "minecraft:block.glass.break", 20.0F, 1.0F);
            }
        }

        for(int i = 0; i < GeneralMethods.generateInt(7, 3); ++i) {
            crystal.getWorld().spawnParticle(Particle.CRIT, crystal.getLocation().clone().add(GeneralMethods.generateDouble(1.0D, -1.0D), GeneralMethods.generateDouble(1.0D, -1.0D), GeneralMethods.generateDouble(1.0D, -1.0D)), 0);
        }

        if (this.enderCrystalHits.containsKey(crystal) && (Integer)this.enderCrystalHits.get(crystal) < 3) {
            NDEnderDragon.crystalColor.setColor(ChatColor.RED);
            NDEnderDragon.crystalColor.addEntry(crystal.getUniqueId().toString());
            crystal.setGlowing(true);
            (new BukkitRunnable() {
                public void run() {
                    NDEnderDragon.crystalColor.removeEntry(crystal.getUniqueId().toString());
                    crystal.setGlowing(false);
                }
            }).runTaskLater(NDEnderDragon.ndenderdragon, 20L);
        }

        if (GeneralMethods.generateInt(4, 1) == 1) {
            var2 = DragonAttacks.dragons.iterator();

            while(var2.hasNext()) {
                EnderDragon dragon = (EnderDragon)var2.next();
                if (dragon.getWorld().equals(crystal.getWorld()) && !dragon.isDead()) {
                    for(int i = 0; i < GeneralMethods.generateInt(3, 1); ++i) {
                        DragonMethods.launchFireballsAtLocation(dragon, crystal.getLocation().clone().add(0.0D, -2.0D, 0.0D));
                    }

                    boolean announceAttack = NDEnderDragon.ndenderdragon.getConfig().getBoolean("dragonAttacksAnnounced");
                    if (announceAttack) {
                        String dragonName = NDEnderDragon.ndenderdragon.getConfig().getString("dragonName");
                        Bukkit.broadcastMessage(dragonName + String.valueOf(ChatColor.WHITE) + " đã phóng " + String.valueOf(ChatColor.YELLOW) + "cầu lửa" + String.valueOf(ChatColor.WHITE) + " tại một " + String.valueOf(ChatColor.YELLOW) + "Pha lê End" + String.valueOf(ChatColor.WHITE) + "!");
                    }
                    break;
                }
            }
        }

    }

    public static void initializeDragons() {
        Iterator var0 = EndWorldsData.endWorldsList.iterator();

        while(var0.hasNext()) {
            String world = (String)var0.next();
            Iterator var2 = Bukkit.getWorld(world).getLivingEntities().iterator();

            while(var2.hasNext()) {
                LivingEntity entity = (LivingEntity)var2.next();
                if (entity instanceof EnderDragon) {
                    EnderDragon dragon = (EnderDragon)entity;
                    DragonAttacks.dragons.add(dragon);
                }
            }
        }

    }

    @EventHandler
    public static void dragonDamageEvent(EntityDamageByEntityEvent event) {
        Entity var2 = event.getEntity();
        if (var2 instanceof EnderDragon) {
            EnderDragon dragon = (EnderDragon)var2;
            Entity var3 = event.getDamager();
            if (var3 instanceof Player) {
                Player player = (Player)var3;
                if (LeaderBoard.playerDamage.containsKey(player)) {
                    double previousDamage = (Double)LeaderBoard.playerDamage.get(player);
                    LeaderBoard.playerDamage.put(player, event.getDamage() + previousDamage);
                } else {
                    LeaderBoard.playerDamage.put(player, event.getDamage());
                }
            }
        }

    }

    @EventHandler
    public void dragonDeathEvent(EntityDeathEvent event) {
        LivingEntity var3 = event.getEntity();
        if (var3 instanceof EnderDragon) {
            EnderDragon dragon = (EnderDragon) var3;
            LeaderBoard.broadcastLeaderBoard();
            DragonMethods.freeTheEnd(dragon.getWorld());

            boolean canCreateLoot = NDEnderDragon.ndenderdragon.getConfig().getBoolean("dragonCreatesLoot");
            if (canCreateLoot) {
                this.createLoot(dragon.getWorld(), dragon);
            }

            if (DragonAttacks.dragons.contains(dragon)) {
                DragonAttacks.dragons.add(dragon);
            }

            /* Phá hủy các EndCrystal trong phạm vi nhất định
            World world = dragon.getWorld();
            for (EnderCrystal crystal : world.getEntitiesByClass(EnderCrystal.class)) {
                double x = crystal.getLocation().getX();
                double y = crystal.getLocation().getY();
                double z = crystal.getLocation().getZ();
                if (x >= -100 && x <= 100 && y >= 0 && y <= 250 && z >= -100 && z <= 100) {
                    crystal.getWorld().createExplosion(crystal.getLocation(), 4.0F);
                    crystal.remove();
                }
            }
            */

        }
    }
/*
    @EventHandler
    public void dragonDeathEvent(EntityDeathEvent event) {
        LivingEntity var3 = event.getEntity();
        if (var3 instanceof EnderDragon) {
            EnderDragon dragon = (EnderDragon)var3;
            LeaderBoard.broadcastLeaderBoard();
            DragonMethods.freeTheEnd(dragon.getWorld());
            boolean canCreateLoot = NDEnderDragon.ndenderdragon.getConfig().getBoolean("dragonCreatesLoot");
            if (canCreateLoot) {
                this.createLoot(dragon.getWorld(), dragon);
            }

            if (DragonAttacks.dragons.contains(dragon)) {
                DragonAttacks.dragons.add(dragon);
            }
        }

    }
*/
    private void createLoot(World dragonWorld, EnderDragon dragon) {
        String world = dragonWorld.getName();
        Location chestLocation = Bukkit.getWorld(world).getHighestBlockAt(0, 0).getLocation().add(0.0D, 1.0D, 0.0D);
        if (chestLocation.clone().add(0.0D, -1.0D, 0.0D).getBlock().getType() == Material.DRAGON_EGG) {
            chestLocation = Bukkit.getWorld(world).getHighestBlockAt(0, 0).getLocation();
            Bukkit.getWorld(world).getHighestBlockAt(0, 0).getLocation().add(0.0D, 1.0D, 0.0D).getBlock().setType(Material.DRAGON_EGG);
        }

        chestLocation.getBlock().setType(Material.CHEST);
        chestLocation.getWorld().spawnParticle(Particle.EXPLOSION, chestLocation, 1);
        Chest chest = (Chest)chestLocation.getBlock().getState();
        if (DragonAttacks.enragedDragons.contains(dragon)) {
            DragonLoot.generateChestLoot(chest.getBlockInventory(), true);
        } else {
            DragonLoot.generateChestLoot(chest.getBlockInventory(), false);
        }

    }
}