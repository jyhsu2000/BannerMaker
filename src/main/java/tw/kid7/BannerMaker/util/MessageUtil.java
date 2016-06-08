package tw.kid7.BannerMaker.util;

import org.bukkit.ChatColor;
import tw.kid7.BannerMaker.configuration.Language;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageUtil {

    public static String format(String message) {
        return format(false, message);
    }

    public static String format(boolean addPrefix, String message) {
        if (addPrefix) {
            message = Language.get("general.prefix") + message;
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static String cutString(String string, int length) {
        if (string.length() > length) {
            string = string.substring(0, length);
        }
        return string;
    }

    public static String cutStringWithoutColor(String string, int length) {
        //一開始就沒超過字數
        if (lengthWithoutColor(string) <= length) {
            return string;
        }
        //字數超過，先從最後段減掉超過的量
        while (lengthWithoutColor(string) > length) {
            string = string.substring(0, string.length() - (lengthWithoutColor(string) - length));
        }
        return string;
    }

    public static int lengthWithoutColor(String string) {
        if (string.isEmpty()) {
            return 0;
        }
        //對應所有文字代碼
        Pattern pattern = Pattern.compile("(&[0-9a-fklmnor])");
        Matcher matcher = pattern.matcher(string);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return string.length() - count * 2;
    }
}
