package tw.kid7.BannerMaker.listener;

import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import tw.kid7.BannerMaker.BannerMaker;
import tw.kid7.BannerMaker.State;
import tw.kid7.BannerMaker.configuration.ConfigManager;
import tw.kid7.BannerMaker.configuration.Language;
import tw.kid7.BannerMaker.util.*;

import java.util.HashMap;

import static tw.kid7.BannerMaker.State.MAIN_MENU;

public class InventoryClickEventListener implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getInventory().getName().startsWith(ChatColor.translateAlternateColorCodes('&', "&b&m"))) {
            return;
        }
        //取消事件
        event.setCancelled(true);
        //只處理箱子內的
        if (event.getRawSlot() >= 54) {
            return;
        }
        //不處理空格
        if (event.getCurrentItem() == null || event.getCurrentItem().getType().equals(Material.AIR)) {
            return;
        }
        //取得玩家狀態
        Player player = (Player) event.getWhoClicked();
        State state = State.get(player);
        //根據狀態決定行為
        switch (state) {
            case CREATE_BANNER:
                onClickCreateBanner(event);
                break;
            case CREATE_ALPHABET:
                onClickCreateAlphabet(event);
                break;
            case BANNER_INFO:
                onClickBannerInfo(event);
                break;
            case MAIN_MENU:
            default:
                onClickMainMenu(event);
        }
    }

    private void onClickMainMenu(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack itemStack = event.getCurrentItem();
        if (event.getRawSlot() < 45) {
            //點擊旗幟
            //記錄選擇的旗幟
            BannerMaker.getInstance().viewInfoBanner.put(player.getName(), itemStack);
            //重置頁數
            BannerMaker.getInstance().currentRecipePage.put(player.getName(), 1);
            //切換畫面
            State.set(player, State.BANNER_INFO);
            //重新開啟選單
            InventoryMenuUtil.openMenu(player);
        } else {
            //點擊按鈕
            String buttonName = itemStack.getItemMeta().getDisplayName();
            buttonName = ChatColor.stripColor(buttonName);
            //當前頁數
            int currentBannerPage = 1;
            if (BannerMaker.getInstance().currentBannerPage.containsKey(player.getName())) {
                currentBannerPage = BannerMaker.getInstance().currentBannerPage.get(player.getName());
            } else {
                BannerMaker.getInstance().currentBannerPage.put(player.getName(), 1);
            }
            //修改狀態
            if (buttonName.equalsIgnoreCase(Language.getIgnoreColors("gui.prev-page"))) {
                BannerMaker.getInstance().currentBannerPage.put(player.getName(), currentBannerPage - 1);
            } else if (buttonName.equalsIgnoreCase(Language.getIgnoreColors("gui.next-page"))) {
                BannerMaker.getInstance().currentBannerPage.put(player.getName(), currentBannerPage + 1);
            } else if (buttonName.equalsIgnoreCase(Language.getIgnoreColors("gui.create-banner"))) {
                State.set(player, State.CREATE_BANNER);
            } else if (buttonName.equalsIgnoreCase(Language.getIgnoreColors("gui.alphabet-and-number"))) {
                BannerMaker.getInstance().currentAlphabetBanner.remove(player.getName());
                State.set(player, State.CREATE_ALPHABET);
            }
            //重新開啟選單
            InventoryMenuUtil.openMenu(player);
        }

    }

    private void onClickCreateBanner(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack itemStack = event.getCurrentItem();
        //取得當前編輯中的旗幟
        ItemStack currentBanner = BannerMaker.getInstance().currentBanner.get(player.getName());
        if (event.getRawSlot() >= 1 && event.getRawSlot() <= 17 && event.getRawSlot() % 9 != 0) {
            if (currentBanner == null) {
                //選擇底色
                BannerMaker.getInstance().currentBanner.put(player.getName(), itemStack);
            } else {
                //點擊顏色
                BannerMaker.getInstance().selectedColor.put(player.getName(), (int) itemStack.getDurability());
            }
            //重新開啟選單
            InventoryMenuUtil.openMenu(player);
        } else if (event.getRawSlot() >= 19 && event.getRawSlot() <= 44 && event.getRawSlot() % 9 != 0) {
            //新增Pattern
            BannerMeta bm = (BannerMeta) itemStack.getItemMeta();
            Pattern pattern = bm.getPattern(bm.numberOfPatterns() - 1);
            BannerMeta currentBm = (BannerMeta) currentBanner.getItemMeta();
            currentBm.addPattern(pattern);
            currentBanner.setItemMeta(currentBm);
            BannerMaker.getInstance().currentBanner.put(player.getName(), currentBanner);
            //重新開啟選單
            InventoryMenuUtil.openMenu(player);
        } else if (event.getRawSlot() >= 45) {
            //點擊按鈕
            String buttonName = itemStack.getItemMeta().getDisplayName();
            buttonName = ChatColor.stripColor(buttonName);
            //修改狀態
            if (buttonName.equalsIgnoreCase(Language.getIgnoreColors("gui.more-patterns"))) {
                if (BannerMaker.getInstance().morePatterns.containsKey(player.getName())) {
                    BannerMaker.getInstance().morePatterns.put(player.getName(), !BannerMaker.getInstance().morePatterns.get(player.getName()));
                } else {
                    BannerMaker.getInstance().morePatterns.put(player.getName(), true);
                }
            } else if (buttonName.equalsIgnoreCase(Language.getIgnoreColors("gui.remove-last-pattern"))) {
                if (currentBanner.hasItemMeta() && ((BannerMeta) currentBanner.getItemMeta()).numberOfPatterns() > 0) {
                    BannerMeta bm = (BannerMeta) currentBanner.getItemMeta();
                    bm.removePattern(bm.numberOfPatterns() - 1);
                    currentBanner.setItemMeta(bm);
                    BannerMaker.getInstance().currentBanner.put(player.getName(), currentBanner);
                }
            } else if (buttonName.equalsIgnoreCase(Language.getIgnoreColors("gui.create"))) {
                IOUtil.saveBanner(player, currentBanner);
                BannerMaker.getInstance().currentBanner.remove(player.getName());
                State.set(player, State.MAIN_MENU);
            } else if (buttonName.equalsIgnoreCase(Language.getIgnoreColors("gui.delete"))) {
                BannerMaker.getInstance().currentBanner.remove(player.getName());
            } else if (buttonName.equalsIgnoreCase(Language.getIgnoreColors("gui.back"))) {
                State.set(player, State.MAIN_MENU);
            }
            //重新開啟選單
            InventoryMenuUtil.openMenu(player);
        }

    }

    private void onClickCreateAlphabet(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack itemStack = event.getCurrentItem();
        //取得當前編輯中的字母
        AlphabetBanner currentAlphabetBanner = BannerMaker.getInstance().currentAlphabetBanner.get(player.getName());
        if (currentAlphabetBanner == null) {
            if (event.getRawSlot() < 45) {
                //選擇字母
                boolean alphabetBorder = true;
                if (BannerMaker.getInstance().alphabetBorder.containsKey(player.getName())) {
                    alphabetBorder = BannerMaker.getInstance().alphabetBorder.get(player.getName());
                }
                currentAlphabetBanner = new AlphabetBanner(itemStack.getItemMeta().getDisplayName(), DyeColor.WHITE, DyeColor.BLACK, alphabetBorder);
                BannerMaker.getInstance().currentAlphabetBanner.put(player.getName(), currentAlphabetBanner);
            } else {
                //點擊按鈕
                String buttonName = itemStack.getItemMeta().getDisplayName();
                buttonName = ChatColor.stripColor(buttonName);
                if (buttonName.equalsIgnoreCase(Language.getIgnoreColors("gui.toggle-border"))) {
                    //切換有無邊框
                    boolean alphabetBorder = true;
                    if (BannerMaker.getInstance().alphabetBorder.containsKey(player.getName())) {
                        alphabetBorder = BannerMaker.getInstance().alphabetBorder.get(player.getName());
                    }
                    alphabetBorder = !alphabetBorder;
                    BannerMaker.getInstance().alphabetBorder.put(player.getName(), alphabetBorder);
                } else if (buttonName.equalsIgnoreCase(Language.getIgnoreColors("gui.back"))) {
                    State.set(player, State.MAIN_MENU);
                }
            }
            //重新開啟選單
            InventoryMenuUtil.openMenu(player);
        } else {
            //選擇顏色
            if (event.getRawSlot() < 1) {
                //預覽圖
            } else if (event.getRawSlot() < 18) {
                //選擇底色
                currentAlphabetBanner.baseColor = DyeColor.getByDyeData((byte) itemStack.getDurability());
                BannerMaker.getInstance().currentAlphabetBanner.put(player.getName(), currentAlphabetBanner);
            } else if (event.getRawSlot() < 36) {
                //選擇主要顏色
                currentAlphabetBanner.dyeColor = DyeColor.getByDyeData((byte) itemStack.getDurability());
                BannerMaker.getInstance().currentAlphabetBanner.put(player.getName(), currentAlphabetBanner);
            } else {
                //點擊按鈕
                String buttonName = itemStack.getItemMeta().getDisplayName();
                buttonName = ChatColor.stripColor(buttonName);
                if (buttonName.equalsIgnoreCase(Language.getIgnoreColors("gui.toggle-border"))) {
                    //切換有無邊框
                    currentAlphabetBanner.bordered = !currentAlphabetBanner.bordered;
                    BannerMaker.getInstance().currentAlphabetBanner.put(player.getName(), currentAlphabetBanner);
                } else if (buttonName.equalsIgnoreCase(Language.getIgnoreColors("gui.banner-info"))) {
                    //檢視旗幟資訊
                    BannerMaker.getInstance().viewInfoBanner.put(player.getName(), currentAlphabetBanner.toItemStack());
                    //重置頁數
                    BannerMaker.getInstance().currentRecipePage.put(player.getName(), 1);
                    State.set(player, State.BANNER_INFO);
                } else if (buttonName.equalsIgnoreCase(Language.getIgnoreColors("gui.back"))) {
                    BannerMaker.getInstance().currentAlphabetBanner.remove(player.getName());
                }
            }
            //重新開啟選單
            InventoryMenuUtil.openMenu(player);
        }
    }

    private void onClickBannerInfo(InventoryClickEvent event) {
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
