package tw.kid7.BannerMaker.listener;

import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.ChatColor;
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
import tw.kid7.BannerMaker.util.IOUtil;
import tw.kid7.BannerMaker.util.InventoryUtil;
import tw.kid7.BannerMaker.util.MessageUtil;

import java.util.HashMap;
import java.util.List;

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
        State state = MAIN_MENU;
        if (BannerMaker.getInstance().stateMap.containsKey(player.getName())) {
            state = BannerMaker.getInstance().stateMap.get(player.getName());
        }
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

    public void onClickMainMenu(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack itemStack = event.getCurrentItem();
        if (event.getRawSlot() < 45) {
            //點擊旗幟
            //記錄選擇的索引值
            BannerMaker.getInstance().selectedIndex.put(player.getName(), event.getRawSlot());
            //重置頁數
            BannerMaker.getInstance().currentRecipePage.put(player.getName(), 1);
            //切換畫面
            BannerMaker.getInstance().stateMap.put(player.getName(), State.BANNER_INFO);
            //重新開啟選單
            InventoryUtil.openMenu(player);
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
                BannerMaker.getInstance().stateMap.put(player.getName(), State.CREATE_BANNER);
            } else if (buttonName.equalsIgnoreCase("Create Alphabet")) {
                //FIXME: 新增至語系檔
                BannerMaker.getInstance().stateMap.put(player.getName(), State.CREATE_ALPHABET);
            }
            //重新開啟選單
            InventoryUtil.openMenu(player);
        }

    }

    public void onClickCreateBanner(InventoryClickEvent event) {
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
            InventoryUtil.openMenu(player);
        } else if (event.getRawSlot() >= 19 && event.getRawSlot() <= 44 && event.getRawSlot() % 9 != 0) {
            //新增Pattern
            BannerMeta bm = (BannerMeta) itemStack.getItemMeta();
            Pattern pattern = bm.getPattern(bm.numberOfPatterns() - 1);
            BannerMeta currentBm = (BannerMeta) currentBanner.getItemMeta();
            currentBm.addPattern(pattern);
            currentBanner.setItemMeta(currentBm);
            BannerMaker.getInstance().currentBanner.put(player.getName(), currentBanner);
            //重新開啟選單
            InventoryUtil.openMenu(player);
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
                BannerMaker.getInstance().stateMap.put(player.getName(), State.MAIN_MENU);
            } else if (buttonName.equalsIgnoreCase(Language.getIgnoreColors("gui.delete"))) {
                BannerMaker.getInstance().currentBanner.remove(player.getName());
            } else if (buttonName.equalsIgnoreCase(Language.getIgnoreColors("gui.back"))) {
                BannerMaker.getInstance().stateMap.put(player.getName(), State.MAIN_MENU);
            }
            //重新開啟選單
            InventoryUtil.openMenu(player);
        }

    }

    public void onClickCreateAlphabet(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack itemStack = event.getCurrentItem();
        //取得當前編輯中的字母
        ItemStack currentAlphabet = BannerMaker.getInstance().currentAlphabet.get(player.getName());
        if (currentAlphabet == null) {
            if (event.getRawSlot() < 45) {
                //選擇字母
                BannerMaker.getInstance().currentAlphabet.put(player.getName(), itemStack);
            } else {
                //點擊按鈕
                String buttonName = itemStack.getItemMeta().getDisplayName();
                buttonName = ChatColor.stripColor(buttonName);
                if (buttonName.equalsIgnoreCase(Language.getIgnoreColors("gui.back"))) {
                    BannerMaker.getInstance().stateMap.put(player.getName(), State.MAIN_MENU);
                }
            }
            //重新開啟選單
            InventoryUtil.openMenu(player);
        } else {
            //選擇顏色
            if (event.getRawSlot() < 18) {
                //TODO: 選擇底色
            } else if (event.getRawSlot() < 36) {
                //TODO: 選擇主要顏色
            } else {
                //點擊按鈕
                String buttonName = itemStack.getItemMeta().getDisplayName();
                buttonName = ChatColor.stripColor(buttonName);
                //TODO: 取得旗幟按鈕
                if (buttonName.equalsIgnoreCase(Language.getIgnoreColors("gui.back"))) {
                    BannerMaker.getInstance().currentAlphabet.remove(player.getName());
                }
            }
            //重新開啟選單
            InventoryUtil.openMenu(player);
        }
    }

    public void onClickBannerInfo(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack itemStack = event.getCurrentItem();
        if (event.getRawSlot() == 22 || event.getRawSlot() == 26 || event.getRawSlot() >= 45) {
            //點擊按鈕
            String buttonName = itemStack.getItemMeta().getDisplayName();
            buttonName = ChatColor.stripColor(buttonName);
            //當前索引值
            int index = BannerMaker.getInstance().selectedIndex.get(player.getName());
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
                    List<ItemStack> bannerList = IOUtil.loadBannerList(player);
                    ItemStack banner = bannerList.get(index);
                    //檢查是否啟用經濟
                    if (BannerMaker.econ != null && !player.hasPermission("BannerMaker.getBanner.free")) {
                        FileConfiguration config = ConfigManager.get("config.yml");
                        Double price = config.getDouble("Economy.Price", 100);
                        //檢查財產是否足夠
                        if (BannerMaker.econ.has(player, price)) {
                            EconomyResponse response = BannerMaker.econ.withdrawPlayer(player, price);
                            //檢查交易是否成功
                            if (response.transactionSuccess()) {
                                HashMap<Integer, ItemStack> itemsCanNotAddToInv = player.getInventory().addItem(banner);
                                if (!itemsCanNotAddToInv.isEmpty()) {
                                    player.getWorld().dropItem(player.getLocation(), itemsCanNotAddToInv.get(0));
                                }
                                player.sendMessage(MessageUtil.format(true, "&a" + Language.get("general.money-transaction", BannerMaker.econ.format(response.amount), BannerMaker.econ.format(response.balance))));
                                success = true;
                            } else {
                                player.sendMessage(MessageUtil.format(true, "&aError: " + response.errorMessage));
                            }
                        } else {
                            player.sendMessage(MessageUtil.format(true, "&c" + Language.get("general.no-money")));
                        }
                    } else {
                        HashMap<Integer, ItemStack> itemsCanNotAddToInv = player.getInventory().addItem(banner);
                        if (!itemsCanNotAddToInv.isEmpty()) {
                            player.getWorld().dropItem(player.getLocation(), itemsCanNotAddToInv.get(0));
                        }
                        success = true;
                    }
                    if (success) {
                        //所在頁數
                        int currentBannerPage = BannerMaker.getInstance().currentBannerPage.get(player.getName());
                        //索引值
                        int showIndex = index + (currentBannerPage - 1) * 45;
                        //顯示訊息
                        player.sendMessage(MessageUtil.format(true, "&a" + Language.get("gui.get-banner", showIndex)));
                    }
                } else {
                    player.sendMessage(MessageUtil.format(true, "&c" + Language.get("general.no-permission")));
                }
            } else if (buttonName.equalsIgnoreCase(Language.getIgnoreColors("gui.clone-and-edit"))) {
                //取得當前旗幟
                List<ItemStack> bannerList = IOUtil.loadBannerList(player);
                ItemStack banner = bannerList.get(index);
                //設定為編輯中旗幟
                BannerMaker.getInstance().currentBanner.put(player.getName(), banner);
                BannerMaker.getInstance().stateMap.put(player.getName(), State.CREATE_BANNER);

            } else if (buttonName.equalsIgnoreCase(Language.getIgnoreColors("gui.delete"))) {
                IOUtil.removeBanner(player, index);
                BannerMaker.getInstance().selectedIndex.remove(player.getName());
                BannerMaker.getInstance().stateMap.put(player.getName(), State.MAIN_MENU);
            } else if (buttonName.equalsIgnoreCase(Language.getIgnoreColors("gui.back"))) {
                BannerMaker.getInstance().stateMap.put(player.getName(), State.MAIN_MENU);
            }
            //重新開啟選單
            InventoryUtil.openMenu(player);
        }
    }
}
