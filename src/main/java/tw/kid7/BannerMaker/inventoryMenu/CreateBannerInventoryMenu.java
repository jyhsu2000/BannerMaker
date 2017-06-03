package tw.kid7.BannerMaker.inventoryMenu;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import tw.kid7.BannerMaker.InventoryMenuState;
import tw.kid7.BannerMaker.PlayerData;
import tw.kid7.BannerMaker.util.*;

import static tw.kid7.BannerMaker.configuration.Language.tl;

public class CreateBannerInventoryMenu extends AbstractInventoryMenu {
    private static CreateBannerInventoryMenu instance = null;
    //按鈕位置
    private int buttonPositionMorePattern = 51;
    private int buttonPositionBackToMenu = 45;
    private int buttonPositionCreate = 53;
    private int buttonPositionDelete = 47;
    private int buttonPositionRemovePattern = 49;

    public static CreateBannerInventoryMenu getInstance() {
        if (instance == null) {
            instance = new CreateBannerInventoryMenu();
        }
        return instance;
    }

    @Override
    public void open(Player player) {
        PlayerData playerData = PlayerData.get(player);
        //建立選單
        Inventory menu = InventoryMenuUtil.create(tl("gui.create-banner"));
        //取得當前編輯中的旗幟
        ItemStack currentBanner = playerData.getCurrentEditBanner();
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
                ItemStack warning = new ItemBuilder(Material.SIGN).amount(1).name(MessageUtil.format("&c" + tl("gui.uncraftable-warning")))
                    .lore(tl("gui.more-than-6-patterns")).build();
                menu.setItem(9, warning);
            }
            //顏色
            for (int i = 0; i < 16; i++) {
                ItemStack dye = new ItemBuilder(Material.INK_SACK).amount(1).durability(i).build();
                menu.setItem(i + 1 + (i / 8), dye);
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
                ItemStack banner = new ItemStack(Material.BANNER, 1, currentBanner.getDurability());
                BannerMeta bm = (BannerMeta) banner.getItemMeta();
                PatternType patternType = BannerUtil.getPatternTypeList().get(patternIndex);
                bm.addPattern(new Pattern(selectedColor, patternType));
                banner.setItemMeta(bm);

                menu.setItem(i + 19 + (i / 8), banner);
            }
            //更多Pattern
            ItemStack btnMorePattern = new ItemBuilder(Material.NETHER_STAR).amount(1).name(MessageUtil.format("&a" + tl("gui.more-patterns"))).build();
            menu.setItem(buttonPositionMorePattern, btnMorePattern);
        }
        //返回
        ItemStack btnBackToMenu = new ItemBuilder(Material.WOOL).amount(1).durability(14).name(MessageUtil.format("&c" + tl("gui.back"))).build();
        menu.setItem(buttonPositionBackToMenu, btnBackToMenu);
        if (currentBanner != null) {
            //建立旗幟
            ItemStack btnCreate = new ItemBuilder(Material.WOOL).amount(1).durability(5).name(MessageUtil.format("&a" + tl("gui.create"))).build();
            menu.setItem(buttonPositionCreate, btnCreate);
            //刪除
            ItemStack btnDelete = new ItemBuilder(Material.BARRIER).amount(1).name(MessageUtil.format("&c" + tl("gui.delete"))).build();
            menu.setItem(buttonPositionDelete, btnDelete);
            if (currentBanner.hasItemMeta() && ((BannerMeta) currentBanner.getItemMeta()).numberOfPatterns() > 0) {
                //移除Pattern
                ItemStack btnRemovePattern = new ItemBuilder(Material.BARRIER).amount(1).name(MessageUtil.format("&c" + tl("gui.remove-last-pattern"))).build();
                menu.setItem(buttonPositionRemovePattern, btnRemovePattern);
            }
        }
        //開啟選單
        player.openInventory(menu);
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        PlayerData playerData = PlayerData.get(player);
        ItemStack itemStack = event.getCurrentItem();
        //取得當前編輯中的旗幟
        ItemStack currentBanner = playerData.getCurrentEditBanner();
        int rawSlot = event.getRawSlot();
        if (rawSlot >= 1 && rawSlot <= 17 && rawSlot % 9 != 0) {
            if (currentBanner == null) {
                //選擇底色
                playerData.setCurrentEditBanner(itemStack);
            } else {
                //點擊顏色
                playerData.setSelectedColor(DyeColorUtil.fromInt(itemStack.getDurability()));
            }
            InventoryMenuUtil.openMenu(player);
            return;
        }
        if (rawSlot >= 19 && rawSlot <= 44 && rawSlot % 9 != 0) {
            //新增Pattern
            BannerMeta bm = (BannerMeta) itemStack.getItemMeta();
            Pattern pattern = bm.getPattern(bm.numberOfPatterns() - 1);
            BannerMeta currentBm = (BannerMeta) currentBanner.getItemMeta();
            currentBm.addPattern(pattern);
            currentBanner.setItemMeta(currentBm);
            playerData.setCurrentEditBanner(currentBanner);
            InventoryMenuUtil.openMenu(player);
            return;
        }
        if (rawSlot < 45) {
            return;
        }
        //修改狀態
        if (rawSlot == buttonPositionMorePattern) {
            playerData.setShowMorePatterns(!playerData.isShowMorePatterns());
            InventoryMenuUtil.openMenu(player);
            return;
        }
        if (rawSlot == buttonPositionRemovePattern) {
            if (currentBanner.hasItemMeta() && ((BannerMeta) currentBanner.getItemMeta()).numberOfPatterns() > 0) {
                BannerMeta bm = (BannerMeta) currentBanner.getItemMeta();
                bm.removePattern(bm.numberOfPatterns() - 1);
                currentBanner.setItemMeta(bm);
                playerData.setCurrentEditBanner(currentBanner);
                InventoryMenuUtil.openMenu(player);
            }
            return;
        }
        if (rawSlot == buttonPositionCreate) {
            IOUtil.saveBanner(player, currentBanner);
            playerData.setCurrentEditBanner(null);
            InventoryMenuUtil.openMenu(player, InventoryMenuState.MAIN_MENU);
            return;
        }
        if (rawSlot == buttonPositionDelete) {
            playerData.setCurrentEditBanner(null);
            InventoryMenuUtil.openMenu(player);
            return;
        }
        if (rawSlot == buttonPositionBackToMenu) {
            InventoryMenuUtil.openMenu(player, InventoryMenuState.MAIN_MENU);
            return;
        }
    }
}
