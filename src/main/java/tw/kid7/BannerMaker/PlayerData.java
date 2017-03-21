package tw.kid7.BannerMaker;

import com.google.common.collect.Maps;
import org.bukkit.entity.Player;
import tw.kid7.BannerMaker.inventoryMenu.AbstractInventoryMenu;

import java.util.HashMap;

public class PlayerData {
    /**
     * 所有玩家資料實例
     */
    private static HashMap<String, PlayerData> playerDataMap = Maps.newHashMap();

    /**
     * 選單狀態
     */
    private InventoryMenuState inventoryMenuState = InventoryMenuState.MAIN_MENU;

    /**
     * 取得玩家資料實例
     *
     * @param player 玩家
     * @return 玩家資料
     */
    public static PlayerData get(Player player) {
        String uuidString = player.getUniqueId().toString();
        PlayerData playerData = playerDataMap.get(uuidString);
        if (playerData == null) {
            playerData = new PlayerData();
            playerDataMap.put(uuidString, playerData);
        }
        return playerData;
    }

    public InventoryMenuState getInventoryMenuState() {
        return inventoryMenuState;
    }

    public void setInventoryMenuState(InventoryMenuState inventoryMenuState) {
        this.inventoryMenuState = inventoryMenuState;
    }

    /**
     * 取得選單狀態對應的GUI選單介面
     *
     * @return GUI選單介面
     */
    public AbstractInventoryMenu getInventoryMenu() {
        return inventoryMenuState.getInventoryMenu();
    }
}
