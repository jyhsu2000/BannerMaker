package club.kid7.bannermaker.configuration;

import club.kid7.bannermaker.BannerMaker;
import club.kid7.bannermaker.util.TagUtil;
import co.aikar.locales.MessageKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.apache.commons.lang.LocaleUtils;
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
    private static final Locale DEFAULT_LOCALE = Locale.US; // en_US
    private final BannerMaker bm;
    private FileConfiguration defaultLanguageConfigResource;
    private FileConfiguration languageConfigResource;
    private Locale locale = DEFAULT_LOCALE;

    public Language(BannerMaker bm) {
        this.bm = bm;
        instance = this;
    }

    private static Locale parseLocale(String localeName) {
        Locale locale;

        // auto / null / empty → 使用系統 locale
        if (localeName == null || localeName.equalsIgnoreCase("auto") || localeName.isEmpty()) {
            locale = Locale.getDefault();
            return normalizeLocale(locale);
        }

        // 正規化：- 轉 _
        String normalized = localeName.trim().replace('-', '_');

        // 嘗試直接解析
        try {
            locale = LocaleUtils.toLocale(normalized);
            return normalizeLocale(locale);
        } catch (IllegalArgumentException ignored) {
        }

        // 嘗試大小寫修正（語言小寫、國家大寫）
        Matcher matcher = Pattern.compile("^([a-zA-Z]{2,3})_([a-zA-Z]{2})$").matcher(normalized);
        if (matcher.matches()) {
            String corrected = matcher.group(1).toLowerCase() + "_" + matcher.group(2).toUpperCase();
            try {
                return LocaleUtils.toLocale(corrected);
            } catch (IllegalArgumentException ignored) {
            }
        }

        // 所有嘗試失敗，使用預設語言
        return DEFAULT_LOCALE;
    }

    private static Locale normalizeLocale(Locale locale) {
        // en（無國家碼）→ en_US（僅對英語進行特殊處理，因為有部分環境會將其解析為 en，而實際上應使用 en_US 作為預設英語）
        if (locale.getLanguage().equals("en") && locale.getCountry().isEmpty()) {
            return Locale.US; // en_US
        }
        return locale;
    }

    private void registerCommandDescriptions(Locale locale) {
        FileConfiguration config = ConfigManager.get(getFileName(locale));
        String[] descKeys = {"default", "help", "reload", "see", "hand", "view"};
        for (String key : descKeys) {
            String path = "command.description." + key;
            if (config.isString(path)) {
                bm.getCommandManager().getLocales().addMessage(
                    locale,
                    MessageKey.of("command.description." + key),
                    config.getString(path)
                );
            }
        }
    }

    public static Component tl(String path, TagResolver... tags) {
        if (instance == null) {
            return Component.empty();
        }
        String raw = instance.getRawString(path);
        return MiniMessage.miniMessage().deserialize(raw, tags);
    }

    public static Component tl(NamedTextColor color, String path, TagResolver... tags) {
        return Component.empty().color(color).append(tl(path, tags));
    }

    private String getFileName(Locale locale) {
        return "language" + File.separator + locale.toString() + ".yml";
    }

    /**
     * 將 Legacy & 碼轉換為 MiniMessage 標籤。
     * 例如 &c → <red>、&l → <bold>。
     * 不影響已有的 MiniMessage 標籤和命名佔位符。
     */
    private static String convertLegacyToMiniMessage(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        StringBuilder result = new StringBuilder(input.length());
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c == '&' && i + 1 < input.length()) {
                String tag = legacyCodeToMiniMessageTag(Character.toLowerCase(input.charAt(i + 1)));
                if (tag != null) {
                    result.append(tag);
                    i++;
                    continue;
                }
            }
            result.append(c);
        }
        return result.toString();
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

    private static String legacyCodeToMiniMessageTag(char code) {
        return switch (code) {
            case '0' -> "<black>";
            case '1' -> "<dark_blue>";
            case '2' -> "<dark_green>";
            case '3' -> "<dark_aqua>";
            case '4' -> "<dark_red>";
            case '5' -> "<dark_purple>";
            case '6' -> "<gold>";
            case '7' -> "<gray>";
            case '8' -> "<dark_gray>";
            case '9' -> "<blue>";
            case 'a' -> "<green>";
            case 'b' -> "<aqua>";
            case 'c' -> "<red>";
            case 'd' -> "<light_purple>";
            case 'e' -> "<yellow>";
            case 'f' -> "<white>";
            case 'k' -> "<obfuscated>";
            case 'l' -> "<bold>";
            case 'm' -> "<strikethrough>";
            case 'n' -> "<underlined>";
            case 'o' -> "<italic>";
            case 'r' -> "<reset>";
            default -> null;
        };
    }

    private String getRawString(String path) {
        FileConfiguration config = ConfigManager.get(getFileName(locale));
        if (!config.contains(path) || !config.isString(path)) {
            config.set(path, getFromLanguageResource(path));
        }
        String messageString = (String) config.get(path);
        // 統一將 Legacy & 碼轉換為 MiniMessage 標籤，使兩種格式可共存
        return convertLegacyToMiniMessage(messageString);
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
            bm.getMessageService().send(bm.getServer().getConsoleSender(), tl("config.add-setting", TagUtil.tag("count", newSettingCount)));
        }
    }

    public void loadLanguage() {
        //從設定檔取得語言
        String configFileName = "config.yml";
        FileConfiguration config = ConfigManager.get(configFileName);
        String language = "auto";
        if (config != null && config.contains("Language")) {
            language = (String) config.get("Language");
        }
        //轉換語言名稱
        locale = parseLocale(language);

        //載入預設語言包（但不儲存於資料夾）
        try {
            Reader defaultLanguageInputStreamReader = new InputStreamReader(Objects.requireNonNull(bm.getResource(getFileName(DEFAULT_LOCALE).replace('\\', '/'))), StandardCharsets.UTF_8);
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
                locale = DEFAULT_LOCALE;
            }
        }
        //載入語言包
        ConfigManager.load(getFileName(locale));
        //檢查語言包
        checkConfig(locale);
        bm.getLogger().info("Language: " + locale);
        // 設定 ACF 語言
        bm.getCommandManager().getLocales().setDefaultLocale(locale);
        // 將指令描述注入到 ACF Locales
        registerCommandDescriptions(locale);
    }
}
