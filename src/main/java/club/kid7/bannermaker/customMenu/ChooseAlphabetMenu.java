package club.kid7.bannermaker.customMenu;

import club.kid7.bannermaker.AlphabetBanner;
import club.kid7.bannermaker.BannerMaker;
import club.kid7.bannermaker.PlayerData;
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

import static club.kid7.bannermaker.configuration.Language.tl;

public class ChooseAlphabetMenu implements CustomGUIMenu {
    @Override
    public CustomGUIInventory build(final Player player) {
        final PlayerData playerData = BannerMaker.getInstance().playerDataMap.get(player);
        //建立選單
        String title = BannerMaker.getInstance().getMessageService().formatToString(tl("gui.prefix") + tl("gui.alphabet-and-number"));
        CustomGUIInventory menu = new CustomGUIInventory(title);
        //清除當前編輯中的字母
        playerData.setCurrentAlphabetBanner(null);
        //邊框切換按鈕
        KItemStack btnBorderedBanner = new KItemStack(Material.WHITE_BANNER)
            .name(BannerMaker.getInstance().getMessageService().formatToString("&a" + tl("gui.toggle-border")))
            .pattern(new Pattern(DyeColor.BLACK, PatternType.BORDER));

        //選擇字母
        boolean alphabetBorder = playerData.isAlphabetBannerBordered();
        char[] alphabetArray = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789?!.".toCharArray();
        for (int i = 0; i < alphabetArray.length && i < 54; i++) {
            char alphabet = alphabetArray[i];
            final AlphabetBanner alphabetBanner = new AlphabetBanner(String.valueOf(alphabet), DyeColor.WHITE, DyeColor.BLACK, alphabetBorder);
            ItemStack alphabetItem = alphabetBanner.toItemStack();
            menu.setItem(i, alphabetItem, new ClickAction(ClickType.LEFT, event -> {
                //設定當前編輯中的字母
                playerData.setCurrentAlphabetBanner(alphabetBanner);
                CustomGUIManager.open(player, CreateAlphabetMenu.class);
            }));
        }
        //切換有無邊框
        menu.setItem(49, btnBorderedBanner, new ClickAction(ClickType.LEFT, event -> {
            playerData.setAlphabetBannerBordered(!playerData.isAlphabetBannerBordered());
            CustomGUIManager.openPrevious(player);
        }));

        //返回
        KItemStack btnBackToMenu = new KItemStack(Material.RED_WOOL).name(BannerMaker.getInstance().getMessageService().formatToString("&c" + tl("gui.back")));
        menu.setItem(45, btnBackToMenu, new ClickAction(ClickType.LEFT, event -> CustomGUIManager.open(player, MainMenu.class)));
        return menu;
    }
}
