package club.kid7.bannermaker.registry;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class DyeColorRegistry {
    private static final Map<DyeColor, DyeData> dyeDataMap = new EnumMap<>(DyeColor.class);
    private static final Map<Integer, DyeData> integerMap = new HashMap<>();
    private static final Map<Material, DyeData> materialReverseMap = new HashMap<>();

    private record DyeData(
        DyeColor dyeColor,
        int value,
        Material bannerMaterial,
        Material dyeMaterial,
        Material woolMaterial
    ) {
    }

    private static void put(DyeColor color, int value, Material banner, Material dye, Material wool) {
        DyeData dyeData = new DyeData(color, value, banner, dye, wool);
        dyeDataMap.put(color, dyeData);
        integerMap.put(value, dyeData);
        materialReverseMap.put(banner, dyeData);
        materialReverseMap.put(dye, dyeData);
        materialReverseMap.put(wool, dyeData);
    }

    static {
        put(DyeColor.WHITE, 15, Material.WHITE_BANNER, Material.WHITE_DYE, Material.WHITE_WOOL);
        put(DyeColor.ORANGE, 14, Material.ORANGE_BANNER, Material.ORANGE_DYE, Material.ORANGE_WOOL);
        put(DyeColor.MAGENTA, 13, Material.MAGENTA_BANNER, Material.MAGENTA_DYE, Material.MAGENTA_WOOL);
        put(DyeColor.LIGHT_BLUE, 12, Material.LIGHT_BLUE_BANNER, Material.LIGHT_BLUE_DYE, Material.LIGHT_BLUE_WOOL);
        put(DyeColor.YELLOW, 11, Material.YELLOW_BANNER, Material.YELLOW_DYE, Material.YELLOW_WOOL);
        put(DyeColor.LIME, 10, Material.LIME_BANNER, Material.LIME_DYE, Material.LIME_WOOL);
        put(DyeColor.PINK, 9, Material.PINK_BANNER, Material.PINK_DYE, Material.PINK_WOOL);
        put(DyeColor.GRAY, 8, Material.GRAY_BANNER, Material.GRAY_DYE, Material.GRAY_WOOL);
        put(DyeColor.LIGHT_GRAY, 7, Material.LIGHT_GRAY_BANNER, Material.LIGHT_GRAY_DYE, Material.LIGHT_GRAY_WOOL);
        put(DyeColor.CYAN, 6, Material.CYAN_BANNER, Material.CYAN_DYE, Material.CYAN_WOOL);
        put(DyeColor.PURPLE, 5, Material.PURPLE_BANNER, Material.PURPLE_DYE, Material.PURPLE_WOOL);
        put(DyeColor.BLUE, 4, Material.BLUE_BANNER, Material.BLUE_DYE, Material.BLUE_WOOL);
        put(DyeColor.BROWN, 3, Material.BROWN_BANNER, Material.BROWN_DYE, Material.BROWN_WOOL);
        put(DyeColor.GREEN, 2, Material.GREEN_BANNER, Material.GREEN_DYE, Material.GREEN_WOOL);
        put(DyeColor.RED, 1, Material.RED_BANNER, Material.RED_DYE, Material.RED_WOOL);
        put(DyeColor.BLACK, 0, Material.BLACK_BANNER, Material.BLACK_DYE, Material.BLACK_WOOL);
    }

    private static <T> DyeData getDyeData(T key) {
        if (key instanceof Integer) {
            return integerMap.get(key);
        } else if (key instanceof Material) {
            return materialReverseMap.get(key);
        } else if (key instanceof DyeColor) {
            return dyeDataMap.get(key);
        } else {
            throw new IllegalArgumentException("Unsupported key type: " + key.getClass().getName());
        }
    }

    public static <T> DyeColor getDyeColor(T key) {
        return getDyeData(key).dyeColor();
    }

    public static <T> int getValue(T key) {
        return getDyeData(key).value();
    }

    public static <T> Material getBannerMaterial(T key) {
        return getDyeData(key).bannerMaterial();
    }

    public static <T> Material getDyeMaterial(T key) {
        return getDyeData(key).dyeMaterial();
    }

    public static <T> Material getWoolMaterial(T key) {
        return getDyeData(key).woolMaterial();
    }

    public static <T> ItemStack getDyeItemStack(T key, int amount) {
        return new ItemStack(getDyeMaterial(key), amount);
    }
}
