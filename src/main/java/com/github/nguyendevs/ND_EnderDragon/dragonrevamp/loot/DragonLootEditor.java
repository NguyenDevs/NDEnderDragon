package com.github.nguyendevs.ND_EnderDragon.dragonrevamp.loot;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Iterator;
import javax.annotation.Nullable;
import com.github.nguyendevs.ND_EnderDragon.NDEnderDragon;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

public class DragonLootEditor implements Listener {
    @EventHandler
    public void saveLootSettings(InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();
        if (event.getView().getTitle().equalsIgnoreCase("Vật phẩm Loot Ender Dragon")) {
            DragonLoot.dragonLoottable.clear();

            for(int i = 0; i < 27; ++i) {
                if (inventory.getItem(i) != null) {
                    DragonLoot.dragonLoottable.add(inventory.getItem(i));
                }
            }
        }

    }

    public static void openLootEditor(Player player) {
        Inventory lootEditor = Bukkit.createInventory(player, 54, "Tùy chỉnh Loot");
        Iterator var2 = DragonLoot.dragonLoottable.iterator();

        while(var2.hasNext()) {
            ItemStack item = (ItemStack)var2.next();
            lootEditor.addItem(new ItemStack[]{item});
        }

        player.openInventory(lootEditor);
    }

    public static void saveObject(String path, Object object) {
        try {
            File file = new File(path);
            if (!file.isFile()) {
                file.createNewFile();
            }

            ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(file));
            output.writeObject(object);
        } catch (IOException var4) {
            var4.printStackTrace();
        }

    }

    @Nullable
    private static Object loadObject(String path) {
        try {
            File file = new File(path);
            if (!file.isFile()) {
                System.out.println("Đã có lỗi khi cố tải config: file " + path + " không tồn tại.");
                return null;
            } else {
                ObjectInputStream input = new ObjectInputStream(new FileInputStream(file));
                return input.readObject();
            }
        } catch (ClassNotFoundException | IOException var3) {
            var3.printStackTrace();
            return null;
        }
    }

    public static void saveDragonLoot() {
        ArrayList encodedObjects = new ArrayList();

        try {
            Iterator var1 = DragonLoot.dragonLoottable.iterator();

            while(var1.hasNext()) {
                ItemStack item = (ItemStack)var1.next();
                ByteArrayOutputStream io = new ByteArrayOutputStream();
                BukkitObjectOutputStream os = new BukkitObjectOutputStream(io);
                os.writeObject(item);
                os.flush();
                byte[] serializedObject = io.toByteArray();
                String encodedObject = Base64.getEncoder().encodeToString(serializedObject);
                encodedObjects.add(encodedObject);
            }

            saveObject("plugins/NDEnderDragon/dragonLootItems.ser", encodedObjects);
        } catch (IOException var7) {
            System.out.println(var7);
        }

    }

    public static void restoreDragonLoot() {
        ArrayList<String> loaded = (ArrayList)loadObject("plugins/NDEnderDragon/dragonLootItems.ser");
        if (loaded == null) {
            NDEnderDragon.initializeLoottable();
        } else {
            DragonLoot.dragonLoottable = new ArrayList();

            try {
                Iterator var1 = loaded.iterator();

                while(var1.hasNext()) {
                    String encodedObject = (String)var1.next();
                    byte[] serializedObject = Base64.getDecoder().decode(encodedObject);
                    ByteArrayInputStream in = new ByteArrayInputStream(serializedObject);
                    BukkitObjectInputStream is = new BukkitObjectInputStream(in);
                    ItemStack decodedItem = (ItemStack)is.readObject();
                    DragonLoot.dragonLoottable.add(decodedItem);
                }
            } catch (ClassNotFoundException | IOException var7) {
                System.out.println(var7);
            }
        }

    }
}