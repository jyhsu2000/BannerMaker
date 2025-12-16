package club.kid7.bannermaker.gui;

import club.kid7.bannermaker.AlphabetBanner;
import club.kid7.bannermaker.BannerMaker;
import club.kid7.bannermaker.PlayerData;
import club.kid7.bannermaker.service.MessageService;
import club.kid7.bannermaker.util.BannerUtil;
import club.kid7.bannermaker.util.EconUtil;
import club.kid7.bannermaker.util.IOUtil;
import club.kid7.bannermaker.util.InventoryUtil;
import club.kid7.bannermaker.util.ItemBuilder;
import club.kid7.bannermaker.util.MessageComponentUtil;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static club.kid7.bannermaker.configuration.Language.tl;

public class BannerInfoGUI {

    public static void show(Player player) {
        MessageService messageService = BannerMaker.getInstance().getMessageService();
        PlayerData playerData = BannerMaker.getInstance().playerDataMap.get(player);

        final ItemStack banner = playerData.getViewInfoBanner();

        if (!BannerUtil.isBanner(banner)) {
            MainMenuGUI.show(player);
            return;
        }

        Component titleComponent = tl("gui.prefix").append(tl("gui.banner-info"));
        String title = LegacyComponentSerializer.legacySection().serialize(titleComponent);
        ChestGui gui = new ChestGui(6, title);
        gui.setOnGlobalClick(event -> event.setCancelled(true));

        // 主面板 (Main Pane)
        StaticPane mainPane = new StaticPane(0, 0, 9, 6);
        gui.addPane(mainPane);

        // Slot 0 (0,0): 旗幟預覽
        mainPane.addItem(new GuiItem(banner), 0, 0);

        // Slot 1 (1,0): 圖案數量
        int patternCount = ((BannerMeta) Objects.requireNonNull(banner.getItemMeta())).numberOfPatterns();
        Component patternCountComp;
        if (patternCount > 0) {
            patternCountComp = Component.text(patternCount + " ").append(tl("gui.pattern-s"));
        } else {
            patternCountComp = tl("gui.no-patterns");
        }
        ItemStack signPatternCount;
        if (BannerUtil.isCraftableInSurvival(banner)) {
            signPatternCount = new ItemBuilder(Material.OAK_SIGN).name(Component.empty().color(NamedTextColor.GREEN).append(patternCountComp)).build();
        } else {
            signPatternCount = new ItemBuilder(Material.OAK_SIGN)
                .name(Component.empty().color(NamedTextColor.GREEN).append(patternCountComp))
                .lore(tl(NamedTextColor.RED, "gui.uncraftable")).build();
        }
        mainPane.addItem(new GuiItem(signPatternCount), 1, 0);

        // Slot 2 (2,0): 材料是否充足 (若可合成)
        if (BannerUtil.isCraftable(player, banner)) {
            ItemStack enoughMaterials;
            if (BannerUtil.hasEnoughMaterials(player.getInventory(), banner)) {
                enoughMaterials = new ItemBuilder(Material.OAK_SIGN).name(tl(NamedTextColor.GREEN, "gui.materials.enough")).build();
            } else {
                enoughMaterials = new ItemBuilder(Material.OAK_SIGN).name(tl(NamedTextColor.RED, "gui.materials.not-enough")).build();
            }
            mainPane.addItem(new GuiItem(enoughMaterials), 2, 0);

            // 材料清單 (Slots 9, 10, 11, 12, 18, 19, 20, 21, 27, 28, 29, 30, 36, 37, 38, 39)
            List<Integer> materialPositions = Arrays.asList(9, 10, 11, 12, 18, 19, 20, 21, 27, 28, 29, 30, 36, 37, 38, 39);
            List<ItemStack> materialList = BannerUtil.getMaterials(banner);
            for (int i = 0; i < materialList.size() && i < materialPositions.size(); i++) {
                // x = slot % 9, y = slot / 9
                mainPane.addItem(new GuiItem(materialList.get(i)), materialPositions.get(i) % 9, materialPositions.get(i) / 9);
            }
        }

        // 合成表 (複雜區塊，需要手動處理分頁)
        if (BannerUtil.isCraftableInSurvival(banner)) {
            updateCraftingRecipeSection(player, gui, mainPane, playerData, banner, messageService);
        }

        // 功能按鈕 (底部行)

        // Slot 45 (0,5): 返回按鈕
        ItemStack btnBackToMenu = new ItemBuilder(Material.RED_WOOL).name(tl(NamedTextColor.RED, "gui.back")).build();
        mainPane.addItem(new GuiItem(btnBackToMenu, event -> {
            if (AlphabetBanner.isAlphabetBanner(banner)) {
                CreateAlphabetGUI.show(player);
            } else {
                MainMenuGUI.show(player);
            }
            event.setCancelled(true);
        }), 0, 5); // 修正為 (0, 5)

        // Slot 47 (2,5): 刪除旗幟 (若已儲存)
        final String key = BannerUtil.getKey(banner);
        if (key != null) {
            ItemStack btnDelete = new ItemBuilder(Material.BARRIER).name(tl(NamedTextColor.RED, "gui.delete")).build();
            mainPane.addItem(new GuiItem(btnDelete, event -> {
                IOUtil.removeBanner(player, key);
                MainMenuGUI.show(player);
                event.setCancelled(true);
            }), 2, 5); // 修正為 (2, 5)
        }

        // Slot 49 (4,5): 取得旗幟
        if (player.hasPermission("BannerMaker.getBanner")) {
            ItemStack btnGetBanner = new ItemBuilder(Material.LIME_WOOL).name(tl(NamedTextColor.GREEN, "gui.get-this-banner")).build();
            final String showName = BannerUtil.getName(banner);

            if (player.hasPermission("BannerMaker.getBanner.free")) {
                btnGetBanner = new ItemBuilder(btnGetBanner).addLore(Component.text("[", NamedTextColor.YELLOW).append(tl("gui.click.left").append(Component.text("] ", NamedTextColor.YELLOW)).append(tl(NamedTextColor.GREEN, "gui.get-banner-for-free")))).build();
                mainPane.addItem(new GuiItem(btnGetBanner, event -> {
                    InventoryUtil.give(player, banner);
                    messageService.send(player, tl(NamedTextColor.GREEN, "gui.get-banner", showName));
                    MainMenuGUI.show(player); // 動作完成後返回主選單
                    event.setCancelled(true);
                }), 4, 5); // 修正為 (4, 5)
            } else {
                btnGetBanner = new ItemBuilder(btnGetBanner).addLore(Component.text("[", NamedTextColor.YELLOW).append(tl("gui.click.left").append(Component.text("] ", NamedTextColor.YELLOW)).append(tl(NamedTextColor.GREEN, "gui.get-banner-by-craft")))).build();
                if (BannerMaker.getInstance().econ != null) {
                    double price = EconUtil.getPrice(banner);
                    String priceStr = BannerMaker.getInstance().econ.format(price);
                    btnGetBanner = new ItemBuilder(btnGetBanner).addLore(Component.text("[", NamedTextColor.YELLOW).append(tl("gui.click.right")).append(Component.text("] ", NamedTextColor.YELLOW)).append(tl(NamedTextColor.GREEN, "gui.buy-banner-in-price", priceStr))).build();
                }

                mainPane.addItem(new GuiItem(btnGetBanner, event -> {
                    if (event.getClick().isLeftClick()) {
                        boolean success = BannerUtil.craft(player, banner);
                        if (success) {
                            messageService.send(player, tl(NamedTextColor.GREEN, "gui.get-banner", showName));
                        } else {
                            messageService.send(player, tl(NamedTextColor.RED, "gui.materials.not-enough"));
                        }
                    } else if (event.getClick().isRightClick() && BannerMaker.getInstance().econ != null) {
                        boolean success = BannerUtil.buy(player, banner);
                        if (success) {
                            messageService.send(player, tl(NamedTextColor.GREEN, "gui.get-banner", showName));
                        }
                    }
                    MainMenuGUI.show(player); // 動作完成後返回主選單
                    event.setCancelled(true);
                }), 4, 5); // 修正為 (4, 5)
            }
        }

        // Slot 51 (6,5): 複製並編輯
        ItemStack btnCloneAndEdit = new ItemBuilder(Material.WRITABLE_BOOK).name(tl(NamedTextColor.BLUE, "gui.clone-and-edit")).build();
        mainPane.addItem(new GuiItem(btnCloneAndEdit, event -> {
            playerData.setCurrentEditBanner(banner);
            CreateBannerGUI.show(player); // 開啟新版編輯介面
            event.setCancelled(true);
        }), 6, 5); // 修正為 (6, 5)

        // Slot 52 (7,5): 展示旗幟
        if (player.hasPermission("BannerMaker.show.nearby") || player.hasPermission("BannerMaker.show.all")) {
            ItemStack btnShow = new ItemBuilder(Material.BELL).name(Component.text("Show banner to players", NamedTextColor.BLUE)).build();
            if (player.hasPermission("BannerMaker.show.nearby")) {
                btnShow = new ItemBuilder(btnShow).addLore(Component.text("[", NamedTextColor.YELLOW).append(tl("gui.click.left")).append(Component.text("] ", NamedTextColor.YELLOW)).append(Component.text("Show to nearby players", NamedTextColor.GREEN))).build();
            }
            if (player.hasPermission("BannerMaker.show.all")) {
                btnShow = new ItemBuilder(btnShow).addLore(Component.text("[", NamedTextColor.YELLOW).append(tl("gui.click.right")).append(Component.text("] ", NamedTextColor.YELLOW)).append(Component.text("Show to all players", NamedTextColor.GREEN))).build();
            }
            mainPane.addItem(new GuiItem(btnShow, event -> {
                String bannerString = BannerUtil.serialize(banner);
                Component msgBannerName = MessageComponentUtil.getTranslatableComponent(banner)
                    .hoverEvent(MessageComponentUtil.getHoverEventItem(banner)) // 直接使用 MessageComponentUtil 返回的 Adventure HoverEvent
                    .clickEvent(ClickEvent.runCommand("/bm view " + bannerString));

                if (event.getClick().isLeftClick() && player.hasPermission("BannerMaker.show.nearby")) {
                    double maxDistance = 16;
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if (!p.hasPermission("BannerMaker.show.receive") && !p.equals(player)) {
                            continue;
                        }
                        if (p.getWorld() != player.getWorld()) {
                            continue;
                        }
                        if (p.getLocation().distanceSquared(player.getLocation()) > maxDistance * maxDistance) {
                            continue;
                        }
                        // 修正：使用 messageService.send
                        messageService.send(p, Component.text(player.getDisplayName()).color(NamedTextColor.YELLOW)
                            .append(Component.text(" shows you the banner ").color(NamedTextColor.GRAY))
                            .append(msgBannerName)
                            .append(Component.text(" (Click to view)").color(NamedTextColor.DARK_GRAY)));
                    }
                } else if (event.getClick().isRightClick() && player.hasPermission("BannerMaker.show.all")) {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if (!p.hasPermission("BannerMaker.show.receive") && !p.equals(player)) {
                            continue;
                        }
                        // 修正：使用 messageService.send
                        messageService.send(p, Component.text(player.getDisplayName()).color(NamedTextColor.YELLOW)
                            .append(Component.text(" shows you the banner ").color(NamedTextColor.GRAY))
                            .append(msgBannerName)
                            .append(Component.text(" (Click to view)").color(NamedTextColor.DARK_GRAY)));
                    }
                }
                player.closeInventory();
                event.setCancelled(true);
            }), 7, 5); // 修正為 (7, 5)
        }

        // Slot 53 (8,5): 生成指令
        if (player.hasPermission("BannerMaker.view")) {
            ItemStack btnGenerateCommand = new ItemBuilder(Material.COMMAND_BLOCK).name(Component.text("Get share command", NamedTextColor.BLUE)).build();
            mainPane.addItem(new GuiItem(btnGenerateCommand, event -> {
                String bannerString = BannerUtil.serialize(banner);
                Component msg = Component.text("[Click here to copy command to clipboard]")
                    .hoverEvent(HoverEvent.showText(Component.text("Copy command to clipboard")))
                    .clickEvent(ClickEvent.copyToClipboard("/bm view " + bannerString));
                // 修正：使用 messageService.send
                messageService.send(player, msg);
                player.closeInventory();
                event.setCancelled(true);
            }), 8, 5); // 修正為 (8, 5)
        }

        gui.show(player);
    }

    private static void updateCraftingRecipeSection(Player player, ChestGui gui, StaticPane mainPane, PlayerData playerData, ItemStack banner, MessageService messageService) {
        int patternCount = ((BannerMeta) Objects.requireNonNull(banner.getItemMeta())).numberOfPatterns();
        final int currentRecipePage = playerData.getCurrentRecipePage();
        int totalPage = patternCount + 1; // 基礎旗幟 + 圖案

        // 清除舊的合成表相關物品 (在換頁時刷新)
        // Slots 4,5,7,8 (row 0), 13,17 (row 1), 22,26 (row 2), 31,35 (row 3), 40,41,42,43,44 (row 4) 用於邊框
        // Slots 14,15,16,23,24,25,32,33,34 用於合成表材料
        // Slot 42 用於合成結果
        // Slot 6 用於工作台圖示
        List<Integer> slotsToClear = Arrays.asList(4, 5, 7, 8, 13, 17, 22, 26, 31, 35, 40, 41, 42, 43, 44, 6, 14, 15, 16, 23, 24, 25, 32, 33, 34, 42);
        for (int slot : slotsToClear) {
            mainPane.removeItem(slot % 9, slot / 9);
        }

        // 邊框 (Border)
        ItemStack border = new ItemBuilder(Material.BROWN_STAINED_GLASS_PANE).name(" ").build();
        List<Integer> borderPositions = Arrays.asList(4, 5, 7, 8, 13, 17, 22, 26, 31, 35, 40, 41, 42, 43, 44);
        for (int pos : borderPositions) {
            mainPane.addItem(new GuiItem(border.clone()), pos % 9, pos / 9);
        }

        // Slot 22 (4,2): 上一頁按鈕
        if (currentRecipePage > 1) {
            ItemStack prevPage = new ItemBuilder(Material.ARROW).amount(currentRecipePage - 1).name(tl(NamedTextColor.GREEN, "gui.prev-page")).build();
            mainPane.addItem(new GuiItem(prevPage, event -> {
                playerData.setCurrentRecipePage(currentRecipePage - 1);
                updateCraftingRecipeSection(player, gui, mainPane, playerData, banner, messageService);
                gui.update();
                event.setCancelled(true);
            }), 4, 2); // 修正為 (4, 2)
        }

        // Slot 26 (8,2): 下一頁按鈕
        if (currentRecipePage < totalPage) {
            ItemStack nextPage = new ItemBuilder(Material.ARROW).amount(currentRecipePage + 1).name(tl(NamedTextColor.GREEN, "gui.next-page")).build();
            mainPane.addItem(new GuiItem(nextPage, event -> {
                playerData.setCurrentRecipePage(currentRecipePage + 1);
                updateCraftingRecipeSection(player, gui, mainPane, playerData, banner, messageService);
                gui.update();
                event.setCancelled(true);
            }), 8, 2); // 修正為 (8, 2)
        }

        // Slot 6 (6,0): 合成表工作台/織布機圖示
        HashMap<Integer, ItemStack> patternRecipe = BannerUtil.getPatternRecipe(banner, currentRecipePage);
        ItemStack workbench = new ItemBuilder(Material.CRAFTING_TABLE).amount(currentRecipePage)
            .name(tl(NamedTextColor.GREEN, "gui.craft-recipe"))
            .lore(Component.text("(" + currentRecipePage + "/" + totalPage + ")")).build();
        if (BannerUtil.isLoomRecipe(patternRecipe)) {
            workbench.setType(Material.LOOM);
        }
        mainPane.addItem(new GuiItem(workbench), 6, 0); // 修正為 (6, 0)

        // 合成材料與結果 (Slots 14, 15, 16, 23, 24, 25, 32, 33, 34, 42)
        // 0-8 為材料，9 為結果
        List<Integer> craftPositions = Arrays.asList(14, 15, 16, 23, 24, 25, 32, 33, 34, 42);
        for (int i = 0; i < 10; i++) { // 9 個材料 + 1 個結果 = 10 個項目
            int position = craftPositions.get(i);
            ItemStack itemStack = patternRecipe.get(i);
            if (itemStack != null && !itemStack.getType().isAir()) {
                mainPane.addItem(new GuiItem(itemStack), position % 9, position / 9);
            }
        }
    }
}
