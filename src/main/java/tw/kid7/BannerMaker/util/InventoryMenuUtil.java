package tw.kid7.BannerMaker.util;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import tw.kid7.BannerMaker.BannerMaker;
import tw.kid7.BannerMaker.InventoryMenuState;
import tw.kid7.BannerMaker.PlayerData;
import tw.kid7.BannerMaker.inventoryMenu.AbstractInventoryMenu;

public class InventoryMenuUtil {

    static public void openMenu(Player player) {
        openMenu(player, null);
    }

    static public void openMenu(Player player, InventoryMenuState inventoryMenuState) {
        PlayerData playerData = BannerMaker.getInstance().playerDataMap.get(player);
        //設定狀態
        if (inventoryMenuState != null) {
            playerData.setInventoryMenuState(inventoryMenuState);
        }
        //取得GUI選單
        AbstractInventoryMenu menu = playerData.getInventoryMenu();
        //開啟選單
        menu.open(player);
    }

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
        openMenu(player, InventoryMenuState.BANNER_INFO);
    }
}
