package com.github.nguyendevs.ND_EnderDragon.commands;

import com.github.nguyendevs.ND_EnderDragon.NDEnderDragon;
import com.github.nguyendevs.ND_EnderDragon.dragonrevamp.loot.DragonLoot;
import com.github.nguyendevs.ND_EnderDragon.dragonrevamp.loot.DragonLootEditor;
import com.github.nguyendevs.ND_EnderDragon.generals.DragonMethods;
import com.github.nguyendevs.ND_EnderDragon.generals.EndWorldsData;
import com.github.nguyendevs.ND_EnderDragon.generals.PresetChatMessages;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.boss.DragonBattle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;

public class CommandReload implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',NDEnderDragon.ndenderdragon.getConfig().getString("messages.prefix") +" &cChỉ người chơi mới được thực hiện lệnh này."));
            return true;
        }

        Player player = (Player) sender;
        if (!player.isOp()||!player.hasPermission("nd.admin")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    NDEnderDragon.ndenderdragon.getConfig().getString("messages.prefix") + " " +
                            Objects.requireNonNull(NDEnderDragon.ndenderdragon.getConfig().getString("messages.no_permission"))));

            return true;
        }

        if (args.length == 0) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',NDEnderDragon.ndenderdragon.getConfig().getString("messages.prefix") + " &eSử dụng: /nd <info|endworlds|editloot|lootcontent>"));
            return true;
        }

        String subCommand = args[0].toLowerCase();
        switch (subCommand) {
            case "info":
                player.sendMessage(PresetChatMessages.info);
                break;
            case "editloot":
                DragonLootEditor.openLootEditor(player);
                break;
            case "lootcontent":
                DragonLoot.generateConceptLoot(player);
                break;
            case "endworlds":
                handleEndWorlds(player, args);
                break;
            case "reload":
                NDEnderDragon.ndenderdragon.saveConfig();

                //NDEnderDragon.ndenderdragon.saveDefaultConfig();
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',NDEnderDragon.ndenderdragon.getConfig().getString("messages.prefix") + " "+ NDEnderDragon.ndenderdragon.getConfig().getString("messages.reload")));
                NDEnderDragon.ndenderdragon.reloadConfig();
                break;
            default:
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',NDEnderDragon.ndenderdragon.getConfig().getString("messages.prefix") +" &cLệnh không hợp lệ. Sử dụng: /nd <info|endworlds|editloot|lootcontent>"));
                break;
        }
        return true;
    }

    private void handleEndWorlds(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',NDEnderDragon.ndenderdragon.getConfig().getString("messages.prefix") + " &eSử dụng: /nd endworlds <add|remove|resetdragon|removedragon|list> [world]"));
            return;
        }

        String action = args[1].toLowerCase();
        String worldName = (args.length >= 3) ? args[2] : "";

        switch (action) {
            case "add":
                if (!worldName.isEmpty()) addWorld(player, worldName);
                break;
            case "remove":
                if (!worldName.isEmpty()) removeWorld(player, worldName);
                break;
            case "resetdragon":
                if (!worldName.isEmpty()) resetDragon(player, worldName);
                break;
            case "removedragon":
                if (!worldName.isEmpty()) removeDragon(player, worldName);
                break;
            case "list":
                listWorlds(player);
                break;
            default:
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',NDEnderDragon.ndenderdragon.getConfig().getString("messages.prefix") +" &cTham số không hợp lệ cho /nd endworlds. Sử dụng: add, remove, resetdragon, removedragon, list"));
                break;
        }
    }

    // Các phương thức xử lý như addWorld, removeWorld, resetDragon, removeDragon, listWorlds
    private void listWorlds(Player player) {
        if (EndWorldsData.endWorldsList.isEmpty()) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    NDEnderDragon.ndenderdragon.getConfig().getString("messages.prefix") + " &cHiện tại không có the_end nào đang hoạt động."));

        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    NDEnderDragon.ndenderdragon.getConfig().getString("messages.prefix") + " &aDanh sách thế giới:"));

            Iterator var2 = EndWorldsData.endWorldsList.iterator();

            while(var2.hasNext()) {
                String world = (String)var2.next();
                String var10001 = String.valueOf(ChatColor.YELLOW);
                player.sendMessage(var10001 + "- " + world);
            }
        }

    }
    private void addWorld(Player player, String worldName) {
        World world = Bukkit.getWorld(worldName);
        if (world != null) {
            if (EndWorldsData.endWorldsList.contains(worldName)) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        NDEnderDragon.ndenderdragon.getConfig().getString("messages.prefix") + " " +
                                Objects.requireNonNull(NDEnderDragon.ndenderdragon.getConfig().getString("messages.already_add")).replace("%world%", worldName)));
            } else {
                EndWorldsData.endWorldsList.add(worldName);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        NDEnderDragon.ndenderdragon.getConfig().getString("messages.prefix") + " " +
                                Objects.requireNonNull(NDEnderDragon.ndenderdragon.getConfig().getString("messages.successfully_add")).replace("%world%", worldName)));
            }
        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    NDEnderDragon.ndenderdragon.getConfig().getString("messages.prefix") + " &cThế giới không tồn tại."));

        }

    }

    private void removeWorld(Player player, String worldName) {
        World world = Bukkit.getWorld(worldName);
        if (world != null) {
            if (EndWorldsData.endWorldsList.contains(worldName)) {
                EndWorldsData.endWorldsList.remove(worldName);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        NDEnderDragon.ndenderdragon.getConfig().getString("messages.prefix") + " " +
                                Objects.requireNonNull(NDEnderDragon.ndenderdragon.getConfig().getString("messages.successfully_remove")).replace("%world%", worldName)));
            } else {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        NDEnderDragon.ndenderdragon.getConfig().getString("messages.prefix") + " &cThế giới không hợp lệ hoặc không có trong danh sách."));
            }
        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    NDEnderDragon.ndenderdragon.getConfig().getString("messages.prefix") + " &cThế giới không tồn tại."));

        }

    }

    private void removeDragon(Player player, String worldName) {
        World world = Bukkit.getWorld(worldName);
        if (world != null) {
            Iterator<LivingEntity> entities = world.getLivingEntities().iterator();
            while (entities.hasNext()) {
                LivingEntity entity = entities.next();
                if (entity instanceof EnderDragon) {
                    entity.damage(1000000.0D, player);
                }
            }

            new BukkitRunnable() {
                @Override
                public void run() {
                    for (EnderCrystal crystal : world.getEntitiesByClass(EnderCrystal.class)) {
                        double x = crystal.getLocation().getX();
                        double y = crystal.getLocation().getY();
                        double z = crystal.getLocation().getZ();
                        if (x >= -100 && x <= 100 && y >= 0 && y <= 250 && z >= -100 && z <= 100) {
                            crystal.getWorld().createExplosion(crystal.getLocation(), 4.0F);
                            crystal.remove();
                        }
                    }
                }
            }.runTaskLater(NDEnderDragon.ndenderdragon, 100L); // Delay 5 giây (100 ticks)
        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    NDEnderDragon.ndenderdragon.getConfig().getString("messages.prefix") + " &cThế giới không tồn tại."));

        }
    }

    private void resetDragon(Player player, String worldName) {
        final World world = Bukkit.getWorld(worldName);
        if (world != null) {
            Iterator var4 = Bukkit.getWorld(world.getName()).getLivingEntities().iterator();

            while(var4.hasNext()) {
                LivingEntity entity = (LivingEntity)var4.next();
                if (entity instanceof EnderDragon) {
                    EnderDragon dragon = (EnderDragon)entity;
                    dragon.damage(1000000.0D, player);
                    dragon.teleport(world.getHighestBlockAt(0, 0).getLocation());
                } else if (entity instanceof EnderCrystal) {
                    EnderCrystal enderCrystal = (EnderCrystal)entity;
                    enderCrystal.setInvulnerable(false);
                    enderCrystal.remove();
                }
            }

            (new BukkitRunnable() {
                public void run() {
                    DragonMethods.createCrystals(world);
                    Bukkit.getServer().getWorld(world.getName()).getEnderDragonBattle().initiateRespawn();
                    Bukkit.getServer().getWorld(world.getName()).getEnderDragonBattle().setRespawnPhase(DragonBattle.RespawnPhase.SUMMONING_DRAGON);
                }
            }).runTaskLater(NDEnderDragon.ndenderdragon, 260L);
        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    NDEnderDragon.ndenderdragon.getConfig().getString("messages.prefix") + " &cThế giới không tồn tại."));

        }

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player) || !((Player) sender).isOp()) {
            return Collections.emptyList();
        }

        if (args.length == 1) {
            return Arrays.asList("info", "endworlds", "editloot", "lootcontent", "reload");
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("endworlds")) {
            return Arrays.asList("add", "remove", "resetdragon", "removedragon", "list");
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("endworlds")) {
            return Bukkit.getWorlds().stream().map(World::getName).collect(Collectors.toList());
        }

        return Collections.emptyList();
    }
}
