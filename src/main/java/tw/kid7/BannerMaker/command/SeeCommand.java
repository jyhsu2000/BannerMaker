package tw.kid7.BannerMaker.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

class SeeCommand extends AbstractCommand {
    //名稱
    private static final String name = "See";
    //介紹
    private static final String description = "Show banner info of the banner you're looking at";
    //權限
    private static final String permission = "BannerMaker.see";
    //使用方法
    private static final String usage = "/bm see";
    //僅能由玩家執行
    private static final boolean onlyFromPlayer = true;

    SeeCommand() {
        super(name, description, permission, usage, onlyFromPlayer);
    }

    @Override
    boolean handle(CommandSender sender, Command command, String label, String[] args) {
        //TODO
        return true;
    }
}
