package tw.kid7.BannerMaker.util;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import tw.kid7.BannerMaker.BannerMaker;
import tw.kid7.BannerMaker.configuration.ConfigManager;

public class EconUtil {
    /**
     * 取得旗幟的價格
     *
     * @param banner 旗幟
     * @return 價格
     */
    public static double getPrice(ItemStack banner) {
        if (!BannerUtil.isBanner(banner)) {
            return 0;
        }
        if (BannerMaker.econ == null) {
            return 0;
        }
        FileConfiguration config = ConfigManager.get("config.yml");
        if (config == null) {
            return 0;
        }
        return config.getDouble("Economy.Price", 0);
    }
}
