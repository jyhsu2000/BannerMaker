package tw.jyhsu.bannermaker.gui;

import tw.jyhsu.bannermaker.BannerMaker;
import tw.jyhsu.bannermaker.state.PlayerData;
import tw.jyhsu.bannermaker.registry.DyeColorRegistry;
import tw.jyhsu.bannermaker.service.BannerRepository;
import tw.jyhsu.bannermaker.service.MessageService;
import tw.jyhsu.bannermaker.banner.BannerPatternLayout;
import tw.jyhsu.bannermaker.util.ItemBuilder;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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

    /**
     * 圖案選擇區一頁可容納的數量（row 2-4 共 3 row、每 row 8 格跳過末欄 = 24）；同時也是 more-patterns
     * 按下後切到第二頁時的 index offset。
     */
    private static final int PATTERNS_PER_PAGE = 24;

    public static void show(Player player) {
        MessageService messageService = BannerMaker.getInstance().getMessageService();
        PlayerData playerData = BannerMaker.getInstance().getPlayerDataMap().get(player);

        ChestGui gui = GuiUtil.createChestGui("gui.title.create-banner");
        StaticPane mainPane = new StaticPane(0, 0, 9, 6);
        gui.addPane(mainPane);

        // 工具列風格：row 5 永遠先填灰玻璃（兩個 stage 都套用），之後 row 5 的按鈕用 GuiUtil.putAt 覆蓋
        GuiUtil.fillToolbarRow(mainPane, 5);

        // slot 0: 返回（兩個 stage 都顯示）
        ItemStack btnBackToMenu = new ItemBuilder(Material.RED_WOOL).name(tl(NamedTextColor.RED, "gui.back")).build();
        GuiUtil.putAt(mainPane, 0, 5, btnBackToMenu, event -> {
            MainMenuGUI.show(player);
        });

        ItemStack currentBanner = playerData.getCurrentEditBanner();
        if (currentBanner == null) {
            renderBaseColorPicker(mainPane, playerData, player);
        } else {
            renderEditMode(mainPane, playerData, currentBanner, player, messageService);
        }

        gui.show(player);
    }

    /**
     * Stage 1：尚未選底色時，row 0-1 給玩家挑 16 種底色 banner。
     */
    private static void renderBaseColorPicker(StaticPane mainPane, PlayerData playerData, Player player) {
        for (int i = 0; i < 16; i++) {
            ItemStack banner = new ItemBuilder(DyeColorRegistry.getBannerMaterial(i)).build();
            int slot = GuiUtil.gridSlot(1, i);
            mainPane.addItem(new GuiItem(banner, event -> {
                playerData.setCurrentEditBanner(banner);
                CreateBannerGUI.show(player); // 重新開啟以進入編輯模式
            }), slot % 9, slot / 9);
        }
    }

    /**
     * Stage 2：已選底色後的完整編輯介面。
     */
    private static void renderEditMode(StaticPane mainPane, PlayerData playerData, ItemStack currentBanner,
                                       Player player, MessageService messageService) {
        renderBannerPreviewAndWarning(mainPane, currentBanner);
        renderDyeColorPicker(mainPane, playerData, player);
        renderPreviewToggle(mainPane, playerData, player);
        renderPatternPicker(mainPane, playerData, currentBanner, player);
        renderEditActionBar(mainPane, playerData, currentBanner, player, messageService);
    }

    /**
     * Slot 0 預覽、Slot 9 圖案 > 6 的「不可合成」警告（僅警告觸發時顯示）。
     */
    private static void renderBannerPreviewAndWarning(StaticPane mainPane, ItemStack currentBanner) {
        mainPane.addItem(new GuiItem(currentBanner), 0, 0);

        if (currentBanner.hasItemMeta() && ((BannerMeta) Objects.requireNonNull(currentBanner.getItemMeta())).numberOfPatterns() > 6) {
            ItemStack warning = new ItemBuilder(Material.OAK_SIGN)
                .name(tl(NamedTextColor.RED, "gui.uncraftable-warning"))
                .lore(tl("gui.more-than-6-patterns")).build();
            mainPane.addItem(new GuiItem(warning), 0, 1);
        }
    }

    /**
     * Row 0/1 slot 1-8 + 10-17：16 種染料 —— 點擊後設為當前 selected pattern color。
     */
    private static void renderDyeColorPicker(StaticPane mainPane, PlayerData playerData, Player player) {
        for (int i = 0; i < 16; i++) {
            ItemStack dye = new ItemBuilder(DyeColorRegistry.getDyeMaterial(DyeColorRegistry.getDyeColor(i))).build();
            int slot = GuiUtil.gridSlot(1, i);
            mainPane.addItem(new GuiItem(dye, event -> {
                playerData.setSelectedColor(DyeColorRegistry.getDyeColor(dye.getType()));
                CreateBannerGUI.show(player); // 重新開啟以刷新圖案
            }), slot % 9, slot / 9);
        }
    }

    /**
     * Slot 18：顯示當前選的染料色，點擊切換 simple-preview-mode（白底黑 pattern 易讀，
     * 否則用實際底色）。
     */
    private static void renderPreviewToggle(StaticPane mainPane, PlayerData playerData, Player player) {
        DyeColor selectedColor = playerData.getSelectedColor();
        boolean isInSimplePreviewMode = playerData.isInSimplePreviewMode();
        ItemStack previewDye = new ItemBuilder(DyeColorRegistry.getDyeMaterial(selectedColor))
            .name(tl(NamedTextColor.BLUE, "gui.selected-pattern-color"))
            .addLore(Component.text("[", NamedTextColor.YELLOW).append(tl("gui.click.left")).append(Component.text("] ", NamedTextColor.YELLOW)).append(tl(NamedTextColor.GREEN, "gui.toggle-preview-mode"))).build();
        mainPane.addItem(new GuiItem(previewDye, event -> {
            playerData.setInSimplePreviewMode(!isInSimplePreviewMode);
            CreateBannerGUI.show(player); // 重新開啟以刷新圖案
        }), 0, 2);
    }

    /**
     * Row 2-4：24 個圖案選擇（more-patterns toggle 切第二頁）。預覽底色依
     * simple-preview-mode 改用「白底黑 pattern」或「實際底色 + 玩家選色」。
     */
    private static void renderPatternPicker(StaticPane mainPane, PlayerData playerData, ItemStack currentBanner, Player player) {
        DyeColor selectedColor = playerData.getSelectedColor();
        boolean isInSimplePreviewMode = playerData.isInSimplePreviewMode();

        final ItemStack baseBannerForPreview;
        final DyeColor selectedColorForPreview;
        if (isInSimplePreviewMode) {
            baseBannerForPreview = new ItemBuilder(Material.WHITE_BANNER).build();
            selectedColorForPreview = DyeColor.BLACK;
        } else {
            baseBannerForPreview = new ItemBuilder(DyeColorRegistry.getBannerMaterial(currentBanner.getType())).build();
            selectedColorForPreview = selectedColor;
        }

        for (int i = 0; i < PATTERNS_PER_PAGE; i++) {
            int patternIndex = i + (playerData.isShowMorePatterns() ? PATTERNS_PER_PAGE : 0);
            if (patternIndex >= BannerPatternLayout.getPatternTypeList().size()) {
                break;
            }
            PatternType patternType = BannerPatternLayout.getPatternTypeList().get(patternIndex);
            ItemStack patternItem = new ItemBuilder(baseBannerForPreview.clone())
                .pattern(new Pattern(selectedColorForPreview, patternType)).build();

            int slot = GuiUtil.gridSlot(19, i);
            mainPane.addItem(new GuiItem(patternItem, event -> {
                BannerMeta currentBm = (BannerMeta) currentBanner.getItemMeta();
                Objects.requireNonNull(currentBm).addPattern(new Pattern(selectedColor, patternType));
                currentBanner.setItemMeta(currentBm);
                CreateBannerGUI.show(player); // 重新開啟以反映變更
            }), slot % 9, slot / 9);
        }
    }

    /**
     * Row 5 編輯模式專屬按鈕：slot 2 重置、slot 4 移除上一個 pattern（僅 pattern > 0 顯示）、
     * slot 6 切換 more-patterns、slot 8 儲存。slot 0 的返回由 show() 開頭、兩 stage 共用。
     */
    private static void renderEditActionBar(StaticPane mainPane, PlayerData playerData, ItemStack currentBanner,
                                            Player player, MessageService messageService) {
        // slot 2: 重置（刪除當前編輯旗幟）
        ItemStack btnDelete = new ItemBuilder(Material.BARRIER).name(tl(NamedTextColor.RED, "gui.delete")).build();
        GuiUtil.putAt(mainPane, 2, 5, btnDelete, event -> {
            playerData.setCurrentEditBanner(null);
            CreateBannerGUI.show(player); // 重新開啟以回到底色選擇
        });

        // slot 4: 移除上一個圖案（條件性、有 pattern 才顯示）
        if (currentBanner.hasItemMeta() && ((BannerMeta) Objects.requireNonNull(currentBanner.getItemMeta())).numberOfPatterns() > 0) {
            ItemStack btnRemovePattern = new ItemBuilder(Material.BARRIER).name(tl(NamedTextColor.RED, "gui.remove-last-pattern")).build();
            GuiUtil.putAt(mainPane, 4, 5, btnRemovePattern, event -> {
                BannerMeta bm = (BannerMeta) currentBanner.getItemMeta();
                bm.removePattern(bm.numberOfPatterns() - 1);
                currentBanner.setItemMeta(bm);
                CreateBannerGUI.show(player); // 重新開啟以反映變更
            });
        }

        // slot 6: 更多圖案
        ItemStack btnMorePattern = new ItemBuilder(Material.NETHER_STAR).name(tl(NamedTextColor.GREEN, "gui.more-patterns")).build();
        GuiUtil.putAt(mainPane, 6, 5, btnMorePattern, event -> {
            playerData.setShowMorePatterns(!playerData.isShowMorePatterns());
            CreateBannerGUI.show(player); // 重新開啟以顯示更多圖案
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
        });
    }
}
