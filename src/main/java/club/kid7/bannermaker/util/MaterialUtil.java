package club.kid7.bannermaker.util;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.bukkit.DyeColor;
import org.bukkit.Material;

public class MaterialUtil {
    final private static BiMap<DyeColor, Material> coloredBannerMaterial = HashBiMap.create();

    static {
        coloredBannerMaterial.put(DyeColor.WHITE, Material.WHITE_BANNER);
        coloredBannerMaterial.put(DyeColor.ORANGE, Material.ORANGE_BANNER);
        coloredBannerMaterial.put(DyeColor.MAGENTA, Material.MAGENTA_BANNER);
        coloredBannerMaterial.put(DyeColor.LIGHT_BLUE, Material.LIGHT_BLUE_BANNER);
        coloredBannerMaterial.put(DyeColor.YELLOW, Material.YELLOW_BANNER);
        coloredBannerMaterial.put(DyeColor.LIME, Material.LIME_BANNER);
        coloredBannerMaterial.put(DyeColor.PINK, Material.PINK_BANNER);
        coloredBannerMaterial.put(DyeColor.GRAY, Material.GRAY_BANNER);
        coloredBannerMaterial.put(DyeColor.LIGHT_GRAY, Material.LIGHT_GRAY_BANNER);
        coloredBannerMaterial.put(DyeColor.CYAN, Material.CYAN_BANNER);
        coloredBannerMaterial.put(DyeColor.PURPLE, Material.PURPLE_BANNER);
        coloredBannerMaterial.put(DyeColor.BLUE, Material.BLUE_BANNER);
        coloredBannerMaterial.put(DyeColor.BROWN, Material.BROWN_BANNER);
        coloredBannerMaterial.put(DyeColor.GREEN, Material.GREEN_BANNER);
        coloredBannerMaterial.put(DyeColor.RED, Material.RED_BANNER);
        coloredBannerMaterial.put(DyeColor.BLACK, Material.BLACK_BANNER);
    }

    public static Material getBanner(DyeColor dyeColor) {
        return coloredBannerMaterial.get(dyeColor);
    }
}
