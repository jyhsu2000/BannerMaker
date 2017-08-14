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
import tw.kid7.BannerMaker.clickableInventory.Clickable;
import tw.kid7.BannerMaker.clickableInventory.ClickableInventory;
import tw.kid7.BannerMaker.util.*;

import static tw.kid7.BannerMaker.configuration.Language.tl;

public class CreateBannerInventoryMenu extends AbstractInventoryMenu {
    private static CreateBannerInventoryMenu instance = null;

    public static CreateBannerInventoryMenu getInstance() {
        if (instance == null) {
            instance = new CreateBannerInventoryMenu();
        }
        return instance;
    }

    @Override
    public void open(final Player player) {
        final PlayerData playerData = BannerMaker.getInstance().playerDataMap.get(player);
        //建立選單
        ClickableInventory menu = ClickableInventory.create(playerData.getInventoryMenuState(), player, tl("gui.create-banner"));
        //取得當前編輯中的旗幟
        final ItemStack currentBanner = playerData.getCurrentEditBanner();
        if (currentBanner == null) {
            //剛開始編輯，先選擇底色
            for (int i = 0; i < 16; i++) {
                final ItemStack banner = new ItemStack(Material.BANNER, 1, (short) i);
                menu.setClickableItem(i + 1 + (i / 8), banner).set(ClickType.LEFT, new Clickable() {
                    @Override
                    public void action() {
                        playerData.setCurrentEditBanner(banner);
                        InventoryMenuUtil.openMenu(player);
                    }
                });
            }
        } else {
            //新增按鈕
            //當前旗幟
            menu.setItem(0, currentBanner);
            //patterns過多的警告
            if (currentBanner.hasItemMeta() && ((BannerMeta) currentBanner.getItemMeta()).numberOfPatterns() > 6) {
                ItemStack warning = new ItemBuilder(Material.SIGN).amount(1).name(MessageUtil.format("&c" + tl("gui.uncraftable-warning")))
                    .lore(tl("gui.more-than-6-patterns")).build();
                menu.setItem(9, warning);
            }
            //顏色
            for (int i = 0; i < 16; i++) {
                final ItemStack dye = new ItemBuilder(Material.INK_SACK).amount(1).durability(i).build();
                menu.setClickableItem(i + 1 + (i / 8), dye).set(ClickType.LEFT, new Clickable() {
                    @Override
                    public void action() {
                        playerData.setSelectedColor(DyeColorUtil.fromInt(dye.getDurability()));
                        InventoryMenuUtil.openMenu(player);
                    }
                });
            }
            //Pattern
            //選擇的顏色
            DyeColor selectedColor = playerData.getSelectedColor();
            for (int i = 0; i < 24; i++) {
                int patternIndex = i;
                if (playerData.isShowMorePatterns()) {
                    patternIndex += 24;
                }
                if (patternIndex >= BannerUtil.getPatternTypeList().size()) {
                    break;
                }
                //預覽旗幟
                final ItemStack banner = new ItemStack(Material.BANNER, 1, currentBanner.getDurability());
                final BannerMeta bm = (BannerMeta) banner.getItemMeta();
                PatternType patternType = BannerUtil.getPatternTypeList().get(patternIndex);
                bm.addPattern(new Pattern(selectedColor, patternType));
                banner.setItemMeta(bm);
                menu.setClickableItem(i + 19 + (i / 8), banner).set(ClickType.LEFT, new Clickable() {
                    @Override
                    public void action() {
                        //新增Pattern
                        BannerMeta bm = (BannerMeta) banner.getItemMeta();
                        Pattern pattern = bm.getPattern(bm.numberOfPatterns() - 1);
                        BannerMeta currentBm = (BannerMeta) currentBanner.getItemMeta();
                        currentBm.addPattern(pattern);
                        currentBanner.setItemMeta(currentBm);
                        playerData.setCurrentEditBanner(currentBanner);
                        InventoryMenuUtil.openMenu(player);
                    }
                });
            }
            //更多Pattern
            ItemStack btnMorePattern = new ItemBuilder(Material.NETHER_STAR).amount(1).name(MessageUtil.format("&a" + tl("gui.more-patterns"))).build();
            menu.setClickableItem(51, btnMorePattern).set(ClickType.LEFT, new Clickable() {
                @Override
                public void action() {
                    playerData.setShowMorePatterns(!playerData.isShowMorePatterns());
                    InventoryMenuUtil.openMenu(player);
                }
            });
        }
        //返回
        ItemStack btnBackToMenu = new ItemBuilder(Material.WOOL).amount(1).durability(14).name(MessageUtil.format("&c" + tl("gui.back"))).build();
        menu.setClickableItem(45, btnBackToMenu).set(ClickType.LEFT, new Clickable() {
            @Override
            public void action() {
                InventoryMenuUtil.openMenu(player, InventoryMenuState.MAIN_MENU);
            }
        });
        if (currentBanner != null) {
            //建立旗幟
            ItemStack btnCreate = new ItemBuilder(Material.WOOL).amount(1).durability(5).name(MessageUtil.format("&a" + tl("gui.create"))).build();
            menu.setClickableItem(53, btnCreate).set(ClickType.LEFT, new Clickable() {
                @Override
                public void action() {
                    IOUtil.saveBanner(player, currentBanner);
                    playerData.setCurrentEditBanner(null);
                    InventoryMenuUtil.openMenu(player, InventoryMenuState.MAIN_MENU);
                }
            });
            //刪除
            ItemStack btnDelete = new ItemBuilder(Material.BARRIER).amount(1).name(MessageUtil.format("&c" + tl("gui.delete"))).build();
            menu.setClickableItem(47, btnDelete).set(ClickType.LEFT, new Clickable() {
                @Override
                public void action() {
                    playerData.setCurrentEditBanner(null);
                    InventoryMenuUtil.openMenu(player);
                }
            });
            if (currentBanner.hasItemMeta() && ((BannerMeta) currentBanner.getItemMeta()).numberOfPatterns() > 0) {
                //移除Pattern
                ItemStack btnRemovePattern = new ItemBuilder(Material.BARRIER).amount(1).name(MessageUtil.format("&c" + tl("gui.remove-last-pattern"))).build();
                menu.setClickableItem(49, btnRemovePattern).set(ClickType.LEFT, new Clickable() {
                    @Override
                    public void action() {
                        BannerMeta bm = (BannerMeta) currentBanner.getItemMeta();
                        bm.removePattern(bm.numberOfPatterns() - 1);
                        currentBanner.setItemMeta(bm);
                        playerData.setCurrentEditBanner(currentBanner);
                        InventoryMenuUtil.openMenu(player);
                    }
                });
            }
        }
        //開啟選單
        menu.open(player);
    }
}
