package tw.kid7.BannerMaker.customGUI;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

public class CustomGUIMenuListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        //只處理自定義選單
        CustomGUIMenu customGUIMenu = CustomGUIMenu.openedCustomGUIMenuMap.get(inventory);
        if (customGUIMenu == null) {
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
        customGUIMenu.action(rawSlot, clickType);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClose(InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();
        CustomGUIMenu.openedCustomGUIMenuMap.remove(inventory);
    }
}
