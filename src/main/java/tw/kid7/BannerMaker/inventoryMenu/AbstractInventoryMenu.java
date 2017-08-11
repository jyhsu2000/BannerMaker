package tw.kid7.BannerMaker.inventoryMenu;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public abstract class AbstractInventoryMenu implements InventoryMenuInterface {
    final private Table<Integer, ClickType, Clickable> clickableItems = HashBasedTable.create();

    @Override
    public void onClick(InventoryClickEvent event) {
        //點擊位置
        int rawSlot = event.getRawSlot();
        //點擊類型
        ClickType clickType = event.getClick();
        //找出對應動作
        Clickable clickableItem = clickableItems.get(rawSlot, clickType);
        if (clickableItem == null) {
            return;
        }
        //執行動作
        clickableItem.action();
    }

    final void setClickableItem(Inventory inventory, int slot, ItemStack itemStack, ClickType clickType, Clickable clickable) {
        //放入點擊動作表
        clickableItems.put(slot, clickType, clickable);
        //顯示於選單
        inventory.setItem(slot, itemStack);
    }
}
