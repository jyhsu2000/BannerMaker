package tw.kid7.BannerMaker.inventoryMenu;

import org.bukkit.entity.Player;

interface InventoryMenuInterface {
    /**
     * 開啟InventoryMenu給特定玩家
     *
     * @param player 欲顯示此InventoryMenu之玩家
     */
    void open(Player player);
}
