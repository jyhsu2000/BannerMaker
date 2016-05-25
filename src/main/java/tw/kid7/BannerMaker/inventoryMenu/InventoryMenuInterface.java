package tw.kid7.BannerMaker.inventoryMenu;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

interface InventoryMenuInterface {
    /**
     * 開啟InventoryMenu給特定玩家
     *
     * @param player 欲顯示此InventoryMenu之玩家
     */
    void open(Player player);

    /**
     * 點擊事件處理
     *
     * @param event 事件
     */
    void onClick(InventoryClickEvent event);
}
