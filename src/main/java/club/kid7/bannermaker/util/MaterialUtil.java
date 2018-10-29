package club.kid7.bannermaker.util;

import org.bukkit.Material;

public class MaterialUtil {
    public static boolean isDye(Material material) {
        switch (material) {
            //<editor-fold defaultstate="collapsed" desc="isDye">
            case BONE_MEAL:
            case ORANGE_DYE:
            case MAGENTA_DYE:
            case LIGHT_BLUE_DYE:
            case DANDELION_YELLOW:
            case LIME_DYE:
            case PINK_DYE:
            case GRAY_DYE:
            case LIGHT_GRAY_DYE:
            case CYAN_DYE:
            case PURPLE_DYE:
            case LAPIS_LAZULI:
            case COCOA_BEANS:
            case CACTUS_GREEN:
            case ROSE_RED:
            case INK_SAC:
                //</editor-fold>
                return true;
            default:
                return false;
        }
    }

    public static boolean isWool(Material material) {
        return material.name().contains("_WOOL");
    }
}
