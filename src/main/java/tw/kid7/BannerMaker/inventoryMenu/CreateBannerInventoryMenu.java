package tw.kid7.BannerMaker.inventoryMenu;

import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.material.Dye;
import tw.kid7.BannerMaker.BannerMaker;
import tw.kid7.BannerMaker.State;
import tw.kid7.BannerMaker.configuration.Language;
import tw.kid7.BannerMaker.util.IOUtil;
import tw.kid7.BannerMaker.util.InventoryMenuUtil;
import tw.kid7.BannerMaker.util.ItemBuilder;
import tw.kid7.BannerMaker.util.MessageUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class CreateBannerInventoryMenu extends AbstractInventoryMenu {
    private static CreateBannerInventoryMenu instance = null;

    public static CreateBannerInventoryMenu getInstance() {
        if (instance == null) {
            instance = new CreateBannerInventoryMenu();
        }
        return instance;
    }

    @Override
    public void open(Player player) {
        //建立選單
        Inventory menu = Bukkit.createInventory(null, 54, MessageUtil.format("&b&m&r" + Language.get("gui.prefix") + Language.get("gui.create-banner")));
        //取得當前編輯中的旗幟
        ItemStack currentBanner = BannerMaker.getInstance().currentBanner.get(player.getName());
        if (currentBanner == null) {
            //剛開始編輯，先選擇底色
            for (int i = 0; i < 16; i++) {
                ItemStack banner = new ItemStack(Material.BANNER, 1, (short) i);
                menu.setItem(i + 1 + (i / 8), banner);
            }
        } else {
            //新增按鈕
            //當前旗幟
            menu.setItem(0, currentBanner);
            //patterns過多的警告
            if (currentBanner.hasItemMeta() && ((BannerMeta) currentBanner.getItemMeta()).numberOfPatterns() > 6) {
                ItemStack warning = new ItemBuilder(Material.SIGN).amount(1).name(MessageUtil.format("&c" + Language.get("gui.uncraftable-warning")))
                    .lore(Language.get("gui.more-than-6-patterns")).build();
                menu.setItem(9, warning);
            }
            //顏色
            for (int i = 0; i < 16; i++) {
                ItemStack dye = new ItemBuilder(Material.INK_SACK).amount(1).durability(i).build();
                menu.setItem(i + 1 + (i / 8), dye);
            }
            //Pattern
            //選擇的顏色
            int selectedColor = 0;
            if (BannerMaker.getInstance().selectedColor.containsKey(player.getName())) {
                selectedColor = BannerMaker.getInstance().selectedColor.get(player.getName());
            }
            for (int i = 0; i < 24; i++) {
                int patternIndex = i;
                if (BannerMaker.getInstance().morePatterns.containsKey(player.getName())) {
                    if (BannerMaker.getInstance().morePatterns.get(player.getName())) {
                        patternIndex += 24;
                    }
                }
                if (patternIndex >= InventoryMenuUtil.getPatternTypeList().size()) {
                    break;
                }
                //預覽旗幟
                ItemStack banner = new ItemStack(Material.BANNER, 1, currentBanner.getDurability());
                BannerMeta bm = (BannerMeta) banner.getItemMeta();
                PatternType patternType = InventoryMenuUtil.getPatternTypeList().get(patternIndex);
                bm.addPattern(new Pattern(DyeColor.getByDyeData((byte) selectedColor), patternType));
                banner.setItemMeta(bm);

                menu.setItem(i + 19 + (i / 8), banner);
            }
            //更多Pattern
            ItemStack btnMorePattern = new ItemBuilder(Material.NETHER_STAR).amount(1).name(MessageUtil.format("&a" + Language.get("gui.more-patterns"))).build();
            menu.setItem(51, btnMorePattern);
        }
        //返回
        ItemStack btnBackToMenu = new ItemBuilder(Material.WOOL).amount(1).durability(14).name(MessageUtil.format("&c" + Language.get("gui.back"))).build();
        menu.setItem(45, btnBackToMenu);
        if (currentBanner != null) {
            //建立旗幟
            ItemStack btnCreate = new ItemBuilder(Material.WOOL).amount(1).durability(5).name(MessageUtil.format("&a" + Language.get("gui.create"))).build();
            menu.setItem(53, btnCreate);
            //刪除
            ItemStack btnDelete = new ItemBuilder(Material.BARRIER).amount(1).name(MessageUtil.format("&c" + Language.get("gui.delete"))).build();
            menu.setItem(47, btnDelete);
            if (currentBanner.hasItemMeta() && ((BannerMeta) currentBanner.getItemMeta()).numberOfPatterns() > 0) {
                //移除Pattern
                ItemStack btnRemovePattern = new ItemBuilder(Material.BARRIER).amount(1).name(MessageUtil.format("&c" + Language.get("gui.remove-last-pattern"))).build();
                menu.setItem(49, btnRemovePattern);
            }
        }
        //開啟選單
        player.openInventory(menu);
    }

    @Override
    public void onClick(InventoryClickEvent event) {
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
}
