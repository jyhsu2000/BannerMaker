package tw.kid7.BannerMaker.command;


import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import tw.kid7.BannerMaker.configuration.Language;
import tw.kid7.BannerMaker.util.MessageUtil;
import tw.kid7.BannerMaker.util.SenderUtil;

/**
 * 抽象子指令
 */
public abstract class AbstractCommand {
    //名稱
    private String name;
    //介紹
    private String description;
    //權限
    private String permission;
    //使用方法
    private String usage;
    //僅能由玩家執行
    private boolean onlyFromPlayer = false;

    protected AbstractCommand(String name, String description, String permission, String usage, boolean onlyFromPlayer) {
        this.name = name;
        this.description = description;
        this.permission = permission;
        this.usage = usage;
        this.onlyFromPlayer = onlyFromPlayer;
    }

    /**
     * Executes the command.
     *
     * @param sender  the sender of the command
     * @param command the command being done
     * @param label   the name of the command
     * @param args    the arguments supplied
     */
    public final boolean execute(CommandSender sender, Command command, String label, String[] args) {
        //僅能由玩家執行
        if (onlyFromPlayer && !SenderUtil.isPlayer(sender)) {
            sender.sendMessage(MessageUtil.format(true, "&c" + Language.get("command.player-only")));
            return true;
        }
        //檢查權限
        if (!hasPermission(sender)) {
            sender.sendMessage(MessageUtil.format(true, Language.get("general.no-permission")));
            return true;
        }
        //執行指令
        return handle(sender, command, label, args);
    }

    /**
     * 指令處理過程
     *
     * @param sender  the sender of the command
     * @param command the command being done
     * @param label   the name of the command
     * @param args    the arguments supplied
     * @return boolean
     */
    public abstract boolean handle(CommandSender sender, Command command, String label, String[] args);

    /**
     * 判斷有無權限
     *
     * @param sender the sender of the command
     * @return boolean
     */
    public boolean hasPermission(CommandSender sender) {
        return permission == null || sender.hasPermission(permission) || SenderUtil.isConsole(sender);
    }

    /**
     * 顯示指令用法
     *
     * @param sender the sender of the command
     */
    public void sendUsage(CommandSender sender) {
        sender.sendMessage(MessageUtil.format("&7- &9Command: &7" + name));
        sender.sendMessage(MessageUtil.format("&7- &9Description: &7" + description));
        sender.sendMessage(MessageUtil.format("&7- &9Usage: &7" + usage));
    }

    /**
     * 參數錯誤提示訊息
     *
     * @param sender the sender of the command
     */
    public void sendParameterWarning(CommandSender sender) {
        //TODO: 加到語言包：參數錯誤提示訊息
        sender.sendMessage(MessageUtil.format(true, "&cInvalid parameter(s), correct usage: " + usage));
    }
}
