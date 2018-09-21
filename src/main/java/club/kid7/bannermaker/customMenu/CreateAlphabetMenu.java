package club.kid7.bannermaker.customMenu;

import club.kid7.bannermaker.BannerMaker;
import club.kid7.bannermaker.PlayerData;
import club.kid7.bannermaker.util.AlphabetBanner;
import club.kid7.bannermaker.util.DyeColorUtil;
import club.kid7.bannermaker.util.MessageUtil;
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
        String title = MessageUtil.format(tl("gui.prefix") + tl("gui.alphabet-and-number"));
        CustomGUIInventory menu = new CustomGUIInventory(title);
        //取得當前編輯中的字母
        final AlphabetBanner currentAlphabetBanner = playerData.getCurrentAlphabetBanner();
        //邊框切換按鈕
        KItemStack btnBorderedBanner = new KItemStack(Material.LEGACY_BANNER).durability(15)
            .name(MessageUtil.format("&a" + tl("gui.toggle-border")))
            .pattern(new Pattern(DyeColor.BLACK, PatternType.BORDER));

        //選擇顏色
        menu.setItem(0, currentAlphabetBanner.toItemStack());
        //選擇底色
        for (int i = 0; i < 16; i++) {
            final KItemStack banner = new KItemStack(Material.LEGACY_BANNER).durability(i);
            menu.setClickableItem(i + 1 + (i / 8), banner).set(ClickType.LEFT, event -> {
                currentAlphabetBanner.baseColor = DyeColorUtil.fromInt(banner.getDurability());
                playerData.setCurrentAlphabetBanner(currentAlphabetBanner);
                CustomGUIManager.openPrevious(player);
            });
        }
        //選擇主要顏色
        for (int i = 0; i < 16; i++) {
            final KItemStack dye = new KItemStack(Material.LEGACY_INK_SACK).durability(i);
            menu.setClickableItem(18 + i + 1 + (i / 8), dye).set(ClickType.LEFT, event -> {
                currentAlphabetBanner.dyeColor = DyeColorUtil.fromInt(dye.getDurability());
                playerData.setCurrentAlphabetBanner(currentAlphabetBanner);
                CustomGUIManager.openPrevious(player);
            });
        }
        //切換有無邊框
        menu.setClickableItem(37, btnBorderedBanner).set(ClickType.LEFT, event -> {
            currentAlphabetBanner.bordered = !currentAlphabetBanner.bordered;
            playerData.setCurrentAlphabetBanner(currentAlphabetBanner);
            CustomGUIManager.openPrevious(player);
        });
        //檢視旗幟資訊按鈕
        KItemStack btnBannerInfo = new KItemStack(Material.LEGACY_WOOL).amount(1).durability(5).name(MessageUtil.format("&a" + tl("gui.banner-info")));
        menu.setClickableItem(49, btnBannerInfo).set(ClickType.LEFT, event -> {
            //檢視旗幟資訊
            playerData.setViewInfoBanner(currentAlphabetBanner.toItemStack());
            //重置頁數
            playerData.setCurrentRecipePage(1);
            CustomGUIManager.open(player, BannerInfoMenu.class);
        });

        //返回
        KItemStack btnBackToMenu = new KItemStack(Material.LEGACY_WOOL).amount(1).durability(14).name(MessageUtil.format("&c" + tl("gui.back")));
        menu.setClickableItem(45, btnBackToMenu).set(ClickType.LEFT, event -> CustomGUIManager.open(player, ChooseAlphabetMenu.class));
        return menu;
    }
}
