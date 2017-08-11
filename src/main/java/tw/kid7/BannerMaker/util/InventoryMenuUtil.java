package tw.kid7.BannerMaker.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import tw.kid7.BannerMaker.BannerMaker;
import tw.kid7.BannerMaker.InventoryMenuState;
import tw.kid7.BannerMaker.PlayerData;
import tw.kid7.BannerMaker.inventoryMenu.AbstractInventoryMenu;

import static tw.kid7.BannerMaker.configuration.Language.tl;

public class InventoryMenuUtil {

    static private final String hiddenPrefix = "&b&m&r";

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

    /**
     * @deprecated
     * @param title 標題
     * @return inventory
     */
    public static Inventory create(String title) {
        return create(null, title);
    }

    public static Inventory create(Player owner, String title) {
        Inventory menu;
        String guiPrefix = hiddenPrefix + tl("gui.prefix");
        String fullTitle = guiPrefix + title;
        int inventorySize = 54;
        //嘗試以完整標題建立
        try {
            menu = Bukkit.createInventory(owner, inventorySize, MessageUtil.format(fullTitle));
            return menu;
        } catch (Exception ignored) {

        }
        //嘗試修剪標題（忽略顏色代碼）
        try {
            menu = Bukkit.createInventory(owner, inventorySize, MessageUtil.format(MessageUtil.cutStringWithoutColor(fullTitle, 32)));
            return menu;
        } catch (Exception ignored) {

        }
        //嘗試修剪標題（直接修剪，不考慮顏色代碼）
        menu = Bukkit.createInventory(owner, inventorySize, MessageUtil.format(fullTitle.substring(0, 32)));
        return menu;
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
