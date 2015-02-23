package tw.kid7.BannerMaker.listener;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import tw.kid7.BannerMaker.BannerMaker;
import tw.kid7.BannerMaker.State;
import tw.kid7.BannerMaker.util.InventoryUtil;

import static tw.kid7.BannerMaker.State.MAIN_MENU;

public class InventoryClickEventListener implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!ChatColor.stripColor(event.getInventory().getName()).contains("[BM]")) {
            return;
        }
        //取消事件
        event.setCancelled(true);
        //取得玩家狀態
        Player player = (Player) event.getWhoClicked();
        State state = MAIN_MENU;
        if (BannerMaker.getInstance().stateMap.containsKey(player.getName())) {
            state = BannerMaker.getInstance().stateMap.get(player.getName());
        }
        //根據狀態決定行為
        switch (state) {
            case CREATE_BANNER:
                break;
            case BANNER_INFO:
                break;
            case CRAFT_RECIPT:
                break;
            case MAIN_MENU:
            default:
                onClickMainMenu(event);
        }
    }

    public void onClickMainMenu(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack itemStack = event.getCurrentItem();
        if (itemStack.getType().equals(Material.BANNER)) {
            //TODO 點擊旗幟
        } else {
            //點擊按鈕
            String buttonName = itemStack.getItemMeta().getDisplayName();
            buttonName = ChatColor.stripColor(buttonName);
            //修改狀態
            switch (buttonName) {
                case "Create Banner":
                    BannerMaker.getInstance().stateMap.put(player.getName(), State.CREATE_BANNER);
            }
            //重新開啟選單
            InventoryUtil.openMenu(player);
        }

    }
}
