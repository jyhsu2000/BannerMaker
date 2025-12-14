package club.kid7.bannermaker.customMenu;

import club.kid7.bannermaker.AlphabetBanner;
import club.kid7.bannermaker.BannerMaker;
import club.kid7.bannermaker.PlayerData;
import club.kid7.bannermaker.registry.DyeColorRegistry;
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

import static club.kid7.bannermaker.configuration.Language.tl;

public class CreateAlphabetMenu implements CustomGUIMenu {
    @Override
    public CustomGUIInventory build(final Player player) {
        final PlayerData playerData = BannerMaker.getInstance().playerDataMap.get(player);
        //建立選單
        String title = BannerMaker.getInstance().getMessageService().formatToString(tl("gui.prefix") + tl("gui.alphabet-and-number"));
        CustomGUIInventory menu = new CustomGUIInventory(title);
        //取得當前編輯中的字母
        final AlphabetBanner currentAlphabetBanner = playerData.getCurrentAlphabetBanner();
        //邊框切換按鈕
        KItemStack btnBorderedBanner = new KItemStack(Material.WHITE_BANNER)
            .name(BannerMaker.getInstance().getMessageService().formatToString("&a" + tl("gui.toggle-border")))
            .pattern(new Pattern(DyeColor.BLACK, PatternType.BORDER));

        //選擇顏色
        menu.setItem(0, currentAlphabetBanner.toItemStack());
        //選擇底色
        for (int i = 0; i < 16; i++) {
            final KItemStack banner = new KItemStack(DyeColorRegistry.getBannerMaterial(i));
            menu.setItem(i + 1 + (i / 8), banner, new ClickAction(ClickType.LEFT, event -> {
                currentAlphabetBanner.baseColor = DyeColorRegistry.getDyeColor(banner.getType());
                playerData.setCurrentAlphabetBanner(currentAlphabetBanner);
                CustomGUIManager.openPrevious(player);
            }));
        }
        //選擇主要顏色
        for (int i = 0; i < 16; i++) {
            final KItemStack dye = new KItemStack(DyeColorRegistry.getDyeMaterial(i));
            menu.setItem(18 + i + 1 + (i / 8), dye, new ClickAction(ClickType.LEFT, event -> {
                currentAlphabetBanner.dyeColor = DyeColorRegistry.getDyeColor(dye.getType());
                playerData.setCurrentAlphabetBanner(currentAlphabetBanner);
                CustomGUIManager.openPrevious(player);
            }));
        }
        //切換有無邊框
        menu.setItem(37, btnBorderedBanner, new ClickAction(ClickType.LEFT, event -> {
            currentAlphabetBanner.bordered = !currentAlphabetBanner.bordered;
            playerData.setCurrentAlphabetBanner(currentAlphabetBanner);
            CustomGUIManager.openPrevious(player);
        }));
        //檢視旗幟資訊按鈕
        KItemStack btnBannerInfo = new KItemStack(Material.LIME_WOOL).name(BannerMaker.getInstance().getMessageService().formatToString("&a" + tl("gui.banner-info")));
        menu.setItem(49, btnBannerInfo, new ClickAction(ClickType.LEFT, event -> {
            //檢視旗幟資訊
            playerData.setViewInfoBanner(currentAlphabetBanner.toItemStack());
            //重置頁數
            playerData.setCurrentRecipePage(1);
            CustomGUIManager.open(player, BannerInfoMenu.class);
        }));

        //返回
        KItemStack btnBackToMenu = new KItemStack(Material.RED_WOOL).name(BannerMaker.getInstance().getMessageService().formatToString("&c" + tl("gui.back")));
        menu.setItem(45, btnBackToMenu, new ClickAction(ClickType.LEFT, event -> CustomGUIManager.open(player, ChooseAlphabetMenu.class)));
        return menu;
    }
}
