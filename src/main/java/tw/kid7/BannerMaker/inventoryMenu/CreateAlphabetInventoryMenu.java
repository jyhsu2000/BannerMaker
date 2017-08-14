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
import tw.kid7.BannerMaker.customMenu.CustomMenuAction;
import tw.kid7.BannerMaker.customMenu.CustomMenu;
import tw.kid7.BannerMaker.util.*;

import static tw.kid7.BannerMaker.configuration.Language.tl;

public class CreateAlphabetInventoryMenu extends AbstractInventoryMenu {
    private static CreateAlphabetInventoryMenu instance = null;

    public static CreateAlphabetInventoryMenu getInstance() {
        if (instance == null) {
            instance = new CreateAlphabetInventoryMenu();
        }
        return instance;
    }

    @Override
    public void open(final Player player) {
        final PlayerData playerData = BannerMaker.getInstance().playerDataMap.get(player);
        //建立選單
        String title = MessageUtil.format(tl("gui.prefix") + tl("gui.alphabet-and-number"));
        CustomMenu menu = new CustomMenu(title);
        //取得當前編輯中的字母
        final AlphabetBanner currentAlphabetBanner = playerData.getCurrentAlphabetBanner();
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
            final ItemStack banner = new ItemStack(Material.BANNER, 1, (short) i);
            menu.setClickableItem(i + 1 + (i / 8), banner).set(ClickType.LEFT, new CustomMenuAction() {
                @Override
                public void action() {
                    currentAlphabetBanner.baseColor = DyeColorUtil.fromInt(banner.getDurability());
                    playerData.setCurrentAlphabetBanner(currentAlphabetBanner);
                    InventoryMenuUtil.openMenu(player);
                }
            });
        }
        //選擇主要顏色
        for (int i = 0; i < 16; i++) {
            final ItemStack dye = new ItemBuilder(Material.INK_SACK).amount(1).durability(i).build();
            menu.setClickableItem(18 + i + 1 + (i / 8), dye).set(ClickType.LEFT, new CustomMenuAction() {
                @Override
                public void action() {
                    currentAlphabetBanner.dyeColor = DyeColorUtil.fromInt(dye.getDurability());
                    playerData.setCurrentAlphabetBanner(currentAlphabetBanner);
                    InventoryMenuUtil.openMenu(player);
                }
            });
        }
        //切換有無邊框
        menu.setClickableItem(37, btnBorderedBanner).set(ClickType.LEFT, new CustomMenuAction() {
            @Override
            public void action() {
                currentAlphabetBanner.bordered = !currentAlphabetBanner.bordered;
                playerData.setCurrentAlphabetBanner(currentAlphabetBanner);
                InventoryMenuUtil.openMenu(player);
            }
        });
        //檢視旗幟資訊按鈕
        ItemStack btnBannerInfo = new ItemBuilder(Material.WOOL).amount(1).durability(5).name(MessageUtil.format("&a" + tl("gui.banner-info"))).build();
        menu.setClickableItem(49, btnBannerInfo).set(ClickType.LEFT, new CustomMenuAction() {
            @Override
            public void action() {
                //檢視旗幟資訊
                playerData.setViewInfoBanner(currentAlphabetBanner.toItemStack());
                //重置頁數
                playerData.setCurrentRecipePage(1);
                InventoryMenuUtil.openMenu(player, InventoryMenuState.BANNER_INFO);
            }
        });

        //返回
        ItemStack btnBackToMenu = new ItemBuilder(Material.WOOL).amount(1).durability(14).name(MessageUtil.format("&c" + tl("gui.back"))).build();
        menu.setClickableItem(45, btnBackToMenu).set(ClickType.LEFT, new CustomMenuAction() {
            @Override
            public void action() {
                InventoryMenuUtil.openMenu(player, InventoryMenuState.CHOOSE_ALPHABET);
            }
        });
        //開啟選單
        menu.open(player);
    }
}
