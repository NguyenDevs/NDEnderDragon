package com.github.nguyendevs.ND_EnderDragon.generals;

import com.github.nguyendevs.ND_EnderDragon.NDEnderDragon;
import org.bukkit.ChatColor;

public class PresetChatMessages {
    public static String pluginPrefix;
    public static String commandPrefix;
    public static String errorMessageMissingOneArg;
    public static String errorMessageMissingTwoArg;
    public static String errorMessageInvalidAction;
    public static String info;
    public static String console1;
    public static String console2;
    public static String console3;
    public static String console4;
    public static String fullCrystalHealthBar;
    public static String crystalHealthBar2;
    public static String crystalHealthBar1;
    public static String crystalHealthBar0;
    static {

        pluginPrefix = ChatColor.translateAlternateColorCodes('&',NDEnderDragon.ndenderdragon.getConfig().getString("messages.prefix"));
        commandPrefix = ChatColor.translateAlternateColorCodes('&',  "&8[&eLệnh&8]");

        errorMessageMissingOneArg = ChatColor.translateAlternateColorCodes('&',  NDEnderDragon.ndenderdragon.getConfig().getString("messages.prefix") + " &cBạn cần thêm tham số cho câu lệnh trên! (Ex. /command <arg>");
        errorMessageMissingTwoArg = ChatColor.translateAlternateColorCodes('&',  NDEnderDragon.ndenderdragon.getConfig().getString("messages.prefix") + " &cBạn cần thêm tham số cho câu lệnh trên! (Ex. /command <arg> <arg>");
        errorMessageInvalidAction = ChatColor.translateAlternateColorCodes('&',  NDEnderDragon.ndenderdragon.getConfig().getString("messages.prefix") + " &cCú pháp không hợp lệ! (/nd info)");

        info = ChatColor.translateAlternateColorCodes('&',  NDEnderDragon.ndenderdragon.getConfig().getString("messages.prefix") + String.valueOf(ChatColor.YELLOW) + "\n     Phát triển bởi NguyenDevs\n            V." + NDEnderDragon.currentVersion + "\n \n" + commandPrefix + String.valueOf(ChatColor.RED) + "\nTùy chỉnh thế giới:" + String.valueOf(ChatColor.YELLOW) + " \n- /nd endworlds add <world>\n- /nd endworlds remove <world>\n- /nd endworlds resetdragon <world>\n- /nd endworlds removedragon <world>\n- /nd endworlds list" + String.valueOf(ChatColor.RED) + "\nTùy chỉnh Loot:\n" + String.valueOf(ChatColor.YELLOW) + "- /nd editloot\n- /nd lootcontent");

        console1 = String.valueOf(ChatColor.DARK_GRAY) + "[ - - - " + String.valueOf(ChatColor.RED) + "NGUYENDEVS ENDER DRAGON" + String.valueOf(ChatColor.DARK_GRAY) + " - - - ]";
        console2 = String.valueOf(ChatColor.DARK_GRAY) + "       Phát triển bởi NguyenDevs";
        console3 = String.valueOf(ChatColor.DARK_GRAY) + "                 V." + NDEnderDragon.currentVersion;
        console4 = String.valueOf(ChatColor.DARK_GRAY) + "[ - - - = - = - =--=--= - = - = - - - ]";

        fullCrystalHealthBar = String.valueOf(ChatColor.DARK_GRAY) + "[" + String.valueOf(ChatColor.RED) + "❤❤❤" + String.valueOf(ChatColor.DARK_GRAY) + "]";
        crystalHealthBar2 = String.valueOf(ChatColor.DARK_GRAY) + "[" + String.valueOf(ChatColor.RED) + "❤❤" + String.valueOf(ChatColor.GRAY) + "❤" + String.valueOf(ChatColor.DARK_GRAY) + "]";
        crystalHealthBar1 = String.valueOf(ChatColor.DARK_GRAY) + "[" + String.valueOf(ChatColor.RED) + "❤" + String.valueOf(ChatColor.GRAY) + "❤❤" + String.valueOf(ChatColor.DARK_GRAY) + "]";
        crystalHealthBar0 = String.valueOf(ChatColor.DARK_GRAY) + "[" + String.valueOf(ChatColor.RED) + String.valueOf(ChatColor.GRAY) + "❤❤❤" + String.valueOf(ChatColor.DARK_GRAY) + "]";
    }
}
