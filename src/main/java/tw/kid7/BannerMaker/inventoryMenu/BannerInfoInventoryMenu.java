package tw.kid7.BannerMaker.inventoryMenu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import tw.kid7.BannerMaker.BannerMaker;
import tw.kid7.BannerMaker.InventoryMenuState;
import tw.kid7.BannerMaker.PlayerData;
import tw.kid7.BannerMaker.clickableInventory.Clickable;
import tw.kid7.BannerMaker.clickableInventory.ClickableInventory;
import tw.kid7.BannerMaker.clickableInventory.ClickableItem;
import tw.kid7.BannerMaker.util.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static tw.kid7.BannerMaker.configuration.Language.tl;

public class BannerInfoInventoryMenu extends AbstractInventoryMenu {
    private static BannerInfoInventoryMenu instance = null;

    public static BannerInfoInventoryMenu getInstance() {
        if (instance == null) {
            instance = new BannerInfoInventoryMenu();
        }
        return instance;
    }

    @Override
    public void open(final Player player) {
        final PlayerData playerData = BannerMaker.getInstance().playerDataMap.get(player);
        //取得欲查看旗幟
        final ItemStack banner = playerData.getViewInfoBanner();
        //僅限旗幟
        if (!BannerUtil.isBanner(banner)) {
            //回到主選單
            InventoryMenuUtil.openMenu(player, InventoryMenuState.MAIN_MENU);
            return;
        }
        //建立選單
        ClickableInventory menu = ClickableInventory.create(playerData.getInventoryMenuState(), player, tl("gui.banner-info"));
        menu.setItem(0, banner);
        //patterns數量
        int patternCount = ((BannerMeta) banner.getItemMeta()).numberOfPatterns();
        String patternCountStr;
        if (patternCount > 0) {
            patternCountStr = patternCount + " " + tl("gui.pattern-s");
        } else {
            patternCountStr = tl("gui.no-patterns");
        }
        ItemStack signPatternCount;
        if (patternCount <= 6) {
            signPatternCount = new ItemBuilder(Material.SIGN).amount(1).name(MessageUtil.format("&a" + patternCountStr)).build();
        } else {
            signPatternCount = new ItemBuilder(Material.SIGN).amount(1).name(MessageUtil.format("&a" + patternCountStr)).lore(MessageUtil.format("&c" + tl("gui.uncraftable"))).build();
        }
        menu.setItem(1, signPatternCount);
        if (patternCount <= 6) {
            //材料是否充足
            ItemStack enoughMaterials;
            if (BannerUtil.hasEnoughMaterials(player.getInventory(), banner)) {
                enoughMaterials = new ItemBuilder(Material.SIGN).amount(1).name(MessageUtil.format("&a" + tl("gui.materials.enough"))).build();
            } else {
                enoughMaterials = new ItemBuilder(Material.SIGN).amount(1).name(MessageUtil.format("&c" + tl("gui.materials.not-enough"))).build();
            }
            menu.setItem(2, enoughMaterials);
            //材料清單
            List<Integer> materialPosition = Arrays.asList(9, 10, 11, 12, 18, 19, 20, 21, 27, 28, 29, 30, 36, 37, 38, 39);
            List<ItemStack> materialList = BannerUtil.getMaterials(banner);
            for (int i = 0; i < materialList.size() && i < materialPosition.size(); i++) {
                ItemStack materialItem = materialList.get(i);
                int position = materialPosition.get(i);
                menu.setItem(position, materialItem);
            }

            //合成表
            //當前頁數
            final int currentRecipePage = playerData.getCurrentRecipePage();
            //總頁數
            int totalPage = patternCount + 1;
            //外框
            ItemStack workbench = new ItemBuilder(Material.WORKBENCH).amount(currentRecipePage).name(MessageUtil.format("&a" + tl("gui.craft-recipe")))
                .lore(MessageUtil.format("&r(" + currentRecipePage + "/" + totalPage + ")")).build();
            menu.setItem(6, workbench);
            ItemStack border = new ItemBuilder(Material.STAINED_GLASS_PANE).amount(1).durability(12).name(" ").build();
            List<Integer> borderPosition = Arrays.asList(4, 5, 7, 8, 13, 17, 22, 26, 31, 35, 40, 41, 42, 43, 44);
            for (int i : borderPosition) {
                menu.setItem(i, border.clone());
            }
            //換頁按鈕
            //上一頁
            if (currentRecipePage > 1) {
                ItemStack prevPage = new ItemBuilder(Material.ARROW).amount(currentRecipePage - 1).name(MessageUtil.format("&a" + tl("gui.prev-page"))).build();
                menu.setClickableItem(22, prevPage).set(ClickType.LEFT, new Clickable() {
                    @Override
                    public void action() {
                        playerData.setCurrentRecipePage(currentRecipePage - 1);
                        InventoryMenuUtil.openMenu(player);
                    }
                });
            }
            //下一頁
            if (currentRecipePage < totalPage) {
                ItemStack nextPage = new ItemBuilder(Material.ARROW).amount(currentRecipePage + 1).name(MessageUtil.format("&a" + tl("gui.next-page"))).build();
                menu.setClickableItem(26, nextPage).set(ClickType.LEFT, new Clickable() {
                    @Override
                    public void action() {
                        playerData.setCurrentRecipePage(currentRecipePage + 1);
                        InventoryMenuUtil.openMenu(player);
                    }
                });
            }
            //合成表
            HashMap<Integer, ItemStack> patternRecipe = BannerUtil.getPatternRecipe(banner, currentRecipePage);
            List<Integer> craftPosition = Arrays.asList(14, 15, 16, 23, 24, 25, 32, 33, 34, 42);
            for (int i = 0; i < 10; i++) {
                int position = craftPosition.get(i);
                ItemStack itemStack = patternRecipe.get(i);
                menu.setItem(position, itemStack);
            }
        }
        //新增按鈕
        //嘗試取得key
        final String key = BannerUtil.getKey(banner);
        //刪除
        if (key != null) {
            //有KEY時（儲存於玩家資料時），才顯示刪除按鈕
            ItemStack btnDelete = new ItemBuilder(Material.BARRIER).amount(1).name(MessageUtil.format("&c" + tl("gui.delete"))).build();
            menu.setClickableItem(47, btnDelete).set(ClickType.LEFT, new Clickable() {
                @Override
                public void action() {
                    //刪除
                    IOUtil.removeBanner(player, key);
                    InventoryMenuUtil.openMenu(player, InventoryMenuState.MAIN_MENU);
                }
            });
        }
        //取得旗幟
        if (player.hasPermission("BannerMaker.getBanner")) {
            ItemBuilder btnGetBannerBuilder = new ItemBuilder(Material.WOOL).amount(1).durability(5).name(MessageUtil.format("&a" + tl("gui.get-this-banner")));
            //顯示名稱
            final String showName = BannerUtil.getName(banner);
            if (player.hasPermission("BannerMaker.getBanner.free")) {
                //具有免費取得權限
                //左鍵：免費取得
                btnGetBannerBuilder.lore(MessageUtil.format("&e[" + tl("gui.click.left") + "] &a" + tl("gui.get-banner-for-free")));
                ItemStack btnGetBanner = btnGetBannerBuilder.build();
                menu.setClickableItem(49, btnGetBanner).set(ClickType.LEFT, new Clickable() {
                    @Override
                    public void action() {
                        //取得旗幟
                        InventoryUtil.give(player, banner);
                        //顯示訊息
                        player.sendMessage(MessageUtil.format(true, "&a" + tl("gui.get-banner", showName)));
                        InventoryMenuUtil.openMenu(player);
                    }
                });
            } else {
                //左鍵：合成
                btnGetBannerBuilder.lore(MessageUtil.format("&e[" + tl("gui.click.left") + "] &a" + tl("gui.get-banner-by-craft")));
                //檢查是否啟用經濟
                if (BannerMaker.getInstance().econ != null) {
                    //右鍵：購買
                    Double price = EconUtil.getPrice(banner);
                    String priceStr = BannerMaker.getInstance().econ.format(price);
                    btnGetBannerBuilder.lore(MessageUtil.format("&e[" + tl("gui.click.right") + "] &a" + tl("gui.buy-banner-in-price", priceStr)));
                }
                ItemStack btnGetBanner = btnGetBannerBuilder.build();
                ClickableItem clickableItemGetBanner = menu.setClickableItem(49, btnGetBanner).set(ClickType.LEFT, new Clickable() {
                    @Override
                    public void action() {
                        //嘗試合成旗幟
                        boolean success = BannerUtil.craft(player, banner);
                        if (success) {
                            player.sendMessage(MessageUtil.format(true, "&a" + tl("gui.get-banner", showName)));
                        } else {
                            player.sendMessage(MessageUtil.format(true, "&c" + tl("gui.materials.not-enough")));
                        }
                        InventoryMenuUtil.openMenu(player);
                    }
                });
                //檢查是否啟用經濟
                if (BannerMaker.getInstance().econ != null) {
                    clickableItemGetBanner.set(ClickType.RIGHT, new Clickable() {
                        @Override
                        public void action() {
                            //取得旗幟
                            //嘗試給予玩家旗幟
                            boolean success = BannerUtil.buy(player, banner);
                            if (success) {
                                //顯示名稱
                                String showName = BannerUtil.getName(banner);
                                //顯示訊息
                                player.sendMessage(MessageUtil.format(true, "&a" + tl("gui.get-banner", showName)));
                            }
                            InventoryMenuUtil.openMenu(player);
                        }
                    });
                }
            }
            //檢查是否啟用經濟
            if (BannerMaker.getInstance().econ != null) {
                Double price = EconUtil.getPrice(banner);
                btnGetBannerBuilder.lore(MessageUtil.format("&a" + tl("gui.price", BannerMaker.getInstance().econ.format(price))));
            }
        }
        //複製並編輯
        ItemStack btnCloneAndEdit = new ItemBuilder(Material.BOOK_AND_QUILL).amount(1).name(MessageUtil.format("&9" + tl("gui.clone-and-edit"))).build();
        menu.setClickableItem(51, btnCloneAndEdit).set(ClickType.LEFT, new Clickable() {
            @Override
            public void action() {
                //設定為編輯中旗幟
                playerData.setCurrentEditBanner(banner);
                InventoryMenuUtil.openMenu(player, InventoryMenuState.CREATE_BANNER);
            }
        });

        //TODO 產生指令
        //返回
        ItemStack btnBackToMenu = new ItemBuilder(Material.WOOL).amount(1).durability(14).name(MessageUtil.format("&c" + tl("gui.back"))).build();
        menu.setClickableItem(45, btnBackToMenu).set(ClickType.LEFT, new Clickable() {
            @Override
            public void action() {
                if (BannerUtil.isAlphabetBanner(banner)) {
                    //若為Alphabet旗幟，回到Alphabet旗幟頁面
                    InventoryMenuUtil.openMenu(player, InventoryMenuState.CREATE_ALPHABET);
                    return;
                }
                InventoryMenuUtil.openMenu(player, InventoryMenuState.MAIN_MENU);
            }
        });
        //開啟選單
        player.openInventory(menu.toInventory());
    }
}
