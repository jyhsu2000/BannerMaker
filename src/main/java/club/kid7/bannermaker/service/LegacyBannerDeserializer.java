package club.kid7.bannermaker.service;

import club.kid7.bannermaker.registry.DyeColorRegistry;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

/**
 * 讀取 v2.0 之前以整數 {@link org.bukkit.DyeColor} 順序儲存的 banner 序列化資料。
 * 新存檔一律以 {@code DyeColor} 名稱字串儲存；本類僅供向後相容讀取，
 * 集中於此以便標記為過渡程式碼，待社群裝置升級率夠高後可整類移除。
 */
class LegacyBannerDeserializer {

    private LegacyBannerDeserializer() {
        // Utility class
    }

    /**
     * 嘗試從舊格式（整數 color）讀取 banner 底色對應的 {@link Material}。
     *
     * @param config configuration 區段，包含特定 banner 的資料
     * @param key    banner 的 id（時間戳記）
     * @return 對應 Material；若 config 中該 key 不為整數格式，則回傳 {@code null}
     */
    static Material tryReadBannerMaterial(ConfigurationSection config, String key) {
        if (!config.isInt(key + ".color")) {
            return null;
        }
        return DyeColorRegistry.getBannerMaterial(config.getInt(key + ".color"));
    }
}
