package club.kid7.bannermaker.customMenu;

import club.kid7.bannermaker.AlphabetBanner;
import club.kid7.bannermaker.BannerMaker;
import club.kid7.bannermaker.PlayerData;
import club.kid7.bannermaker.util.BannerUtil;
import club.kid7.bannermaker.util.EconUtil;
import club.kid7.bannermaker.util.IOUtil;
import club.kid7.bannermaker.util.InventoryUtil;
import club.kid7.bannermaker.util.MessageUtil;
import club.kid7.pluginutilities.gui.CustomGUIInventory;
import club.kid7.pluginutilities.gui.CustomGUIItem;
import club.kid7.pluginutilities.gui.CustomGUIManager;
import club.kid7.pluginutilities.gui.CustomGUIMenu;
import club.kid7.pluginutilities.kitemstack.KItemStack;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static club.kid7.bannermaker.configuration.Language.tl;

public class BannerInfoMenu implements CustomGUIMenu {
    @Override
    public CustomGUIInventory build(final Player player) {
        final PlayerData playerData = BannerMaker.getInstance().playerDataMap.get(player);
        //取得欲查看旗幟
        final ItemStack banner = playerData.getViewInfoBanner();
        //僅限旗幟
        if (!BannerUtil.isBanner(banner)) {
            //回到主選單
            CustomGUIManager.open(player, MainMenu.class);
            return null;
        }
        //建立選單
        String title = MessageUtil.format(tl("gui.prefix") + tl("gui.banner-info"));
        CustomGUIInventory menu = new CustomGUIInventory(title);
        menu.setItem(0, banner);
        //patterns數量
        int patternCount = ((BannerMeta) Objects.requireNonNull(banner.getItemMeta())).numberOfPatterns();
        String patternCountStr;
        if (patternCount > 0) {
            patternCountStr = patternCount + " " + tl("gui.pattern-s");
        } else {
            patternCountStr = tl("gui.no-patterns");
        }
        KItemStack signPatternCount;
        if (BannerUtil.isCraftableInSurvival(banner)) {
            signPatternCount = new KItemStack(Material.OAK_SIGN).name(MessageUtil.format("&a" + patternCountStr));
        } else {
            signPatternCount = new KItemStack(Material.OAK_SIGN).name(MessageUtil.format("&a" + patternCountStr)).lore(MessageUtil.format("&c" + tl("gui.uncraftable")));
        }
        menu.setItem(1, signPatternCount);
        if (BannerUtil.isCraftableInSurvival(banner)) {
            //材料是否充足
            KItemStack enoughMaterials;
            if (BannerUtil.hasEnoughMaterials(player.getInventory(), banner)) {
                enoughMaterials = new KItemStack(Material.OAK_SIGN).name(MessageUtil.format("&a" + tl("gui.materials.enough")));
            } else {
                enoughMaterials = new KItemStack(Material.OAK_SIGN).name(MessageUtil.format("&c" + tl("gui.materials.not-enough")));
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
            KItemStack border = new KItemStack(Material.BROWN_STAINED_GLASS_PANE).name(" ");
            List<Integer> borderPosition = Arrays.asList(4, 5, 7, 8, 13, 17, 22, 26, 31, 35, 40, 41, 42, 43, 44);
            for (int i : borderPosition) {
                menu.setItem(i, border.clone());
            }
            //換頁按鈕
            //上一頁
            if (currentRecipePage > 1) {
                KItemStack prevPage = new KItemStack(Material.ARROW).amount(currentRecipePage - 1).name(MessageUtil.format("&a" + tl("gui.prev-page")));
                menu.setClickableItem(22, prevPage).set(ClickType.LEFT, event -> {
                    playerData.setCurrentRecipePage(currentRecipePage - 1);
                    CustomGUIManager.openPrevious(player);
                });
            }
            //下一頁
            if (currentRecipePage < totalPage) {
                KItemStack nextPage = new KItemStack(Material.ARROW).amount(currentRecipePage + 1).name(MessageUtil.format("&a" + tl("gui.next-page")));
                menu.setClickableItem(26, nextPage).set(ClickType.LEFT, event -> {
                    playerData.setCurrentRecipePage(currentRecipePage + 1);
                    CustomGUIManager.openPrevious(player);
                });
            }
            //取得合成表配方
            HashMap<Integer, ItemStack> patternRecipe = BannerUtil.getPatternRecipe(banner, currentRecipePage);
            //合成表圖示
            KItemStack workbench = new KItemStack(Material.CRAFTING_TABLE).amount(currentRecipePage)
                .name(MessageUtil.format("&a" + tl("gui.craft-recipe")))
                .lore(MessageUtil.format("&r(" + currentRecipePage + "/" + totalPage + ")"));
            if (BannerUtil.isLoomRecipe(patternRecipe)) {
                workbench.setType(Material.LOOM);
            }
            menu.setItem(6, workbench);
            //合成表
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
            KItemStack btnDelete = new KItemStack(Material.BARRIER).name(MessageUtil.format("&c" + tl("gui.delete")));
            menu.setClickableItem(47, btnDelete).set(ClickType.LEFT, event -> {
                //刪除
                IOUtil.removeBanner(player, key);
                CustomGUIManager.open(player, MainMenu.class);
            });
        }
        //取得旗幟
        if (player.hasPermission("BannerMaker.getBanner")) {
            KItemStack btnGetBanner = new KItemStack(Material.LIME_WOOL).name(MessageUtil.format("&a" + tl("gui.get-this-banner")));
            //顯示名稱
            final String showName = BannerUtil.getName(banner);
            if (player.hasPermission("BannerMaker.getBanner.free")) {
                //具有免費取得權限
                //左鍵：免費取得
                btnGetBanner.lore(MessageUtil.format("&e[" + tl("gui.click.left") + "] &a" + tl("gui.get-banner-for-free")));
                menu.setClickableItem(49, btnGetBanner).set(ClickType.LEFT, event -> {
                    //取得旗幟
                    InventoryUtil.give(player, banner);
                    //顯示訊息
                    player.sendMessage(MessageUtil.format(true, "&a" + tl("gui.get-banner", showName)));
                    CustomGUIManager.openPrevious(player);
                });
            } else {
                //左鍵：合成
                btnGetBanner.lore(MessageUtil.format("&e[" + tl("gui.click.left") + "] &a" + tl("gui.get-banner-by-craft")));
                //檢查是否啟用經濟
                if (BannerMaker.getInstance().econ != null) {
                    //右鍵：購買
                    double price = EconUtil.getPrice(banner);
                    String priceStr = BannerMaker.getInstance().econ.format(price);
                    btnGetBanner.lore(MessageUtil.format("&e[" + tl("gui.click.right") + "] &a" + tl("gui.buy-banner-in-price", priceStr)));
                }
                CustomGUIItem customGUIItemGetBanner = menu.setClickableItem(49, btnGetBanner).set(ClickType.LEFT, event -> {
                    //嘗試合成旗幟
                    boolean success = BannerUtil.craft(player, banner);
                    if (success) {
                        player.sendMessage(MessageUtil.format(true, "&a" + tl("gui.get-banner", showName)));
                    } else {
                        player.sendMessage(MessageUtil.format(true, "&c" + tl("gui.materials.not-enough")));
                    }
                    CustomGUIManager.openPrevious(player);
                });
                //檢查是否啟用經濟
                if (BannerMaker.getInstance().econ != null) {
                    customGUIItemGetBanner.set(ClickType.RIGHT, event -> {
                        //取得旗幟
                        //嘗試給予玩家旗幟
                        boolean success = BannerUtil.buy(player, banner);
                        if (success) {
                            player.sendMessage(MessageUtil.format(true, "&a" + tl("gui.get-banner", showName)));
                        }
                        CustomGUIManager.openPrevious(player);
                    });
                }
            }
        }
        //複製並編輯
        KItemStack btnCloneAndEdit = new KItemStack(Material.WRITABLE_BOOK).name(MessageUtil.format("&9" + tl("gui.clone-and-edit")));
        menu.setClickableItem(51, btnCloneAndEdit).set(ClickType.LEFT, event -> {
            //設定為編輯中旗幟
            playerData.setCurrentEditBanner(banner);
            CustomGUIManager.open(player, CreateBannerMenu.class);
        });

        //返回
        KItemStack btnBackToMenu = new KItemStack(Material.RED_WOOL).name(MessageUtil.format("&c" + tl("gui.back")));
        menu.setClickableItem(45, btnBackToMenu).set(ClickType.LEFT, event -> {
            if (AlphabetBanner.isAlphabetBanner(banner)) {
                //若為Alphabet旗幟，回到Alphabet旗幟頁面
                CustomGUIManager.open(player, CreateAlphabetMenu.class);
                return;
            }
            CustomGUIManager.open(player, MainMenu.class);
        });
        return menu;
    }
}
