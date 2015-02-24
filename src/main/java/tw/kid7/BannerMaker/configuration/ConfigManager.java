/*
 * The MIT License
 *
 * Copyright 2013 Goblom.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package tw.kid7.BannerMaker.configuration;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Config Manager v1
 * <p/>
 * Easily Create, Load and get data from files in your plugins folder. Supports
 * only YAML configuration files, can hold as many configurations as you want.
 *
 * @author Goblom
 */
public class ConfigManager {

    private static final Plugin PLUGIN = JavaPlugin.getProvidingPlugin(ConfigManager.class);
    private static Map<String, FileConfiguration> configs = new HashMap();

    /**
     * Checks to see if the ConfigManager knows about fileName
     *
     * @param fileName file to check
     * @return true if file is loaded, false if not
     */
    public static boolean isFileLoaded(String fileName) {
        return configs.containsKey(fileName);
    }

    /**
     * Loads a files configuration into Memory
     *
     * @param fileName File to load
     */
    public static void load(String fileName) {
        File file = new File(PLUGIN.getDataFolder(), fileName);
        if (!file.exists()) {
            try {
                PLUGIN.saveResource(fileName, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!isFileLoaded(fileName)) {
            configs.put(fileName, YamlConfiguration.loadConfiguration(file));
        }
    }

    /**
     * Gets the FileConfiguration for a specified file
     *
     * @param fileName File to load data from
     * @return File Configuration
     */
    public static FileConfiguration get(String fileName) {
        if (isFileLoaded(fileName)) {
            return configs.get(fileName);
        }
        return null;
    }

    /**
     * Updates the FileConfiguration at the given path. If path already exists
     * this will return false.
     *
     * @param fileName File to update
     * @param path     Path to update
     * @param value    Data to set at path
     * @return True if successful, otherwise false
     */
    public static boolean update(String fileName, String path, Object value) {
        if (isFileLoaded(fileName)) {
            if (!configs.get(fileName).contains(path)) {
                configs.get(fileName).set(path, value);
                return true;
            }
        }
        return false;
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
        if (isFileLoaded(fileName)) {
            configs.get(fileName).set(path, value);
        }
    }

    /**
     * Create YAML Comments at the given path
     *
     * @param fileName File to add comments to
     * @param path     Path to add comments too
     * @param comments Comments to add
     * @deprecated Not Tested/Experimental
     */
    public void addComment(String fileName, String path, String... comments) {
        if (isFileLoaded(fileName)) {
            for (String comment : comments) {
                if (!configs.get(fileName).contains(path)) {
                    configs.get(fileName).set("_COMMENT_" + comments.length, " " + comment);
                }
            }
        }
    }

    /**
     * Removes a path from the FileConfiguration.
     *
     * @param fileName File to update
     * @param path     Path to remove
     */
    public static void remove(String fileName, String path) {
        if (isFileLoaded(fileName)) {
            configs.get(fileName).set(path, null);
        }
    }

    /**
     * Checks if a file has a path.
     *
     * @param fileName File to check
     * @param path     Path to check
     * @return True if path exists, otherwise false.
     */
    public static boolean contains(String fileName, String path) {
        if (isFileLoaded(fileName)) {
            return configs.get(fileName).contains(path);
        }
        return false;
    }

    /**
     * Reload the config from the given Plugin.
     *
     * @param fileName File to reload
     */
    public static void reload(String fileName) {
        File file = new File(PLUGIN.getDataFolder(), fileName);
        if (isFileLoaded(fileName)) {
            try {
                configs.get(fileName).load(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Save the config for the given plugin
     *
     * @param fileName File to save
     */
    public static void save(String fileName) {
        File file = new File(PLUGIN.getDataFolder(), fileName);
        if (isFileLoaded(fileName)) {
            try {
                configs.get(fileName).save(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void reloadAll() {
        for (String fileName : configs.keySet()) {
            reload(fileName);
        }
    }

    public static void saveAll() {
        for (String fileName : configs.keySet()) {
            save(fileName);
        }
    }
}