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
import tw.kid7.BannerMaker.BannerMaker;
import tw.kid7.BannerMaker.InventoryMenuState;
import tw.kid7.BannerMaker.PlayerData;
import tw.kid7.BannerMaker.util.*;

import static tw.kid7.BannerMaker.configuration.Language.tl;

public class CreateAlphabetInventoryMenu extends AbstractInventoryMenu {
    private static CreateAlphabetInventoryMenu instance = null;
    //按鈕位置
    private final int buttonPositionBackToMenu = 45;
    private final int buttonPositionToggleBorder = 37;
    private final int buttonPositionBannerInfo = 49;

    public static CreateAlphabetInventoryMenu getInstance() {
        if (instance == null) {
            instance = new CreateAlphabetInventoryMenu();
        }
        return instance;
    }

    @Override
    public void open(Player player) {
        PlayerData playerData = BannerMaker.getInstance().playerDataMap.get(player);
        //建立選單
        Inventory menu = InventoryMenuUtil.create(tl("gui.alphabet-and-number"));
        //取得當前編輯中的字母
        AlphabetBanner currentAlphabetBanner = playerData.getCurrentAlphabetBanner();
        //邊框切換按鈕
        ItemStack btnBorderedBanner = new ItemStack(Material.BANNER, 1, (short) 15);
        BannerMeta borderedBannerMeta = (BannerMeta) btnBorderedBanner.getItemMeta();
        borderedBannerMeta.setDisplayName(MessageUtil.format("&a" + tl("gui.toggle-border")));
        borderedBannerMeta.addPattern(new Pattern(DyeColor.BLACK, PatternType.BORDER));
        btnBorderedBanner.setItemMeta(borderedBannerMeta);

        //選擇顏色
        menu.setItem(0, currentAlphabetBanner.toItemStack());
        //選擇底色
        for (int i = 0; i < 16; i++) {
            ItemStack banner = new ItemStack(Material.BANNER, 1, (short) i);
            menu.setItem(i + 1 + (i / 8), banner);
        }
        //選擇主要顏色
        for (int i = 0; i < 16; i++) {
            ItemStack dye = new ItemBuilder(Material.INK_SACK).amount(1).durability(i).build();
            menu.setItem(18 + i + 1 + (i / 8), dye);
        }
        //切換有無邊框
        menu.setItem(buttonPositionToggleBorder, btnBorderedBanner);
        //檢視旗幟資訊按鈕
        ItemStack btnBannerInfo = new ItemBuilder(Material.WOOL).amount(1).durability(5).name(MessageUtil.format("&a" + tl("gui.banner-info"))).build();
        menu.setItem(buttonPositionBannerInfo, btnBannerInfo);

        //返回
        ItemStack btnBackToMenu = new ItemBuilder(Material.WOOL).amount(1).durability(14).name(MessageUtil.format("&c" + tl("gui.back"))).build();
        menu.setItem(buttonPositionBackToMenu, btnBackToMenu);
        //開啟選單
        player.openInventory(menu);
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        PlayerData playerData = BannerMaker.getInstance().playerDataMap.get(player);
        ItemStack itemStack = event.getCurrentItem();
        //取得當前編輯中的字母
        AlphabetBanner currentAlphabetBanner = playerData.getCurrentAlphabetBanner();
        int rawSlot = event.getRawSlot();
        //選擇顏色
        if (rawSlot < 1) {
            //預覽圖
            return;
        }
        if (rawSlot < 18) {
            //選擇底色
            currentAlphabetBanner.baseColor = DyeColorUtil.fromInt(itemStack.getDurability());
            playerData.setCurrentAlphabetBanner(currentAlphabetBanner);
            InventoryMenuUtil.openMenu(player);
            return;
        }
        if (rawSlot < 36) {
            //選擇主要顏色
            currentAlphabetBanner.dyeColor = DyeColorUtil.fromInt(itemStack.getDurability());
            playerData.setCurrentAlphabetBanner(currentAlphabetBanner);
            InventoryMenuUtil.openMenu(player);
            return;
        }
        //點擊按鈕
        if (rawSlot == buttonPositionToggleBorder) {
            //切換有無邊框
            currentAlphabetBanner.bordered = !currentAlphabetBanner.bordered;
            playerData.setCurrentAlphabetBanner(currentAlphabetBanner);
            InventoryMenuUtil.openMenu(player);
            return;
        }
        if (rawSlot == buttonPositionBannerInfo) {
            //檢視旗幟資訊
            playerData.setViewInfoBanner(currentAlphabetBanner.toItemStack());
            //重置頁數
            playerData.setCurrentRecipePage(1);
            InventoryMenuUtil.openMenu(player, InventoryMenuState.BANNER_INFO);
            return;
        }
        if (rawSlot == buttonPositionBackToMenu) {
            InventoryMenuUtil.openMenu(player, InventoryMenuState.CHOOSE_ALPHABET);
            return;
        }
    }
}
