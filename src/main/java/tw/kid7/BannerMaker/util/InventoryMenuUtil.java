package tw.kid7.BannerMaker.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import tw.kid7.BannerMaker.PlayerData;
import tw.kid7.BannerMaker.configuration.Language;
import tw.kid7.BannerMaker.inventoryMenu.AbstractInventoryMenu;

public class InventoryMenuUtil {

    static private final String hiddenPrefix = "&b&m&r";

    static public void openMenu(Player player) {
        //取得GUI選單
        AbstractInventoryMenu menu = PlayerData.get(player).getInventoryMenu();
        //開啟選單
        menu.open(player);
    }

    public static Inventory create(String title) {
        Inventory menu;
        String guiPrefix = hiddenPrefix + Language.get("gui.prefix");
        String fullTitle = guiPrefix + title;
        int inventorySize = 54;
        //嘗試以完整標題建立
        try {
            menu = Bukkit.createInventory(null, inventorySize, MessageUtil.format(fullTitle));
            return menu;
        } catch (Exception ignored) {

        }
        //嘗試修剪標題（忽略顏色代碼）
        try {
            menu = Bukkit.createInventory(null, inventorySize, MessageUtil.format(MessageUtil.cutStringWithoutColor(fullTitle, 32)));
            return menu;
        } catch (Exception ignored) {

        }
        //嘗試修剪標題（直接修剪，不考慮顏色代碼）
        menu = Bukkit.createInventory(null, inventorySize, MessageUtil.format(fullTitle.substring(0, 32)));
        return menu;
    }
}
