package tw.kid7.BannerMaker.util;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import tw.kid7.BannerMaker.BannerMaker;
import tw.kid7.BannerMaker.PlayerData;
import tw.kid7.BannerMaker.customMenu.BannerInfoMenu;
import tw.kid7.util.customGUI.CustomGUIManager;

public class InventoryMenuUtil {

    public static void showBannerInfo(Player player, ItemStack banner) {
        if (!BannerUtil.isBanner(banner)) {
            return;
        }
        PlayerData playerData = BannerMaker.getInstance().playerDataMap.get(player);
        //設定查看旗幟
        playerData.setViewInfoBanner(banner);
        //重置頁數
        playerData.setCurrentRecipePage(1);
        //開啟選單
        CustomGUIManager.open(player, BannerInfoMenu.class);
    }
}
