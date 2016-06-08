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
import tw.kid7.BannerMaker.State;
import tw.kid7.BannerMaker.configuration.Language;
import tw.kid7.BannerMaker.util.AlphabetBanner;
import tw.kid7.BannerMaker.util.InventoryMenuUtil;
import tw.kid7.BannerMaker.util.ItemBuilder;
import tw.kid7.BannerMaker.util.MessageUtil;

import java.util.HashMap;

public class CreateAlphabetInventoryMenu extends AbstractInventoryMenu {
    private static CreateAlphabetInventoryMenu instance = null;
    final HashMap<String, AlphabetBanner> currentAlphabetBannerMap = Maps.newHashMap();
    final HashMap<String, Boolean> alphabetBorderMap = Maps.newHashMap();

    public static CreateAlphabetInventoryMenu getInstance() {
        if (instance == null) {
            instance = new CreateAlphabetInventoryMenu();
        }
        return instance;
    }

    @Override
    public void open(Player player) {
        //建立選單
        Inventory menu = Bukkit.createInventory(null, 54, InventoryMenuUtil.createTitle(Language.get("gui.alphabet-and-number")));
        //取得當前編輯中的字母
        AlphabetBanner currentAlphabetBanner = currentAlphabetBannerMap.get(player.getName());
        //邊框切換按鈕
        ItemStack btnBorderedBanner = new ItemStack(Material.BANNER, 1, (short) 15);
        BannerMeta borderedBannerMeta = (BannerMeta) btnBorderedBanner.getItemMeta();
        borderedBannerMeta.setDisplayName(MessageUtil.format("&a" + Language.get("gui.toggle-border")));
        borderedBannerMeta.addPattern(new Pattern(DyeColor.BLACK, PatternType.BORDER));
        btnBorderedBanner.setItemMeta(borderedBannerMeta);
        if (currentAlphabetBanner == null) {
            //選擇字母
            boolean alphabetBorder = true;
            if (alphabetBorderMap.containsKey(player.getName())) {
                alphabetBorder = alphabetBorderMap.get(player.getName());
            }
            char[] alphabetArray = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789?!.".toCharArray();
            for (int i = 0; i < alphabetArray.length && i < 54; i++) {
                char alphabet = alphabetArray[i];
                ItemStack alphabetItem = AlphabetBanner.get(String.valueOf(alphabet), DyeColor.WHITE, DyeColor.BLACK, alphabetBorder);
                menu.setItem(i, alphabetItem);
            }
            //切換有無邊框
            menu.setItem(49, btnBorderedBanner);
        } else {
            //選擇顏色
            menu.setItem(0, currentAlphabetBanner.toItemStack());
            //選擇底色
            for (int i = 0; i < 16; i++) {
                ItemStack banner = new ItemStack(Material.BANNER, 1, (short) i);
                menu.setItem(i + 1 + (i / 8), banner);
            }
            //選擇主要顏色
            for (int i = 0; i < 16; i++) {
                ItemStack dye = new ItemBuilder(Material.INK_SACK).amount(1).durability(i).build();
                menu.setItem(18 + i + 1 + (i / 8), dye);
            }
            //切換有無邊框
            menu.setItem(37, btnBorderedBanner);
            //檢視旗幟資訊按鈕
            ItemStack btnBannerInfo = new ItemBuilder(Material.WOOL).amount(1).durability(5).name(MessageUtil.format("&a" + Language.get("gui.banner-info"))).build();
            menu.setItem(49, btnBannerInfo);
        }
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
        //取得當前編輯中的字母
        AlphabetBanner currentAlphabetBanner = currentAlphabetBannerMap.get(player.getName());
        if (currentAlphabetBanner == null) {
            if (event.getRawSlot() < 45) {
                //選擇字母
                boolean alphabetBorder = true;
                if (alphabetBorderMap.containsKey(player.getName())) {
                    alphabetBorder = alphabetBorderMap.get(player.getName());
                }
                currentAlphabetBanner = new AlphabetBanner(itemStack.getItemMeta().getDisplayName(), DyeColor.WHITE, DyeColor.BLACK, alphabetBorder);
                currentAlphabetBannerMap.put(player.getName(), currentAlphabetBanner);
            } else {
                //點擊按鈕
                String buttonName = itemStack.getItemMeta().getDisplayName();
                buttonName = ChatColor.stripColor(buttonName);
                if (buttonName.equalsIgnoreCase(Language.getIgnoreColors("gui.toggle-border"))) {
                    //切換有無邊框
                    boolean alphabetBorder = true;
                    if (alphabetBorderMap.containsKey(player.getName())) {
                        alphabetBorder = alphabetBorderMap.get(player.getName());
                    }
                    alphabetBorder = !alphabetBorder;
                    alphabetBorderMap.put(player.getName(), alphabetBorder);
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
                currentAlphabetBannerMap.put(player.getName(), currentAlphabetBanner);
            } else if (event.getRawSlot() < 36) {
                //選擇主要顏色
                currentAlphabetBanner.dyeColor = DyeColor.getByDyeData((byte) itemStack.getDurability());
                currentAlphabetBannerMap.put(player.getName(), currentAlphabetBanner);
            } else {
                //點擊按鈕
                String buttonName = itemStack.getItemMeta().getDisplayName();
                buttonName = ChatColor.stripColor(buttonName);
                if (buttonName.equalsIgnoreCase(Language.getIgnoreColors("gui.toggle-border"))) {
                    //切換有無邊框
                    currentAlphabetBanner.bordered = !currentAlphabetBanner.bordered;
                    currentAlphabetBannerMap.put(player.getName(), currentAlphabetBanner);
                } else if (buttonName.equalsIgnoreCase(Language.getIgnoreColors("gui.banner-info"))) {
                    //檢視旗幟資訊
                    BannerInfoInventoryMenu.getInstance().viewInfoBannerMap.put(player.getName(), currentAlphabetBanner.toItemStack());
                    //重置頁數
                    BannerInfoInventoryMenu.getInstance().currentRecipePageMap.put(player.getName(), 1);
                    State.set(player, State.BANNER_INFO);
                } else if (buttonName.equalsIgnoreCase(Language.getIgnoreColors("gui.back"))) {
                    currentAlphabetBannerMap.remove(player.getName());
                }
            }
            //重新開啟選單
            InventoryMenuUtil.openMenu(player);
        }
    }
}
