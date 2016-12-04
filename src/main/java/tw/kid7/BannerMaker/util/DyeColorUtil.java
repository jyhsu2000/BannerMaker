package tw.kid7.BannerMaker.util;


import com.google.common.collect.Maps;
import org.bukkit.DyeColor;

import java.util.HashMap;

class DyeColorUtil {
    private static HashMap<DyeColor, Integer> map = Maps.newHashMap();

    static {
        map.put(DyeColor.WHITE, 15);
        map.put(DyeColor.ORANGE, 14);
        map.put(DyeColor.MAGENTA, 13);
        map.put(DyeColor.LIGHT_BLUE, 12);
        map.put(DyeColor.YELLOW, 11);
        map.put(DyeColor.LIME, 10);
        map.put(DyeColor.PINK, 9);
        map.put(DyeColor.GRAY, 8);
        map.put(DyeColor.SILVER, 7);
        map.put(DyeColor.CYAN, 6);
        map.put(DyeColor.PURPLE, 5);
        map.put(DyeColor.BLUE, 4);
        map.put(DyeColor.BROWN, 3);
        map.put(DyeColor.GREEN, 2);
        map.put(DyeColor.RED, 1);
        map.put(DyeColor.BLACK, 0);
    }

    static short toShort(DyeColor dyeColor) {
        return map.get(dyeColor).shortValue();
    }
}
