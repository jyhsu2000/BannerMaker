package tw.kid7.BannerMaker.configuration;


import com.google.common.collect.Maps;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Map;

/**
 * Config Manager
 *
 * @author jyhsu(KID)
 * @version 1.0
 */
public class KConfigManager {
    private static final Plugin plugin = JavaPlugin.getProvidingPlugin(KConfigManager.class);
    private static final Map<String, FileConfiguration> configs = Maps.newHashMap();


    private static String getFileName(String fileName) {
        if (!fileName.endsWith(".yml")) {
            fileName += ".yml";
        }
        return fileName;
    }

    /**
     * Checks to see if the ConfigManager knows about fileName
     *
     * @param fileName file to check
     * @return true if file is loaded, false if not
     */
    public static boolean isFileLoaded(String fileName) {
        fileName = getFileName(fileName);
        return configs.containsKey(fileName);
    }

    /**
     * Loads a files configuration into Memory
     *
     * @param fileName File to load
     */
    public static void load(String fileName) {
        fileName = getFileName(fileName);
        File file = new File(plugin.getDataFolder(), fileName);
        if (!file.exists()) {
            try {
                plugin.saveResource(fileName, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!isFileLoaded(fileName)) {
            configs.put(fileName, YamlConfiguration.loadConfiguration(file));
        }
    }

    /**
     * Gets the FileConfiguration for a specified file, if not loaded, load it.
     *
     * @param fileName File to load data from
     * @return File Configuration
     */
    public static FileConfiguration get(String fileName) {
        fileName = getFileName(fileName);
        if (!isFileLoaded(fileName)) {
            load(fileName);
        }
        return configs.get(fileName);
    }

    /**
     * Sets data at any given path. If path already exists it will be over
     * written.
     *
     * @param fileName File to update
     * @param path     Path to set
     * @param value    Data to set at path
     */
    public static void set(String fileName, String path, Object value) {
        fileName = getFileName(fileName);
        if (!isFileLoaded(fileName)) {
            throw new RuntimeException("Config not loaded: " + fileName);
        }
        configs.get(fileName).set(path, value);
    }

    /**
     * Removes a path from the FileConfiguration.
     *
     * @param fileName File to update
     * @param path     Path to remove
     */
    public static void remove(String fileName, String path) {
        fileName = getFileName(fileName);
        if (!isFileLoaded(fileName)) {
            throw new RuntimeException("Config not loaded: " + fileName);
        }
        configs.get(fileName).set(path, null);
    }

    /**
     * Checks if a file has a path.
     *
     * @param fileName File to check
     * @param path     Path to check
     * @return True if path exists, otherwise false.
     */
    public static boolean contains(String fileName, String path) {
        fileName = getFileName(fileName);
        if (!isFileLoaded(fileName)) {
            throw new RuntimeException("Config not loaded: " + fileName);
        }
        return configs.get(fileName).contains(path);
    }

    /**
     * Reload the config from the given Plugin.
     *
     * @param fileName File to reload
     */
    public static void reload(String fileName) {
        fileName = getFileName(fileName);
        if (!isFileLoaded(fileName)) {
            throw new RuntimeException("Config not loaded: " + fileName);
        }
        File file = new File(plugin.getDataFolder(), fileName);
        try {
            configs.get(fileName).load(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Save the config for the given plugin
     *
     * @param fileName File to save
     */
    public static void save(String fileName) {
        fileName = getFileName(fileName);
        if (!isFileLoaded(fileName)) {
            throw new RuntimeException("Config not loaded: " + fileName);
        }
        File file = new File(plugin.getDataFolder(), fileName);
        try {
            configs.get(fileName).save(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Reload all config from file into memory
     */
    public static void reloadAll() {
        for (String fileName : configs.keySet()) {
            reload(fileName);
        }
    }

    /**
     * Save all config in memory into file
     */
    public static void saveAll() {
        for (String fileName : configs.keySet()) {
            save(fileName);
        }
    }
}
