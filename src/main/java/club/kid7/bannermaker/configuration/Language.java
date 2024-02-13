package club.kid7.bannermaker.configuration;

import club.kid7.bannermaker.BannerMaker;
import club.kid7.bannermaker.util.MessageUtil;
import club.kid7.pluginutilities.configuration.KConfigManager;
import org.apache.commons.lang.LocaleUtils;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Language {
    private static Language instance = null;
    private final BannerMaker bm;
    private FileConfiguration defaultLanguageConfigResource;
    private FileConfiguration languageConfigResource;
    private Locale locale = Locale.ENGLISH;

    public Language(BannerMaker bm) {
        this.bm = bm;
        instance = this;
    }

    public void loadLanguage() {
        //從設定檔取得語言
        String configFileName = "config.yml";
        FileConfiguration config = KConfigManager.get(configFileName);
        Locale defaultLocale = Locale.ENGLISH;
        String language = "auto";
        if (config != null && config.contains("Language")) {
            language = (String) config.get("Language");
        }
        //轉換語言名稱
        locale = parseLocale(language);

        //載入預設語言包（但不儲存於資料夾）
        try {
            Reader defaultLanguageInputStreamReader = new InputStreamReader(Objects.requireNonNull(bm.getResource(getFileName(defaultLocale).replace('\\', '/'))), StandardCharsets.UTF_8);
            defaultLanguageConfigResource = YamlConfiguration.loadConfiguration(defaultLanguageInputStreamReader);
        } catch (Exception ignored) {
        }
        //嘗試當前語言資源檔（但不儲存於資料夾）
        try {
            Reader languageInputStreamReader = new InputStreamReader(Objects.requireNonNull(bm.getResource(getFileName(locale).replace('\\', '/'))), StandardCharsets.UTF_8);
            languageConfigResource = YamlConfiguration.loadConfiguration(languageInputStreamReader);
        } catch (Exception ignored) {
        }
        //嘗試載入語言包檔案
        String fileName = getFileName(locale);
        File file = new File(bm.getDataFolder(), fileName);
        //檢查檔案是否存在
        if (!file.exists()) {
            try {
                //若不存在，則嘗試尋找語言包
                bm.saveResource(fileName, false);
            } catch (Exception e) {
                //若無該語言之語言包，則使用預設語言
                locale = defaultLocale;
//                assert config != null;
//                config.set("Language", language);
//                KConfigManager.save(configFileName);
            }
        }
        //載入語言包
        KConfigManager.load(getFileName(locale));
        //檢查語言包
        checkConfig(locale);
        bm.getLogger().info("Language: " + locale);
    }

    private String getFileName(Locale locale) {
        return "language" + File.separator + locale.toString() + ".yml";
    }

    public static String tl(String path, Object... args) {
        if (instance == null) {
            return null;
        }
        return instance.get(path, args);
    }

    private String get(String path, Object... args) {
        FileConfiguration config = KConfigManager.get(getFileName(locale));
        if (!config.contains(path) || !config.isString(path)) {
            //若無法取得，則自該語言資源檔取得，並儲存於語系檔
            config.set(path, getFromLanguageResource(path, args));
            KConfigManager.save(getFileName(locale));
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

    private void checkConfig(Locale checkLocale) {
        //當前語言設定檔
        FileConfiguration config = KConfigManager.get(getFileName(checkLocale));
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
            KConfigManager.save(getFileName(checkLocale));
            bm.getServer().getConsoleSender().sendMessage(MessageUtil.format(true, tl("config.add-setting", newSettingCount)));
        }
    }

    private static Locale parseLocale(String localeName) {
        //自動
        if (localeName == null || localeName.equalsIgnoreCase("auto") || localeName.isEmpty()) {
            return Locale.getDefault();
        }
        //嘗試直接搜尋
        try {
            return LocaleUtils.toLocale(localeName);
        } catch (IllegalArgumentException ignored) {
        }
        //剖析重組後搜尋
        Pattern pattern = Pattern.compile("^(.*?)(?:[-_](.*))?$");
        Matcher matcher = pattern.matcher(localeName);
        if (matcher.find()) {
            String languageName = matcher.group(1).toLowerCase();
            String countryName = matcher.group(2) != null ? matcher.group(2).toUpperCase() : null;
            String fullLocaleName = languageName;
            if (countryName != null) {
                fullLocaleName += "_" + matcher.group(2).toUpperCase();
            }
            try {
                return LocaleUtils.toLocale(fullLocaleName);
            } catch (IllegalArgumentException ignored) {
                try {
                    return LocaleUtils.toLocale(languageName);
                } catch (IllegalArgumentException ignored2) {
                }
            }
        }
        //預設語言
        return Locale.ENGLISH;
    }
}
