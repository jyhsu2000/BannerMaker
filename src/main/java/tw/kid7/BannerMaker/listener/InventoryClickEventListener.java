package tw.kid7.BannerMaker.listener;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
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
        State state = MAIN_MENU;
        if (BannerMaker.getInstance().stateMap.containsKey(player.getName())) {
            state = BannerMaker.getInstance().stateMap.get(player.getName());
        }
        //根據狀態決定行為
        switch (state) {
            case CREATE_BANNER:
                onClickCreateBanner(event);
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
            buttonName = ChatColor.stripColor(buttonName).toLowerCase();
            //修改狀態
            switch (buttonName) {
                case "create banner":
                    BannerMaker.getInstance().stateMap.put(player.getName(), State.CREATE_BANNER);
            }
            //重新開啟選單
            InventoryUtil.openMenu(player);
        }

    }

    public void onClickCreateBanner(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack itemStack = event.getCurrentItem();
        //取得當前編輯中的旗幟
        ItemStack currentBanner = BannerMaker.getInstance().currentBanner.get(player.getName());
        if (event.getRawSlot() >= 1 && event.getRawSlot() <= 17 && event.getRawSlot() % 9 != 0) {
            if (currentBanner == null) {
                //選擇底色
                BannerMaker.getInstance().currentBanner.put(player.getName(), itemStack);
            } else {
                //點擊顏色
                BannerMaker.getInstance().selectedColor.put(player.getName(), (int) itemStack.getDurability());
            }
            //重新開啟選單
            InventoryUtil.openMenu(player);
        } else if (event.getRawSlot() >= 19 && event.getRawSlot() <= 44 && event.getRawSlot() % 9 != 0) {
            //新增Pattern
            BannerMeta bm = (BannerMeta) itemStack.getItemMeta();
            Pattern pattern = bm.getPattern(bm.numberOfPatterns() - 1);
            BannerMeta currentBm = (BannerMeta) currentBanner.getItemMeta();
            currentBm.addPattern(pattern);
            currentBanner.setItemMeta(currentBm);
            BannerMaker.getInstance().currentBanner.put(player.getName(), currentBanner);
            //重新開啟選單
            InventoryUtil.openMenu(player);
        } else if (event.getRawSlot() >= 45) {
            //點擊按鈕
            String buttonName = itemStack.getItemMeta().getDisplayName();
            buttonName = ChatColor.stripColor(buttonName).toLowerCase();
            //修改狀態
            switch (buttonName) {
                case "more patterns":
                    if (BannerMaker.getInstance().morePatterns.containsKey(player.getName())) {
                        BannerMaker.getInstance().morePatterns.put(player.getName(), !BannerMaker.getInstance().morePatterns.get(player.getName()));
                    } else {
                        BannerMaker.getInstance().morePatterns.put(player.getName(), true);
                    }
                    break;
                case "remove last pattern":
                    if (currentBanner.hasItemMeta() && ((BannerMeta) currentBanner.getItemMeta()).numberOfPatterns() > 0) {
                        BannerMeta bm = (BannerMeta) currentBanner.getItemMeta();
                        bm.removePattern(bm.numberOfPatterns() - 1);
                        currentBanner.setItemMeta(bm);
                        BannerMaker.getInstance().currentBanner.put(player.getName(), currentBanner);
                    }
                    break;
                case "create":
                    break;
                case "delete":
                    BannerMaker.getInstance().currentBanner.remove(player.getName());
                    break;
                case "back":
                    BannerMaker.getInstance().stateMap.put(player.getName(), State.MAIN_MENU);
            }
            //重新開啟選單
            InventoryUtil.openMenu(player);
        }

    }
}
