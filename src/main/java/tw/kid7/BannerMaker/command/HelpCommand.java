package tw.kid7.BannerMaker.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import tw.kid7.BannerMaker.BannerMaker;
import tw.kid7.BannerMaker.util.MessageUtil;

import java.util.HashMap;
import java.util.Map;

class HelpCommand extends AbstractCommand {
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

    HelpCommand(BannerMaker bannerMaker) {
        super(bannerMaker, name, description, permission, usage, onlyFromPlayer);
    }

    @Override
    boolean handle(CommandSender sender, Command command, String label, String[] args) {
        //插件資訊
        String pluginName = bannerMaker.getName();
        String pluginVersion = bannerMaker.getDescription().getVersion();
        //顯示標題
        sender.sendMessage(MessageUtil.format(true, pluginName + " - " + pluginVersion));
        //主要指令
        BannerMakerCommand bmCommand = new BannerMakerCommand(bannerMaker);
        sender.sendMessage(MessageUtil.format("&c" + bmCommand.getUsage() + "&7: " + bmCommand.getDescription()));
        //子指令
        HashMap<String, AbstractCommand> subCommandMap = bannerMaker.commandManager.subCommandMap;
        for (Map.Entry<String, AbstractCommand> subCommandEntry : subCommandMap.entrySet()) {
            AbstractCommand subCommand = subCommandEntry.getValue();
            if (subCommand.hasPermission(sender)) {
                sender.sendMessage(MessageUtil.format("&c" + subCommand.getUsage() + "&7: " + subCommand.getDescription()));
            }
        }
        return true;
    }
}
