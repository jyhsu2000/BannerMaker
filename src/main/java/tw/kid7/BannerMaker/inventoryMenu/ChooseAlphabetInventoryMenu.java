package tw.kid7.BannerMaker.inventoryMenu;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import tw.kid7.BannerMaker.InventoryMenuState;
import tw.kid7.BannerMaker.PlayerData;
import tw.kid7.BannerMaker.PlayerDataMap;
import tw.kid7.BannerMaker.util.AlphabetBanner;
import tw.kid7.BannerMaker.util.InventoryMenuUtil;
import tw.kid7.BannerMaker.util.ItemBuilder;
import tw.kid7.BannerMaker.util.MessageUtil;

import static tw.kid7.BannerMaker.configuration.Language.tl;

public class ChooseAlphabetInventoryMenu extends AbstractInventoryMenu {
    private static ChooseAlphabetInventoryMenu instance = null;
    //按鈕位置
    private int buttonPositionBackToMenu = 45;
    private int buttonPositionToggleBorder = 49;

    public static ChooseAlphabetInventoryMenu getInstance() {
        if (instance == null) {
            instance = new ChooseAlphabetInventoryMenu();
        }
        return instance;
    }

    @Override
    public void open(Player player) {
        PlayerData playerData = PlayerDataMap.get(player);
        //建立選單
        Inventory menu = InventoryMenuUtil.create(tl("gui.alphabet-and-number"));
        //清除當前編輯中的字母
        playerData.setCurrentAlphabetBanner(null);
        //邊框切換按鈕
        ItemStack btnBorderedBanner = new ItemStack(Material.BANNER, 1, (short) 15);
        BannerMeta borderedBannerMeta = (BannerMeta) btnBorderedBanner.getItemMeta();
        borderedBannerMeta.setDisplayName(MessageUtil.format("&a" + tl("gui.toggle-border")));
        borderedBannerMeta.addPattern(new Pattern(DyeColor.BLACK, PatternType.BORDER));
        btnBorderedBanner.setItemMeta(borderedBannerMeta);

        //選擇字母
        boolean alphabetBorder = playerData.isAlphabetBannerBordered();
        char[] alphabetArray = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789?!.".toCharArray();
        for (int i = 0; i < alphabetArray.length && i < 54; i++) {
            char alphabet = alphabetArray[i];
            ItemStack alphabetItem = AlphabetBanner.get(String.valueOf(alphabet), DyeColor.WHITE, DyeColor.BLACK, alphabetBorder);
            menu.setItem(i, alphabetItem);
        }
        //切換有無邊框
        menu.setItem(buttonPositionToggleBorder, btnBorderedBanner);

        //返回
        ItemStack btnBackToMenu = new ItemBuilder(Material.WOOL).amount(1).durability(14).name(MessageUtil.format("&c" + tl("gui.back"))).build();
        menu.setItem(buttonPositionBackToMenu, btnBackToMenu);
        //開啟選單
        player.openInventory(menu);
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        PlayerData playerData = PlayerDataMap.get(player);
        ItemStack itemStack = event.getCurrentItem();
        int rawSlot = event.getRawSlot();

        if (rawSlot < 45) {
            //選擇字母
            boolean alphabetBorder = playerData.isAlphabetBannerBordered();
            //設定當前編輯中的字母
            AlphabetBanner currentAlphabetBanner = new AlphabetBanner(itemStack.getItemMeta().getDisplayName(), DyeColor.WHITE, DyeColor.BLACK, alphabetBorder);
            playerData.setCurrentAlphabetBanner(currentAlphabetBanner);
            InventoryMenuUtil.openMenu(player, InventoryMenuState.CREATE_ALPHABET);
            return;
        }
        //點擊按鈕
        if (rawSlot == buttonPositionToggleBorder) {
            //切換有無邊框
            playerData.setAlphabetBannerBordered(!playerData.isAlphabetBannerBordered());
            InventoryMenuUtil.openMenu(player);
            return;
        }
        if (rawSlot == buttonPositionBackToMenu) {
            InventoryMenuUtil.openMenu(player, InventoryMenuState.MAIN_MENU);
            return;
        }

    }
}
