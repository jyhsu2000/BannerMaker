package club.kid7.bannermaker.customMenu;

import club.kid7.bannermaker.BannerMaker;
import club.kid7.bannermaker.PlayerData;
import club.kid7.bannermaker.registry.DyeColorRegistry;
import club.kid7.bannermaker.util.BannerUtil;
import club.kid7.bannermaker.util.IOUtil;
import club.kid7.bannermaker.util.ItemBuilder;
import club.kid7.pluginutilities.gui.ClickAction;
import club.kid7.pluginutilities.gui.CustomGUIInventory;
import club.kid7.pluginutilities.gui.CustomGUIManager;
import club.kid7.pluginutilities.gui.CustomGUIMenu;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

import java.util.Objects;

import static club.kid7.bannermaker.configuration.Language.tl;

public class CreateBannerMenu implements CustomGUIMenu {
    @Override
    public CustomGUIInventory build(final Player player) {
        final PlayerData playerData = BannerMaker.getInstance().playerDataMap.get(player);
        //建立選單
        String title = BannerMaker.getInstance().getMessageService().formatToString(tl("gui.prefix") + tl("gui.create-banner"));
        CustomGUIInventory menu = new CustomGUIInventory(title);
        //返回
        ItemStack btnBackToMenu = new ItemBuilder(Material.RED_WOOL).name(BannerMaker.getInstance().getMessageService().formatToString("&c" + tl("gui.back"))).build();
        menu.setItem(45, btnBackToMenu, new ClickAction(ClickType.LEFT, event -> CustomGUIManager.open(player, MainMenu.class)));
        //取得當前編輯中的旗幟
        final ItemStack currentBanner = playerData.getCurrentEditBanner();
        if (currentBanner == null) {
            //剛開始編輯，先選擇底色
            for (int i = 0; i < 16; i++) {
                final ItemStack banner = new ItemBuilder(DyeColorRegistry.getBannerMaterial(i)).build();
                menu.setItem(i + 1 + (i / 8), banner, new ClickAction(ClickType.LEFT, event -> {
                    playerData.setCurrentEditBanner(banner);
                    CustomGUIManager.openPrevious(player);
                }));
            }
            return menu;
        }
        //新增按鈕
        //當前旗幟
        menu.setItem(0, currentBanner);
        //patterns過多的警告
        if (currentBanner.hasItemMeta() && ((BannerMeta) Objects.requireNonNull(currentBanner.getItemMeta())).numberOfPatterns() > 6) {
            ItemStack warning = new ItemBuilder(Material.OAK_SIGN).name(BannerMaker.getInstance().getMessageService().formatToString("&c" + tl("gui.uncraftable-warning")))
                .lore(tl("gui.more-than-6-patterns")).build();
            menu.setItem(9, warning);
        }
        //顏色
        for (int i = 0; i < 16; i++) {
            final ItemStack dye = new ItemBuilder(DyeColorRegistry.getDyeMaterial(DyeColorRegistry.getDyeColor(i))).build();
            menu.setItem(i + 1 + (i / 8), dye, new ClickAction(ClickType.LEFT, event -> {
                playerData.setSelectedColor(DyeColorRegistry.getDyeColor(dye.getType()));
                CustomGUIManager.openPrevious(player);
            }));
        }
        //選擇的顏色
        DyeColor selectedColor = playerData.getSelectedColor();
        //預覽模式
        boolean isInSimplePreviewMode = playerData.isInSimplePreviewMode();
        //預覽模式切換按鈕
        final ItemStack previewDye = new ItemBuilder(DyeColorRegistry.getDyeMaterial(selectedColor))
            .name(BannerMaker.getInstance().getMessageService().formatToString("&9" + tl("gui.selected-pattern-color")))
            .lore(BannerMaker.getInstance().getMessageService().formatToString("&e[" + tl("gui.click.left") + "] &a" + tl("gui.toggle-preview-mode"))).build();
        menu.setItem(18, previewDye, new ClickAction(ClickType.LEFT, event -> {
            playerData.setInSimplePreviewMode(!isInSimplePreviewMode);
            CustomGUIManager.openPrevious(player);
        }));

        //預覽模式
        final ItemStack baseBannerForPreview;
        final DyeColor selectedColorForPreview;
        if (isInSimplePreviewMode) {
            //簡易預覽模式：一律黑底+白色圖樣
            baseBannerForPreview = new ItemBuilder(Material.WHITE_BANNER).build();
            selectedColorForPreview = DyeColor.BLACK;
        } else {
            //預設預覽模式：與旗幟相同底色+選擇顏色之圖樣
            baseBannerForPreview = new ItemBuilder(DyeColorRegistry.getBannerMaterial(currentBanner.getType())).build();
            selectedColorForPreview = selectedColor;
        }
        //Pattern
        for (int i = 0; i < 24; i++) {
            int patternIndex = i;
            if (playerData.isShowMorePatterns()) {
                patternIndex += 24;
            }
            if (patternIndex >= BannerUtil.getPatternTypeList().size()) {
                break;
            }
            //預覽旗幟
            PatternType patternType = BannerUtil.getPatternTypeList().get(patternIndex);
            final ItemStack banner = new ItemBuilder(baseBannerForPreview)
                .pattern(new Pattern(selectedColorForPreview, patternType)).build();
            menu.setItem(i + 19 + (i / 8), banner, new ClickAction(ClickType.LEFT, event -> {
                //新增Pattern
                BannerMeta currentBm = (BannerMeta) currentBanner.getItemMeta();
                Objects.requireNonNull(currentBm).addPattern(new Pattern(selectedColor, patternType));
                currentBanner.setItemMeta(currentBm);
                playerData.setCurrentEditBanner(currentBanner);
                CustomGUIManager.openPrevious(player);
            }));
        }
        //更多Pattern
        ItemStack btnMorePattern = new ItemBuilder(Material.NETHER_STAR).name(BannerMaker.getInstance().getMessageService().formatToString("&a" + tl("gui.more-patterns"))).build();
        menu.setItem(51, btnMorePattern, new ClickAction(ClickType.LEFT, event -> {
            playerData.setShowMorePatterns(!playerData.isShowMorePatterns());
            CustomGUIManager.openPrevious(player);
        }));
        //建立旗幟
        ItemStack btnCreate = new ItemBuilder(Material.LIME_WOOL).name(BannerMaker.getInstance().getMessageService().formatToString("&a" + tl("gui.create"))).build();
        menu.setItem(53, btnCreate, new ClickAction(ClickType.LEFT, event -> {
            IOUtil.saveBanner(player, currentBanner);
            playerData.setCurrentEditBanner(null);
            CustomGUIManager.open(player, MainMenu.class);
        }));
        //刪除
        ItemStack btnDelete = new ItemBuilder(Material.BARRIER).name(BannerMaker.getInstance().getMessageService().formatToString("&c" + tl("gui.delete"))).build();
        menu.setItem(47, btnDelete, new ClickAction(ClickType.LEFT, event -> {
            playerData.setCurrentEditBanner(null);
            CustomGUIManager.openPrevious(player);
        }));
        if (currentBanner.hasItemMeta() && ((BannerMeta) Objects.requireNonNull(currentBanner.getItemMeta())).numberOfPatterns() > 0) {
            //移除Pattern
            ItemStack btnRemovePattern = new ItemBuilder(Material.BARRIER).name(BannerMaker.getInstance().getMessageService().formatToString("&c" + tl("gui.remove-last-pattern"))).build();
            menu.setItem(49, btnRemovePattern, new ClickAction(ClickType.LEFT, event -> {
                BannerMeta bm = (BannerMeta) currentBanner.getItemMeta();
                bm.removePattern(bm.numberOfPatterns() - 1);
                currentBanner.setItemMeta(bm);
                playerData.setCurrentEditBanner(currentBanner);
                CustomGUIManager.openPrevious(player);
            }));
        }
        return menu;
    }
}
