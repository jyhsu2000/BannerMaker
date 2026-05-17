package tw.jyhsu.bannermaker.gui;

import tw.jyhsu.bannermaker.AlphabetBanner;
import tw.jyhsu.bannermaker.BannerMaker;
import tw.jyhsu.bannermaker.util.ItemBuilder;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static tw.jyhsu.bannermaker.configuration.Language.tl;

public class MainMenuGUI {

    public static void show(Player player) {
        ChestGui gui = GuiUtil.createChestGui("gui.title.main-menu");

        // 1. 旗幟列表分頁面 (Paginated Pane)
        PaginatedPane paginatedPane = new PaginatedPane(0, 0, 9, 5);
        List<ItemStack> banners = BannerMaker.getInstance().getBannerRepository().loadBannerList(player);
        List<GuiItem> bannerItems = new ArrayList<>();

        for (ItemStack banner : banners) {
            GuiItem item = new GuiItem(banner, event -> {
                BannerInfoGUI.open(player, banner);
            });
            bannerItems.add(item);
        }

        paginatedPane.populateWithGuiItems(bannerItems);
        gui.addPane(paginatedPane);

        // 2. 靜態控制面板 (Static Pane) - 用於放置導航和功能按鈕
        StaticPane navigationPane = new StaticPane(0, 5, 9, 1);

        // 採「位置固定 + 灰玻璃填空」工具列設計，跟 BannerInfoGUI 對齊
        GuiUtil.fillToolbarRow(navigationPane, 0);

        // 初始化導航按鈕（上一頁 slot 0、下一頁 slot 8）
        updateNavigation(navigationPane, paginatedPane, gui);

        // 製作旗幟按鈕（slot 4，永遠顯示）
        ItemStack btnCreateBanner = new ItemBuilder(Material.LIME_WOOL)
            .name(tl(NamedTextColor.GREEN, "gui.create-banner"))
            .build();
        GuiUtil.putAt(navigationPane, 4, 0, btnCreateBanner, event -> {
            CreateBannerGUI.show(player);
        });

        // 製作字母按鈕（slot 6，若啟用 AlphabetAndNumber、否則維持灰玻璃）
        if (BannerMaker.getInstance().isEnableAlphabetAndNumber()) {
            ItemStack btnCreateAlphabet = new ItemBuilder(AlphabetBanner.get("A"))
                .name(tl(NamedTextColor.GREEN, "gui.alphabet-and-number"))
                .build();
            GuiUtil.putAt(navigationPane, 6, 0, btnCreateAlphabet, event -> {
                ChooseAlphabetGUI.show(player);
            });
        }

        gui.addPane(navigationPane);
        gui.show(player);
    }

    private static void updateNavigation(StaticPane navigationPane, PaginatedPane paginatedPane, ChestGui gui) {
        // 跟 BannerInfoGUI 工具列同模式：先把 slot 0/8 重設為灰玻璃，有按鈕時再覆蓋。
        // 不依賴外層 fillToolbarRow，遞迴呼叫時也能獨立把箭頭換回灰玻璃。
        navigationPane.removeItem(0, 0);
        navigationPane.addItem(GuiUtil.grayPaneFiller(), 0, 0);
        navigationPane.removeItem(8, 0);
        navigationPane.addItem(GuiUtil.grayPaneFiller(), 8, 0);

        // slot 0: 上一頁
        if (paginatedPane.getPage() > 0) {
            ItemStack prevPage = new ItemBuilder(Material.ARROW)
                .amount(paginatedPane.getPage()) // 將當前頁碼設為物品數量（視覺效果）
                .name(tl(NamedTextColor.GREEN, "gui.prev-page"))
                .build();
            GuiUtil.putAt(navigationPane, 0, 0, prevPage, event -> {
                paginatedPane.setPage(paginatedPane.getPage() - 1);
                updateNavigation(navigationPane, paginatedPane, gui);
                gui.update();
            });
        }

        // slot 8: 下一頁
        if (paginatedPane.getPage() < paginatedPane.getPages() - 1) {
            ItemStack nextPage = new ItemBuilder(Material.ARROW)
                .amount(paginatedPane.getPage() + 2) // 將下一頁碼設為物品數量（視覺效果）
                .name(tl(NamedTextColor.GREEN, "gui.next-page"))
                .build();
            GuiUtil.putAt(navigationPane, 8, 0, nextPage, event -> {
                paginatedPane.setPage(paginatedPane.getPage() + 1);
                updateNavigation(navigationPane, paginatedPane, gui);
                gui.update();
            });
        }
    }
}
