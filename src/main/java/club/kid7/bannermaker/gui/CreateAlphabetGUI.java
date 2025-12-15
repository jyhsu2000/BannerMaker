package club.kid7.bannermaker.gui;

import club.kid7.bannermaker.AlphabetBanner;
import club.kid7.bannermaker.BannerMaker;
import club.kid7.bannermaker.PlayerData;
import club.kid7.bannermaker.registry.DyeColorRegistry;
import club.kid7.bannermaker.service.MessageService;
import club.kid7.bannermaker.util.ItemBuilder;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static club.kid7.bannermaker.configuration.Language.tl;

public class CreateAlphabetGUI {

    public static void show(Player player) {
        MessageService messageService = BannerMaker.getInstance().getMessageService();
        PlayerData playerData = BannerMaker.getInstance().playerDataMap.get(player);

        String title = messageService.formatToString(tl("gui.prefix") + tl("gui.alphabet-and-number"));
        ChestGui gui = new ChestGui(6, title);
        StaticPane mainPane = new StaticPane(0, 0, 9, 6);
        gui.addPane(mainPane);

        final AlphabetBanner currentAlphabetBanner = playerData.getCurrentAlphabetBanner();
        if (currentAlphabetBanner == null) {
            ChooseAlphabetGUI.show(player);
            return;
        }

        // Slot 0: 預覽
        mainPane.addItem(new GuiItem(currentAlphabetBanner.toItemStack()), 0, 0);

        // 底色選擇 (Slots 1-17, 大約是第 0 和 1 行)
        // 原始邏輯: i + 1 + (i / 8) -> 1, 2, ..., 8, 10, 11, ...
        // 我們將其映射到整潔的佈局。
        // 讓我們使用 Slot 1-8 (第 0 行) 和 9-16 (第 1 行) 作為底色選擇區
        for (int i = 0; i < 16; i++) {
            final ItemStack banner = new ItemBuilder(DyeColorRegistry.getBannerMaterial(i)).build();
            // 原始公式映射:
            // i=0 -> slot 1 (1,0)
            // i=7 -> slot 8 (8,0)
            // i=8 -> slot 10 (1,1)
            // i=15 -> slot 17 (8,1)
            // 這裡我們重現這個邏輯以保持熟悉感。
            int slot = i + 1 + (i / 8);
            mainPane.addItem(new GuiItem(banner, event -> {
                currentAlphabetBanner.baseColor = DyeColorRegistry.getDyeColor(banner.getType());
                playerData.setCurrentAlphabetBanner(currentAlphabetBanner);
                CreateAlphabetGUI.show(player);
                event.setCancelled(true);
            }), slot % 9, slot / 9);
        }

        // 染料顏色選擇 (Slots 19-35, 大約是第 2 和 3 行)
        // 原始邏輯: 18 + i + 1 + (i / 8)
        // i=0 -> 19 (1,2)
        // i=7 -> 26 (8,2)
        // i=8 -> 28 (1,3)
        // i=15 -> 35 (8,3)
        for (int i = 0; i < 16; i++) {
            final ItemStack dye = new ItemBuilder(DyeColorRegistry.getDyeMaterial(i)).build();
            int slot = 18 + i + 1 + (i / 8);
            mainPane.addItem(new GuiItem(dye, event -> {
                currentAlphabetBanner.dyeColor = DyeColorRegistry.getDyeColor(dye.getType());
                playerData.setCurrentAlphabetBanner(currentAlphabetBanner);
                CreateAlphabetGUI.show(player);
                event.setCancelled(true);
            }), slot % 9, slot / 9);
        }

        // Slot 37: 切換邊框
        ItemStack btnBorderedBanner = new ItemBuilder(Material.WHITE_BANNER)
            .name(messageService.formatToString("&a" + tl("gui.toggle-border")))
            .pattern(new Pattern(DyeColor.BLACK, PatternType.BORDER)).build();
        mainPane.addItem(new GuiItem(btnBorderedBanner, event -> {
            currentAlphabetBanner.bordered = !currentAlphabetBanner.bordered;
            playerData.setCurrentAlphabetBanner(currentAlphabetBanner);
            CreateAlphabetGUI.show(player);
            event.setCancelled(true);
        }), 1, 4); // Slot 37 (1,4)

        // Slot 49: 旗幟資訊
        ItemStack btnBannerInfo = new ItemBuilder(Material.LIME_WOOL).name(messageService.formatToString("&a" + tl("gui.banner-info"))).build();
        mainPane.addItem(new GuiItem(btnBannerInfo, event -> {
            playerData.setViewInfoBanner(currentAlphabetBanner.toItemStack());
            playerData.setCurrentRecipePage(1);
            BannerInfoGUI.show(player);
            event.setCancelled(true);
        }), 4, 5); // Slot 49

        // Slot 45: 返回按鈕
        ItemStack btnBackToMenu = new ItemBuilder(Material.RED_WOOL).name(messageService.formatToString("&c" + tl("gui.back"))).build();
        mainPane.addItem(new GuiItem(btnBackToMenu, event -> {
            ChooseAlphabetGUI.show(player);
            event.setCancelled(true);
        }), 0, 5); // Slot 45

        gui.show(player);
    }
}