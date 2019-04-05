package club.kid7.bannermaker.util;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class DyeColorUtil {
    final private static BiMap<DyeColor, Integer> integerMap = HashBiMap.create();
    final private static BiMap<DyeColor, Material> bannerMaterialMap = HashBiMap.create();
    final private static BiMap<DyeColor, Material> dyeMaterialMap = HashBiMap.create();
    final private static BiMap<DyeColor, Material> woolMaterialMap = HashBiMap.create();

    static {
        integerMap.put(DyeColor.WHITE, 15);
        integerMap.put(DyeColor.ORANGE, 14);
        integerMap.put(DyeColor.MAGENTA, 13);
        integerMap.put(DyeColor.LIGHT_BLUE, 12);
        integerMap.put(DyeColor.YELLOW, 11);
        integerMap.put(DyeColor.LIME, 10);
        integerMap.put(DyeColor.PINK, 9);
        integerMap.put(DyeColor.GRAY, 8);
        integerMap.put(DyeColor.LIGHT_GRAY, 7);
        integerMap.put(DyeColor.CYAN, 6);
        integerMap.put(DyeColor.PURPLE, 5);
        integerMap.put(DyeColor.BLUE, 4);
        integerMap.put(DyeColor.BROWN, 3);
        integerMap.put(DyeColor.GREEN, 2);
        integerMap.put(DyeColor.RED, 1);
        integerMap.put(DyeColor.BLACK, 0);

        bannerMaterialMap.put(DyeColor.WHITE, Material.WHITE_BANNER);
        bannerMaterialMap.put(DyeColor.ORANGE, Material.ORANGE_BANNER);
        bannerMaterialMap.put(DyeColor.MAGENTA, Material.MAGENTA_BANNER);
        bannerMaterialMap.put(DyeColor.LIGHT_BLUE, Material.LIGHT_BLUE_BANNER);
        bannerMaterialMap.put(DyeColor.YELLOW, Material.YELLOW_BANNER);
        bannerMaterialMap.put(DyeColor.LIME, Material.LIME_BANNER);
        bannerMaterialMap.put(DyeColor.PINK, Material.PINK_BANNER);
        bannerMaterialMap.put(DyeColor.GRAY, Material.GRAY_BANNER);
        bannerMaterialMap.put(DyeColor.LIGHT_GRAY, Material.LIGHT_GRAY_BANNER);
        bannerMaterialMap.put(DyeColor.CYAN, Material.CYAN_BANNER);
        bannerMaterialMap.put(DyeColor.PURPLE, Material.PURPLE_BANNER);
        bannerMaterialMap.put(DyeColor.BLUE, Material.BLUE_BANNER);
        bannerMaterialMap.put(DyeColor.BROWN, Material.BROWN_BANNER);
        bannerMaterialMap.put(DyeColor.GREEN, Material.GREEN_BANNER);
        bannerMaterialMap.put(DyeColor.RED, Material.RED_BANNER);
        bannerMaterialMap.put(DyeColor.BLACK, Material.BLACK_BANNER);

        dyeMaterialMap.put(DyeColor.WHITE, Material.BONE_MEAL);
        dyeMaterialMap.put(DyeColor.ORANGE, Material.ORANGE_DYE);
        dyeMaterialMap.put(DyeColor.MAGENTA, Material.MAGENTA_DYE);
        dyeMaterialMap.put(DyeColor.LIGHT_BLUE, Material.LIGHT_BLUE_DYE);
        dyeMaterialMap.put(DyeColor.YELLOW, Material.DANDELION_YELLOW);
        dyeMaterialMap.put(DyeColor.LIME, Material.LIME_DYE);
        dyeMaterialMap.put(DyeColor.PINK, Material.PINK_DYE);
        dyeMaterialMap.put(DyeColor.GRAY, Material.GRAY_DYE);
        dyeMaterialMap.put(DyeColor.LIGHT_GRAY, Material.LIGHT_GRAY_DYE);
        dyeMaterialMap.put(DyeColor.CYAN, Material.CYAN_DYE);
        dyeMaterialMap.put(DyeColor.PURPLE, Material.PURPLE_DYE);
        dyeMaterialMap.put(DyeColor.BLUE, Material.LAPIS_LAZULI);
        dyeMaterialMap.put(DyeColor.BROWN, Material.COCOA_BEANS);
        dyeMaterialMap.put(DyeColor.GREEN, Material.CACTUS_GREEN);
        dyeMaterialMap.put(DyeColor.RED, Material.ROSE_RED);
        dyeMaterialMap.put(DyeColor.BLACK, Material.INK_SAC);

        woolMaterialMap.put(DyeColor.WHITE, Material.WHITE_WOOL);
        woolMaterialMap.put(DyeColor.ORANGE, Material.ORANGE_WOOL);
        woolMaterialMap.put(DyeColor.MAGENTA, Material.MAGENTA_WOOL);
        woolMaterialMap.put(DyeColor.LIGHT_BLUE, Material.LIGHT_BLUE_WOOL);
        woolMaterialMap.put(DyeColor.YELLOW, Material.YELLOW_WOOL);
        woolMaterialMap.put(DyeColor.LIME, Material.LIME_WOOL);
        woolMaterialMap.put(DyeColor.PINK, Material.PINK_WOOL);
        woolMaterialMap.put(DyeColor.GRAY, Material.GRAY_WOOL);
        woolMaterialMap.put(DyeColor.LIGHT_GRAY, Material.LIGHT_GRAY_WOOL);
        woolMaterialMap.put(DyeColor.CYAN, Material.CYAN_WOOL);
        woolMaterialMap.put(DyeColor.PURPLE, Material.PURPLE_WOOL);
        woolMaterialMap.put(DyeColor.BLUE, Material.BLUE_WOOL);
        woolMaterialMap.put(DyeColor.BROWN, Material.BROWN_WOOL);
        woolMaterialMap.put(DyeColor.GREEN, Material.GREEN_WOOL);
        woolMaterialMap.put(DyeColor.RED, Material.RED_WOOL);
        woolMaterialMap.put(DyeColor.BLACK, Material.BLACK_WOOL);
    }

    public static DyeColor of(int number) {
        return integerMap.inverse().get(number);
    }

    public static DyeColor of(Material material) {
        DyeColor dyeColor;
        dyeColor = bannerMaterialMap.inverse().get(material);
        if (dyeColor != null) {
            return dyeColor;
        }

        dyeColor = dyeMaterialMap.inverse().get(material);
        if (dyeColor != null) {
            return dyeColor;
        }

        dyeColor = woolMaterialMap.inverse().get(material);
        if (dyeColor != null) {
            return dyeColor;
        }
        return null;
    }

    public static short toShort(DyeColor dyeColor) {
        return integerMap.get(dyeColor).shortValue();
    }

    public static Material toBannerMaterial(DyeColor dyeColor) {
        return bannerMaterialMap.get(dyeColor);
    }

    public static Material toDyeMaterial(DyeColor dyeColor) {
        return dyeMaterialMap.get(dyeColor);
    }

    public static ItemStack toDyeItemStack(DyeColor dyeColor, int amount) {
        return new ItemStack(toDyeMaterial(dyeColor), amount);
    }

    public static Material toWoolMaterial(DyeColor dyeColor) {
        return woolMaterialMap.get(dyeColor);
    }
}
