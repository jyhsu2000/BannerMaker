package club.kid7.bannermaker.util;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class SenderUtil {
    public static boolean isPlayer(CommandSender sender) {
        return (sender instanceof Player);
    }

    public static boolean isConsole(CommandSender sender) {
        return (sender instanceof ConsoleCommandSender);
    }
}
