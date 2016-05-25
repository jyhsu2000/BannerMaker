package tw.kid7.BannerMaker.util;

import com.google.common.collect.Maps;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.material.Dye;
import tw.kid7.BannerMaker.State;
import tw.kid7.BannerMaker.inventoryMenu.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class InventoryMenuUtil {

    static public void openMenu(Player player) {
        //取得玩家狀態
        State state = State.get(player);
        AbstractInventoryMenu menu = null;
        //根據狀態決定行為
        switch (state) {
            case CREATE_BANNER:
                menu = CreateBannerInventoryMenu.getInstance();
                break;
            case CREATE_ALPHABET:
                menu = CreateAlphabetInventoryMenu.getInstance();
                break;
            case BANNER_INFO:
                menu = BannerInfoInventoryMenu.getInstance();
                break;
            case MAIN_MENU:
            default:
                menu = MainInventoryMenu.getInstance();
        }
        //開啟選單
        if (menu != null) {
            menu.open(player);
        }
    }

    static public List<PatternType> getPatternTypeList() {
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

    static public HashMap<Integer, ItemStack> getPatternRecipe(final ItemStack banner, int step) {
        HashMap<Integer, ItemStack> recipe = Maps.newHashMap();
        //填滿空氣
        for (int i = 0; i < 10; i++) {
            recipe.put(i, new ItemStack(Material.AIR));
        }
        //只處理旗幟
        if (banner != null && banner.getType().equals(Material.BANNER)) {
            BannerMeta bm = (BannerMeta) banner.getItemMeta();
            int totalStep = bm.numberOfPatterns() + 1;
            if (step == 1) {
                //第一步，旗幟合成
                //顏色
                int color = 15 - banner.getDurability();
                //羊毛
                ItemStack wool = new ItemStack(Material.WOOL, 1, (short) color);
                for (int i = 0; i < 6; i++) {
                    recipe.put(i, wool.clone());
                }
                //木棒
                ItemStack stick = new ItemStack(Material.STICK);
                recipe.put(7, stick);
            } else if (step <= totalStep) {
                //新增Pattern
                //當前banner
                ItemStack prevBanner = new ItemStack(Material.BANNER, 1, banner.getDurability());
                BannerMeta pbm = (BannerMeta) prevBanner.getItemMeta();
                //新增至目前的Pattern
                for (int i = 0; i < step - 2; i++) {
                    pbm.addPattern(bm.getPattern(i));
                }
                prevBanner.setItemMeta(pbm);
                //當前Pattern
                Pattern pattern = bm.getPattern(step - 2);
                //所需染料
                Dye dye = new Dye();
                dye.setColor(pattern.getColor());
                ItemStack dyeItem = dye.toItemStack(1);
                //旗幟位置
                int bannerPosition = 4;
                //染料位置
                List<Integer> dyePosition = Collections.emptyList();
                //根據Pattern決定位置
                switch (pattern.getPattern()) {
                    case SQUARE_BOTTOM_LEFT:
                        dyePosition = Collections.singletonList(6);
                        break;
                    case SQUARE_BOTTOM_RIGHT:
                        dyePosition = Collections.singletonList(8);
                        break;
                    case SQUARE_TOP_LEFT:
                        dyePosition = Collections.singletonList(0);
                        break;
                    case SQUARE_TOP_RIGHT:
                        dyePosition = Collections.singletonList(2);
                        break;
                    case STRIPE_BOTTOM:
                        dyePosition = Arrays.asList(6, 7, 8);
                        break;
                    case STRIPE_TOP:
                        dyePosition = Arrays.asList(0, 1, 2);
                        break;
                    case STRIPE_LEFT:
                        dyePosition = Arrays.asList(0, 3, 6);
                        break;
                    case STRIPE_RIGHT:
                        dyePosition = Arrays.asList(2, 5, 8);
                        break;
                    case STRIPE_CENTER:
                        bannerPosition = 3;
                        dyePosition = Arrays.asList(1, 4, 7);
                        break;
                    case STRIPE_MIDDLE:
                        bannerPosition = 1;
                        dyePosition = Arrays.asList(3, 4, 5);
                        break;
                    case STRIPE_DOWNRIGHT:
                        bannerPosition = 1;
                        dyePosition = Arrays.asList(0, 4, 8);
                        break;
                    case STRIPE_DOWNLEFT:
                        bannerPosition = 1;
                        dyePosition = Arrays.asList(2, 4, 6);
                        break;
                    case STRIPE_SMALL:
                        dyePosition = Arrays.asList(0, 2, 3, 5);
                        break;
                    case CROSS:
                        bannerPosition = 1;
                        dyePosition = Arrays.asList(0, 2, 4, 6, 8);
                        break;
                    case STRAIGHT_CROSS:
                        bannerPosition = 0;
                        dyePosition = Arrays.asList(1, 3, 4, 5, 7);
                        break;
                    case TRIANGLE_BOTTOM:
                        bannerPosition = 7;
                        dyePosition = Arrays.asList(4, 6, 8);
                        break;
                    case TRIANGLE_TOP:
                        bannerPosition = 1;
                        dyePosition = Arrays.asList(0, 2, 4);
                        break;
                    case TRIANGLES_BOTTOM:
                        dyePosition = Arrays.asList(3, 5, 7);
                        break;
                    case TRIANGLES_TOP:
                        dyePosition = Arrays.asList(1, 3, 5);
                        break;
                    case DIAGONAL_LEFT:
                        dyePosition = Arrays.asList(0, 1, 3);
                        break;
                    case DIAGONAL_RIGHT:
                        dyePosition = Arrays.asList(5, 7, 8);
                        break;
                    case DIAGONAL_LEFT_MIRROR:
                        dyePosition = Arrays.asList(3, 6, 7);
                        break;
                    case DIAGONAL_RIGHT_MIRROR:
                        dyePosition = Arrays.asList(1, 2, 5);
                        break;
                    case CIRCLE_MIDDLE:
                        bannerPosition = 1;
                        dyePosition = Collections.singletonList(4);
                        break;
                    case RHOMBUS_MIDDLE:
                        dyePosition = Arrays.asList(1, 3, 5, 7);
                        break;
                    case HALF_VERTICAL:
                        bannerPosition = 5;
                        dyePosition = Arrays.asList(0, 1, 3, 4, 6, 7);
                        break;
                    case HALF_HORIZONTAL:
                        bannerPosition = 7;
                        dyePosition = Arrays.asList(0, 1, 2, 3, 4, 5);
                        break;
                    case HALF_VERTICAL_MIRROR:
                        bannerPosition = 3;
                        dyePosition = Arrays.asList(1, 2, 4, 5, 7, 8);
                        break;
                    case HALF_HORIZONTAL_MIRROR:
                        bannerPosition = 1;
                        dyePosition = Arrays.asList(3, 4, 5, 6, 7, 8);
                        break;
                    case BORDER:
                        dyePosition = Arrays.asList(0, 1, 2, 3, 5, 6, 7, 8);
                        break;
                    case CURLY_BORDER:
                        recipe.put(1, new ItemStack(Material.VINE));
                        if (!pattern.getColor().equals(DyeColor.BLACK)) {
                            dyePosition = Collections.singletonList(7);
                        }
                        break;
                    case CREEPER:
                        recipe.put(1, new ItemStack(Material.SKULL_ITEM, 1, (short) 4));
                        if (!pattern.getColor().equals(DyeColor.BLACK)) {
                            dyePosition = Collections.singletonList(7);
                        }
                        break;
                    case GRADIENT:
                        bannerPosition = 1;
                        dyePosition = Arrays.asList(0, 2, 4, 7);
                        break;
                    case GRADIENT_UP:
                        bannerPosition = 7;
                        dyePosition = Arrays.asList(1, 4, 6, 8);
                        break;
                    case BRICKS:
                        recipe.put(1, new ItemStack(Material.BRICK));
                        if (!pattern.getColor().equals(DyeColor.BLACK)) {
                            dyePosition = Collections.singletonList(7);
                        }
                        break;
                    case SKULL:
                        recipe.put(1, new ItemStack(Material.SKULL_ITEM, 1, (short) 1));
                        if (!pattern.getColor().equals(DyeColor.BLACK)) {
                            dyePosition = Collections.singletonList(7);
                        }
                        break;
                    case FLOWER:
                        recipe.put(1, new ItemStack(Material.RED_ROSE, 1, (short) 8));
                        if (!pattern.getColor().equals(DyeColor.BLACK)) {
                            dyePosition = Collections.singletonList(7);
                        }
                        break;
                    case MOJANG:
                        recipe.put(1, new ItemStack(Material.GOLDEN_APPLE, 1, (short) 1));
                        if (!pattern.getColor().equals(DyeColor.BLACK)) {
                            dyePosition = Collections.singletonList(7);
                        }
                        break;
                }
                //放置旗幟與染料
                recipe.put(bannerPosition, prevBanner);
                for (int i : dyePosition) {
                    recipe.put(i, dyeItem.clone());
                }
            }
            //合成結果
            //當前banner
            ItemStack currentBanner = new ItemStack(Material.BANNER, 1, banner.getDurability());
            BannerMeta cbm = (BannerMeta) currentBanner.getItemMeta();
            //新增至目前的Pattern
            for (int i = 0; i < step - 1; i++) {
                cbm.addPattern(bm.getPattern(i));
            }
            currentBanner.setItemMeta(cbm);
            recipe.put(9, currentBanner);
        }
        return recipe;
    }
}
