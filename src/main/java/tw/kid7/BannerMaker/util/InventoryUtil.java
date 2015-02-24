package tw.kid7.BannerMaker.util;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import tw.kid7.BannerMaker.BannerMaker;
import tw.kid7.BannerMaker.State;

import java.util.Arrays;
import java.util.List;

import static tw.kid7.BannerMaker.State.MAIN_MENU;

public class InventoryUtil {

    static public void openMenu(Player player) {
        //取得玩家狀態
        State state = MAIN_MENU;
        if (BannerMaker.getInstance().stateMap.containsKey(player.getName())) {
            state = BannerMaker.getInstance().stateMap.get(player.getName());
        }
        //根據狀態決定行為
        switch (state) {
            case CREATE_BANNER:
                openCreateBanner(player);
                break;
            case BANNER_INFO:
                openBannerInfo(player);
                break;
            case CRAFT_RECIPT:
                openBannerRecipe(player);
                break;
            case MAIN_MENU:
            default:
                openMainMenu(player);
        }

    }

    static public void openMainMenu(Player player) {
        //建立選單
        Inventory menu = Bukkit.createInventory(null, 54, MessageUtil.format("&f[&4BM&f] &rMain menu"));
        //顯示現有旗幟
        //TODO 分頁功能
        List<ItemStack> bannerList = IOUtil.loadBanner(player);
        for (int i = 0; i < bannerList.size() && i < 45; i++) {
            ItemStack banner = bannerList.get(i);
            menu.setItem(i, banner);
        }
        //新增按鈕
        //Create banner
        ItemStack btnCreateBanner = new ItemBuilder(Material.WOOL).amount(1).durability(5).name(MessageUtil.format("&aCreate Banner")).build();
        menu.setItem(49, btnCreateBanner);
        //開啟選單
        player.openInventory(menu);
    }

    static public void openCreateBanner(Player player) {
        //建立選單
        Inventory menu = Bukkit.createInventory(null, 54, MessageUtil.format("&f[&4BM&f] &rCreate banner"));
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
                ItemStack warning = new ItemBuilder(Material.SIGN).amount(1).name(MessageUtil.format("&cUncraftable Warning"))
                        .lore("More than 6 patterns.").build();
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
                if (patternIndex >= getPatternTypeList().size()) {
                    break;
                }
                ItemStack banner = currentBanner.clone();
                BannerMeta bm = (BannerMeta) banner.getItemMeta();
                PatternType patternType = getPatternTypeList().get(patternIndex);
                bm.addPattern(new Pattern(DyeColor.getByDyeData((byte) selectedColor), patternType));
                banner.setItemMeta(bm);

                menu.setItem(i + 19 + (i / 8), banner);
            }
            //更多Pattern
            ItemStack btnMorePattern = new ItemBuilder(Material.NETHER_STAR).amount(1).name(MessageUtil.format("&aMore patterns")).build();
            menu.setItem(51, btnMorePattern);
        }
        //返回
        ItemStack btnBackToMenu = new ItemBuilder(Material.WOOL).amount(1).durability(14).name(MessageUtil.format("&cBack")).build();
        menu.setItem(45, btnBackToMenu);
        if (currentBanner != null) {
            //建立旗幟
            ItemStack btnCreate = new ItemBuilder(Material.WOOL).amount(1).durability(5).name(MessageUtil.format("&aCreate")).build();
            menu.setItem(53, btnCreate);
            //刪除
            ItemStack btnDelete = new ItemBuilder(Material.BARRIER).amount(1).name(MessageUtil.format("&cDELETE")).build();
            menu.setItem(47, btnDelete);
            if (currentBanner.hasItemMeta() && ((BannerMeta) currentBanner.getItemMeta()).numberOfPatterns() > 0) {
                //移除Pattern
                ItemStack btnRemovePattern = new ItemBuilder(Material.BARRIER).amount(1).name(MessageUtil.format("&cRemove last pattern")).build();
                menu.setItem(49, btnRemovePattern);
            }
        }
        //開啟選單
        player.openInventory(menu);
    }

    static public void openBannerInfo(Player player) {
        //建立選單
        Inventory menu = Bukkit.createInventory(null, 54, MessageUtil.format("&f[&4BM&f] &rBanner info"));
        //索引值
        if (!BannerMaker.getInstance().selectedIndex.containsKey(player.getName())) {
            //回到主選單
            BannerMaker.getInstance().stateMap.put(player.getName(), State.MAIN_MENU);
            //重新開啟選單
            InventoryUtil.openMenu(player);
            return;
        }
        int index = BannerMaker.getInstance().selectedIndex.get(player.getName());
        //新增旗幟
        List<ItemStack> bannerList = IOUtil.loadBanner(player);
        ItemStack banner = bannerList.get(index);
        if (banner == null || !banner.getType().equals(Material.BANNER)) {
            //回到主選單
            BannerMaker.getInstance().stateMap.put(player.getName(), State.MAIN_MENU);
            //重新開啟選單
            InventoryUtil.openMenu(player);
            return;
        }
        menu.setItem(0, banner);
        //patterns數量
        int patternCount = ((BannerMeta) banner.getItemMeta()).numberOfPatterns();
        String patternCountStr = "";
        if (patternCount > 0) {
            patternCountStr = patternCount + " pattern(s)";
        } else {
            patternCountStr = "No patterns";
        }
        ItemStack signPatternCount;
        if (patternCount <= 6) {
            signPatternCount = new ItemBuilder(Material.SIGN).amount(1).name(MessageUtil.format("&a" + patternCountStr)).build();
        } else {
            signPatternCount = new ItemBuilder(Material.SIGN).amount(1).name(MessageUtil.format("&a" + patternCountStr)).lore(MessageUtil.format("&cUncraftable")).build();
        }
        menu.setItem(1, signPatternCount);
        //新增按鈕
        //刪除
        ItemStack btnDelete = new ItemBuilder(Material.BARRIER).amount(1).name(MessageUtil.format("&cDELETE")).build();
        menu.setItem(47, btnDelete);
        //TODO
        //返回
        ItemStack btnBackToMenu = new ItemBuilder(Material.WOOL).amount(1).durability(14).name(MessageUtil.format("&cBack")).build();
        menu.setItem(45, btnBackToMenu);
        //開啟選單
        player.openInventory(menu);
    }

    static public void openBannerRecipe(Player player) {
        //建立選單
        Inventory menu = Bukkit.createInventory(null, 54, MessageUtil.format("&f[&4BM&f] &rBanner recipe"));
        //新增按鈕
        //TODO
        //返回
        ItemStack btnBackToMenu = new ItemBuilder(Material.WOOL).amount(1).durability(14).name(MessageUtil.format("&cBack")).build();
        menu.setItem(45, btnBackToMenu);
        //開啟選單
        player.openInventory(menu);
    }

    static private List<PatternType> getPatternTypeList() {
        List<PatternType> list = Arrays.asList(
                PatternType.BORDER,
                PatternType.BRICKS,
                PatternType.CIRCLE_MIDDLE,
                PatternType.CREEPER,
                PatternType.CROSS,
                PatternType.CURLY_BORDER,
                PatternType.DIAGONAL_LEFT,
                PatternType.DIAGONAL_LEFT_MIRROR,
                PatternType.DIAGONAL_RIGHT,
                PatternType.DIAGONAL_RIGHT_MIRROR,
                PatternType.FLOWER,
                PatternType.GRADIENT,
                PatternType.GRADIENT_UP,
                PatternType.HALF_HORIZONTAL,
                PatternType.HALF_HORIZONTAL_MIRROR,
                PatternType.HALF_VERTICAL,
                PatternType.HALF_VERTICAL_MIRROR,
                PatternType.MOJANG,
                PatternType.RHOMBUS_MIDDLE,
                PatternType.SKULL,
                PatternType.SQUARE_BOTTOM_LEFT,
                PatternType.SQUARE_BOTTOM_RIGHT,
                PatternType.SQUARE_TOP_LEFT,
                PatternType.SQUARE_TOP_RIGHT,
                PatternType.STRAIGHT_CROSS,
                PatternType.STRIPE_BOTTOM,
                PatternType.STRIPE_CENTER,
                PatternType.STRIPE_DOWNLEFT,
                PatternType.STRIPE_DOWNRIGHT,
                PatternType.STRIPE_LEFT,
                PatternType.STRIPE_MIDDLE,
                PatternType.STRIPE_RIGHT,
                PatternType.STRIPE_SMALL,
                PatternType.STRIPE_TOP,
                PatternType.TRIANGLE_BOTTOM,
                PatternType.TRIANGLE_TOP,
                PatternType.TRIANGLES_BOTTOM,
                PatternType.TRIANGLES_TOP
        );
        return list;
    }
}
