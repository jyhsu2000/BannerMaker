package tw.kid7.BannerMaker.listener;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import tw.kid7.BannerMaker.State;
import tw.kid7.BannerMaker.inventoryMenu.*;

public class InventoryClickEventListener implements Listener {
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
        State state = State.get(player);
        //取得該狀態的GUI選單
        AbstractInventoryMenu menu = state.getInventoryMenu();
        if (menu != null) {
            menu.onClick(event);
        }
    }
}
