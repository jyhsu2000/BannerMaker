package tw.kid7.BannerMaker.util;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Banner;
import org.bukkit.material.Wool;
import tw.kid7.BannerMaker.BannerMaker;
import tw.kid7.BannerMaker.State;

import static tw.kid7.BannerMaker.State.MAIN_MENU;

public class InventoryUtil {

    static public void openMenu(Player player) {
        //取得玩家狀態
        State state = MAIN_MENU;
        if (BannerMaker.getInstance().stateMap.containsKey(player.getName())) {
            state = BannerMaker.getInstance().stateMap.get(player.getName());
        }
        //根據狀態決定行為
        switch (state) {
            case CREATE_BANNER:
                openCreateBanner(player);
                break;
            case BANNER_INFO:
                break;
            case CRAFT_RECIPT:
                break;
            case MAIN_MENU:
            default:
                openMainMenu(player);
        }

    }

    static public void openMainMenu(Player player) {
        //建立選單
        Inventory menu = Bukkit.createInventory(null, 54, MessageUtil.format("&f[&4BM&f] &rMain menu"));
        //新增按鈕
        ItemStack btnCreateBanner = new Wool(DyeColor.GREEN).toItemStack(1);
        ItemMeta im = btnCreateBanner.getItemMeta();
        im.setDisplayName(MessageUtil.format("&aCreate Banner"));
        btnCreateBanner.setItemMeta(im);
        menu.setItem(49, btnCreateBanner);
        //開啟選單
        player.openInventory(menu);
    }

    static public void openCreateBanner(Player player) {
        //建立選單
        Inventory menu = Bukkit.createInventory(null, 54, MessageUtil.format("&f[&4BM&f] &rCreate banner"));
        //新增按鈕

        //開啟選單
        player.openInventory(menu);
    }
}
