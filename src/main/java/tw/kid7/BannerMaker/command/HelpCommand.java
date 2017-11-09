package tw.kid7.BannerMaker.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import tw.kid7.BannerMaker.BannerMaker;
import tw.kid7.BannerMaker.util.MessageUtil;

import java.util.Map;

public class HelpCommand extends CommandComponent {
    //名稱
    private static final String name = "Help";
    //介紹
    private static final String description = "Command list";
    //權限
    private static final String permission = null;
    //使用方法
    private static final String usage = "/bm help";
    //僅能由玩家執行
    private static final boolean onlyFromPlayer = false;

    public HelpCommand(BannerMaker bm) {
        super(bm, name, description, permission, usage, onlyFromPlayer);
    }

    @Override
    public boolean executeCommand(CommandSender sender, Command cmd, String label, String[] args) {
        BannerMaker bm = (BannerMaker) plugin;
        //插件資訊
        String pluginName = bm.getName();
        String pluginVersion = bm.getDescription().getVersion();
        //顯示標題
        sender.sendMessage(MessageUtil.format(true, pluginName + " - " + pluginVersion));
        //主要指令
        sender.sendMessage(getParent().getUsage() + ChatColor.GRAY + " - " + getParent().getDescription());
        //子指令
        for (Map.Entry<String, CommandComponent> subCommandEntry : getParent().getSubCommands().entrySet()) {
            if (subCommandEntry.getValue().hasPermission(sender)) {
                CommandComponent subCommand = subCommandEntry.getValue();
                sender.sendMessage(subCommand.getUsage() + ChatColor.GRAY + " - " + subCommand.getDescription());
            }
        }
        return true;
    }
}
