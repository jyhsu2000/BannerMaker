package tw.kid7.BannerMaker.clickableInventory;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import tw.kid7.BannerMaker.InventoryMenuState;
import tw.kid7.BannerMaker.util.InventoryMenuUtil;

import java.util.HashMap;

public class ClickableInventory {
    /**
     * 物品欄
     */
    private Inventory inventory;
    /**
     * 每個玩家的可點擊物品欄
     */
    private static Table<InventoryMenuState, String, ClickableInventory> clickableInventoryTable = HashBasedTable.create();
    /**
     * 每個位置的可點擊物件
     */
    private final HashMap<Integer, ClickableItem> clickableItemHashMap = Maps.newHashMap();

    /**
     * 私有建構子
     *
     * @param owner 擁有者
     * @param title 物品欄標題
     */
    private ClickableInventory(Player owner, String title) {
        inventory = InventoryMenuUtil.create(owner, title);
    }

    /**
     * 靜態建構子
     *
     * @param owner 擁有者
     * @param title 物品欄標題
     * @return 可點擊物品欄
     */
    public static ClickableInventory create(InventoryMenuState inventoryMenuState, Player owner, String title) {
        //建立可點擊物品欄
        ClickableInventory clickableInventory = new ClickableInventory(owner, title);
        //紀錄該玩家的最後一組
        clickableInventoryTable.put(inventoryMenuState, owner.getUniqueId().toString(), clickableInventory);
        return clickableInventory;
    }

    /**
     * 取得特定玩家的可點擊物品欄
     *
     * @param owner 擁有者
     * @return 可點擊物品欄
     */
    public static ClickableInventory get(InventoryMenuState inventoryMenuState, Player owner) {
        return clickableInventoryTable.get(inventoryMenuState, owner.getUniqueId().toString());
    }

    /**
     * 取得物品欄
     *
     * @return 物品欄
     */
    public Inventory toInventory() {
        return inventory;
    }

    /**
     * 設定一般物品
     *
     * @param slot      位置
     * @param itemStack 顯示用物品
     */
    public void setItem(int slot, ItemStack itemStack) {
        inventory.setItem(slot, itemStack);
    }

    /**
     * 設定可點擊物品
     *
     * @param slot      位置
     * @param itemStack 顯示用物品
     * @return 可點擊物品
     */
    public ClickableItem setClickableItem(int slot, ItemStack itemStack) {
        setItem(slot, itemStack);
        ClickableItem clickableItem = new ClickableItem();
        clickableItemHashMap.put(slot, clickableItem);
        return clickableItem;
    }

    /**
     * 執行在特定位置使用特定點擊類型的動作
     *
     * @param slot      位置
     * @param clickType 點擊類型
     */
    public void action(int slot, ClickType clickType) {
        ClickableItem clickableItem = clickableItemHashMap.get(slot);
        if (clickableItem == null) {
            return;
        }
        clickableItem.action(clickType);
    }
}
