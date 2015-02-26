package tw.kid7.BannerMaker.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class BannerUtil {
    //檢查ItemStack是否為旗幟
    static public boolean isBanner(ItemStack itemStack) {
        if (itemStack == null) {
            return false;
        }
        if (itemStack.getType().equals(Material.BANNER)) {
            return true;
        }
        return false;
    }
}
