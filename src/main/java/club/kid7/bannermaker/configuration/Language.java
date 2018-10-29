package club.kid7.bannermaker.configuration;

import club.kid7.bannermaker.BannerMaker;
import club.kid7.bannermaker.util.MessageUtil;
import club.kid7.pluginutilities.configuration.KConfigManager;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

public class Language {
    private static Language instance = null;
    private final BannerMaker bm;
    private FileConfiguration defaultLanguageConfigResource;
    private FileConfiguration languageConfigResource;
    private String language = "en";

    public Language(BannerMaker bm) {
        this.bm = bm;
        instance = this;
    }

    public void loadLanguage() {
        //從設定檔取得語言
        String configFileName = "config.yml";
        FileConfiguration config = KConfigManager.get(configFileName);
        String defaultLanguage = "en";
        language = defaultLanguage;
        if (config != null && config.contains("Language")) {
            language = (String) config.get("Language");
        }
        //載入預設語言包（但不儲存於資料夾）
        try {
            Reader defaultLanguageInputStreamReader = new InputStreamReader(bm.getResource(getFileName(defaultLanguage).replace('\\', '/')), StandardCharsets.UTF_8);
            defaultLanguageConfigResource = YamlConfiguration.loadConfiguration(defaultLanguageInputStreamReader);
        } catch (Exception ignored) {
        }
        //嘗試當前語言資源檔（但不儲存於資料夾）
        try {
            Reader languageInputStreamReader = new InputStreamReader(bm.getResource(getFileName(language).replace('\\', '/')), StandardCharsets.UTF_8);
            languageConfigResource = YamlConfiguration.loadConfiguration(languageInputStreamReader);
        } catch (Exception ignored) {
        }
        //嘗試載入語言包檔案
        String fileName = getFileName(language);
        File file = new File(bm.getDataFolder(), fileName);
        //檢查檔案是否存在
        if (!file.exists()) {
            try {
                //若不存在，則嘗試尋找語言包
                bm.saveResource(fileName, false);
            } catch (Exception e) {
                //若無該語言之語言包，則使用預設語言
                language = defaultLanguage;
                assert config != null;
                config.set("Language", language);
                KConfigManager.save(configFileName);
            }
        }
        //載入語言包
        KConfigManager.load(getFileName(language));
        //檢查語言包
        checkConfig(language);
        bm.getLogger().info("Language: " + language);
    }

    private String getFileName(String lang) {
        return "language" + File.separator + lang + ".yml";
    }

    public static String tl(String path, Object... args) {
        if (instance == null) {
            return null;
        }
        return instance.get(path, args);
    }

    private String get(String path, Object... args) {
        FileConfiguration config = KConfigManager.get(getFileName(language));
        if (!config.contains(path) || !config.isString(path)) {
            //若無法取得，則自該語言資源檔取得，並儲存於語系檔
            config.set(path, getFromLanguageResource(path, args));
            KConfigManager.save(getFileName(language));
        }
        return replaceArgument((String) config.get(path), args);
    }

    private String getFromLanguageResource(String path, Object... args) {
        if (!languageConfigResource.contains(path) || !languageConfigResource.isString(path)) {
            //若無法取得，則自預設語言資源檔取得
            languageConfigResource.set(path, getFromDefaultLanguageResource(path, args));
        }
        return replaceArgument((String) languageConfigResource.get(path), args);
    }

    private String getFromDefaultLanguageResource(String path, Object... args) {
        if (!defaultLanguageConfigResource.contains(path) || !defaultLanguageConfigResource.isString(path)) {
            //若無法取得，則給予[Missing Message]標記
            defaultLanguageConfigResource.set(path, "&c[Missing Message] &r" + path);
        }
        return replaceArgument((String) defaultLanguageConfigResource.get(path), args);
    }

    private String replaceArgument(String message, Object... args) {
        for (int i = 0; i < args.length; i++) {
            message = message.replace("{" + i + "}", String.valueOf(args[i]));
        }
        return message;
    }

    public String getIgnoreColors(String path, Object... args) {
        String translatedString = tl(path, args);
        if (translatedString == null) {
            return null;
        }
        return ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', translatedString));
    }

    private void checkConfig(String lang) {
        //當前語言設定檔
        FileConfiguration config = KConfigManager.get(getFileName(lang));
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
            KConfigManager.save(getFileName(lang));
            bm.getServer().getConsoleSender().sendMessage(MessageUtil.format(true, tl("config.add-setting", newSettingCount)));
        }
    }
}
