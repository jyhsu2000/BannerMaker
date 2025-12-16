package club.kid7.bannermaker.gui;

import club.kid7.bannermaker.AlphabetBanner;
import club.kid7.bannermaker.BannerMaker;
import club.kid7.bannermaker.service.MessageService;
import club.kid7.bannermaker.util.IOUtil;
import club.kid7.bannermaker.util.InventoryMenuUtil;
import club.kid7.bannermaker.util.ItemBuilder;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static club.kid7.bannermaker.configuration.Language.tl;

public class MainMenuGUI {

    public static void show(Player player) {
        MessageService messageService = BannerMaker.getInstance().getMessageService();
        Component titleComponent = tl("gui.prefix").append(tl("gui.main-menu"));
        // InventoryFramework 標題需要 Legacy String
        String title = LegacyComponentSerializer.legacySection().serialize(titleComponent);

        ChestGui gui = new ChestGui(6, title);
        gui.setOnGlobalClick(event -> event.setCancelled(true));

        // 1. 旗幟列表分頁面 (Paginated Pane)
        PaginatedPane paginatedPane = new PaginatedPane(0, 0, 9, 5);
        List<ItemStack> banners = IOUtil.loadBannerList(player);
        List<GuiItem> bannerItems = new ArrayList<>();

        for (ItemStack banner : banners) {
            GuiItem item = new GuiItem(banner, event -> {
                InventoryMenuUtil.openBannerInfo(player, banner);
                event.setCancelled(true);
            });
            bannerItems.add(item);
        }

        paginatedPane.populateWithGuiItems(bannerItems);
        gui.addPane(paginatedPane);

        // 2. 靜態控制面板 (Static Pane) - 用於放置導航和功能按鈕
        StaticPane navigationPane = new StaticPane(0, 5, 9, 1);

        // 初始化導航按鈕
        updateNavigation(navigationPane, paginatedPane, gui, messageService);

        // 製作旗幟按鈕
        ItemStack btnCreateBanner = new ItemBuilder(Material.LIME_WOOL)
            .name(Component.empty().color(NamedTextColor.GREEN).append(tl("gui.create-banner")))
            .build();
        navigationPane.addItem(new GuiItem(btnCreateBanner, event -> {
            CreateBannerGUI.show(player);
            event.setCancelled(true);
        }), 4, 0);

        // 製作字母按鈕 (若啟用)
        if (BannerMaker.getInstance().enableAlphabetAndNumber) {
            ItemStack btnCreateAlphabet = AlphabetBanner.get("A");
            ItemBuilder btnBuilder = new ItemBuilder(btnCreateAlphabet);
            btnBuilder.name(Component.empty().color(NamedTextColor.GREEN).append(tl("gui.alphabet-and-number")));
            navigationPane.addItem(new GuiItem(btnBuilder.build(), event -> {
                ChooseAlphabetGUI.show(player);
                event.setCancelled(true);
            }), 6, 0); // Slot 51 是最後一行的第 7 格 (索引 6)
        }

        gui.addPane(navigationPane);
        gui.show(player);
    }

    private static void updateNavigation(StaticPane navigationPane, PaginatedPane paginatedPane, ChestGui gui, MessageService messageService) {
        // 清除舊的導航按鈕 (僅清除 Slot 45 和 53，即 StaticPane 的 (0,0) 和 (8,0))
        // 注意: StaticPane (0, 5, 9, 1) 使用本地座標。
        // Slot 45 對應本地 (0, 0)。 Slot 53 對應本地 (8, 0)。

        // 上一頁
        if (paginatedPane.getPage() > 0) {
            ItemStack prevPage = new ItemBuilder(Material.ARROW)
                .amount(paginatedPane.getPage()) // 將當前頁碼設為物品數量 (視覺效果)
                .name(Component.empty().color(NamedTextColor.GREEN).append(tl("gui.prev-page")))
                .build();

            navigationPane.addItem(new GuiItem(prevPage, event -> {
                paginatedPane.setPage(paginatedPane.getPage() - 1);
                updateNavigation(navigationPane, paginatedPane, gui, messageService);
                gui.update();
                event.setCancelled(true);
            }), 0, 0);
        } else {
            navigationPane.removeItem(0, 0);
        }

        // 下一頁
        if (paginatedPane.getPage() < paginatedPane.getPages() - 1) {
            ItemStack nextPage = new ItemBuilder(Material.ARROW)
                .amount(paginatedPane.getPage() + 2) // 將下一頁碼設為物品數量 (視覺效果)
                .name(Component.empty().color(NamedTextColor.GREEN).append(tl("gui.next-page")))
                .build();

            navigationPane.addItem(new GuiItem(nextPage, event -> {
                paginatedPane.setPage(paginatedPane.getPage() + 1);
                updateNavigation(navigationPane, paginatedPane, gui, messageService);
                gui.update();
                event.setCancelled(true);
            }), 8, 0);
        } else {
            navigationPane.removeItem(8, 0);
        }
    }
}
