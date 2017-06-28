package tw.kid7.BannerMaker.listener;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import tw.kid7.BannerMaker.BannerMaker;
import tw.kid7.BannerMaker.inventoryMenu.AbstractInventoryMenu;

public class InventoryClickEventListener implements Listener {
    private BannerMaker bm;

    public InventoryClickEventListener(BannerMaker bm) {
        this.bm = bm;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getInventory().getName().startsWith(ChatColor.translateAlternateColorCodes('&', "&b&m"))) {
            return;
        }
        //取消事件
        event.setCancelled(true);
        //只處理箱子內的
        if (event.getRawSlot() >= 54) {
            return;
        }
        //不處理空格
        if (event.getCurrentItem() == null || event.getCurrentItem().getType().equals(Material.AIR)) {
            return;
        }
        //取得玩家狀態
        Player player = (Player) event.getWhoClicked();
        //取得GUI選單
        AbstractInventoryMenu menu = bm.playerDataMap.get(player).getInventoryMenu();
        //觸發點擊事件
        menu.onClick(event);
    }
}
