package club.kid7.bannermaker.util;

import org.bukkit.Material;

public class MaterialUtil {
    public static boolean isDye(Material material) {
        return material.name().endsWith("_DYE");
    }

    public static boolean isWool(Material material) {
        return material.name().endsWith("_WOOL");
    }
}
