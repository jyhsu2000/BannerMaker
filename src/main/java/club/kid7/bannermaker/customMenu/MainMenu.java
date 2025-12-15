package club.kid7.bannermaker.customMenu;

import club.kid7.bannermaker.AlphabetBanner;
import club.kid7.bannermaker.BannerMaker;
import club.kid7.bannermaker.PlayerData;
import club.kid7.bannermaker.util.IOUtil;
import club.kid7.bannermaker.util.InventoryMenuUtil;
import club.kid7.bannermaker.util.ItemBuilder;
import club.kid7.pluginutilities.gui.ClickAction;
import club.kid7.pluginutilities.gui.CustomGUIInventory;
import club.kid7.pluginutilities.gui.CustomGUIManager;
import club.kid7.pluginutilities.gui.CustomGUIMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Objects;

import static club.kid7.bannermaker.configuration.Language.tl;

public class MainMenu implements CustomGUIMenu {
    @Override
    public CustomGUIInventory build(final Player player) {
        final PlayerData playerData = BannerMaker.getInstance().playerDataMap.get(player);
        //建立選單
        String title = BannerMaker.getInstance().getMessageService().formatToString(tl("gui.prefix") + tl("gui.main-menu"));
        CustomGUIInventory menu = new CustomGUIInventory(title);
        //當前頁數
        final int currentPage = playerData.getCurrentPage();
        //顯示現有旗幟
        List<ItemStack> bannerList = IOUtil.loadBannerList(player, currentPage);
        for (int i = 0; i < bannerList.size() && i < 45; i++) {
            final ItemStack banner = bannerList.get(i);
            menu.setItem(i, banner, new ClickAction(ClickType.LEFT, event -> InventoryMenuUtil.openBannerInfo(player, banner)));
        }
        //總頁數
        int totalPage = (int) Math.ceil(IOUtil.getBannerCount(player) / 45.0);
        //新增按鈕
        //換頁按鈕
        //上一頁
        if (currentPage > 1) {
            ItemStack prevPage = new ItemBuilder(Material.ARROW).amount(currentPage - 1).name(BannerMaker.getInstance().getMessageService().formatToString("&a" + tl("gui.prev-page"))).build();
            menu.setItem(45, prevPage, new ClickAction(ClickType.LEFT, event -> {
                playerData.setCurrentPage(currentPage - 1);
                CustomGUIManager.openPrevious(player);
            }));
        }
        //下一頁
        if (currentPage < totalPage) {
            ItemStack nextPage = new ItemBuilder(Material.ARROW).amount(currentPage + 1).name(BannerMaker.getInstance().getMessageService().formatToString("&a" + tl("gui.next-page"))).build();
            menu.setItem(53, nextPage, new ClickAction(ClickType.LEFT, event -> {
                playerData.setCurrentPage(currentPage + 1);
                CustomGUIManager.openPrevious(player);
            }));
        }
        //Create banner
        ItemStack btnCreateBanner = new ItemBuilder(Material.LIME_WOOL).name(BannerMaker.getInstance().getMessageService().formatToString("&a" + tl("gui.create-banner"))).build();
        menu.setItem(49, btnCreateBanner, new ClickAction(ClickType.LEFT, event -> CustomGUIManager.open(player, CreateBannerMenu.class)));
        //建立字母
        if (BannerMaker.getInstance().enableAlphabetAndNumber) {
            ItemStack btnCreateAlphabet = AlphabetBanner.get("A");
            ItemMeta btnCreateAlphabetItemMeta = btnCreateAlphabet.getItemMeta();
            Objects.requireNonNull(btnCreateAlphabetItemMeta).setDisplayName(BannerMaker.getInstance().getMessageService().formatToString("&a" + tl("gui.alphabet-and-number")));
            btnCreateAlphabet.setItemMeta(btnCreateAlphabetItemMeta);
            menu.setItem(51, btnCreateAlphabet, new ClickAction(ClickType.LEFT, event -> CustomGUIManager.open(player, ChooseAlphabetMenu.class)));
        }
        return menu;
    }
}
