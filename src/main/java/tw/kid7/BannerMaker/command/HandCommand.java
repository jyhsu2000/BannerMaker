package tw.kid7.BannerMaker.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import tw.kid7.BannerMaker.BannerMaker;
import tw.kid7.BannerMaker.InventoryMenuState;
import tw.kid7.BannerMaker.PlayerData;
import tw.kid7.BannerMaker.util.BannerUtil;
import tw.kid7.BannerMaker.util.InventoryMenuUtil;
import tw.kid7.BannerMaker.util.MessageUtil;

class HandCommand extends AbstractCommand {
    //名稱
    private static final String name = "Hand";
    //介紹
    private static final String description = "Show banner info of the banner in hand";
    //權限
    private static final String permission = "BannerMaker.hand";
    //使用方法
    private static final String usage = "/bm hand";
    //僅能由玩家執行
    private static final boolean onlyFromPlayer = true;

    HandCommand() {
        super(name, description, permission, usage, onlyFromPlayer);
    }

    @Override
    boolean handle(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        ItemStack itemStack = BannerMaker.getInstance().getVersionHandler().getItemInMainHand(player);
        if (!BannerUtil.isBanner(itemStack)) {
            //TODO: 加到語言包
            player.sendMessage(MessageUtil.format(true, "&cItem in hand is not a banner."));
            return true;
        }
        PlayerData playerData = PlayerData.get(player);
        //設定查看旗幟
        //FIXME: 清理旗幟資訊（自訂名稱等）
        playerData.setViewInfoBanner(itemStack);
        //重置頁數
        playerData.setCurrentRecipePage(1);
        //設定畫面
        playerData.setInventoryMenuState(InventoryMenuState.BANNER_INFO);
        //開啟選單
        InventoryMenuUtil.openMenu(player);

        return true;
    }
}
