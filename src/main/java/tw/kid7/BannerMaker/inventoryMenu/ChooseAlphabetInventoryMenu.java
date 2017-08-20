package tw.kid7.BannerMaker.inventoryMenu;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import tw.kid7.BannerMaker.BannerMaker;
import tw.kid7.BannerMaker.InventoryMenuState;
import tw.kid7.BannerMaker.PlayerData;
import tw.kid7.BannerMaker.customGUI.CustomGUIItemListener;
import tw.kid7.BannerMaker.customGUI.CustomGUIMenu;
import tw.kid7.BannerMaker.util.AlphabetBanner;
import tw.kid7.BannerMaker.util.InventoryMenuUtil;
import tw.kid7.BannerMaker.util.ItemBuilder;
import tw.kid7.BannerMaker.util.MessageUtil;

import static tw.kid7.BannerMaker.configuration.Language.tl;

public class ChooseAlphabetInventoryMenu extends AbstractInventoryMenu {
    private static ChooseAlphabetInventoryMenu instance = null;

    public static ChooseAlphabetInventoryMenu getInstance() {
        if (instance == null) {
            instance = new ChooseAlphabetInventoryMenu();
        }
        return instance;
    }

    @Override
    public void open(final Player player) {
        final PlayerData playerData = BannerMaker.getInstance().playerDataMap.get(player);
        //建立選單
        String title = MessageUtil.format(tl("gui.prefix") + tl("gui.alphabet-and-number"));
        CustomGUIMenu menu = new CustomGUIMenu(title);
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
            final AlphabetBanner alphabetBanner = new AlphabetBanner(String.valueOf(alphabet), DyeColor.WHITE, DyeColor.BLACK, alphabetBorder);
            ItemStack alphabetItem = alphabetBanner.toItemStack();
            menu.setClickableItem(i, alphabetItem).set(ClickType.LEFT, new CustomGUIItemListener() {
                @Override
                public void action() {
                    //設定當前編輯中的字母
                    playerData.setCurrentAlphabetBanner(alphabetBanner);
                    InventoryMenuUtil.openMenu(player, InventoryMenuState.CREATE_ALPHABET);
                }
            });
        }
        //切換有無邊框
        menu.setClickableItem(49, btnBorderedBanner).set(ClickType.LEFT, new CustomGUIItemListener() {
            @Override
            public void action() {
                playerData.setAlphabetBannerBordered(!playerData.isAlphabetBannerBordered());
                InventoryMenuUtil.openMenu(player);
            }
        });

        //返回
        ItemStack btnBackToMenu = new ItemBuilder(Material.WOOL).amount(1).durability(14).name(MessageUtil.format("&c" + tl("gui.back"))).build();
        menu.setClickableItem(45, btnBackToMenu).set(ClickType.LEFT, new CustomGUIItemListener() {
            @Override
            public void action() {
                InventoryMenuUtil.openMenu(player, InventoryMenuState.MAIN_MENU);
            }
        });
        //開啟選單
        menu.open(player);
    }
}
