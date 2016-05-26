package tw.kid7.BannerMaker.inventoryMenu;

import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import tw.kid7.BannerMaker.BannerMaker;
import tw.kid7.BannerMaker.State;
import tw.kid7.BannerMaker.configuration.ConfigManager;
import tw.kid7.BannerMaker.configuration.Language;
import tw.kid7.BannerMaker.util.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class BannerInfoInventoryMenu extends AbstractInventoryMenu {
    private static BannerInfoInventoryMenu instance = null;

    public static BannerInfoInventoryMenu getInstance() {
        if (instance == null) {
            instance = new BannerInfoInventoryMenu();
        }
        return instance;
    }

    @Override
    public void open(Player player) {
        //取得欲查看旗幟
        ItemStack banner = BannerMaker.getInstance().viewInfoBanner.get(player.getName());
        //僅限旗幟
        if (!BannerUtil.isBanner(banner)) {
            //回到主選單
            State.set(player, State.MAIN_MENU);
            //重新開啟選單
            InventoryMenuUtil.openMenu(player);
            return;
        }
        //建立選單
        Inventory menu = Bukkit.createInventory(null, 54, MessageUtil.format("&b&m&r" + Language.get("gui.prefix") + Language.get("gui.banner-info")));
        menu.setItem(0, banner);
        //patterns數量
        int patternCount = ((BannerMeta) banner.getItemMeta()).numberOfPatterns();
        String patternCountStr = "";
        if (patternCount > 0) {
            patternCountStr = patternCount + " " + Language.get("gui.pattern-s");
        } else {
            patternCountStr = Language.get("gui.no-patterns");
        }
        ItemStack signPatternCount;
        if (patternCount <= 6) {
            signPatternCount = new ItemBuilder(Material.SIGN).amount(1).name(MessageUtil.format("&a" + patternCountStr)).build();
        } else {
            signPatternCount = new ItemBuilder(Material.SIGN).amount(1).name(MessageUtil.format("&a" + patternCountStr)).lore(MessageUtil.format("&c" + Language.get("gui.uncraftable"))).build();
        }
        menu.setItem(1, signPatternCount);
        if (patternCount <= 6) {
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
            int currentRecipePage = BannerMaker.getInstance().currentRecipePage.get(player.getName());
            //總頁數
            int totalPage = patternCount + 1;
            //外框
            ItemStack workbench = new ItemBuilder(Material.WORKBENCH).amount(currentRecipePage).name(MessageUtil.format("&a" + Language.get("gui.craft-recipe")))
                .lore(MessageUtil.format("(" + currentRecipePage + "/" + totalPage + ")")).build();
            menu.setItem(6, workbench);
            ItemStack border = new ItemBuilder(Material.STAINED_GLASS_PANE).amount(1).durability(12).name(" ").build();
            List<Integer> borderPosition = Arrays.asList(4, 5, 7, 8, 13, 17, 22, 26, 31, 35, 40, 41, 42, 43, 44);
            for (int i : borderPosition) {
                menu.setItem(i, border.clone());
            }
            //換頁按鈕
            //上一頁
            if (currentRecipePage > 1) {
                ItemStack prevPage = new ItemBuilder(Material.ARROW).amount(currentRecipePage - 1).name(MessageUtil.format("&a" + Language.get("gui.prev-page"))).build();
                menu.setItem(22, prevPage);
            }
            //下一頁
            if (currentRecipePage < totalPage) {
                ItemStack nextPage = new ItemBuilder(Material.ARROW).amount(currentRecipePage + 1).name(MessageUtil.format("&a" + Language.get("gui.next-page"))).build();
                menu.setItem(26, nextPage);
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
        //嘗試取德key
        String key = BannerUtil.getKey(banner);
        //刪除
        if (key != null) {
            //有KEY時（儲存於玩家資料時），才顯示刪除按鈕
            ItemStack btnDelete = new ItemBuilder(Material.BARRIER).amount(1).name(MessageUtil.format("&c" + Language.get("gui.delete"))).build();
            menu.setItem(47, btnDelete);
        }
        //取得旗幟
        if (player.hasPermission("BannerMaker.getBanner")) {
            //檢查是否啟用經濟
            ItemBuilder btnGetBannerBuilder = new ItemBuilder(Material.WOOL).amount(1).durability(5).name(MessageUtil.format("&a" + Language.get("gui.get-this-banner")));
            if (BannerMaker.econ != null) {
                FileConfiguration config = ConfigManager.get("config.yml");
                Double price = config.getDouble("Economy.Price", 100);
                //FIXME 可能造成 IndexOutOfBoundsException: No group 1
                btnGetBannerBuilder.lore(MessageUtil.format("&a" + Language.get("gui.price", BannerMaker.econ.format(price))));
            }
            ItemStack btnGetBanner = btnGetBannerBuilder.build();
            menu.setItem(49, btnGetBanner);
        }
        //複製並編輯
        ItemStack btnCloneAndEdit = new ItemBuilder(Material.BOOK_AND_QUILL).amount(1).name(MessageUtil.format("&9" + Language.get("gui.clone-and-edit"))).build();
        menu.setItem(51, btnCloneAndEdit);

        //TODO 產生指令
        //返回
        ItemStack btnBackToMenu = new ItemBuilder(Material.WOOL).amount(1).durability(14).name(MessageUtil.format("&c" + Language.get("gui.back"))).build();
        menu.setItem(45, btnBackToMenu);
        //開啟選單
        player.openInventory(menu);
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack itemStack = event.getCurrentItem();
        if (event.getRawSlot() == 22 || event.getRawSlot() == 26 || event.getRawSlot() >= 45) {
            //點擊按鈕
            String buttonName = itemStack.getItemMeta().getDisplayName();
            buttonName = ChatColor.stripColor(buttonName);
            //取得欲查看旗幟
            ItemStack banner = BannerMaker.getInstance().viewInfoBanner.get(player.getName());
            //當前頁數
            int currentRecipePage = BannerMaker.getInstance().currentRecipePage.get(player.getName());
            //修改狀態
            if (buttonName.equalsIgnoreCase(Language.getIgnoreColors("gui.prev-page"))) {
                BannerMaker.getInstance().currentRecipePage.put(player.getName(), currentRecipePage - 1);
            } else if (buttonName.equalsIgnoreCase(Language.getIgnoreColors("gui.next-page"))) {
                BannerMaker.getInstance().currentRecipePage.put(player.getName(), currentRecipePage + 1);
            } else if (buttonName.equalsIgnoreCase(Language.getIgnoreColors("gui.get-this-banner"))) {
                //取得旗幟
                if (player.hasPermission("BannerMaker.getBanner")) {
                    //交易成功的標記
                    boolean success = false;
                    //檢查是否啟用經濟
                    if (BannerMaker.econ != null && !player.hasPermission("BannerMaker.getBanner.free")) {
                        FileConfiguration config = ConfigManager.get("config.yml");
                        Double price = config.getDouble("Economy.Price", 100);
                        //檢查財產是否足夠
                        if (BannerMaker.econ.has(player, price)) {
                            EconomyResponse response = BannerMaker.econ.withdrawPlayer(player, price);
                            //檢查交易是否成功
                            if (response.transactionSuccess()) {
                                InventoryUtil.give(player, banner);
                                player.sendMessage(MessageUtil.format(true, "&a" + Language.get("general.money-transaction", BannerMaker.econ.format(response.amount), BannerMaker.econ.format(response.balance))));
                                success = true;
                            } else {
                                player.sendMessage(MessageUtil.format(true, "&aError: " + response.errorMessage));
                            }
                        } else {
                            player.sendMessage(MessageUtil.format(true, "&c" + Language.get("general.no-money")));
                        }
                    } else {
                        InventoryUtil.give(player, banner);
                        success = true;
                    }
                    if (success) {
                        //顯示名稱
                        String showName = BannerUtil.getName(banner);
                        //顯示訊息
                        player.sendMessage(MessageUtil.format(true, "&a" + Language.get("gui.get-banner", showName)));
                    }
                } else {
                    player.sendMessage(MessageUtil.format(true, "&c" + Language.get("general.no-permission")));
                }
            } else if (buttonName.equalsIgnoreCase(Language.getIgnoreColors("gui.clone-and-edit"))) {
                //設定為編輯中旗幟
                BannerMaker.getInstance().currentBanner.put(player.getName(), banner);
                State.set(player, State.CREATE_BANNER);

            } else if (buttonName.equalsIgnoreCase(Language.getIgnoreColors("gui.delete"))) {
                String key = BannerUtil.getKey(banner);
                if (key != null) {
                    //有KEY時（儲存於玩家資料時），才能刪除
                    IOUtil.removeBanner(player, key);
                }
                State.set(player, State.MAIN_MENU);
            } else if (buttonName.equalsIgnoreCase(Language.getIgnoreColors("gui.back"))) {
                //返回
                String key = BannerUtil.getKey(banner);
                if (key == null) {
                    //若無KEY（Alphabet旗幟），回到Alphabet旗幟頁面
                    State.set(player, State.CREATE_ALPHABET);
                } else {
                    State.set(player, State.MAIN_MENU);
                }
            }
            //重新開啟選單
            InventoryMenuUtil.openMenu(player);
        }
    }
}
