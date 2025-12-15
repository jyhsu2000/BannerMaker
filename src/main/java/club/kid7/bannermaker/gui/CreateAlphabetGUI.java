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
        gui.setOnGlobalClick(event -> event.setCancelled(true));

        StaticPane mainPane = new StaticPane(0, 0, 9, 6);
        gui.addPane(mainPane);

        final AlphabetBanner currentAlphabetBanner = playerData.getCurrentAlphabetBanner();
        if (currentAlphabetBanner == null) {
            ChooseAlphabetGUI.show(player);
            return;
        }

        // Slot 0 (0,0): 預覽
        mainPane.addItem(new GuiItem(currentAlphabetBanner.toItemStack()), 0, 0);

        // 底色選擇 (Slots 1-17, 第 0 和 1 行)
        // 原始邏輯: i + 1 + (i / 8) -> Slot 1-8, 10-17
        for (int i = 0; i < 16; i++) {
            final ItemStack banner = new ItemBuilder(DyeColorRegistry.getBannerMaterial(i)).build();
            int slot = i + 1 + (i / 8);
            mainPane.addItem(new GuiItem(banner, event -> {
                currentAlphabetBanner.baseColor = DyeColorRegistry.getDyeColor(banner.getType());
                playerData.setCurrentAlphabetBanner(currentAlphabetBanner);
                CreateAlphabetGUI.show(player);
                event.setCancelled(true);
            }), slot % 9, slot / 9);
        }

        // 染料顏色選擇 (Slots 19-35, 第 2 和 3 行)
        // 原始邏輯: 18 + i + 1 + (i / 8) -> Slot 19-26, 28-35
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

        // Slot 37 (1,4): 切換邊框
        ItemStack btnBorderedBanner = new ItemBuilder(Material.WHITE_BANNER)
            .name(messageService.formatToString("&a" + tl("gui.toggle-border")))
            .pattern(new Pattern(DyeColor.BLACK, PatternType.BORDER)).build();
        mainPane.addItem(new GuiItem(btnBorderedBanner, event -> {
            currentAlphabetBanner.bordered = !currentAlphabetBanner.bordered;
            playerData.setCurrentAlphabetBanner(currentAlphabetBanner);
            CreateAlphabetGUI.show(player);
            event.setCancelled(true);
        }), 1, 4); // 修正為 (1, 4)

        // Slot 49 (4,5): 旗幟資訊
        ItemStack btnBannerInfo = new ItemBuilder(Material.LIME_WOOL).name(messageService.formatToString("&a" + tl("gui.banner-info"))).build();
        mainPane.addItem(new GuiItem(btnBannerInfo, event -> {
            playerData.setViewInfoBanner(currentAlphabetBanner.toItemStack());
            playerData.setCurrentRecipePage(1);
            BannerInfoGUI.show(player);
            event.setCancelled(true);
        }), 4, 5); // 修正為 (4, 5)

        // Slot 45 (0,5): 返回按鈕
        ItemStack btnBackToMenu = new ItemBuilder(Material.RED_WOOL).name(messageService.formatToString("&c" + tl("gui.back"))).build();
        mainPane.addItem(new GuiItem(btnBackToMenu, event -> {
            ChooseAlphabetGUI.show(player);
            event.setCancelled(true);
        }), 0, 5); // 修正為 (0, 5)

        gui.show(player);
    }
}
