package tw.kid7.BannerMaker.inventoryMenu;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import tw.kid7.BannerMaker.util.InventoryMenuUtil;

import java.util.HashMap;

public abstract class AbstractInventoryMenu implements InventoryMenuInterface {
    //玩家的點擊動作表
    final private HashMap<String, Table<Integer, ClickType, Clickable>> clickableItemTables = Maps.newHashMap();

    /**
     * 點擊事件處理
     *
     * @param event 事件
     */
    final public void onClick(InventoryClickEvent event) {
        //玩家
        Player player = (Player) event.getWhoClicked();
        //點擊位置
        int rawSlot = event.getRawSlot();
        //點擊類型
        ClickType clickType = event.getClick();
        //玩家的點擊動作表
        Table<Integer, ClickType, Clickable> clickableItemTable = getClickableItemTable(player);
        //找出對應動作
        Clickable clickableItem = clickableItemTable.get(rawSlot, clickType);
        if (clickableItem == null) {
            return;
        }
        //執行動作
        clickableItem.action();
    }

    /**
     * 清除點擊動作表並建立選單
     *
     * @param player 玩家
     * @param title  選單標題
     * @return 選單物品欄
     */
    final Inventory createClickableMenu(Player player, String title) {
        //清除點擊動作表
        resetClickableItemTable(player);
        //建立並回傳選單物品欄
        return InventoryMenuUtil.create(player, title);
    }

    /**
     * 設定可點擊的物件
     *
     * @param inventory 選單物品欄
     * @param slot      位置
     * @param itemStack 顯示物品
     * @param clickType 點擊類型
     * @param clickable 點擊動作
     */
    final void setClickableItem(Inventory inventory, int slot, ItemStack itemStack, ClickType clickType, Clickable clickable) {
        Player player = (Player) inventory.getHolder();
        //玩家的點擊動作表
        Table<Integer, ClickType, Clickable> clickableItemTable = getClickableItemTable(player);
        //放入點擊動作表
        clickableItemTable.put(slot, clickType, clickable);
        //顯示於選單
        inventory.setItem(slot, itemStack);
    }

    /**
     * 取得特定玩家的點擊動作表
     *
     * @param player 玩家
     * @return 點擊動作表
     */
    private Table<Integer, ClickType, Clickable> getClickableItemTable(Player player) {
        String uuid = player.getUniqueId().toString();
        Table<Integer, ClickType, Clickable> clickableItems = clickableItemTables.get(uuid);
        if (clickableItems == null) {
            clickableItems = HashBasedTable.<Integer, ClickType, Clickable>create();
            clickableItemTables.put(uuid, clickableItems);
        }
        return clickableItems;
    }

    /**
     * 重設特定玩家的點擊動作表，避免切換選單時，因殘留資料導致問題
     *
     * @param player 玩家
     */
    private void resetClickableItemTable(Player player) {
        String uuid = player.getUniqueId().toString();
        clickableItemTables.remove(uuid);
    }
}
