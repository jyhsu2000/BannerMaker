package tw.kid7.BannerMaker.customGUI;

import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class CustomGUIMenu {
    /**
     * 被玩家開啟的自定義選單與物品欄的對應
     */
    final static HashMap<Inventory, CustomGUIMenu> openedCustomGUIMenuMap = Maps.newHashMap();
    /**
     * 物品欄
     */
    private Inventory inventory;
    /**
     * 每個位置的自定義選單物品
     */
    private final HashMap<Integer, CustomGUIItem> customGUIItemMap = Maps.newHashMap();

    /**
     * 建構子
     *
     * @param title 選單標題
     */
    public CustomGUIMenu(String title) {
        this(title, 54);
    }

    /**
     * 建構子
     *
     * @param title 選單標題
     * @param size  物品欄尺寸
     */
    public CustomGUIMenu(String title, int size) {
        inventory = Bukkit.createInventory(null, size, title);
    }

    /**
     * 開啟自定義選單
     *
     * @param player 玩家
     */
    public void open(Player player) {
        openedCustomGUIMenuMap.put(inventory, this);
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
     * 設定自定義選單物品
     *
     * @param slot      位置
     * @param itemStack 顯示用物品
     * @return 自定義選單物品
     */
    public CustomGUIItem setClickableItem(int slot, ItemStack itemStack) {
        setItem(slot, itemStack);
        CustomGUIItem customGUIItem = new CustomGUIItem();
        customGUIItemMap.put(slot, customGUIItem);
        return customGUIItem;
    }

    /**
     * 執行在特定位置使用特定點擊類型的動作
     *
     * @param slot      位置
     * @param clickType 點擊類型
     */
    public void action(int slot, ClickType clickType) {
        CustomGUIItem customGUIItem = customGUIItemMap.get(slot);
        if (customGUIItem == null) {
            return;
        }
        customGUIItem.action(clickType);
    }
}
