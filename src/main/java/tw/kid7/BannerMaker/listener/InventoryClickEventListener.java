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
        AbstractInventoryMenu menu = null;
        //根據狀態決定行為
        switch (state) {
            case CREATE_BANNER:
                menu = CreateBannerInventoryMenu.getInstance();
                break;
            case CREATE_ALPHABET:
                menu = CreateAlphabetInventoryMenu.getInstance();
                break;
            case BANNER_INFO:
                menu = BannerInfoInventoryMenu.getInstance();
                break;
            case MAIN_MENU:
            default:
                menu = MainInventoryMenu.getInstance();
        }
        //觸發點擊動作
        if (menu != null) {
            menu.onClick(event);
        }
    }
}
