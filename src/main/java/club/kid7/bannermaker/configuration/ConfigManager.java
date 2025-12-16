package club.kid7.bannermaker.configuration;

import club.kid7.bannermaker.BannerMaker;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class ConfigManager {
    private static final Map<String, FileConfiguration> configs = new HashMap<>();

    /**
     * 取得帶有 ".yml" 副檔名的檔案名稱
     * 並統一將路徑分隔符號轉換為 "/"
     *
     * @param fileName 檔案名稱
     * @return 帶有副檔名的檔案名稱
     */
    private static String getFileName(String fileName) {
        fileName = fileName.replace('\\', '/');
        if (!fileName.endsWith(".yml")) {
            fileName += ".yml";
        }
        return fileName;
    }

    /**
     * 檢查 ConfigManager 是否已載入該檔案
     *
     * @param fileName 要檢查的檔案
     * @return 如果檔案已載入則返回 true，否則返回 false
     */
    public static boolean isFileLoaded(String fileName) {
        fileName = getFileName(fileName);
        return configs.containsKey(fileName);
    }

    /**
     * 將檔案設定載入到記憶體中
     *
     * @param fileName 要載入的檔案
     */
    public static void load(String fileName) {
        fileName = getFileName(fileName);
        BannerMaker plugin = BannerMaker.getInstance();
        File file = new File(plugin.getDataFolder(), fileName);
        if (!file.exists()) {
            try {
                plugin.saveResource(fileName, false);
            } catch (Exception e) {
                // 忽略錯誤，可能是因為 jar 中沒有對應的資源檔（例如玩家資料）
                // 但如果是語言檔或設定檔，這可能是一個問題，所以在 debug 模式下或是測試時這很有用
                plugin.getLogger().warning("Could not save resource: " + fileName + " (" + e.getMessage() + ")");
            }
        }
        if (!isFileLoaded(fileName)) {
            configs.put(fileName, YamlConfiguration.loadConfiguration(file));
        }
    }

    /**
     * 取得指定檔案的 FileConfiguration，如果尚未載入則載入它。
     *
     * @param fileName 要讀取資料的檔案
     * @return 檔案設定 (FileConfiguration)
     */
    public static FileConfiguration get(String fileName) {
        fileName = getFileName(fileName);
        if (!isFileLoaded(fileName)) {
            load(fileName);
        }
        return configs.get(fileName);
    }

    /**
     * 在指定路徑設定資料。如果路徑已存在，它將被覆蓋。
     *
     * @param fileName 要更新的檔案
     * @param path     要設定的路徑
     * @param value    要設定的值
     */
    public static void set(String fileName, String path, Object value) {
        fileName = getFileName(fileName);
        if (!isFileLoaded(fileName)) {
            BannerMaker.getInstance().getLogger().warning("Config not loaded: " + fileName);
            return;
        }
        configs.get(fileName).set(path, value);
    }

    /**
     * 從 FileConfiguration 中移除一個路徑。
     *
     * @param fileName 要更新的檔案
     * @param path     要移除的路徑
     */
    public static void remove(String fileName, String path) {
        fileName = getFileName(fileName);
        if (!isFileLoaded(fileName)) {
            BannerMaker.getInstance().getLogger().warning("Config not loaded: " + fileName);
            return;
        }
        configs.get(fileName).set(path, null);
    }

    /**
     * 檢查檔案是否包含指定路徑。
     *
     * @param fileName 要檢查的檔案
     * @param path     要檢查的路徑
     * @return 如果路徑存在則返回 true，否則返回 false。
     */
    public static boolean contains(String fileName, String path) {
        fileName = getFileName(fileName);
        if (!isFileLoaded(fileName)) {
            BannerMaker.getInstance().getLogger().warning("Config not loaded: " + fileName);
            return false;
        }
        return configs.get(fileName).contains(path);
    }

    /**
     * 從插件資料夾重新載入設定檔。
     *
     * @param fileName 要重新載入的檔案
     */
    public static void reload(String fileName) {
        fileName = getFileName(fileName);
        if (!isFileLoaded(fileName)) {
            BannerMaker.getInstance().getLogger().warning("Config not loaded: " + fileName);
            return;
        }
        BannerMaker plugin = BannerMaker.getInstance();
        File file = new File(plugin.getDataFolder(), fileName);
        try {
            configs.get(fileName).load(file);
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Could not reload config: " + fileName, e);
        }
    }

    /**
     * 儲存設定到檔案。
     *
     * @param fileName 要儲存的檔案
     */
    public static void save(String fileName) {
        fileName = getFileName(fileName);
        if (!isFileLoaded(fileName)) {
            BannerMaker.getInstance().getLogger().warning("Config not loaded: " + fileName);
            return;
        }
        BannerMaker plugin = BannerMaker.getInstance();
        File file = new File(plugin.getDataFolder(), fileName);
        try {
            configs.get(fileName).save(file);
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save config: " + fileName, e);
        }
    }

    /**
     * 重新載入所有記憶體中的設定檔。
     */
    public static void reloadAll() {
        for (String fileName : configs.keySet()) {
            reload(fileName);
        }
    }
}
