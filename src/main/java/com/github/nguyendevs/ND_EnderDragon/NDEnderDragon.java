package com.github.nguyendevs.ND_EnderDragon;

import com.github.nguyendevs.ND_EnderDragon.commands.CommandReload;
import com.github.nguyendevs.ND_EnderDragon.dragonrevamp.loot.DragonLoot;
import com.github.nguyendevs.ND_EnderDragon.dragonrevamp.loot.DragonLootEditor;
import com.github.nguyendevs.ND_EnderDragon.dragonrevamp.moves.DragonAI;
import com.github.nguyendevs.ND_EnderDragon.dragonrevamp.moves.DragonAttacks;
import com.github.nguyendevs.ND_EnderDragon.generals.EndWorldsData;
import com.github.nguyendevs.ND_EnderDragon.generals.PresetChatMessages;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import java.util.Iterator;

public final class NDEnderDragon extends JavaPlugin {
    public static double currentVersion = 1.0D;
    FileConfiguration config = this.getConfig();
    public static Plugin ndenderdragon;
    public static Team crystalColor;

    public void onEnable() {
        this.saveDefaultConfig();

        ndenderdragon = this;
        this.initializeListeners();
        DragonAI.initializeDragons();
        this.initializeCommands();
        initializeLoottable();
        EndWorldsData.restoreEndWorlds();
        DragonLootEditor.restoreDragonLoot();
        this.setupTeam();
        this.sendConsoleMessages();
    }

    private void setupTeam() {
        Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
        boolean foundSameTeam = false;
        Iterator var3 = board.getTeams().iterator();

        while(var3.hasNext()) {
            Team team = (Team)var3.next();
            if (team.getName().equalsIgnoreCase("crystalColor")) {
                crystalColor = team;
                foundSameTeam = true;
                break;
            }
        }

        if (!foundSameTeam) {
            Team newTeam = board.registerNewTeam("crystalColor");
            crystalColor = newTeam;
        }

    }

    public void onDisable() {
        EndWorldsData.saveEndWorlds();
        DragonLootEditor.saveDragonLoot();
    }

    public void sendConsoleMessages() {
        this.getServer().getConsoleSender().sendMessage(PresetChatMessages.console1);
        this.getServer().getConsoleSender().sendMessage(" ");
        this.getServer().getConsoleSender().sendMessage(PresetChatMessages.console2);
        this.getServer().getConsoleSender().sendMessage(PresetChatMessages.console3);
        this.getServer().getConsoleSender().sendMessage(" ");
        this.getServer().getConsoleSender().sendMessage(PresetChatMessages.console4);
    }

    public static void initializeLoottable() {
        DragonLoot.loadOrCreateLoottableRateFile();
        DragonLoot.updateLoottableRateConfiguration();
        if (DragonLoot.dragonLoottable.isEmpty()) {
            DragonLoot.initializeDefaultLoot();
        }

    }

    public void initializeListeners() {
        this.getServer().getPluginManager().registerEvents(new DragonAttacks(), this);
        this.getServer().getPluginManager().registerEvents(new DragonAI(), this);
        this.getServer().getPluginManager().registerEvents(new DragonLootEditor(), this);
        this.getServer().getPluginManager().registerEvents(new DragonLoot(), this);
    }

    public void initializeCommands() {

        this.getCommand("nd").setExecutor(new CommandReload());
    }

    public FileConfiguration getConfigFile() {
        return this.config;
    }
}
