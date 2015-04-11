package tw.kid7.BannerMaker.configuration;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import tw.kid7.BannerMaker.BannerMaker;
import tw.kid7.BannerMaker.util.MessageUtil;

import java.io.File;
import java.lang.reflect.Array;
import java.util.Arrays;

public class Language {
    private static final String defaultLanguage = "en";
    private static String language = "en";

    public static void loadLanguage() {
        //從設定檔取得語言
        String configFileName = "config.yml";
        ConfigManager.load(configFileName);
        FileConfiguration config = ConfigManager.get(configFileName);
        language = defaultLanguage;
        if (config != null && config.contains("Language")) {
            language = (String) config.get("Language");
        }
        //嘗試載入語言包檔案
        String fileName = getFileName(language);
        File file = new File(BannerMaker.getInstance().getDataFolder(), fileName);
        //檢查檔案是否存在
        if (!file.exists()) {
            try {
                //若不存在，則嘗試尋找語言包
                BannerMaker.getInstance().saveResource(fileName, false);
            } catch (Exception e) {
                //若無該語言之語言包，則使用預設語言
                language = defaultLanguage;
                config.set("Language", language);
                ConfigManager.save(configFileName);
            }
        }
        //載入語言包
        ConfigManager.load(getFileName(language));
        //若使用非預設語言，再額外載入預設語言
        if (!language.equals(defaultLanguage)) {
            ConfigManager.load(getFileName(defaultLanguage));
        }
        BannerMaker.getInstance().getServer().getConsoleSender().sendMessage(MessageUtil.format(true, "Language: " + language));
    }

    private static String getFileName(String lang) {
        return "language/" + lang + ".yml";
    }

    public static String get(String path, Object... args) {
        if (!ConfigManager.isFileLoaded(getFileName(language))) {
            return null;
        }
        FileConfiguration config = ConfigManager.get(getFileName(language));
        if (!config.contains(path) || !config.isString(path)) {
            config.set(path, getFromDefaultLanguage(path, args));
            ConfigManager.save(getFileName(language));
        }
        return replaceArgument((String) config.get(path), args);
    }

    public static String getFromDefaultLanguage(String path, Object... args) {
        if (!ConfigManager.isFileLoaded(getFileName(defaultLanguage))) {
            return null;
        }
        FileConfiguration config = ConfigManager.get(getFileName(defaultLanguage));
        if (!config.contains(path) || !config.isString(path)) {
            config.set(path, "&c[Missing Message] &r" + path);
            ConfigManager.save(getFileName(defaultLanguage));
        }
        return replaceArgument((String) config.get(path), args);
    }

    public static String replaceArgument(String message, Object... args) {
        for (int i = 0; i < args.length; i++) {
            message = message.replaceAll("\\{" + i + "\\}", String.valueOf(args[i]));
        }
        return message;
    }

    public static String getIgnoreColors(String path, Object... args) {
        return ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', get(path, args)));
    }
}
