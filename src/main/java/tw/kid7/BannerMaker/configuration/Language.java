package tw.kid7.BannerMaker.configuration;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import tw.kid7.BannerMaker.BannerMaker;
import tw.kid7.BannerMaker.util.MessageUtil;

import java.io.File;
import java.io.InputStream;

public class Language {
    private static final String defaultLanguage = "en";
    private static FileConfiguration defaultLanguageConfigResource;
    private static FileConfiguration languageConfigResource;
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
        //載入預設語言包（但不儲存於資料夾）
        try {
            InputStream defaultLanguageInputStream = BannerMaker.getInstance().getResource(getFileName(defaultLanguage).replace('\\', '/'));
            defaultLanguageConfigResource = YamlConfiguration.loadConfiguration(defaultLanguageInputStream);
        } catch (Exception ignored) {
        }
        //嘗試當前語言資源檔（但不儲存於資料夾）
        try {
            InputStream languageInputStream = BannerMaker.getInstance().getResource(getFileName(language).replace('\\', '/'));
            languageConfigResource = YamlConfiguration.loadConfiguration(languageInputStream);
        } catch (Exception ignored) {
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
        //檢查語言包
        checkConfig(language);
        BannerMaker.getInstance().getServer().getConsoleSender().sendMessage(MessageUtil.format(true, "Language: " + language));
    }

    private static String getFileName(String lang) {
        return "language" + File.separator + lang + ".yml";
    }

    public static String get(String path, Object... args) {
        if (!ConfigManager.isFileLoaded(getFileName(language))) {
            return null;
        }
        FileConfiguration config = ConfigManager.get(getFileName(language));
        if (!config.contains(path) || !config.isString(path)) {
            //若無法取得，則自該語言資源檔取得，並儲存於語系檔
            config.set(path, getFromLanguageResource(path, args));
            ConfigManager.save(getFileName(language));
        }
        return replaceArgument((String) config.get(path), args);
    }

    private static String getFromLanguageResource(String path, Object... args) {
        if (!languageConfigResource.contains(path) || !languageConfigResource.isString(path)) {
            //若無法取得，則自預設語言資源檔取得
            languageConfigResource.set(path, getFromDefaultLanguageResource(path, args));
        }
        return replaceArgument((String) languageConfigResource.get(path), args);
    }

    private static String getFromDefaultLanguageResource(String path, Object... args) {
        if (!defaultLanguageConfigResource.contains(path) || !defaultLanguageConfigResource.isString(path)) {
            //若無法取得，則給予[Missing Message]標記
            defaultLanguageConfigResource.set(path, "&c[Missing Message] &r" + path);
        }
        return replaceArgument((String) defaultLanguageConfigResource.get(path), args);
    }

    private static String replaceArgument(String message, Object... args) {
        for (int i = 0; i < args.length; i++) {
            //message = message.replaceAll("\\{" + i + "\\}", String.valueOf(args[i]));
            message = message.replace("{" + i + "}", String.valueOf(args[i]));
        }
        return message;
    }

    public static String getIgnoreColors(String path, Object... args) {
        return ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', get(path, args)));
    }

    private static void checkConfig(String lang) {
        //當前語言設定檔
        FileConfiguration config = ConfigManager.get(getFileName(lang));
        //根據預設語言資源檔檢查
        int newSettingCount = 0;
        for (String key : defaultLanguageConfigResource.getKeys(true)) {
            //不直接檢查整個段落
            if (defaultLanguageConfigResource.isConfigurationSection(key)) {
                continue;
            }
            //若key已存在也不檢查
            if (config.contains(key)) {
                continue;
            }
            //若未包含該key，將預設值填入語系檔
            if (languageConfigResource != null && languageConfigResource.contains(key)) {
                //優先使用相同語言之資源檔
                config.set(key, languageConfigResource.get(key));
            } else {
                //採用預設語言
                config.set(key, defaultLanguageConfigResource.get(key));
            }
            newSettingCount++;
        }
        if (newSettingCount > 0) {
            ConfigManager.save(getFileName(lang));
            BannerMaker.getInstance().getServer().getConsoleSender().sendMessage(MessageUtil.format(true, Language.get("config.add-setting", newSettingCount)));
        }
    }
}
