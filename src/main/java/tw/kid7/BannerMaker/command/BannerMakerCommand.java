package tw.kid7.BannerMaker.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tw.kid7.BannerMaker.BannerMaker;
import tw.kid7.BannerMaker.util.InventoryMenuUtil;

class BannerMakerCommand extends AbstractCommand {
    //名稱
    private static String name = "BannerMaker";
    //介紹
    private static String description = "Show menu of BannerMaker";
    //權限
    private static String permission = null;
    //使用方法
    private static String usage = "/bm";
    //僅能由玩家執行
    private static boolean onlyFromPlayer = true;

    BannerMakerCommand(BannerMaker bannerMaker) {
        super(bannerMaker, name, description, permission, usage, onlyFromPlayer);
    }

    @Override
    boolean handle(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        //開啟選單
        InventoryMenuUtil.openMenu(player);
        return true;
    }
}
