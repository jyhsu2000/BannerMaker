package tw.kid7.BannerMaker.customMenu;

import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class CustomMenu {
    /**
     * 被玩家開啟的自定義選單與物品欄的對應
     */
    final static HashMap<Inventory, CustomMenu> openedCustomMenuMap = Maps.newHashMap();
    /**
     * 物品欄
     */
    private Inventory inventory;
    /**
     * 每個位置的自定義選單物品
     */
    private final HashMap<Integer, CustomMenuItem> customMenuItemMap = Maps.newHashMap();

    /**
     * 建構子
     *
     * @param title 選單標題
     */
    public CustomMenu(String title) {
        this(title, 54);
    }

    /**
     * 建構子
     *
     * @param title 選單標題
     * @param size  物品欄尺寸
     */
    public CustomMenu(String title, int size) {
        inventory = Bukkit.createInventory(null, size, title);
    }

    /**
     * 開啟自定義選單
     *
     * @param player 玩家
     */
    public void open(Player player) {
        openedCustomMenuMap.put(inventory, this);
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
    public CustomMenuItem setClickableItem(int slot, ItemStack itemStack) {
        setItem(slot, itemStack);
        CustomMenuItem customMenuItem = new CustomMenuItem();
        customMenuItemMap.put(slot, customMenuItem);
        return customMenuItem;
    }

    /**
     * 執行在特定位置使用特定點擊類型的動作
     *
     * @param slot      位置
     * @param clickType 點擊類型
     */
    public void action(int slot, ClickType clickType) {
        CustomMenuItem customMenuItem = customMenuItemMap.get(slot);
        if (customMenuItem == null) {
            return;
        }
        customMenuItem.action(clickType);
    }
}
