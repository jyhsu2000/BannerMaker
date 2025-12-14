package club.kid7.bannermaker.command;

import club.kid7.bannermaker.BannerMaker;
import club.kid7.bannermaker.util.BannerUtil;
import club.kid7.bannermaker.util.InventoryMenuUtil;
import club.kid7.pluginutilities.command.CommandComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ViewCommand extends CommandComponent {
    //名稱
    private static final String name = "View";
    //介紹
    private static final String description = "View banner info of the banner command";
    //權限
    private static final String permission = "BannerMaker.view";
    //使用方法
    private static final String usage = "/bm view <bannerString>";
    //僅能由玩家執行
    private static final boolean onlyFromPlayer = true;

    public ViewCommand(BannerMaker bm) {
        super(bm, name, description, permission, usage, onlyFromPlayer);
    }

    @Override
    public boolean executeCommand(CommandSender sender, Command command, String label, String[] args) {
        // TODO: 將訊息新增至語系檔
        Player player = (Player) sender;
        if (args.length != 1) {
            BannerMaker.getInstance().getMessageService().send(player, "&cUsage: " + usage);
            return true;
        }
        String bannerString = args[0];
        try {
            ItemStack banner = BannerUtil.deserialize(bannerString);
            //顯示旗幟
            InventoryMenuUtil.openBannerInfo(player, banner);
        } catch (Exception e) {
            BannerMaker.getInstance().getMessageService().send(player, "&cInvalid banner string");
            return true;
        }
        return true;
    }
}
