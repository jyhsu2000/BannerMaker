package club.kid7.bannermaker.customMenu;

import club.kid7.bannermaker.BannerMaker;
import club.kid7.bannermaker.PlayerData;
import club.kid7.bannermaker.registry.DyeColorRegistry;
import club.kid7.bannermaker.util.BannerUtil;
import club.kid7.bannermaker.util.IOUtil;
import club.kid7.bannermaker.util.MessageUtil;
import club.kid7.pluginutilities.gui.ClickAction;
import club.kid7.pluginutilities.gui.CustomGUIInventory;
import club.kid7.pluginutilities.gui.CustomGUIManager;
import club.kid7.pluginutilities.gui.CustomGUIMenu;
import club.kid7.pluginutilities.kitemstack.KItemStack;
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
        String title = MessageUtil.format(tl("gui.prefix") + tl("gui.create-banner"));
        CustomGUIInventory menu = new CustomGUIInventory(title);
        //返回
        KItemStack btnBackToMenu = new KItemStack(Material.RED_WOOL).name(MessageUtil.format("&c" + tl("gui.back")));
        menu.setItem(45, btnBackToMenu, new ClickAction(ClickType.LEFT, event -> CustomGUIManager.open(player, MainMenu.class)));
        //取得當前編輯中的旗幟
        final ItemStack currentBanner = playerData.getCurrentEditBanner();
        if (currentBanner == null) {
            //剛開始編輯，先選擇底色
            for (int i = 0; i < 16; i++) {
                final KItemStack banner = new KItemStack(DyeColorRegistry.getBannerMaterial(i));
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
            KItemStack warning = new KItemStack(Material.OAK_SIGN).name(MessageUtil.format("&c" + tl("gui.uncraftable-warning")))
                .lore(tl("gui.more-than-6-patterns"));
            menu.setItem(9, warning);
        }
        //顏色
        for (int i = 0; i < 16; i++) {
            final KItemStack dye = new KItemStack(DyeColorRegistry.getDyeMaterial(DyeColorRegistry.getDyeColor(i)));
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
        final KItemStack previewDye = new KItemStack(DyeColorRegistry.getDyeMaterial(selectedColor))
            .name(MessageUtil.format("&9" + tl("gui.selected-pattern-color")))
            .lore(MessageUtil.format("&e[" + tl("gui.click.left") + "] &a" + tl("gui.toggle-preview-mode")));
        menu.setItem(18, previewDye, new ClickAction(ClickType.LEFT, event -> {
            playerData.setInSimplePreviewMode(!isInSimplePreviewMode);
            CustomGUIManager.openPrevious(player);
        }));

        //預覽模式
        final KItemStack baseBannerForPreview;
        final DyeColor selectedColorForPreview;
        if (isInSimplePreviewMode) {
            //簡易預覽模式：一律黑底+白色圖樣
            baseBannerForPreview = new KItemStack(Material.WHITE_BANNER);
            selectedColorForPreview = DyeColor.BLACK;
        } else {
            //預設預覽模式：與旗幟相同底色+選擇顏色之圖樣
            baseBannerForPreview = new KItemStack(DyeColorRegistry.getBannerMaterial(currentBanner.getType()));
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
            final KItemStack banner = (new KItemStack(baseBannerForPreview))
                .pattern(new Pattern(selectedColorForPreview, patternType));
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
        KItemStack btnMorePattern = new KItemStack(Material.NETHER_STAR).name(MessageUtil.format("&a" + tl("gui.more-patterns")));
        menu.setItem(51, btnMorePattern, new ClickAction(ClickType.LEFT, event -> {
            playerData.setShowMorePatterns(!playerData.isShowMorePatterns());
            CustomGUIManager.openPrevious(player);
        }));
        //建立旗幟
        KItemStack btnCreate = new KItemStack(Material.LIME_WOOL).name(MessageUtil.format("&a" + tl("gui.create")));
        menu.setItem(53, btnCreate, new ClickAction(ClickType.LEFT, event -> {
            IOUtil.saveBanner(player, currentBanner);
            playerData.setCurrentEditBanner(null);
            CustomGUIManager.open(player, MainMenu.class);
        }));
        //刪除
        KItemStack btnDelete = new KItemStack(Material.BARRIER).name(MessageUtil.format("&c" + tl("gui.delete")));
        menu.setItem(47, btnDelete, new ClickAction(ClickType.LEFT, event -> {
            playerData.setCurrentEditBanner(null);
            CustomGUIManager.openPrevious(player);
        }));
        if (currentBanner.hasItemMeta() && ((BannerMeta) Objects.requireNonNull(currentBanner.getItemMeta())).numberOfPatterns() > 0) {
            //移除Pattern
            KItemStack btnRemovePattern = new KItemStack(Material.BARRIER).name(MessageUtil.format("&c" + tl("gui.remove-last-pattern")));
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
