package club.kid7.bannermaker.gui;

import club.kid7.bannermaker.AlphabetBanner;
import club.kid7.bannermaker.BannerMaker;
import club.kid7.bannermaker.PlayerData;
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

public class ChooseAlphabetGUI {

    public static void show(Player player) {
        MessageService messageService = BannerMaker.getInstance().getMessageService();
        PlayerData playerData = BannerMaker.getInstance().playerDataMap.get(player);

        String title = messageService.formatToString(tl("gui.prefix") + tl("gui.alphabet-and-number"));
        ChestGui gui = new ChestGui(6, title);

        StaticPane mainPane = new StaticPane(0, 0, 9, 6);
        gui.addPane(mainPane);

        // 清除當前編輯中的字母旗幟
        playerData.setCurrentAlphabetBanner(null);

        boolean alphabetBorder = playerData.isAlphabetBannerBordered();
        char[] alphabetArray = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789?!.".toCharArray();

        // 填充字母物品
        for (int i = 0; i < alphabetArray.length && i < 54; i++) {
            char alphabet = alphabetArray[i];
            final AlphabetBanner alphabetBanner = new AlphabetBanner(String.valueOf(alphabet), DyeColor.WHITE, DyeColor.BLACK, alphabetBorder);
            ItemStack alphabetItem = alphabetBanner.toItemStack();
            mainPane.addItem(new GuiItem(alphabetItem, event -> {
                playerData.setCurrentAlphabetBanner(alphabetBanner);
                CreateAlphabetGUI.show(player);
                event.setCancelled(true);
            }), i % 9, i / 9);
        }

        // Slot 49: 切換邊框
        ItemStack btnBorderedBanner = new ItemBuilder(Material.WHITE_BANNER)
            .name(messageService.formatToString("&a" + tl("gui.toggle-border")))
            .pattern(new Pattern(DyeColor.BLACK, PatternType.BORDER)).build();
        mainPane.addItem(new GuiItem(btnBorderedBanner, event -> {
            playerData.setAlphabetBannerBordered(!playerData.isAlphabetBannerBordered());
            ChooseAlphabetGUI.show(player); // 刷新以顯示變更
            event.setCancelled(true);
        }), 4, 5); // Slot 49

        // Slot 45: 返回按鈕
        ItemStack btnBackToMenu = new ItemBuilder(Material.RED_WOOL).name(messageService.formatToString("&c" + tl("gui.back"))).build();
        mainPane.addItem(new GuiItem(btnBackToMenu, event -> {
            MainMenuGUI.show(player);
            event.setCancelled(true);
        }), 0, 5); // Slot 45

        gui.show(player);
    }
}
