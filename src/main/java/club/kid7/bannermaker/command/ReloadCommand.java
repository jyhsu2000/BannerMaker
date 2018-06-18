package club.kid7.bannermaker.command;

import club.kid7.bannermaker.BannerMaker;
import club.kid7.bannermaker.util.MessageUtil;
import club.kid7.pluginutilities.command.CommandComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import static club.kid7.bannermaker.configuration.Language.tl;

public class ReloadCommand extends CommandComponent {
    //名稱
    private static final String name = "Reload";
    //介紹
    private static final String description = "Reload all config";
    //權限
    private static final String permission = "BannerMaker.reload";
    //使用方法
    private static final String usage = "/bm reload";
    //僅能由玩家執行
    private static final boolean onlyFromPlayer = false;

    public ReloadCommand(BannerMaker bm) {
        super(bm, name, description, permission, usage, onlyFromPlayer);
    }

    @Override
    public boolean executeCommand(CommandSender sender, Command cmd, String label, String[] args) {
        BannerMaker bm = (BannerMaker) plugin;
        bm.reload();
        sender.sendMessage(MessageUtil.format(true, tl("general.reload")));
        return true;
    }
}
