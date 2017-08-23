package tw.kid7.util.customGUI;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

public class CustomGUIInventoryListener implements Listener {

    /**
     * 點擊時的處理
     *
     * @param event 點擊事件
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        //只處理自定義選單
        CustomGUIInventory customGUIInventory = CustomGUIInventory.openedCustomGUIInventoryMap.get(inventory);
        if (customGUIInventory == null) {
            return;
        }
        //取消事件
        event.setCancelled(true);
        //不處理空格
        if (event.getCurrentItem() == null || event.getCurrentItem().getType().equals(Material.AIR)) {
            return;
        }

        //點擊位置
        int rawSlot = event.getRawSlot();
        //點擊類型
        ClickType clickType = event.getClick();
        //觸發點擊事件
        customGUIInventory.action(rawSlot, clickType);
    }

    /**
     * 關閉物品欄時，將物品欄從清單移除，避免誤進入自定義選單的事件處理
     *
     * @param event 物品欄關閉事件
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClose(InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();
        CustomGUIInventory.openedCustomGUIInventoryMap.remove(inventory);
    }
}
