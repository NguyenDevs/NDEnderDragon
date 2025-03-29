package com.github.nguyendevs.ND_EnderDragon.generals;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import com.github.nguyendevs.ND_EnderDragon.NDEnderDragon;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;

public class LeaderBoard {
    public static HashMap<Player, Double> playerDamage = new HashMap();

    public static void broadcastLeaderBoard() {
        boolean canDisplayLeaderboard = NDEnderDragon.ndenderdragon.getConfig().getBoolean("shouldDisplayLeaderboard");
        if (canDisplayLeaderboard) {
            Bukkit.broadcastMessage(" ");
            Bukkit.broadcastMessage("    " + ChatColor.translateAlternateColorCodes('&',
                    NDEnderDragon.ndenderdragon.getConfig().getString("messages.prefix")));
            Bukkit.broadcastMessage(String.valueOf(ChatColor.GRAY) + "Rồng Ender đã bị đánh bại!");
            Bukkit.broadcastMessage(String.valueOf(ChatColor.DARK_GRAY) + "[- - - - - - - - - - - - - - - - -]");
        }

        HashMap<Player, Double> sortedMap = sortByDamage(playerDamage);
        Iterator<Entry<Player, Double>> iterator = sortedMap.entrySet().iterator();
        List<Player> highestDamagers = new ArrayList();
        int iteratedThrough = 1;

        Player player;
        while(iterator.hasNext()) {
            Entry<Player, Double> entry = (Entry)iterator.next();
            player = (Player)entry.getKey();
            Double damage = (Double)entry.getValue();
            if (canDisplayLeaderboard) {
                DecimalFormat format = new DecimalFormat("#.##");
                String formattedDamage = format.format(damage);
                String var10000 = String.valueOf(ChatColor.GOLD);
                Bukkit.broadcastMessage(var10000 + "  #" + iteratedThrough + " " + player.getDisplayName() + ": " + String.valueOf(ChatColor.WHITE) + " " + formattedDamage + " sát thương!");
            }

            ++iteratedThrough;
            if (iteratedThrough <= 4) {
                highestDamagers.add(player);
            }
        }

        if (canDisplayLeaderboard) {
            Bukkit.broadcastMessage(String.valueOf(ChatColor.DARK_GRAY) + "[- - - - - - - - - - - - - - - - -]");
            Bukkit.broadcastMessage(" ");
        }

        Iterator var10 = highestDamagers.iterator();

        while(var10.hasNext()) {
            player = (Player)var10.next();
            ExperienceOrb orb = (ExperienceOrb)player.getWorld().spawn(player.getLocation().clone().add(0.0D, 0.0D, 0.0D), ExperienceOrb.class);
            orb.setExperience(GeneralMethods.generateInt(60, 10));
        }

    }

    private static HashMap<Player, Double> sortByDamage(HashMap<Player, Double> unsortedMap) {
        LinkedHashMap<Player, Double> sortedMap = new LinkedHashMap();
        ArrayList<Double> list = new ArrayList();
        Iterator var3 = unsortedMap.entrySet().iterator();

        while(var3.hasNext()) {
            Entry<Player, Double> entry = (Entry)var3.next();
            list.add((Double)entry.getValue());
        }

        Collections.sort(list);
        var3 = list.iterator();

        while(var3.hasNext()) {
            double num = (Double)var3.next();
            Iterator var6 = unsortedMap.entrySet().iterator();

            while(var6.hasNext()) {
                Entry<Player, Double> entry = (Entry)var6.next();
                if (((Double)entry.getValue()).equals(num)) {
                    sortedMap.put((Player)entry.getKey(), num);
                }
            }
        }

        return sortedMap;
    }
}
