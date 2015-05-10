package tw.kid7.BannerMaker.configuration;

import com.google.common.collect.Maps;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import tw.kid7.BannerMaker.BannerMaker;
import tw.kid7.BannerMaker.util.MessageUtil;

import java.io.File;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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
        //檢查語言包
        checkConfig(language);
        //若使用非預設語言，再額外載入預設語言
        if (!language.equals(defaultLanguage)) {
            ConfigManager.load(getFileName(defaultLanguage));
            checkConfig(defaultLanguage);
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

    public static void checkConfig(String lang) {
        HashMap<String, Object> defaultLanguage = Maps.newHashMap();

        defaultLanguage.put("general.reload", "Reload config");
        defaultLanguage.put("general.no-permission", "No permission");
        defaultLanguage.put("general.no-money", "Not enough money");
        defaultLanguage.put("general.money-transaction", "Use {0} to get banner. Now you have {1}");

        defaultLanguage.put("io.save-failed", "Save failed.");
        defaultLanguage.put("io.save-success", "Save success.");
        defaultLanguage.put("io.remove-banner", "Remove banner &r#{0}");

        defaultLanguage.put("command.player-only", "This command can only be used by players in game");

        defaultLanguage.put("config.add-setting", " Add {0} new settings");

        defaultLanguage.put("gui.main-menu", "Main menu");
        defaultLanguage.put("gui.prev-page", "Prev Page");
        defaultLanguage.put("gui.next-page", "Next Page");
        defaultLanguage.put("gui.create-banner", "Create Banner");
        defaultLanguage.put("gui.uncraftable-warning", "Uncraftable Warning");
        defaultLanguage.put("gui.uncraftable", "Uncraftable");
        defaultLanguage.put("gui.more-than-6-patterns", "More than 6 patterns.");
        defaultLanguage.put("gui.more-patterns", "More patterns");
        defaultLanguage.put("gui.back", "Back");
        defaultLanguage.put("gui.create", "Create");
        defaultLanguage.put("gui.delete", "DELETE");
        defaultLanguage.put("gui.remove-last-pattern", "Remove last pattern");
        defaultLanguage.put("gui.banner-info", "Banner info");
        defaultLanguage.put("gui.pattern-s", "pattern(s)");
        defaultLanguage.put("gui.no-patterns", "No patterns");
        defaultLanguage.put("gui.craft-recipe", "Craft Recipe");
        defaultLanguage.put("gui.get-this-banner", "Get this banner");
        defaultLanguage.put("gui.get-banner", "Get banner &r#{0}");
        defaultLanguage.put("gui.price", "Price: {0}");

        FileConfiguration config = ConfigManager.get(getFileName(lang));

        int i = 0;
        for (Map.Entry<String, Object> entry : defaultLanguage.entrySet()) {
            if (!config.contains(entry.getKey())) {
                config.set(entry.getKey(), entry.getValue());
                i++;
            }
        }
        if (i > 0) {
            ConfigManager.save(getFileName(lang));
            BannerMaker.getInstance().getServer().getConsoleSender().sendMessage(MessageUtil.format(true, Language.get("config.add-setting", i)));
        }
    }
}
