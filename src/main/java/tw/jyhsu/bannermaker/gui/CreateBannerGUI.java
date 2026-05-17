package tw.jyhsu.bannermaker.gui;

import tw.jyhsu.bannermaker.BannerMaker;
import tw.jyhsu.bannermaker.PlayerData;
import tw.jyhsu.bannermaker.registry.DyeColorRegistry;
import tw.jyhsu.bannermaker.service.BannerRepository;
import tw.jyhsu.bannermaker.service.MessageService;
import tw.jyhsu.bannermaker.util.BannerPatternLayout;
import tw.jyhsu.bannermaker.util.ItemBuilder;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

import java.util.Objects;

import static tw.jyhsu.bannermaker.configuration.Language.tl;

public class CreateBannerGUI {

    public static void show(Player player) {
        MessageService messageService = BannerMaker.getInstance().getMessageService();
        PlayerData playerData = BannerMaker.getInstance().getPlayerDataMap().get(player);

        Component titleComponent = tl("gui.title.prefix").append(tl("gui.title.create-banner"));
        String title = LegacyComponentSerializer.legacySection().serialize(titleComponent);
        ChestGui gui = new ChestGui(6, title);
        gui.setOnGlobalClick(event -> event.setCancelled(true));

        StaticPane mainPane = new StaticPane(0, 0, 9, 6);
        gui.addPane(mainPane);

        // 工具列風格：row 5 永遠先填灰玻璃（兩個 stage 都套用），下方按鈕用 GuiUtil.putAt 覆蓋
        GuiUtil.fillToolbarRow(mainPane, 5);

        // slot 0: 返回（兩個 stage 都顯示）
        ItemStack btnBackToMenu = new ItemBuilder(Material.RED_WOOL).name(tl(NamedTextColor.RED, "gui.back")).build();
        GuiUtil.putAt(mainPane, 0, 5, btnBackToMenu, event -> {
            MainMenuGUI.show(player);
            event.setCancelled(true);
        });

        // 取得當前正在編輯的旗幟
        ItemStack currentBanner = playerData.getCurrentEditBanner();

        if (currentBanner == null) {
            // 初次開啟，選擇底色 (使用與顏色選擇相同的佈局邏輯)
            for (int i = 0; i < 16; i++) {
                final int colorIndex = i;
                ItemStack banner = new ItemBuilder(DyeColorRegistry.getBannerMaterial(colorIndex)).build();
                int slot = i + 1 + (i / 8);
                mainPane.addItem(new GuiItem(banner, event -> {
                    playerData.setCurrentEditBanner(banner);
                    CreateBannerGUI.show(player); // 重新開啟以進入編輯模式
                    event.setCancelled(true);
                }), slot % 9, slot / 9);
            }
            gui.show(player);
            return;
        }

        // --- 編輯模式 ---

        // Slot 0 (0,0): 當前編輯中的旗幟
        mainPane.addItem(new GuiItem(currentBanner), 0, 0);

        // Slot 9 (0,1): 圖案過多警告
        if (currentBanner.hasItemMeta() && ((BannerMeta) Objects.requireNonNull(currentBanner.getItemMeta())).numberOfPatterns() > 6) {
            ItemStack warning = new ItemBuilder(Material.OAK_SIGN)
                .name(tl(NamedTextColor.RED, "gui.uncraftable-warning"))
                .lore(tl("gui.more-than-6-patterns")).build();
            mainPane.addItem(new GuiItem(warning), 0, 1);
        }

        // 顏色選擇 (i=0-15)
        // 放置在 Slot 1-8 (Row 0) 和 Slot 10-17 (Row 1)
        for (int i = 0; i < 16; i++) {
            final int colorIndex = i;
            ItemStack dye = new ItemBuilder(DyeColorRegistry.getDyeMaterial(DyeColorRegistry.getDyeColor(colorIndex))).build();
            int slot = i + 1 + (i / 8);
            mainPane.addItem(new GuiItem(dye, event -> {
                playerData.setSelectedColor(DyeColorRegistry.getDyeColor(dye.getType()));
                CreateBannerGUI.show(player); // 重新開啟以刷新圖案
                event.setCancelled(true);
            }), slot % 9, slot / 9);
        }

        // Slot 18 (0,2): 選擇的顏色與預覽模式切換
        DyeColor selectedColor = playerData.getSelectedColor();
        boolean isInSimplePreviewMode = playerData.isInSimplePreviewMode();
        ItemStack previewDye = new ItemBuilder(DyeColorRegistry.getDyeMaterial(selectedColor))
            .name(tl(NamedTextColor.BLUE, "gui.selected-pattern-color"))
            .addLore(Component.text("[", NamedTextColor.YELLOW).append(tl("gui.click.left")).append(Component.text("] ", NamedTextColor.YELLOW)).append(tl(NamedTextColor.GREEN, "gui.toggle-preview-mode"))).build();
        mainPane.addItem(new GuiItem(previewDye, event -> {
            playerData.setInSimplePreviewMode(!isInSimplePreviewMode);
            CreateBannerGUI.show(player); // 重新開啟以刷新圖案
            event.setCancelled(true);
        }), 0, 2);

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

        // 圖案選擇 (i=0-23)
        // 放置在 Slot 19-26 (Row 2), 28-35 (Row 3), 37-44 (Row 4)
        for (int i = 0; i < 24; i++) {
            int patternIndex = i;
            if (playerData.isShowMorePatterns()) {
                patternIndex += 24;
            }
            if (patternIndex >= BannerPatternLayout.getPatternTypeList().size()) {
                break;
            }
            PatternType patternType = BannerPatternLayout.getPatternTypeList().get(patternIndex);
            ItemStack patternItem = new ItemBuilder(baseBannerForPreview.clone())
                .pattern(new Pattern(selectedColorForPreview, patternType)).build();

            int slot = i + 19 + (i / 8);
            mainPane.addItem(new GuiItem(patternItem, event -> {
                BannerMeta currentBm = (BannerMeta) currentBanner.getItemMeta();
                Objects.requireNonNull(currentBm).addPattern(new Pattern(selectedColor, patternType));
                currentBanner.setItemMeta(currentBm);
                playerData.setCurrentEditBanner(currentBanner);
                CreateBannerGUI.show(player); // 重新開啟以反映變更
                event.setCancelled(true);
            }), slot % 9, slot / 9);
        }

        // Row 5 編輯模式專屬按鈕（返回按鈕已在 show 開頭、兩個 stage 共用）

        // slot 2: 重置（刪除當前編輯旗幟）
        ItemStack btnDelete = new ItemBuilder(Material.BARRIER).name(tl(NamedTextColor.RED, "gui.delete")).build();
        GuiUtil.putAt(mainPane, 2, 5, btnDelete, event -> {
            playerData.setCurrentEditBanner(null);
            CreateBannerGUI.show(player); // 重新開啟以回到底色選擇
            event.setCancelled(true);
        });

        // slot 4: 移除上一個圖案（條件性、有 pattern 才顯示）
        if (currentBanner.hasItemMeta() && ((BannerMeta) Objects.requireNonNull(currentBanner.getItemMeta())).numberOfPatterns() > 0) {
            ItemStack btnRemovePattern = new ItemBuilder(Material.BARRIER).name(tl(NamedTextColor.RED, "gui.remove-last-pattern")).build();
            GuiUtil.putAt(mainPane, 4, 5, btnRemovePattern, event -> {
                BannerMeta bm = (BannerMeta) currentBanner.getItemMeta();
                bm.removePattern(bm.numberOfPatterns() - 1);
                currentBanner.setItemMeta(bm);
                playerData.setCurrentEditBanner(currentBanner);
                CreateBannerGUI.show(player); // 重新開啟以反映變更
                event.setCancelled(true);
            });
        }

        // slot 6: 更多圖案
        ItemStack btnMorePattern = new ItemBuilder(Material.NETHER_STAR).name(tl(NamedTextColor.GREEN, "gui.more-patterns")).build();
        GuiUtil.putAt(mainPane, 6, 5, btnMorePattern, event -> {
            playerData.setShowMorePatterns(!playerData.isShowMorePatterns());
            CreateBannerGUI.show(player); // 重新開啟以顯示更多圖案
            event.setCancelled(true);
        });

        // slot 8: 建立 / 儲存旗幟
        ItemStack btnCreate = new ItemBuilder(Material.LIME_WOOL).name(tl(NamedTextColor.GREEN, "gui.create")).build();
        GuiUtil.putAt(mainPane, 8, 5, btnCreate, event -> {
            BannerRepository bannerRepository = BannerMaker.getInstance().getBannerRepository();
            boolean saved = bannerRepository.saveBanner(player, currentBanner);
            if (saved) {
                messageService.send(player, tl(NamedTextColor.GREEN, "io.save-success"));
            } else {
                messageService.send(player, tl(NamedTextColor.RED, "io.save-failed"));
            }
            playerData.setCurrentEditBanner(null);
            MainMenuGUI.show(player); // 返回主選單
            event.setCancelled(true);
        });

        gui.show(player);
    }
}
