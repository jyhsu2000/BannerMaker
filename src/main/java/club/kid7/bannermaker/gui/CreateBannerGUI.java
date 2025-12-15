package club.kid7.bannermaker.gui;

import club.kid7.bannermaker.BannerMaker;
import club.kid7.bannermaker.PlayerData;
import club.kid7.bannermaker.registry.DyeColorRegistry;
import club.kid7.bannermaker.service.MessageService;
import club.kid7.bannermaker.util.BannerUtil;
import club.kid7.bannermaker.util.IOUtil;
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
import org.bukkit.inventory.meta.BannerMeta;

import java.util.Objects;

import static club.kid7.bannermaker.configuration.Language.tl;

public class CreateBannerGUI {

    public static void show(Player player) {
        MessageService messageService = BannerMaker.getInstance().getMessageService();
        PlayerData playerData = BannerMaker.getInstance().playerDataMap.get(player);

        String title = messageService.formatToString(tl("gui.prefix") + tl("gui.create-banner"));
        ChestGui gui = new ChestGui(6, title);

        StaticPane mainPane = new StaticPane(0, 0, 9, 6);
        gui.addPane(mainPane);

        // Slot 45: 返回按鈕
        ItemStack btnBackToMenu = new ItemBuilder(Material.RED_WOOL).name(messageService.formatToString("&c" + tl("gui.back"))).build();
        mainPane.addItem(new GuiItem(btnBackToMenu, event -> {
            MainMenuGUI.show(player);
            event.setCancelled(true);
        }), 0, 5); // Slot 45 (0-indexed 座標為 0,5)

        // 取得當前正在編輯的旗幟
        ItemStack currentBanner = playerData.getCurrentEditBanner();

        if (currentBanner == null) {
            // 初次開啟，選擇底色
            for (int i = 0; i < 16; i++) {
                final int colorIndex = i;
                ItemStack banner = new ItemBuilder(DyeColorRegistry.getBannerMaterial(colorIndex)).build();
                mainPane.addItem(new GuiItem(banner, event -> {
                    playerData.setCurrentEditBanner(banner);
                    CreateBannerGUI.show(player); // 重新開啟以進入編輯模式
                    event.setCancelled(true);
                }), (i % 9), (i / 9) + 1); // 從第二行開始
            }
            gui.show(player);
            return;
        }

        // --- 編輯模式 ---

        // Slot 0: 當前編輯中的旗幟
        mainPane.addItem(new GuiItem(currentBanner), 0, 0);

        // Slot 9: 圖案過多警告
        if (currentBanner.hasItemMeta() && ((BannerMeta) Objects.requireNonNull(currentBanner.getItemMeta())).numberOfPatterns() > 6) {
            ItemStack warning = new ItemBuilder(Material.OAK_SIGN)
                .name(messageService.formatToString("&c" + tl("gui.uncraftable-warning")))
                .lore(messageService.formatToString(tl("gui.more-than-6-patterns"))).build();
            mainPane.addItem(new GuiItem(warning), 0, 1); // Slot 9
        }

        // 顏色選擇 (第 2 和 3 行)
        for (int i = 0; i < 16; i++) {
            final int colorIndex = i;
            ItemStack dye = new ItemBuilder(DyeColorRegistry.getDyeMaterial(DyeColorRegistry.getDyeColor(colorIndex))).build();
            mainPane.addItem(new GuiItem(dye, event -> {
                playerData.setSelectedColor(DyeColorRegistry.getDyeColor(dye.getType()));
                CreateBannerGUI.show(player); // 重新開啟以刷新圖案
                event.setCancelled(true);
            }), (i % 9), (i / 9) + 2); // 第 2 和 3 行
        }

        // 選擇的顏色與預覽模式切換 (Slot 18)
        DyeColor selectedColor = playerData.getSelectedColor();
        boolean isInSimplePreviewMode = playerData.isInSimplePreviewMode();
        ItemStack previewDye = new ItemBuilder(DyeColorRegistry.getDyeMaterial(selectedColor))
            .name(messageService.formatToString("&9" + tl("gui.selected-pattern-color")))
            .lore(messageService.formatToString("&e[" + tl("gui.click.left") + "] &a" + tl("gui.toggle-preview-mode"))).build();
        mainPane.addItem(new GuiItem(previewDye, event -> {
            playerData.setInSimplePreviewMode(!isInSimplePreviewMode);
            CreateBannerGUI.show(player); // 重新開啟以刷新圖案
            event.setCancelled(true);
        }), 0, 2); // Slot 18

        // 圖案預覽邏輯
        final ItemStack baseBannerForPreview;
        final DyeColor selectedColorForPreview;
        if (isInSimplePreviewMode) {
            baseBannerForPreview = new ItemBuilder(Material.WHITE_BANNER).build();
            selectedColorForPreview = DyeColor.BLACK;
        } else {
            baseBannerForPreview = new ItemBuilder(DyeColorRegistry.getBannerMaterial(currentBanner.getType())).build();
            selectedColorForPreview = selectedColor;
        }

        // 圖案選擇 (第 3, 4, 5 行)
        for (int i = 0; i < 24; i++) {
            int patternIndex = i;
            if (playerData.isShowMorePatterns()) {
                patternIndex += 24;
            }
            if (patternIndex >= BannerUtil.getPatternTypeList().size()) {
                break;
            }
            PatternType patternType = BannerUtil.getPatternTypeList().get(patternIndex);
            ItemStack patternItem = new ItemBuilder(baseBannerForPreview.clone())
                .pattern(new Pattern(selectedColorForPreview, patternType)).build();

            mainPane.addItem(new GuiItem(patternItem, event -> {
                BannerMeta currentBm = (BannerMeta) currentBanner.getItemMeta();
                Objects.requireNonNull(currentBm).addPattern(new Pattern(selectedColor, patternType));
                currentBanner.setItemMeta(currentBm);
                playerData.setCurrentEditBanner(currentBanner);
                CreateBannerGUI.show(player); // 重新開啟以反映變更
                event.setCancelled(true);
            }), (i % 9), (i / 9) + 3); // 第 3, 4, 5 行
        }

        // Slot 51: 更多圖案按鈕
        ItemStack btnMorePattern = new ItemBuilder(Material.NETHER_STAR).name(messageService.formatToString("&a" + tl("gui.more-patterns"))).build();
        mainPane.addItem(new GuiItem(btnMorePattern, event -> {
            playerData.setShowMorePatterns(!playerData.isShowMorePatterns());
            CreateBannerGUI.show(player); // 重新開啟以顯示更多圖案
            event.setCancelled(true);
        }), 6, 5); // Slot 51

        // Slot 53: 建立/儲存旗幟
        ItemStack btnCreate = new ItemBuilder(Material.LIME_WOOL).name(messageService.formatToString("&a" + tl("gui.create"))).build();
        mainPane.addItem(new GuiItem(btnCreate, event -> {
            IOUtil.saveBanner(player, currentBanner);
            playerData.setCurrentEditBanner(null);
            MainMenuGUI.show(player); // 返回主選單
            event.setCancelled(true);
        }), 8, 5); // Slot 53

        // Slot 47: 刪除當前編輯旗幟
        ItemStack btnDelete = new ItemBuilder(Material.BARRIER).name(messageService.formatToString("&c" + tl("gui.delete"))).build();
        mainPane.addItem(new GuiItem(btnDelete, event -> {
            playerData.setCurrentEditBanner(null);
            CreateBannerGUI.show(player); // 重新開啟以回到底色選擇
            event.setCancelled(true);
        }), 2, 5); // Slot 47

        // Slot 49: 移除上一個圖案
        if (currentBanner.hasItemMeta() && ((BannerMeta) Objects.requireNonNull(currentBanner.getItemMeta())).numberOfPatterns() > 0) {
            ItemStack btnRemovePattern = new ItemBuilder(Material.BARRIER).name(messageService.formatToString("&c" + tl("gui.remove-last-pattern"))).build();
            mainPane.addItem(new GuiItem(btnRemovePattern, event -> {
                BannerMeta bm = (BannerMeta) currentBanner.getItemMeta();
                bm.removePattern(bm.numberOfPatterns() - 1);
                currentBanner.setItemMeta(bm);
                playerData.setCurrentEditBanner(currentBanner);
                CreateBannerGUI.show(player); // 重新開啟以反映變更
                event.setCancelled(true);
            }), 4, 5); // Slot 49
        }

        gui.show(player);
    }
}
