package tw.kid7.BannerMaker.inventoryMenu;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import tw.kid7.BannerMaker.BannerMaker;
import tw.kid7.BannerMaker.PlayerData;
import tw.kid7.BannerMaker.clickableInventory.ClickableInventory;

public abstract class AbstractInventoryMenu implements InventoryMenuInterface {

    /**
     * 點擊事件處理
     *
     * @param event 事件
     */
    final public void onClick(InventoryClickEvent event) {
        //玩家
        Player player = (Player) event.getWhoClicked();
        //玩家資料
        PlayerData playerData = BannerMaker.getInstance().playerDataMap.get(player);
        //點擊位置
        int rawSlot = event.getRawSlot();
        //點擊類型
        ClickType clickType = event.getClick();
        //找出可點擊物品欄
        //TODO ClickableInventory應該要有自己獨立的EventListener
        ClickableInventory clickableInventory = ClickableInventory.get(playerData.getInventoryMenuState(), player);
        if (clickableInventory == null) {
            return;
        }
        //執行動作
        clickableInventory.action(rawSlot, clickType);
    }
}
