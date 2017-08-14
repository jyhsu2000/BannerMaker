package tw.kid7.BannerMaker.clickableInventory;

import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class ClickableInventory {
    /**
     * 被玩家開啟的可點擊物品欄與物品欄的對應
     */
    final static HashMap<Inventory, ClickableInventory> openedClickableInventory = Maps.newHashMap();
    /**
     * 物品欄
     */
    private Inventory inventory;
    /**
     * 每個位置的可點擊物件
     */
    private final HashMap<Integer, ClickableItem> clickableItemHashMap = Maps.newHashMap();

    /**
     * 建構子
     *
     * @param title 物品欄標題
     */
    public ClickableInventory(String title) {
        this(title, 54);
    }

    /**
     * 建構子
     *
     * @param title 物品欄標題
     * @param size  物品欄尺寸
     */
    public ClickableInventory(String title, int size) {
        inventory = Bukkit.createInventory(null, size, title);
    }

    /**
     * 開啟物品欄
     *
     * @param player 玩家
     */
    public void open(Player player) {
        openedClickableInventory.put(inventory, this);
        player.openInventory(inventory);
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
