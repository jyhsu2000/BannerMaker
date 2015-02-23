package tw.kid7.BannerMaker.util;

import org.bukkit.ChatColor;

public class MessageUtil {
    private static String prefix = "&6[&aBannerMaker&6]&r ";

    public static String format(String message) {
        return format(false, message);
    }

    public static String format(boolean addPrefix, String message) {
        if (addPrefix) {
            message = prefix + message;
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
