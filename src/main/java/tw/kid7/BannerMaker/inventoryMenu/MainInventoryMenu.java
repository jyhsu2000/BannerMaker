package tw.kid7.BannerMaker.inventoryMenu;

import com.google.common.collect.Maps;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import tw.kid7.BannerMaker.State;
import tw.kid7.BannerMaker.configuration.Language;
import tw.kid7.BannerMaker.util.*;

import java.util.HashMap;
import java.util.List;

public class MainInventoryMenu extends AbstractInventoryMenu {
    private static MainInventoryMenu instance = null;
    private final HashMap<String, Integer> currentPageMap = Maps.newHashMap();

    public static MainInventoryMenu getInstance() {
        if (instance == null) {
            instance = new MainInventoryMenu();
        }
        return instance;
    }

    @Override
    public void open(Player player) {
        //建立選單
        Inventory menu = InventoryMenuUtil.create(Language.get("gui.main-menu"));
        //當前頁數
        int currentPage = 1;
        if (currentPageMap.containsKey(player.getName())) {
            currentPage = currentPageMap.get(player.getName());
        } else {
            currentPageMap.put(player.getName(), 1);
        }
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
            ItemStack prevPage = new ItemBuilder(Material.ARROW).amount(currentPage - 1).name(MessageUtil.format("&a" + Language.get("gui.prev-page"))).build();
            menu.setItem(45, prevPage);
        }
        //下一頁
        if (currentPage < totalPage) {
            ItemStack nextPage = new ItemBuilder(Material.ARROW).amount(currentPage + 1).name(MessageUtil.format("&a" + Language.get("gui.next-page"))).build();
            menu.setItem(53, nextPage);
        }
        //Create banner
        ItemStack btnCreateBanner = new ItemBuilder(Material.WOOL).amount(1).durability(5).name(MessageUtil.format("&a" + Language.get("gui.create-banner"))).build();
        menu.setItem(49, btnCreateBanner);
        //建立字母
        ItemStack btnCreateAlphabet = AlphabetBanner.get("A");
        ItemMeta btnCreateAlphabetItemMeta = btnCreateAlphabet.getItemMeta();
        btnCreateAlphabetItemMeta.setDisplayName(MessageUtil.format("&a" + Language.get("gui.alphabet-and-number")));
        btnCreateAlphabet.setItemMeta(btnCreateAlphabetItemMeta);
        menu.setItem(51, btnCreateAlphabet);
        //開啟選單
        player.openInventory(menu);
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack itemStack = event.getCurrentItem();
        if (event.getRawSlot() < 45) {
            //點擊旗幟
            //記錄選擇的旗幟
            BannerInfoInventoryMenu.getInstance().viewInfoBannerMap.put(player.getName(), itemStack);
            //重置頁數
            BannerInfoInventoryMenu.getInstance().currentRecipePageMap.put(player.getName(), 1);
            //切換畫面
            State.set(player, State.BANNER_INFO);
            //重新開啟選單
            InventoryMenuUtil.openMenu(player);
        } else {
            //點擊按鈕
            String buttonName = itemStack.getItemMeta().getDisplayName();
            buttonName = ChatColor.stripColor(buttonName);
            //當前頁數
            int currentPage = 1;
            if (currentPageMap.containsKey(player.getName())) {
                currentPage = currentPageMap.get(player.getName());
            } else {
                currentPageMap.put(player.getName(), 1);
            }
            //修改狀態
            if (buttonName.equalsIgnoreCase(Language.getIgnoreColors("gui.prev-page"))) {
                currentPageMap.put(player.getName(), currentPage - 1);
            } else if (buttonName.equalsIgnoreCase(Language.getIgnoreColors("gui.next-page"))) {
                currentPageMap.put(player.getName(), currentPage + 1);
            } else if (buttonName.equalsIgnoreCase(Language.getIgnoreColors("gui.create-banner"))) {
                State.set(player, State.CREATE_BANNER);
            } else if (buttonName.equalsIgnoreCase(Language.getIgnoreColors("gui.alphabet-and-number"))) {
                CreateAlphabetInventoryMenu.getInstance().currentAlphabetBannerMap.remove(player.getName());
                State.set(player, State.CREATE_ALPHABET);
            }
            //重新開啟選單
            InventoryMenuUtil.openMenu(player);
        }
    }
}
