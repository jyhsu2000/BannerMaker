package tw.kid7.BannerMaker.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tw.kid7.BannerMaker.BannerMaker;
import tw.kid7.BannerMaker.customMenu.MainMenu;
import tw.kid7.util.customGUI.CustomGUIManager;

class BannerMakerCommand extends AbstractCommand {
    //名稱
    private static final String name = "BannerMaker";
    //介紹
    private static final String description = "Show menu of BannerMaker";
    //權限
    private static final String permission = null;
    //使用方法
    private static final String usage = "/bm";
    //僅能由玩家執行
    private static final boolean onlyFromPlayer = true;

    BannerMakerCommand(BannerMaker bm) {
        super(bm, name, description, permission, usage, onlyFromPlayer);
    }

    @Override
    boolean handle(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        //開啟選單
        CustomGUIManager.openPrevious(player, MainMenu.class);
        return true;
    }
}
