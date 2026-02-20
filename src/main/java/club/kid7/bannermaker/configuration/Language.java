package club.kid7.bannermaker.configuration;

import club.kid7.bannermaker.BannerMaker;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
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
        FileConfiguration config = ConfigManager.get(configFileName);
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
        ConfigManager.load(getFileName(locale));
        //檢查語言包
        checkConfig(locale);
        bm.getLogger().info("Language: " + locale);
        // 設定 ACF 語言
        bm.getCommandManager().getLocales().setDefaultLocale(locale);
    }

    public static Component tl(String path, TagResolver... tags) {
        if (instance == null) {
            return Component.empty();
        }
        String raw = instance.getRawString(path);
        return MiniMessage.miniMessage().deserialize(raw, tags);
    }

    public static Component tl(NamedTextColor color, String path, Object... args) {
        return Component.empty().color(color).append(tl(path, args));
    }

    public static Component tl(String path, Object... args) {
        if (instance == null) {
            // 如果 Language 實例尚未初始化，返回一個空的 Component
            return Component.empty();
        }
        String raw = instance.get(path, args);

        // 判斷是否包含 MiniMessage 標記 (簡單判斷 < 和 >)
        boolean containsMiniMessageTags = raw.contains("<") && raw.contains(">");

        if (containsMiniMessageTags) {
            return MiniMessage.miniMessage().deserialize(raw);
        } else {
            // 如果沒有 MiniMessage 標記，則假定為 Legacy 格式
            return LegacyComponentSerializer.legacyAmpersand().deserialize(raw);
        }
    }

    private String getFileName(Locale locale) {
        return "language" + File.separator + locale.toString() + ".yml";
    }

    private String getRawString(String path) {
        FileConfiguration config = ConfigManager.get(getFileName(locale));
        if (!config.contains(path) || !config.isString(path)) {
            config.set(path, getFromLanguageResource(path)); // 這裡也不傳 args
        }
        String messageString = (String) config.get(path); // 取得原始 YAML 字串

        // 判斷是否包含 MiniMessage 標記
        if (messageString.contains("<") && messageString.contains(">")) {
            return messageString;
        }

        // 如果是 Legacy 格式，轉換為 MiniMessage 格式
        return MiniMessage.miniMessage().serialize(LegacyComponentSerializer.legacyAmpersand().deserialize(messageString));
    }

    private String get(String path, Object... args) {
        FileConfiguration config = ConfigManager.get(getFileName(locale));
        if (!config.contains(path) || !config.isString(path)) {
            //若無法取得，則自該語言資源檔取得，但不儲存於語系檔 (避免執行時阻塞 I/O)
            config.set(path, getFromLanguageResource(path));
        }
        // 取得訊息字串並替換參數
        // 返回原始字串，讓 tl() 判斷解析器
        return replaceArgument((String) config.get(path), args);
    }

    private String getFromLanguageResource(String path) {
        if (!languageConfigResource.contains(path) || !languageConfigResource.isString(path)) {
            //若無法取得，則自預設語言資源檔取得
            languageConfigResource.set(path, getFromDefaultLanguageResource(path));
        }
        return (String) languageConfigResource.get(path);
    }

    private String getFromDefaultLanguageResource(String path) {
        if (!defaultLanguageConfigResource.contains(path) || !defaultLanguageConfigResource.isString(path)) {
            //若無法取得，則給予[Missing Message]標記
            defaultLanguageConfigResource.set(path, "&c[Missing Message] &r" + path);
        }
        return (String) defaultLanguageConfigResource.get(path);
    }

    private String replaceArgument(String message, Object... args) {
        for (int i = 0; i < args.length; i++) {
            message = message.replace("{" + i + "}", String.valueOf(args[i]));
        }
        return message;
    }

    public String getIgnoreColors(String path, Object... args) {
        Component translatedComponent = tl(path, args);
        // 先轉為 Legacy String，再使用 ChatColor 移除顏色代碼
        String legacyString = LegacyComponentSerializer.legacyAmpersand().serialize(translatedComponent);
        return ChatColor.stripColor(legacyString);
    }

    private void checkConfig(Locale checkLocale) {
        //當前語言設定檔
        FileConfiguration config = ConfigManager.get(getFileName(checkLocale));
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
            ConfigManager.save(getFileName(checkLocale));
            bm.getMessageService().send(bm.getServer().getConsoleSender(), tl("config.add-setting", newSettingCount));
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
