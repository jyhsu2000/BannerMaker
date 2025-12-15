package club.kid7.bannermaker.customMenu;

import club.kid7.bannermaker.AlphabetBanner;
import club.kid7.bannermaker.BannerMaker;
import club.kid7.bannermaker.PlayerData;
import club.kid7.bannermaker.util.BannerUtil;
import club.kid7.bannermaker.util.EconUtil;
import club.kid7.bannermaker.util.IOUtil;
import club.kid7.bannermaker.util.InventoryUtil;
import club.kid7.bannermaker.util.ItemBuilder;
import club.kid7.bannermaker.util.MessageComponentUtil;
import club.kid7.pluginutilities.gui.ClickAction;
import club.kid7.pluginutilities.gui.CustomGUIInventory;
import club.kid7.pluginutilities.gui.CustomGUIManager;
import club.kid7.pluginutilities.gui.CustomGUIMenu;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
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
        String title = BannerMaker.getInstance().getMessageService().formatToString(tl("gui.prefix") + tl("gui.banner-info"));
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
        ItemStack signPatternCount;
        if (BannerUtil.isCraftableInSurvival(banner)) {
            signPatternCount = new ItemBuilder(Material.OAK_SIGN).name(BannerMaker.getInstance().getMessageService().formatToString("&a" + patternCountStr)).build();
        } else {
            signPatternCount = new ItemBuilder(Material.OAK_SIGN).name(BannerMaker.getInstance().getMessageService().formatToString("&a" + patternCountStr)).lore(BannerMaker.getInstance().getMessageService().formatToString("&c" + tl("gui.uncraftable"))).build();
        }
        menu.setItem(1, signPatternCount);
        // 材料清單
        if (BannerUtil.isCraftable(player, banner)) {
            //材料是否充足
            ItemStack enoughMaterials;
            if (BannerUtil.hasEnoughMaterials(player.getInventory(), banner)) {
                enoughMaterials = new ItemBuilder(Material.OAK_SIGN).name(BannerMaker.getInstance().getMessageService().formatToString("&a" + tl("gui.materials.enough"))).build();
            } else {
                enoughMaterials = new ItemBuilder(Material.OAK_SIGN).name(BannerMaker.getInstance().getMessageService().formatToString("&c" + tl("gui.materials.not-enough"))).build();
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
        }
        // 合成表
        if (BannerUtil.isCraftableInSurvival(banner)) {
            //當前頁數
            final int currentRecipePage = playerData.getCurrentRecipePage();
            //總頁數
            int totalPage = patternCount + 1;
            //外框
            ItemStack border = new ItemBuilder(Material.BROWN_STAINED_GLASS_PANE).name(" ").build();
            List<Integer> borderPosition = Arrays.asList(4, 5, 7, 8, 13, 17, 22, 26, 31, 35, 40, 41, 42, 43, 44);
            for (int i : borderPosition) {
                menu.setItem(i, border.clone());
            }
            //換頁按鈕
            //上一頁
            if (currentRecipePage > 1) {
                ItemStack prevPage = new ItemBuilder(Material.ARROW).amount(currentRecipePage - 1).name(BannerMaker.getInstance().getMessageService().formatToString("&a" + tl("gui.prev-page"))).build();
                menu.setItem(22, prevPage, new ClickAction(ClickType.LEFT, event -> {
                    playerData.setCurrentRecipePage(currentRecipePage - 1);
                    CustomGUIManager.openPrevious(player);
                }));
            }
            //下一頁
            if (currentRecipePage < totalPage) {
                ItemStack nextPage = new ItemBuilder(Material.ARROW).amount(currentRecipePage + 1).name(BannerMaker.getInstance().getMessageService().formatToString("&a" + tl("gui.next-page"))).build();
                menu.setItem(26, nextPage, new ClickAction(ClickType.LEFT, event -> {
                    playerData.setCurrentRecipePage(currentRecipePage + 1);
                    CustomGUIManager.openPrevious(player);
                }));
            }
            //取得合成表配方
            HashMap<Integer, ItemStack> patternRecipe = BannerUtil.getPatternRecipe(banner, currentRecipePage);
            //合成表圖示
            ItemStack workbench = new ItemBuilder(Material.CRAFTING_TABLE).amount(currentRecipePage)
                .name(BannerMaker.getInstance().getMessageService().formatToString("&a" + tl("gui.craft-recipe")))
                .lore(BannerMaker.getInstance().getMessageService().formatToString("&r(" + currentRecipePage + "/" + totalPage + ")")).build();
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
            ItemStack btnDelete = new ItemBuilder(Material.BARRIER).name(BannerMaker.getInstance().getMessageService().formatToString("&c" + tl("gui.delete"))).build();
            menu.setItem(47, btnDelete, new ClickAction(ClickType.LEFT, event -> {
                //刪除
                IOUtil.removeBanner(player, key);
                CustomGUIManager.open(player, MainMenu.class);
            }));
        }
        //取得旗幟
        if (player.hasPermission("BannerMaker.getBanner")) {
            ItemStack btnGetBanner = new ItemBuilder(Material.LIME_WOOL).name(BannerMaker.getInstance().getMessageService().formatToString("&a" + tl("gui.get-this-banner"))).build();
            //顯示名稱
            final String showName = BannerUtil.getName(banner);
            if (player.hasPermission("BannerMaker.getBanner.free")) {
                //具有免費取得權限
                //左鍵：免費取得
                btnGetBanner = new ItemBuilder(btnGetBanner).addLore(BannerMaker.getInstance().getMessageService().formatToString("&e[" + tl("gui.click.left") + "] &a" + tl("gui.get-banner-for-free"))).build();
                menu.setItem(49, btnGetBanner, new ClickAction(ClickType.LEFT, event -> {
                    //取得旗幟
                    InventoryUtil.give(player, banner);
                    //顯示訊息
                    BannerMaker.getInstance().getMessageService().send(player, "&a" + tl("gui.get-banner", showName));
                    CustomGUIManager.openPrevious(player);
                }));
            } else {
                //左鍵：合成
                btnGetBanner = new ItemBuilder(btnGetBanner).addLore(BannerMaker.getInstance().getMessageService().formatToString("&e[" + tl("gui.click.left") + "] &a" + tl("gui.get-banner-by-craft"))).build();
                //檢查是否啟用經濟
                if (BannerMaker.getInstance().econ != null) {
                    //右鍵：購買
                    double price = EconUtil.getPrice(banner);
                    String priceStr = BannerMaker.getInstance().econ.format(price);
                    btnGetBanner = new ItemBuilder(btnGetBanner).addLore(BannerMaker.getInstance().getMessageService().formatToString("&e[" + tl("gui.click.right") + "] &a" + tl("gui.buy-banner-in-price", priceStr))).build();
                }
                menu.setItem(49, btnGetBanner, new ClickAction(ClickType.LEFT, event -> {
                    //嘗試合成旗幟
                    boolean success = BannerUtil.craft(player, banner);
                    if (success) {
                        BannerMaker.getInstance().getMessageService().send(player, "&a" + tl("gui.get-banner", showName));
                    } else {
                        BannerMaker.getInstance().getMessageService().send(player, "&c" + tl("gui.materials.not-enough"));
                    }
                    CustomGUIManager.openPrevious(player);
                }));
                //檢查是否啟用經濟
                if (BannerMaker.getInstance().econ != null) {
                    menu.addActions(49, new ClickAction(ClickType.RIGHT, event -> {
                        //取得旗幟
                        //嘗試給予玩家旗幟
                        boolean success = BannerUtil.buy(player, banner);
                        if (success) {
                            BannerMaker.getInstance().getMessageService().send(player, "&a" + tl("gui.get-banner", showName));
                        }
                        CustomGUIManager.openPrevious(player);
                    }));
                }
            }
        }
        //複製並編輯
        ItemStack btnCloneAndEdit = new ItemBuilder(Material.WRITABLE_BOOK).name(BannerMaker.getInstance().getMessageService().formatToString("&9" + tl("gui.clone-and-edit"))).build();
        menu.setItem(51, btnCloneAndEdit, new ClickAction(ClickType.LEFT, event -> {
            //設定為編輯中旗幟
            playerData.setCurrentEditBanner(banner);
            CustomGUIManager.open(player, CreateBannerMenu.class);
        }));
        // 展示旗幟
        // TODO: 新增至語系檔
        if (player.hasPermission("BannerMaker.show.nearby") || player.hasPermission("BannerMaker.show.all")) {
            ItemStack btnShow = new ItemBuilder(Material.BELL).name(BannerMaker.getInstance().getMessageService().formatToString("&9Show banner to players")).build();
            if (player.hasPermission("BannerMaker.show.nearby")) {
                btnShow = new ItemBuilder(btnShow).addLore(BannerMaker.getInstance().getMessageService().formatToString("&e[" + tl("gui.click.left") + "] &aShow to nearby players")).build();
            }
            if (player.hasPermission("BannerMaker.show.all")) {
                btnShow = new ItemBuilder(btnShow).addLore(BannerMaker.getInstance().getMessageService().formatToString("&e[" + tl("gui.click.right") + "] &aShow to all players")).build();
            }
            menu.setItem(52, btnShow);
            if (player.hasPermission("BannerMaker.show.nearby")) {
                menu.addActions(52, new ClickAction(ClickType.LEFT, event -> {
                    String bannerString = BannerUtil.serialize(banner);
                    TextComponent msgBannerName = new TextComponent(new TextComponent("["), MessageComponentUtil.getTranslatableComponent(banner), new TextComponent("]"));
                    msgBannerName.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, MessageComponentUtil.getHoverEventItem(banner)));
                    msgBannerName.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bm view " + bannerString));
                    // 廣播給附近的玩家
                    double maxDistance = 16;
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        // 只發給有權限的玩家（與發送者自己）
                        if (!p.hasPermission("BannerMaker.show.receive") && p != player) {
                            continue;
                        }
                        if (p.getWorld() != player.getWorld()) {
                            continue;
                        }
                        if (p.getLocation().distanceSquared(player.getLocation()) > maxDistance * maxDistance) {
                            continue;
                        }
                        p.spigot().sendMessage(
                            new TextComponent(player.getDisplayName() + " shows you the banner "),
                            msgBannerName,
                            new TextComponent(" (Click to view)")
                        );
                    }
                    player.closeInventory();
                }));
            }
            if (player.hasPermission("BannerMaker.show.all")) {
                menu.addActions(52, new ClickAction(ClickType.RIGHT, event -> {
                    String bannerString = BannerUtil.serialize(banner);
                    TextComponent msgBannerName = new TextComponent(new TextComponent("["), MessageComponentUtil.getTranslatableComponent(banner), new TextComponent("]"));
                    msgBannerName.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, MessageComponentUtil.getHoverEventItem(banner)));
                    msgBannerName.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bm view " + bannerString));
                    // 廣播給所有玩家
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        // 只發給有權限的玩家（與發送者自己）
                        if (!p.hasPermission("BannerMaker.show.receive") && p != player) {
                            continue;
                        }
                        p.spigot().sendMessage(
                            new TextComponent(player.getDisplayName() + " shows you the banner "),
                            msgBannerName,
                            new TextComponent(" (Click to view)")
                        );
                    }
                    player.closeInventory();
                }));
            }
        }

        // 生成指令
        if (player.hasPermission("BannerMaker.view")) {
            // TODO: 新增至語系檔
            ItemStack btnGenerateCommand = new ItemBuilder(Material.COMMAND_BLOCK).name(BannerMaker.getInstance().getMessageService().formatToString("&9Get share command")).build();
            menu.setItem(53, btnGenerateCommand, new ClickAction(ClickType.LEFT, event -> {
                String bannerString = BannerUtil.serialize(banner);
                TextComponent msg = new TextComponent("[Click here to copy command to clipboard]");
                msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Copy command to clipboard")));
                msg.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, "/bm view " + bannerString));
                player.spigot().sendMessage(msg);
                player.closeInventory();
            }));
        }

        //返回
        ItemStack btnBackToMenu = new ItemBuilder(Material.RED_WOOL).name(BannerMaker.getInstance().getMessageService().formatToString("&c" + tl("gui.back"))).build();
        menu.setItem(45, btnBackToMenu, new ClickAction(ClickType.LEFT, event -> {
            if (AlphabetBanner.isAlphabetBanner(banner)) {
                //若為Alphabet旗幟，回到Alphabet旗幟頁面
                CustomGUIManager.open(player, CreateAlphabetMenu.class);
                return;
            }
            CustomGUIManager.open(player, MainMenu.class);
        }));
        return menu;
    }
}
