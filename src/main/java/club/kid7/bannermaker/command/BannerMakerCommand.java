package club.kid7.bannermaker.command;

import club.kid7.bannermaker.BannerMaker;
import club.kid7.bannermaker.customMenu.MainMenu;
import club.kid7.pluginutilities.command.CommandComponent;
import club.kid7.pluginutilities.gui.CustomGUIManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BannerMakerCommand extends CommandComponent {
    //名稱
    private static final String name = "BannerMaker";
    //介紹
    private static final String description = "Show menu of BannerMaker";
    //權限
    private static final String permission = "BannerMaker.use";
    //使用方法
    private static final String usage = "/bm";
    //僅能由玩家執行
    private static final boolean onlyFromPlayer = true;

    public BannerMakerCommand(BannerMaker bm) {
        super(bm, name, description, permission, usage, onlyFromPlayer);
        registerSubCommand("help", new HelpCommand(bm));
        registerSubCommand("see", new SeeCommand(bm));
        registerSubCommand("hand", new HandCommand(bm));
        registerSubCommand("reload", new ReloadCommand(bm));
    }


    @Override
    public boolean executeCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player player = (Player) sender;
        //開啟選單
        CustomGUIManager.openPrevious(player, MainMenu.class);
        return true;
    }
}
