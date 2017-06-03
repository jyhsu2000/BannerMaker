package tw.kid7.BannerMaker.inventoryMenu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import tw.kid7.BannerMaker.BannerMaker;
import tw.kid7.BannerMaker.InventoryMenuState;
import tw.kid7.BannerMaker.PlayerData;
import tw.kid7.BannerMaker.util.*;

import java.util.List;

import static tw.kid7.BannerMaker.configuration.Language.tl;

public class MainInventoryMenu extends AbstractInventoryMenu {
    private static MainInventoryMenu instance = null;
    //按鈕位置
    private int buttonPositionPrevPage = 45;
    private int buttonPositionNextPage = 53;
    private int buttonPositionCreateBanner = 49;
    private int buttonPositionCreateAlphabet = 51;

    public static MainInventoryMenu getInstance() {
        if (instance == null) {
            instance = new MainInventoryMenu();
        }
        return instance;
    }

    @Override
    public void open(Player player) {
        PlayerData playerData = PlayerData.get(player);
        //建立選單
        Inventory menu = InventoryMenuUtil.create(tl("gui.main-menu"));
        //當前頁數
        int currentPage = playerData.getCurrentPage();
        //顯示現有旗幟
        List<ItemStack> bannerList = IOUtil.loadBannerList(player, currentPage);
        for (int i = 0; i < bannerList.size() && i < 45; i++) {
            ItemStack banner = bannerList.get(i);
            menu.setItem(i, banner);
        }
        //總頁數
        int totalPage = (int) Math.ceil(IOUtil.getBannerCount(player) / 45.0);
        //新增按鈕
        //換頁按鈕
        //上一頁
        if (currentPage > 1) {
            ItemStack prevPage = new ItemBuilder(Material.ARROW).amount(currentPage - 1).name(MessageUtil.format("&a" + tl("gui.prev-page"))).build();
            menu.setItem(buttonPositionPrevPage, prevPage);
        }
        //下一頁
        if (currentPage < totalPage) {
            ItemStack nextPage = new ItemBuilder(Material.ARROW).amount(currentPage + 1).name(MessageUtil.format("&a" + tl("gui.next-page"))).build();
            menu.setItem(buttonPositionNextPage, nextPage);
        }
        //Create banner
        ItemStack btnCreateBanner = new ItemBuilder(Material.WOOL).amount(1).durability(5).name(MessageUtil.format("&a" + tl("gui.create-banner"))).build();
        menu.setItem(buttonPositionCreateBanner, btnCreateBanner);
        //建立字母
        if (BannerMaker.enableAlphabetAndNumber) {
            ItemStack btnCreateAlphabet = AlphabetBanner.get("A");
            ItemMeta btnCreateAlphabetItemMeta = btnCreateAlphabet.getItemMeta();
            btnCreateAlphabetItemMeta.setDisplayName(MessageUtil.format("&a" + tl("gui.alphabet-and-number")));
            btnCreateAlphabet.setItemMeta(btnCreateAlphabetItemMeta);
            menu.setItem(buttonPositionCreateAlphabet, btnCreateAlphabet);
        }
        //開啟選單
        player.openInventory(menu);
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        PlayerData playerData = PlayerData.get(player);
        ItemStack itemStack = event.getCurrentItem();
        int rawSlot = event.getRawSlot();
        if (rawSlot < 45) {
            //點擊旗幟
            //顯示旗幟
            InventoryMenuUtil.showBannerInfo(player, itemStack);
            return;
        }
        //當前頁數
        int currentPage = playerData.getCurrentPage();
        //修改狀態
        if (rawSlot == buttonPositionPrevPage) {
            playerData.setCurrentPage(currentPage - 1);
            InventoryMenuUtil.openMenu(player);
            return;
        }
        if (rawSlot == buttonPositionNextPage) {
            playerData.setCurrentPage(currentPage + 1);
            InventoryMenuUtil.openMenu(player);
            return;
        }
        if (rawSlot == buttonPositionCreateBanner) {
            InventoryMenuUtil.openMenu(player, InventoryMenuState.CREATE_BANNER);
            return;
        }
        if (rawSlot == buttonPositionCreateAlphabet) {
            if (BannerMaker.enableAlphabetAndNumber) {
                InventoryMenuUtil.openMenu(player, InventoryMenuState.CHOOSE_ALPHABET);
            }
            return;
        }
    }
}
