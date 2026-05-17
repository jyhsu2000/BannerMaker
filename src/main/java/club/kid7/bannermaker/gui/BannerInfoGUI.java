package club.kid7.bannermaker.gui;

import club.kid7.bannermaker.AlphabetBanner;
import club.kid7.bannermaker.BannerMaker;
import club.kid7.bannermaker.PlayerData;
import club.kid7.bannermaker.service.EconomyService;
import club.kid7.bannermaker.service.MessageService;
import club.kid7.bannermaker.util.BannerCost;
import club.kid7.bannermaker.util.BannerPatternLayout;
import club.kid7.bannermaker.util.BannerUtil;
import club.kid7.bannermaker.util.InventoryUtil;
import club.kid7.bannermaker.util.ItemBuilder;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import static club.kid7.bannermaker.configuration.Language.tl;
import static club.kid7.bannermaker.util.TagUtil.tag;

public class BannerInfoGUI {

    /**
     * 開啟旗幟資訊選單，頁碼自第一頁起。
     *
     * @param player 開啟選單的玩家
     * @param banner 要展示的旗幟物品
     */
    public static void open(Player player, ItemStack banner) {
        if (!BannerUtil.isBanner(banner)) {
            return;
        }
        refresh(player, banner, 1);
    }

    /**
     * 以指定旗幟與配方頁碼重新繪製選單。
     * 私有：外部呼叫者一律使用 {@link #open(Player, ItemStack)} 進入；內部於玩家動作後
     * （取得旗幟、購買等）以此重整當前頁面。
     * <p>
     * {@code initialRecipePage} 是進入本次 refresh 時要顯示的頁碼；之後 pagination 按鈕
     * 會改動 closure 內共享的 {@link AtomicInteger} holder，讓「取得旗幟」按鈕在事件
     * 觸發時讀到的是玩家目前實際停留的頁碼、而非進入時的初始頁。
     */
    private static void refresh(Player player, ItemStack banner, int initialRecipePage) {
        if (!BannerUtil.isBanner(banner)) {
            MainMenuGUI.show(player);
            return;
        }

        MessageService messageService = BannerMaker.getInstance().getMessageService();
        PlayerData playerData = BannerMaker.getInstance().getPlayerDataMap().get(player);

        Component titleComponent = tl("gui.title.prefix").append(tl("gui.title.banner-info"));
        String title = LegacyComponentSerializer.legacySection().serialize(titleComponent);
        ChestGui gui = new ChestGui(6, title);
        gui.setOnGlobalClick(event -> event.setCancelled(true));

        StaticPane mainPane = new StaticPane(0, 0, 9, 6);
        gui.addPane(mainPane);

        AtomicInteger currentRecipePage = new AtomicInteger(initialRecipePage);

        renderTopRow(mainPane, player, banner);
        renderGenerateCommandButton(mainPane, player, banner);

        if (BannerUtil.isCraftableInSurvival(banner)) {
            renderCraftingRecipe(gui, mainPane, banner, currentRecipePage);
        }

        renderActionBar(mainPane, player, banner, currentRecipePage, playerData, messageService);

        gui.show(player);
    }

    /**
     * 上方資訊列：Slot 0 旗幟預覽、Slot 1 圖案數量、Slot 2 材料是否充足；若可合成則同時鋪 4x4 材料清單。
     */
    private static void renderTopRow(StaticPane mainPane, Player player, ItemStack banner) {
        // Slot 0: 旗幟預覽
        mainPane.addItem(new GuiItem(banner), 0, 0);

        // Slot 1: 圖案數量
        int patternCount = ((BannerMeta) Objects.requireNonNull(banner.getItemMeta())).numberOfPatterns();
        Component patternCountComp = patternCount > 0
            ? Component.text(patternCount + " ").append(tl("gui.pattern-s"))
            : tl("gui.no-patterns");

        ItemBuilder signBuilder = new ItemBuilder(Material.OAK_SIGN)
            .name(Component.empty().color(NamedTextColor.GREEN).append(patternCountComp));
        if (!BannerUtil.isCraftableInSurvival(banner)) {
            signBuilder.lore(tl(NamedTextColor.RED, "gui.uncraftable"));
        }
        mainPane.addItem(new GuiItem(signBuilder.build()), 1, 0);

        // Slot 2: 材料是否充足 + 材料清單 (Slots 9..39, 4x4)
        if (BannerUtil.isCraftable(player, banner)) {
            ItemStack enoughMaterials = BannerCost.hasEnoughMaterials(player.getInventory(), banner)
                ? new ItemBuilder(Material.OAK_SIGN).name(tl(NamedTextColor.GREEN, "gui.materials.enough")).build()
                : new ItemBuilder(Material.OAK_SIGN).name(tl(NamedTextColor.RED, "gui.materials.not-enough")).build();
            mainPane.addItem(new GuiItem(enoughMaterials), 2, 0);

            List<Integer> materialPositions = Arrays.asList(9, 10, 11, 12, 18, 19, 20, 21, 27, 28, 29, 30, 36, 37, 38, 39);
            List<ItemStack> materialList = BannerCost.getMaterials(banner);
            for (int i = 0; i < materialList.size() && i < materialPositions.size(); i++) {
                int slot = materialPositions.get(i);
                mainPane.addItem(new GuiItem(materialList.get(i)), slot % 9, slot / 9);
            }
        }
    }

    /**
     * Row 0 Slot 3：生成指令按鈕。有 {@code BannerMaker.view} 權限才顯示，否則此 slot 留白。
     * <p>
     * 從原本 Row 5 移上來，騰出 Row 5 給「附近 / 全服展示」拆分後的兩個獨立按鈕使用。
     */
    private static void renderGenerateCommandButton(StaticPane mainPane, Player player, ItemStack banner) {
        if (!player.hasPermission("BannerMaker.view")) {
            return;
        }
        ItemStack btnGenerateCommand = new ItemBuilder(Material.COMMAND_BLOCK)
            .name(tl(NamedTextColor.BLUE, "gui.get-share-command"))
            .build();
        mainPane.addItem(new GuiItem(btnGenerateCommand, event -> {
            BannerMaker.getInstance().getBannerService().sendShareCommand(player, banner);
            player.closeInventory();
            event.setCancelled(true);
        }), 3, 0);
    }

    /**
     * 底部操作列（Row 5）。採「位置固定 + 無權限即灰玻璃」設計：
     * <ul>
     *   <li>slot 0 返回（永遠）</li>
     *   <li>slot 1 灰玻璃（永遠，視覺分隔）</li>
     *   <li>slot 2 刪除（僅當 banner 為個人收藏時）</li>
     *   <li>slot 3 灰玻璃（永遠，視覺分隔）</li>
     *   <li>slot 4 取得（合成 / 免費，依 free 權限切換 icon）</li>
     *   <li>slot 5 購買（僅當無 free 權限且 economy 可用）</li>
     *   <li>slot 6 複製並編輯（永遠）</li>
     *   <li>slot 7 附近展示（僅當有 show.nearby 權限）</li>
     *   <li>slot 8 全服展示（僅當有 show.all 權限）</li>
     * </ul>
     * <p>
     * 任何條件性 slot 沒顯示按鈕時填上同色（gray）玻璃，跟分隔玻璃視覺一致 ── 玩家無從推測該位置
     * 是否潛在有功能、不揭露其他權限的存在。
     */
    private static void renderActionBar(StaticPane mainPane, Player player, ItemStack banner,
                                       AtomicInteger currentRecipePage,
                                       PlayerData playerData, MessageService messageService) {
        // 先把 row 5 全部填灰玻璃，後面個別按鈕條件性覆蓋
        for (int x = 0; x < 9; x++) {
            mainPane.addItem(grayPaneFiller(), x, 5);
        }

        // slot 0: 返回
        putAt(mainPane, 0, 5, new ItemBuilder(Material.RED_WOOL).name(tl(NamedTextColor.RED, "gui.back")).build(), event -> {
            if (AlphabetBanner.isAlphabetBanner(banner)) {
                CreateAlphabetGUI.show(player);
            } else {
                MainMenuGUI.show(player);
            }
            event.setCancelled(true);
        });

        // slot 2: 刪除（若為個人收藏）
        final String key = BannerUtil.getKey(banner);
        if (key != null) {
            putAt(mainPane, 2, 5,
                new ItemBuilder(Material.BARRIER).name(tl(NamedTextColor.RED, "gui.delete")).build(),
                event -> {
                    BannerMaker.getInstance().getBannerRepository().removeBanner(player, key);
                    messageService.send(player, tl(NamedTextColor.GREEN, "io.remove-banner", tag("key", key)));
                    MainMenuGUI.show(player);
                    event.setCancelled(true);
                });
        }

        // slot 4 / slot 5: 取得旗幟
        if (player.hasPermission("BannerMaker.getBanner")) {
            renderGetBannerButtons(mainPane, player, banner, currentRecipePage, messageService);
        }

        // slot 6: 複製並編輯（永遠）
        putAt(mainPane, 6, 5,
            new ItemBuilder(Material.WRITABLE_BOOK).name(tl(NamedTextColor.BLUE, "gui.clone-and-edit")).build(),
            event -> {
                playerData.setCurrentEditBanner(banner);
                CreateBannerGUI.show(player);
                event.setCancelled(true);
            });

        // slot 7 / slot 8: 展示旗幟
        renderShowBannerButtons(mainPane, player, banner);
    }

    /**
     * Row 5 slot 4 / slot 5：取得旗幟。
     * <ul>
     *   <li>有 {@code getBanner.free} 權限：slot 4 = 免費取得；slot 5 維持灰玻璃</li>
     *   <li>否則：slot 4 = 合成取得；slot 5 = 購買（若 economy 可用）</li>
     * </ul>
     * 每個動作獨立按鈕，玩家不再需要透過左右鍵分辨。
     */
    private static void renderGetBannerButtons(StaticPane mainPane, Player player, ItemStack banner,
                                              AtomicInteger currentRecipePage,
                                              MessageService messageService) {
        final String showName = BannerUtil.getName(banner);

        if (player.hasPermission("BannerMaker.getBanner.free")) {
            putAt(mainPane, 4, 5,
                new ItemBuilder(Material.LIME_WOOL).name(tl(NamedTextColor.GREEN, "gui.get-banner-for-free")).build(),
                event -> {
                    InventoryUtil.give(player, banner);
                    messageService.send(player, tl(NamedTextColor.GREEN, "gui.get-banner", tag("name", showName)));
                    refresh(player, banner, currentRecipePage.get());
                    event.setCancelled(true);
                });
            return;
        }

        // 合成
        putAt(mainPane, 4, 5,
            new ItemBuilder(Material.LIME_WOOL).name(tl(NamedTextColor.GREEN, "gui.get-banner-by-craft")).build(),
            event -> {
                boolean success = BannerMaker.getInstance().getBannerService().craft(player, banner);
                if (success) {
                    messageService.send(player, tl(NamedTextColor.GREEN, "gui.get-banner", tag("name", showName)));
                } else {
                    messageService.send(player, tl(NamedTextColor.RED, "gui.materials.not-enough"));
                }
                refresh(player, banner, currentRecipePage.get());
                event.setCancelled(true);
            });

        // 購買（若 economy 可用）
        EconomyService economyService = BannerMaker.getInstance().getEconomyService();
        if (economyService.isAvailable()) {
            double price = economyService.getPrice(banner);
            String priceStr = economyService.format(price);
            putAt(mainPane, 5, 5,
                new ItemBuilder(Material.EMERALD)
                    .name(tl(NamedTextColor.GREEN, "gui.buy-banner-in-price", tag("price", priceStr)))
                    .build(),
                event -> {
                    boolean success = BannerMaker.getInstance().getBannerService().buy(player, banner);
                    if (success) {
                        messageService.send(player, tl(NamedTextColor.GREEN, "gui.get-banner", tag("name", showName)));
                    }
                    refresh(player, banner, currentRecipePage.get());
                    event.setCancelled(true);
                });
        }
    }

    /**
     * Row 5 slot 7 / slot 8：展示旗幟。拆成兩個獨立按鈕、不再依賴左右鍵分歧。
     */
    private static void renderShowBannerButtons(StaticPane mainPane, Player player, ItemStack banner) {
        if (player.hasPermission("BannerMaker.show.nearby")) {
            putAt(mainPane, 7, 5,
                new ItemBuilder(Material.BELL).name(tl(NamedTextColor.BLUE, "gui.show-to-nearby")).build(),
                event -> {
                    BannerMaker.getInstance().getBannerService().showToNearby(player, banner, 16);
                    player.closeInventory();
                    event.setCancelled(true);
                });
        }
        if (player.hasPermission("BannerMaker.show.all")) {
            putAt(mainPane, 8, 5,
                new ItemBuilder(Material.NOTE_BLOCK).name(tl(NamedTextColor.BLUE, "gui.show-to-all")).build(),
                event -> {
                    BannerMaker.getInstance().getBannerService().showToAll(player, banner);
                    player.closeInventory();
                    event.setCancelled(true);
                });
        }
    }

    /**
     * 合成表區塊：邊框、分頁按鈕、工作台/織布機圖示、3x3 材料與成品。
     * 分頁按鈕點擊時更新 holder 內頁碼並遞迴呼叫自身重繪此區塊，不重建整個 GUI。
     * holder 為 {@link #refresh} 內共享的 {@link AtomicInteger}，「取得旗幟」按鈕透過它讀
     * 玩家目前實際停留的頁碼。
     */
    private static void renderCraftingRecipe(ChestGui gui, StaticPane mainPane, ItemStack banner, AtomicInteger currentRecipePage) {
        int page = currentRecipePage.get();
        int patternCount = ((BannerMeta) Objects.requireNonNull(banner.getItemMeta())).numberOfPatterns();
        int totalPage = patternCount + 1;

        // 清除舊的合成表相關物品（在換頁時刷新）
        List<Integer> slotsToClear = Arrays.asList(4, 5, 7, 8, 13, 17, 22, 26, 31, 35, 40, 41, 42, 43, 44, 6, 14, 15, 16, 23, 24, 25, 32, 33, 34, 42);
        for (int slot : slotsToClear) {
            mainPane.removeItem(slot % 9, slot / 9);
        }

        // 邊框
        ItemStack border = new ItemBuilder(Material.BROWN_STAINED_GLASS_PANE).name(" ").build();
        List<Integer> borderPositions = Arrays.asList(4, 5, 7, 8, 13, 17, 22, 26, 31, 35, 40, 41, 42, 43, 44);
        for (int pos : borderPositions) {
            mainPane.addItem(new GuiItem(border.clone()), pos % 9, pos / 9);
        }

        // Slot 22: 上一頁
        if (page > 1) {
            ItemStack prevPage = new ItemBuilder(Material.ARROW).amount(page - 1).name(tl(NamedTextColor.GREEN, "gui.prev-page")).build();
            mainPane.addItem(new GuiItem(prevPage, event -> {
                currentRecipePage.decrementAndGet();
                renderCraftingRecipe(gui, mainPane, banner, currentRecipePage);
                gui.update();
                event.setCancelled(true);
            }), 4, 2);
        }

        // Slot 26: 下一頁
        if (page < totalPage) {
            ItemStack nextPage = new ItemBuilder(Material.ARROW).amount(page + 1).name(tl(NamedTextColor.GREEN, "gui.next-page")).build();
            mainPane.addItem(new GuiItem(nextPage, event -> {
                currentRecipePage.incrementAndGet();
                renderCraftingRecipe(gui, mainPane, banner, currentRecipePage);
                gui.update();
                event.setCancelled(true);
            }), 8, 2);
        }

        // Slot 6: 工作台 / 織布機圖示
        HashMap<Integer, ItemStack> patternRecipe = BannerPatternLayout.getPatternRecipe(banner, page);
        ItemStack workbench = new ItemBuilder(Material.CRAFTING_TABLE).amount(page)
            .name(tl(NamedTextColor.GREEN, "gui.pattern-layout"))
            .lore(tl("gui.recipe-page", tag("page", page), tag("total", totalPage))).build();
        if (BannerPatternLayout.isLoomRecipe(patternRecipe)) {
            workbench.setType(Material.LOOM);
        }
        mainPane.addItem(new GuiItem(workbench), 6, 0);

        // 合成材料與結果（前 9 為材料、第 10 為結果）
        List<Integer> craftPositions = Arrays.asList(14, 15, 16, 23, 24, 25, 32, 33, 34, 42);
        for (int i = 0; i < 10; i++) {
            int position = craftPositions.get(i);
            ItemStack itemStack = patternRecipe.get(i);
            if (itemStack != null && !itemStack.getType().isAir()) {
                mainPane.addItem(new GuiItem(itemStack), position % 9, position / 9);
            }
        }
    }

    // ===== Helpers =====

    /**
     * 將指定 ItemStack 與 click handler 放到 pane 的某個位置，先 remove 既有 item 再 add
     * 以確保覆蓋 row 5 預先填的灰玻璃。
     */
    private static void putAt(StaticPane pane, int x, int y, ItemStack item,
                              java.util.function.Consumer<org.bukkit.event.inventory.InventoryClickEvent> handler) {
        pane.removeItem(x, y);
        pane.addItem(new GuiItem(item, handler::accept), x, y);
    }

    /**
     * 操作列上「無動作」位置的視覺填充：純灰玻璃，displayName 設為單一空格以隱藏 vanilla
     * 預設名（跟合成表 brown glass 邊框同手法），玩家 hover 不會看到「Gray Stained Glass Pane」
     * 這類預設名稱、也無從推測該位置潛在有功能。
     */
    private static GuiItem grayPaneFiller() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).name(" ").build();
        return new GuiItem(pane);
    }
}
