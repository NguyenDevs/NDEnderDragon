package com.github.nguyendevs.ND_EnderDragon.dragonrevamp.loot;

import com.github.nguyendevs.ND_EnderDragon.generals.GeneralMethods;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class DragonLoot implements Listener {
    public static FileConfiguration lootrateData;
    public static List<ItemStack> dragonLoottable = new ArrayList<>();

    // Unique NamespacedKeys for each armor piece's modifiers
    // Crown
    private static final NamespacedKey CROWN_SPEED_KEY = new NamespacedKey("nd_enderdragon", "crown_speed_boost");
    private static final NamespacedKey CROWN_HEALTH_KEY = new NamespacedKey("nd_enderdragon", "crown_health_boost");
    private static final NamespacedKey CROWN_ARMOR_KEY = new NamespacedKey("nd_enderdragon", "crown_armor_boost");
    private static final NamespacedKey CROWN_TOUGHNESS_KEY = new NamespacedKey("nd_enderdragon", "crown_toughness_boost");
    private static final NamespacedKey CROWN_ATTACK_SPEED_KEY = new NamespacedKey("nd_enderdragon", "crown_attack_speed_boost");

    // Chestplate
    private static final NamespacedKey CHESTPLATE_SPEED_KEY = new NamespacedKey("nd_enderdragon", "chestplate_speed_boost");
    private static final NamespacedKey CHESTPLATE_HEALTH_KEY = new NamespacedKey("nd_enderdragon", "chestplate_health_boost");
    private static final NamespacedKey CHESTPLATE_ARMOR_KEY = new NamespacedKey("nd_enderdragon", "chestplate_armor_boost");
    private static final NamespacedKey CHESTPLATE_TOUGHNESS_KEY = new NamespacedKey("nd_enderdragon", "chestplate_toughness_boost");
    private static final NamespacedKey CHESTPLATE_ATTACK_SPEED_KEY = new NamespacedKey("nd_enderdragon", "chestplate_attack_speed_boost");

    // Leggings
    private static final NamespacedKey LEGGINGS_SPEED_KEY = new NamespacedKey("nd_enderdragon", "leggings_speed_boost");
    private static final NamespacedKey LEGGINGS_HEALTH_KEY = new NamespacedKey("nd_enderdragon", "leggings_health_boost");
    private static final NamespacedKey LEGGINGS_ARMOR_KEY = new NamespacedKey("nd_enderdragon", "leggings_armor_boost");
    private static final NamespacedKey LEGGINGS_TOUGHNESS_KEY = new NamespacedKey("nd_enderdragon", "leggings_toughness_boost");
    private static final NamespacedKey LEGGINGS_ATTACK_SPEED_KEY = new NamespacedKey("nd_enderdragon", "leggings_attack_speed_boost");

    // Boots
    private static final NamespacedKey BOOTS_SPEED_KEY = new NamespacedKey("nd_enderdragon", "boots_speed_boost");
    private static final NamespacedKey BOOTS_HEALTH_KEY = new NamespacedKey("nd_enderdragon", "boots_health_boost");
    private static final NamespacedKey BOOTS_ARMOR_KEY = new NamespacedKey("nd_enderdragon", "boots_armor_boost");
    private static final NamespacedKey BOOTS_TOUGHNESS_KEY = new NamespacedKey("nd_enderdragon", "boots_toughness_boost");
    private static final NamespacedKey BOOTS_ATTACK_SPEED_KEY = new NamespacedKey("nd_enderdragon", "boots_attack_speed_boost");

    public static boolean shouldPlace(int chance, int modifier) {
        int newChance = chance - modifier;
        return GeneralMethods.generateInt(newChance, 1) == 1;
    }

    public static void generateConceptLoot(Player player) {
        Inventory lootEditor = Bukkit.createInventory(player, 27, "Danh sách vật phẩm");
        int progression = 0;
        Iterator<ItemStack> var3 = dragonLoottable.iterator();

        while (var3.hasNext()) {
            ItemStack item = var3.next();
            ++progression;
            int chance = lootrateData.getInt("loot_item_" + progression + ".chance");
            boolean multipleRolls = lootrateData.getBoolean("loot_item_" + progression + ".multiple_rolls");
            boolean hasPlaced = false;

            for (int i = 0; i < lootEditor.getSize(); ++i) {
                if (!hasPlaced && lootEditor.getItem(i) == null && shouldPlace(chance, 0)) {
                    if (!multipleRolls) {
                        hasPlaced = true;
                    }
                    lootEditor.setItem(i, item);
                    int maxAmount = lootrateData.getInt("loot_item_" + progression + ".maxamount");
                    int minAmount = lootrateData.getInt("loot_item_" + progression + ".minamount");
                    int amount = GeneralMethods.generateInt(maxAmount, minAmount);
                    lootEditor.getItem(i).setAmount(amount);
                }
            }
        }
        player.openInventory(lootEditor);
    }

    public static void generateChestLoot(Inventory chestInventory, boolean isEnraged) {
        int progression = 0;
        Iterator<ItemStack> var3 = dragonLoottable.iterator();

        while (var3.hasNext()) {
            ItemStack item = var3.next();
            ++progression;
            int modifier = isEnraged ? lootrateData.getInt("loot_item_" + progression + ".enraged_addition") : 0;
            int chance = lootrateData.getInt("loot_item_" + progression + ".chance");
            boolean multipleRolls = lootrateData.getBoolean("loot_item_" + progression + ".multiple_rolls");
            boolean enragedOnly = lootrateData.getBoolean("loot_item_" + progression + ".enraged_only");
            boolean hasPlaced = false;

            for (int i = 0; i < chestInventory.getSize(); ++i) {
                if (!hasPlaced) {
                    if (enragedOnly && !isEnraged) {
                        hasPlaced = true;
                    }
                    if (chestInventory.getItem(i) == null && shouldPlace(chance, modifier)) {
                        if (!multipleRolls) {
                            hasPlaced = true;
                        }
                        chestInventory.setItem(i, item);
                        int maxAmount = lootrateData.getInt("loot_item_" + progression + ".maxamount");
                        int minAmount = lootrateData.getInt("loot_item_" + progression + ".minamount");
                        int amount = GeneralMethods.generateInt(maxAmount, minAmount);
                        chestInventory.getItem(i).setAmount(amount);
                    }
                }
            }
        }
    }

    public static void updateLoottableRateConfiguration() {
        try {
            File file = new File("plugins/NDEnderDragon/lootrates.yml");
            FileConfiguration configuration = YamlConfiguration.loadConfiguration(file);
            if (!file.exists()) {
                file.createNewFile();
            } else {
                lootrateData = configuration;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadOrCreateLoottableRateFile() {
        try {
            File file = new File("plugins/NDEnderDragon/lootrates.yml");
            FileConfiguration configuration = YamlConfiguration.loadConfiguration(file);
            initializeDefaultLootValues(configuration);

            for (int i = 20; i < 28; ++i) {
                configuration.addDefault("loot_item_" + i, 50);
                configuration.addDefault("loot_item_" + i + ".chance", 100);
                configuration.addDefault("loot_item_" + i + ".maxamount", 1);
                configuration.addDefault("loot_item_" + i + ".minamount", 1);
                configuration.addDefault("loot_item_" + i + ".enraged_addition", 0);
                configuration.addDefault("loot_item_" + i + ".multiple_rolls", false);
                configuration.addDefault("loot_item_" + i + ".enraged_only", false);
            }

            configuration.options().copyDefaults(true);
            configuration.save(file);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void initializeDefaultLootValues(FileConfiguration configuration) {
        initializeLootWithValue(configuration, "loot_item_1", 50, 3, 1, 1, false, 0, false);
        initializeLootWithValue(configuration, "loot_item_2", 50, 3, 1, 1, false, 0, false);
        initializeLootWithValue(configuration, "loot_item_3", 50, 3, 1, 1, false, 0, false);
        initializeLootWithValue(configuration, "loot_item_4", 50, 3, 1, 1, false, 0, false);
        initializeLootWithValue(configuration, "loot_item_5", 50, 20, 1, 1, false, 0, false);
        initializeLootWithValue(configuration, "loot_item_6", 50, 10, 1, 1, false, 0, false);
        initializeLootWithValue(configuration, "loot_item_7", 50, 24, 8, 1, true, 6, false);
        initializeLootWithValue(configuration, "loot_item_8", 50, 24, 12, 1, true, 6, false);
        initializeLootWithValue(configuration, "loot_item_9", 50, 28, 2, 1, true, 4, false);
        initializeLootWithValue(configuration, "loot_item_10", 50, 16, 8, 1, true, 0, false);
        initializeLootWithValue(configuration, "loot_item_11", 50, 16, 8, 1, true, 0, false);
        initializeLootWithValue(configuration, "loot_item_12", 50, 16, 4, 1, true, 4, false);
        initializeLootWithValue(configuration, "loot_item_13", 50, 16, 2, 1, true, 0, false);
        initializeLootWithValue(configuration, "loot_item_14", 50, 16, 8, 1, true, 0, false);
        initializeLootWithValue(configuration, "loot_item_15", 50, 16, 8, 1, true, 0, false);
        initializeLootWithValue(configuration, "loot_item_16", 50, 16, 8, 1, true, 0, false);
        initializeLootWithValue(configuration, "loot_item_17", 50, 16, 8, 1, true, 0, false);
        initializeLootWithValue(configuration, "loot_item_18", 50, 16, 8, 1, true, 0, false);
        initializeLootWithValue(configuration, "loot_item_19", 50, 2, 1, 1, false, 0, false);
    }

    public static void initializeLootWithValue(FileConfiguration configuration, String path, int value, int chance, int max, int min, boolean multipleRolls, int enragedModifier, boolean enragedOnly) {
        configuration.addDefault(path, value);
        configuration.addDefault(path + ".chance", chance);
        configuration.addDefault(path + ".maxamount", max);
        configuration.addDefault(path + ".minamount", min);
        configuration.addDefault(path + ".enraged_addition", enragedModifier);
        configuration.addDefault(path + ".multiple_rolls", multipleRolls);
        configuration.addDefault(path + ".enraged_only", enragedOnly);
    }

    public static void initializeDefaultLoot() {
        dragonLoottable.add(dragonSlayerCrown());
        dragonLoottable.add(dragonSlayerChestplate());
        dragonLoottable.add(dragonSlayerLeggings());
        dragonLoottable.add(dragonSlayerBoots());
        dragonLoottable.add(dragonHeart());
        dragonLoottable.add(dragonElixir());
        dragonLoottable.add(new ItemStack(Material.SKELETON_SKULL));
        dragonLoottable.add(new ItemStack(Material.GOLD_INGOT));
        dragonLoottable.add(new ItemStack(Material.RAW_GOLD));
        dragonLoottable.add(new ItemStack(Material.RAW_GOLD_BLOCK));
        dragonLoottable.add(new ItemStack(Material.BONE));
        dragonLoottable.add(new ItemStack(Material.ROTTEN_FLESH));
        dragonLoottable.add(new ItemStack(Material.EMERALD));
        dragonLoottable.add(new ItemStack(Material.ENDER_PEARL));
        dragonLoottable.add(new ItemStack(Material.PURPUR_BLOCK));
        dragonLoottable.add(new ItemStack(Material.END_STONE));
        dragonLoottable.add(new ItemStack(Material.END_STONE_BRICKS));
        dragonLoottable.add(new ItemStack(Material.CRYING_OBSIDIAN));
        dragonLoottable.add(new ItemStack(Material.OBSIDIAN));
    }

    public static ItemStack dragonHeart() {
        return GeneralMethods.createBasicPlayerHead(
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTg1MTRkODIzMGI3NTUxMWE1YTVhNjljYTkzZGNiMmQzZTdjZDFhMjhjNDhkYzM4MDg3ZjE1OGQyODNiN2ZhNyJ9fX0=",
                ChatColor.DARK_RED + "Trái tim long chủng"
        );
    }

    public static ItemStack createArmorPiece(Plugin plugin, Material material, String name, String key, int durability) {
        ItemStack armor = new ItemStack(material);
        ItemMeta meta = armor.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(ChatColor.YELLOW + name);

            // Use unique NamespacedKeys based on the key parameter
            NamespacedKey speedKey = new NamespacedKey(plugin, key + "_speed_boost");
            NamespacedKey healthKey = new NamespacedKey(plugin, key + "_health_boost");
            NamespacedKey armorKey = new NamespacedKey(plugin, key + "_armor_boost");
            NamespacedKey toughnessKey = new NamespacedKey(plugin, key + "_toughness_boost");
            NamespacedKey attackSpeedKey = new NamespacedKey(plugin, key + "_attack_speed_boost");

            AttributeModifier speedModifier = new AttributeModifier(speedKey, 0.015, AttributeModifier.Operation.ADD_NUMBER, getSlotGroupForMaterial(material));
            AttributeModifier healthModifier = new AttributeModifier(healthKey, 2.0, AttributeModifier.Operation.ADD_NUMBER, getSlotGroupForMaterial(material));
            AttributeModifier armorModifier = new AttributeModifier(armorKey, getArmorValue(material), AttributeModifier.Operation.ADD_NUMBER, getSlotGroupForMaterial(material));
            AttributeModifier toughnessModifier = new AttributeModifier(toughnessKey, 3.0, AttributeModifier.Operation.ADD_NUMBER, getSlotGroupForMaterial(material));
            AttributeModifier attackSpeedModifier = new AttributeModifier(attackSpeedKey, 0.25, AttributeModifier.Operation.ADD_NUMBER, getSlotGroupForMaterial(material));

            meta.addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED, speedModifier);
            meta.addAttributeModifier(Attribute.GENERIC_MAX_HEALTH, healthModifier);
            meta.addAttributeModifier(Attribute.GENERIC_ARMOR, armorModifier);
            meta.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, toughnessModifier);
            meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, attackSpeedModifier);

            armor.setItemMeta(meta);
        }

        armor.setDurability((short) GeneralMethods.generateInt(durability, durability / 2));
        return armor;
    }

    private static EquipmentSlotGroup getSlotGroupForMaterial(Material material) {
        return switch (material) {
            case DIAMOND_HELMET -> EquipmentSlotGroup.HEAD;
            case DIAMOND_CHESTPLATE -> EquipmentSlotGroup.CHEST;
            case DIAMOND_LEGGINGS -> EquipmentSlotGroup.LEGS;
            case DIAMOND_BOOTS -> EquipmentSlotGroup.FEET;
            default -> EquipmentSlotGroup.HAND; // Fallback, though not used here
        };
    }

    private static double getArmorValue(Material material) {
        return switch (material) {
            case DIAMOND_HELMET -> 3.0;
            case DIAMOND_CHESTPLATE -> 8.0;
            case DIAMOND_LEGGINGS -> 6.0;
            case DIAMOND_BOOTS -> 3.0;
            default -> 0.0;
        };
    }

    public static ItemStack dragonSlayerCrown() {
        ItemStack armor = new ItemStack(Material.DIAMOND_HELMET);
        ArmorMeta armorMeta = (ArmorMeta) armor.getItemMeta();

        if (armorMeta != null) {
            armorMeta.setDisplayName(ChatColor.AQUA + "Vương miện long chủng");

            AttributeModifier speedModifier = new AttributeModifier(CROWN_SPEED_KEY, 0.015, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.HEAD);
            AttributeModifier healthModifier = new AttributeModifier(CROWN_HEALTH_KEY, 2.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.HEAD);
            AttributeModifier armorModifier = new AttributeModifier(CROWN_ARMOR_KEY, 3.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.HEAD);
            AttributeModifier toughnessModifier = new AttributeModifier(CROWN_TOUGHNESS_KEY, 3.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.HEAD);
            AttributeModifier attackSpeedModifier = new AttributeModifier(CROWN_ATTACK_SPEED_KEY, 0.25, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.HEAD);

            armorMeta.addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED, speedModifier);
            armorMeta.addAttributeModifier(Attribute.GENERIC_MAX_HEALTH, healthModifier);
            armorMeta.addAttributeModifier(Attribute.GENERIC_ARMOR, armorModifier);
            armorMeta.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, toughnessModifier);
            armorMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, attackSpeedModifier);

            ArmorTrim trim = new ArmorTrim(TrimMaterial.NETHERITE, TrimPattern.RAISER);
            armorMeta.setTrim(trim);

            armor.setItemMeta(armorMeta);
        }

        armor.setDurability((short) GeneralMethods.generateInt(350, 30));
        return armor;
    }

    public static ItemStack dragonSlayerChestplate() {
        ItemStack armor = new ItemStack(Material.DIAMOND_CHESTPLATE);
        ArmorMeta armorMeta = (ArmorMeta) armor.getItemMeta();

        if (armorMeta != null) {
            armorMeta.setDisplayName(ChatColor.AQUA + "Giáp ngực long chủng");

            AttributeModifier speedModifier = new AttributeModifier(CHESTPLATE_SPEED_KEY, 0.015, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.CHEST);
            AttributeModifier healthModifier = new AttributeModifier(CHESTPLATE_HEALTH_KEY, 2.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.CHEST);
            AttributeModifier armorModifier = new AttributeModifier(CHESTPLATE_ARMOR_KEY, 8.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.CHEST);
            AttributeModifier toughnessModifier = new AttributeModifier(CHESTPLATE_TOUGHNESS_KEY, 3.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.CHEST);
            AttributeModifier attackSpeedModifier = new AttributeModifier(CHESTPLATE_ATTACK_SPEED_KEY, 0.25, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.CHEST);

            armorMeta.addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED, speedModifier);
            armorMeta.addAttributeModifier(Attribute.GENERIC_MAX_HEALTH, healthModifier);
            armorMeta.addAttributeModifier(Attribute.GENERIC_ARMOR, armorModifier);
            armorMeta.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, toughnessModifier);
            armorMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, attackSpeedModifier);

            ArmorTrim trim = new ArmorTrim(TrimMaterial.NETHERITE, TrimPattern.HOST);
            armorMeta.setTrim(trim);

            armor.setItemMeta(armorMeta);
        }

        armor.setDurability((short) GeneralMethods.generateInt(520, 20));
        return armor;
    }

    public static ItemStack dragonSlayerLeggings() {
        ItemStack armor = new ItemStack(Material.DIAMOND_LEGGINGS);
        ArmorMeta armorMeta = (ArmorMeta) armor.getItemMeta();

        if (armorMeta != null) {
            armorMeta.setDisplayName(ChatColor.AQUA + "Giáp quần long chủng");

            AttributeModifier speedModifier = new AttributeModifier(LEGGINGS_SPEED_KEY, 0.015, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.LEGS);
            AttributeModifier healthModifier = new AttributeModifier(LEGGINGS_HEALTH_KEY, 2.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.LEGS);
            AttributeModifier armorModifier = new AttributeModifier(LEGGINGS_ARMOR_KEY, 6.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.LEGS);
            AttributeModifier toughnessModifier = new AttributeModifier(LEGGINGS_TOUGHNESS_KEY, 3.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.LEGS);
            AttributeModifier attackSpeedModifier = new AttributeModifier(LEGGINGS_ATTACK_SPEED_KEY, 0.25, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.LEGS);

            armorMeta.addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED, speedModifier);
            armorMeta.addAttributeModifier(Attribute.GENERIC_MAX_HEALTH, healthModifier);
            armorMeta.addAttributeModifier(Attribute.GENERIC_ARMOR, armorModifier);
            armorMeta.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, toughnessModifier);
            armorMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, attackSpeedModifier);

            ArmorTrim trim = new ArmorTrim(TrimMaterial.NETHERITE, TrimPattern.SHAPER);
            armorMeta.setTrim(trim);

            armor.setItemMeta(armorMeta);
        }

        armor.setDurability((short) GeneralMethods.generateInt(490, 20));
        return armor;
    }

    public static ItemStack dragonSlayerBoots() {
        ItemStack armor = new ItemStack(Material.DIAMOND_BOOTS);
        ArmorMeta armorMeta = (ArmorMeta) armor.getItemMeta();

        if (armorMeta != null) {
            armorMeta.setDisplayName(ChatColor.AQUA + "Giày long chủng");

            AttributeModifier speedModifier = new AttributeModifier(BOOTS_SPEED_KEY, 0.015, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.FEET);
            AttributeModifier healthModifier = new AttributeModifier(BOOTS_HEALTH_KEY, 2.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.FEET);
            AttributeModifier armorModifier = new AttributeModifier(BOOTS_ARMOR_KEY, 3.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.FEET);
            AttributeModifier toughnessModifier = new AttributeModifier(BOOTS_TOUGHNESS_KEY, 3.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.FEET);
            AttributeModifier attackSpeedModifier = new AttributeModifier(BOOTS_ATTACK_SPEED_KEY, 0.25, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.FEET);

            armorMeta.addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED, speedModifier);
            armorMeta.addAttributeModifier(Attribute.GENERIC_MAX_HEALTH, healthModifier);
            armorMeta.addAttributeModifier(Attribute.GENERIC_ARMOR, armorModifier);
            armorMeta.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, toughnessModifier);
            armorMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, attackSpeedModifier);

            ArmorTrim trim = new ArmorTrim(TrimMaterial.NETHERITE, TrimPattern.HOST);
            armorMeta.setTrim(trim);

            armor.setItemMeta(armorMeta);
        }

        armor.setDurability((short) GeneralMethods.generateInt(400, 20));
        return armor;
    }

    public static ItemStack dragonElixir() {
        ItemStack potItem = new ItemStack(Material.POTION);
        PotionMeta potionMeta = (PotionMeta) potItem.getItemMeta();

        if (potionMeta != null) {
            potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.DARKNESS, 240, 0), false);
            potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.RESISTANCE, 140, 2), false);
            potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.REGENERATION, 140, 4), false);

            Color randomColor = getRandomColor();
            potionMeta.setColor(randomColor);

            generateRandomEffects(potionMeta);

            potionMeta.setDisplayName(ChatColor.DARK_PURPLE + "Thuốc đa dịch");
            potItem.setItemMeta(potionMeta);
        }
        return potItem;
    }

    private static Color getRandomColor() {
        Random random = new Random();
        int red = random.nextInt(256);
        int green = random.nextInt(256);
        int blue = random.nextInt(256);
        return Color.fromRGB(red, green, blue);
    }

    private static void generateRandomEffects(PotionMeta potionMeta) {
        int defaultMaxTime = 2400;
        int defaultMinTime = 400;

        List<PotionEffectType> allEffects = new ArrayList<>();
        allEffects.add(PotionEffectType.RESISTANCE);
        allEffects.add(PotionEffectType.JUMP_BOOST);
        allEffects.add(PotionEffectType.SLOWNESS);
        allEffects.add(PotionEffectType.SLOW_FALLING);
        allEffects.add(PotionEffectType.REGENERATION);
        allEffects.add(PotionEffectType.POISON);
        allEffects.add(PotionEffectType.BLINDNESS);
        allEffects.add(PotionEffectType.INSTANT_DAMAGE);
        allEffects.add(PotionEffectType.WEAKNESS);
        allEffects.add(PotionEffectType.SPEED);
        allEffects.add(PotionEffectType.HASTE);
        allEffects.add(PotionEffectType.FIRE_RESISTANCE);
        allEffects.add(PotionEffectType.HUNGER);
        allEffects.add(PotionEffectType.INVISIBILITY);
        allEffects.add(PotionEffectType.LEVITATION);
        allEffects.add(PotionEffectType.NIGHT_VISION);
        allEffects.add(PotionEffectType.WATER_BREATHING);
        allEffects.add(PotionEffectType.ABSORPTION);
        allEffects.add(PotionEffectType.GLOWING);
        allEffects.add(PotionEffectType.LUCK);
        allEffects.add(PotionEffectType.UNLUCK);

        Collections.shuffle(allEffects);
        for (int i = 0; i < 3; i++) {
            PotionEffectType effectType = allEffects.get(i);
            int duration = GeneralMethods.generateInt(defaultMaxTime, defaultMinTime);
            int amplifier = GeneralMethods.generateInt(3, 0);
            potionMeta.addCustomEffect(new PotionEffect(effectType, duration, amplifier), false);
        }
    }
}