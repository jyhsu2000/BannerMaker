package club.kid7.bannermaker.util;

import club.kid7.bannermaker.BannerMaker;
import com.google.common.collect.Maps;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static club.kid7.bannermaker.configuration.Language.tl;

public class BannerUtil {
    /**
     * 檢查ItemStack是否為旗幟
     *
     * @param itemStack 欲檢查的物品
     * @return boolean
     */
    static public boolean isBanner(ItemStack itemStack) {
        return itemStack != null && itemStack.getType().name().endsWith("_BANNER");
    }

    /**
     * 判斷是否為紡織機配方
     *
     * @param patternRecipe 欲檢查配方，由 getPatternRecipe() 產出
     * @return 是否為紡織機配方
     */
    static public boolean isLoomRecipe(HashMap<Integer, ItemStack> patternRecipe) {
        for (Map.Entry<Integer, ItemStack> entry : patternRecipe.entrySet()) {
            ItemStack itemStack = entry.getValue();
            // 包含旗幟圖形物品者，直接視為紡織機配方
            if (isBannerPatternItemStack(itemStack)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判斷是否為旗幟圖形物品
     *
     * @param itemStack 欲檢查的物品
     * @return 是否為旗幟圖形物品
     */
    static public boolean isBannerPatternItemStack(ItemStack itemStack) {
        return itemStack.getType().toString().endsWith("_BANNER_PATTERN");
    }

    /**
     * 取得旗幟材料清單
     *
     * @param banner 欲取得材料清單之旗幟
     * @return List<ItemStack>
     */
    static public List<ItemStack> getMaterials(ItemStack banner) {
        List<ItemStack> materialList = new ArrayList<>();
        //只檢查旗幟
        if (!isBanner(banner)) {
            return materialList;
        }
        //基本材料
        //木棒
        ItemStack stick = new ItemStack(Material.STICK, 1);
        materialList.add(stick);
        //羊毛
        //顏色
        DyeColor baseColor = DyeColorUtil.of(banner.getType());
        //羊毛
        ItemStack wool = new ItemStack(DyeColorUtil.toWoolMaterial(baseColor), 6);
        materialList.add(wool);
        //Pattern材料
        Inventory materialInventory = Bukkit.createInventory(null, 54);
        BannerMeta bm = (BannerMeta) Objects.requireNonNull(banner.getItemMeta());
        //逐Pattern計算
        for (Pattern pattern : bm.getPatterns()) {
            //所需染料
            DyeColor dyeColor = pattern.getColor();
            PatternType patternType = pattern.getPattern();
            if (patternType.equals(PatternType.SQUARE_BOTTOM_LEFT)
                || patternType.equals(PatternType.SQUARE_BOTTOM_RIGHT)
                || patternType.equals(PatternType.SQUARE_TOP_LEFT)
                || patternType.equals(PatternType.SQUARE_TOP_RIGHT)
                || patternType.equals(PatternType.CIRCLE)) {
                materialInventory.addItem(DyeColorUtil.toDyeItemStack(dyeColor, 1));
            } else if (patternType.equals(PatternType.STRIPE_BOTTOM)
                || patternType.equals(PatternType.STRIPE_TOP)
                || patternType.equals(PatternType.STRIPE_LEFT)
                || patternType.equals(PatternType.STRIPE_RIGHT)
                || patternType.equals(PatternType.STRIPE_CENTER)
                || patternType.equals(PatternType.STRIPE_MIDDLE)
                || patternType.equals(PatternType.STRIPE_DOWNRIGHT)
                || patternType.equals(PatternType.STRIPE_DOWNLEFT)
                || patternType.equals(PatternType.TRIANGLE_BOTTOM)
                || patternType.equals(PatternType.TRIANGLE_TOP)
                || patternType.equals(PatternType.TRIANGLES_BOTTOM)
                || patternType.equals(PatternType.TRIANGLES_TOP)
                || patternType.equals(PatternType.DIAGONAL_LEFT)
                || patternType.equals(PatternType.DIAGONAL_RIGHT)
                || patternType.equals(PatternType.DIAGONAL_UP_LEFT)
                || patternType.equals(PatternType.DIAGONAL_UP_RIGHT)) {
                materialInventory.addItem(DyeColorUtil.toDyeItemStack(dyeColor, 3));
            } else if (patternType.equals(PatternType.SMALL_STRIPES)
                || patternType.equals(PatternType.RHOMBUS)
                || patternType.equals(PatternType.GRADIENT)
                || patternType.equals(PatternType.GRADIENT_UP)) {
                materialInventory.addItem(DyeColorUtil.toDyeItemStack(dyeColor, 4));
            } else if (patternType.equals(PatternType.CROSS)
                || patternType.equals(PatternType.STRAIGHT_CROSS)) {
                materialInventory.addItem(DyeColorUtil.toDyeItemStack(dyeColor, 5));
            } else if (patternType.equals(PatternType.HALF_VERTICAL)
                || patternType.equals(PatternType.HALF_HORIZONTAL)
                || patternType.equals(PatternType.HALF_VERTICAL_RIGHT)
                || patternType.equals(PatternType.HALF_HORIZONTAL_BOTTOM)) {
                materialInventory.addItem(DyeColorUtil.toDyeItemStack(dyeColor, 6));
            } else if (patternType.equals(PatternType.BORDER)) {
                materialInventory.addItem(DyeColorUtil.toDyeItemStack(dyeColor, 8));
            } else if (patternType.equals(PatternType.CURLY_BORDER)) {
                materialInventory.addItem(new ItemStack(Material.VINE));
                if (!pattern.getColor().equals(DyeColor.BLACK)) {
                    materialInventory.addItem(DyeColorUtil.toDyeItemStack(dyeColor, 1));
                }
            } else if (patternType.equals(PatternType.CREEPER)) {
                materialInventory.addItem(new ItemStack(Material.CREEPER_HEAD));
                if (!pattern.getColor().equals(DyeColor.BLACK)) {
                    materialInventory.addItem(DyeColorUtil.toDyeItemStack(dyeColor, 1));
                }
            } else if (patternType.equals(PatternType.BRICKS)) {
                materialInventory.addItem(new ItemStack(Material.BRICK));
                if (!pattern.getColor().equals(DyeColor.BLACK)) {
                    materialInventory.addItem(DyeColorUtil.toDyeItemStack(dyeColor, 1));
                }
            } else if (patternType.equals(PatternType.SKULL)) {
                materialInventory.addItem(new ItemStack(Material.WITHER_SKELETON_SKULL));
                if (!pattern.getColor().equals(DyeColor.BLACK)) {
                    materialInventory.addItem(DyeColorUtil.toDyeItemStack(dyeColor, 1));
                }
            } else if (patternType.equals(PatternType.FLOWER)) {
                materialInventory.addItem(new ItemStack(Material.OXEYE_DAISY));
                if (!pattern.getColor().equals(DyeColor.BLACK)) {
                    materialInventory.addItem(DyeColorUtil.toDyeItemStack(dyeColor, 1));
                }
            } else if (patternType.equals(PatternType.MOJANG)) {
                materialInventory.addItem(new ItemStack(Material.ENCHANTED_GOLDEN_APPLE));
                if (!pattern.getColor().equals(DyeColor.BLACK)) {
                    materialInventory.addItem(DyeColorUtil.toDyeItemStack(dyeColor, 1));
                }
            } else if (patternType.equals(PatternType.PIGLIN)) {// 圖形樣式材料不會被消耗，最多只會需要一個
                // TODO: 應該移到後面整個一起處理
                if (!materialInventory.contains(Material.PIGLIN_BANNER_PATTERN)) {
                    materialInventory.addItem(new ItemStack(Material.PIGLIN_BANNER_PATTERN));
                }
                materialInventory.addItem(DyeColorUtil.toDyeItemStack(dyeColor, 1));
            } else if (patternType.equals(PatternType.GLOBE)) {// 圖形樣式材料不會被消耗，最多只會需要一個
                // TODO: 應該移到後面整個一起處理
                if (!materialInventory.contains(Material.GLOBE_BANNER_PATTERN)) {
                    materialInventory.addItem(new ItemStack(Material.GLOBE_BANNER_PATTERN));
                }
                materialInventory.addItem(DyeColorUtil.toDyeItemStack(dyeColor, 1));
            } else if (patternType.equals(PatternType.FLOW)) {// 圖形樣式材料不會被消耗，最多只會需要一個
                // TODO: 應該移到後面整個一起處理
                if (!materialInventory.contains(Material.FLOW_BANNER_PATTERN)) {
                    materialInventory.addItem(new ItemStack(Material.FLOW_BANNER_PATTERN));
                }
                materialInventory.addItem(DyeColorUtil.toDyeItemStack(dyeColor, 1));
            } else if (patternType.equals(PatternType.GUSTER)) {// 圖形樣式材料不會被消耗，最多只會需要一個
                // TODO: 應該移到後面整個一起處理
                if (!materialInventory.contains(Material.GUSTER_BANNER_PATTERN)) {
                    materialInventory.addItem(new ItemStack(Material.GUSTER_BANNER_PATTERN));
                }
                materialInventory.addItem(DyeColorUtil.toDyeItemStack(dyeColor, 1));
            }
        }
        //加到暫存清單
        List<ItemStack> patternMaterials = new ArrayList<>();
        Collections.addAll(patternMaterials, materialInventory.getContents());
        //重新排序
        InventoryUtil.sort(patternMaterials);
        //將材料加到清單中
        materialList.addAll(patternMaterials);

        return materialList;
    }

    /**
     * 檢查是否擁有足夠材料
     *
     * @param inventory 指定物品欄
     * @param banner    旗幟
     * @return 是否擁有足夠材料
     */
    static public boolean hasEnoughMaterials(Inventory inventory, ItemStack banner) {
        //只檢查旗幟
        if (!isBanner(banner)) {
            return false;
        }
        //材料清單
        List<ItemStack> materials = getMaterials(banner);
        for (ItemStack material : materials) {
            //任何一項不足
            if (!inventory.containsAtLeast(material, material.getAmount())) {
                //直接回傳false
                return false;
            }
        }
        return true;
    }

    /**
     * 是否可以在生存模式合成（不超過6個pattern）
     *
     * @param banner 旗幟
     * @return 是否可以合成
     */
    static public boolean isCraftableInSurvival(ItemStack banner) {
        //只檢查旗幟
        if (!isBanner(banner)) {
            return false;
        }
        int patternCount = ((BannerMeta) Objects.requireNonNull(banner.getItemMeta())).numberOfPatterns();
        return patternCount <= 6;
    }

    /**
     * 是否可以合成
     *
     * @param player 玩家
     * @param banner 旗幟
     * @return 是否可以合成
     */
    static public boolean isCraftable(Player player, ItemStack banner) {
        //只檢查旗幟
        if (!isBanner(banner)) {
            return false;
        }
        // 若啟用複雜合成功能，則額外檢查玩家是否擁有對應權限
        if (BannerMaker.getInstance().enableComplexBannerCraft) {
            if (player.hasPermission("BannerMaker.getBanner.complex-craft")) {
                return true;
            }
        }
        return isCraftableInSurvival(banner);
    }

    /**
     * 從物品欄移除材料
     *
     * @param inventory 指定物品欄
     * @param banner    旗幟
     * @return 是否順利移除材料
     */
    static private boolean removeMaterials(Inventory inventory, ItemStack banner) {
        //只檢查旗幟
        if (!isBanner(banner)) {
            return false;
        }
        //材料必須足夠
        if (!hasEnoughMaterials(inventory, banner)) {
            return false;
        }
        //材料清單
        List<ItemStack> materials = getMaterials(banner);
        //過濾材料，不須消耗旗幟圖形
        materials.removeIf(BannerUtil::isBannerPatternItemStack);
        HashMap<Integer, ItemStack> itemCannotRemoved = inventory.removeItem(materials.toArray(new ItemStack[0]));
        if (!itemCannotRemoved.isEmpty()) {
            return false;
        }
        return true;
    }

    /**
     * 給予玩家單一旗幟
     *
     * @param player 要給予物品的玩家
     * @param banner 要給予的旗幟
     * @return 是否成功給予
     */
    public static boolean buy(Player player, ItemStack banner) {
        //檢查是否啟用經濟
        if (BannerMaker.getInstance().econ == null) {
            //未啟用經濟，強制失敗
            player.sendMessage(MessageUtil.format(true, "&cError: Economy not supported"));
            return false;
        }
        //價格
        double price = EconUtil.getPrice(banner);
        //檢查財產是否足夠
        if (!BannerMaker.getInstance().econ.has(player, price)) {
            //財產不足
            player.sendMessage(MessageUtil.format(true, "&c" + tl("general.no-money")));
            return false;
        }
        //扣款
        EconomyResponse response = BannerMaker.getInstance().econ.withdrawPlayer(player, price);
        //檢查交易是否成功
        if (!response.transactionSuccess()) {
            //交易失敗
            player.sendMessage(MessageUtil.format(true, "&cError: " + response.errorMessage));
            return false;
        }
        InventoryUtil.give(player, banner);
        player.sendMessage(MessageUtil.format(true, "&a" + tl("general.money-transaction", BannerMaker.getInstance().econ.format(response.amount), BannerMaker.getInstance().econ.format(response.balance))));
        return true;
    }

    /**
     * 使用材料合成旗幟
     * FIXME: 即使pattern過多，也仍然能合成，可能需要權限限制
     *
     * @param player 要給予物品的玩家
     * @param banner 要給予的旗幟
     * @return 是否成功給予
     */
    public static boolean craft(Player player, ItemStack banner) {
        //檢查材料
        if (!hasEnoughMaterials(player.getInventory(), banner)) {
            return false;
        }
        //移除材料
        removeMaterials(player.getInventory(), banner);

        InventoryUtil.give(player, banner);
        return true;
    }

    /**
     * 取得旗幟在玩家存檔中的Key
     *
     * @param banner 欲檢查之旗幟
     * @return String
     */
    static public String getKey(ItemStack banner) {
        //只處理旗幟
        if (!isBanner(banner)) {
            return null;
        }
        ItemMeta itemMeta = Objects.requireNonNull(banner.getItemMeta());
        return PersistentDataUtil.get(itemMeta, "banner-key");
    }

    /**
     * 取得旗幟名稱，若無名稱則嘗試取得KEY
     *
     * @param banner 欲檢查之旗幟
     * @return String
     */
    static public String getName(ItemStack banner) {
        //只處理旗幟
        if (!isBanner(banner)) {
            return null;
        }
        //先試著取得自訂名稱
        if (banner.hasItemMeta() && Objects.requireNonNull(banner.getItemMeta()).hasDisplayName()) {
            return banner.getItemMeta().getDisplayName();
        }
        //嘗試取得key
        String key = BannerUtil.getKey(banner);
        if (key != null) {
            return key;
        }
        //若都沒有，回傳空字串
        return "";
    }

    public static List<PatternType> getPatternTypeList() {
        List<PatternType> list = Arrays.asList(
            PatternType.BORDER,
            PatternType.BRICKS,
            PatternType.CIRCLE,
            PatternType.CREEPER,
            PatternType.CROSS,
            PatternType.CURLY_BORDER,
            PatternType.DIAGONAL_LEFT,
            PatternType.DIAGONAL_UP_LEFT,
            PatternType.DIAGONAL_RIGHT,
            PatternType.DIAGONAL_UP_RIGHT,
            PatternType.FLOWER,
            PatternType.GLOBE,
            PatternType.GRADIENT,
            PatternType.GRADIENT_UP,
            PatternType.HALF_HORIZONTAL,
            PatternType.HALF_HORIZONTAL_BOTTOM,
            PatternType.HALF_VERTICAL,
            PatternType.HALF_VERTICAL_RIGHT,
            PatternType.MOJANG,
            PatternType.PIGLIN,
            PatternType.RHOMBUS,
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
            PatternType.SMALL_STRIPES,
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
        if (!isBanner(banner)) {
            return recipe;
        }
        BannerMeta bm = (BannerMeta) Objects.requireNonNull(banner.getItemMeta());
        int totalStep = bm.numberOfPatterns() + 1;
        //顏色
        DyeColor baseColor = DyeColorUtil.of(banner.getType());
        if (step == 1) {
            //第一步，旗幟合成
            //羊毛
            ItemStack wool = new ItemStack(DyeColorUtil.toWoolMaterial(baseColor));
            for (int i = 0; i < 6; i++) {
                recipe.put(i, wool.clone());
            }
            //木棒
            ItemStack stick = new ItemStack(Material.STICK);
            recipe.put(7, stick);
        } else if (step <= totalStep) {
            //新增Pattern
            //當前banner
            ItemStack prevBanner = new ItemStack(DyeColorUtil.toBannerMaterial(baseColor));
            BannerMeta pbm = (BannerMeta) Objects.requireNonNull(prevBanner.getItemMeta());
            //新增至目前的Pattern
            for (int i = 0; i < step - 2; i++) {
                pbm.addPattern(bm.getPattern(i));
            }
            prevBanner.setItemMeta(pbm);
            //當前Pattern
            Pattern pattern = bm.getPattern(step - 2);
            //所需染料
            DyeColor dyeColor = pattern.getColor();
            ItemStack dyeItem = DyeColorUtil.toDyeItemStack(dyeColor, 1);
            //旗幟位置
            int bannerPosition = 4;
            //染料位置
            List<Integer> dyePosition = Collections.emptyList();
            //根據Pattern決定位置
            PatternType patternType = pattern.getPattern();
            if (patternType.equals(PatternType.SQUARE_BOTTOM_LEFT)) {
                dyePosition = Collections.singletonList(6);
            } else if (patternType.equals(PatternType.SQUARE_BOTTOM_RIGHT)) {
                dyePosition = Collections.singletonList(8);
            } else if (patternType.equals(PatternType.SQUARE_TOP_LEFT)) {
                dyePosition = Collections.singletonList(0);
            } else if (patternType.equals(PatternType.SQUARE_TOP_RIGHT)) {
                dyePosition = Collections.singletonList(2);
            } else if (patternType.equals(PatternType.STRIPE_BOTTOM)) {
                dyePosition = Arrays.asList(6, 7, 8);
            } else if (patternType.equals(PatternType.STRIPE_TOP)) {
                dyePosition = Arrays.asList(0, 1, 2);
            } else if (patternType.equals(PatternType.STRIPE_LEFT)) {
                dyePosition = Arrays.asList(0, 3, 6);
            } else if (patternType.equals(PatternType.STRIPE_RIGHT)) {
                dyePosition = Arrays.asList(2, 5, 8);
            } else if (patternType.equals(PatternType.STRIPE_CENTER)) {
                bannerPosition = 3;
                dyePosition = Arrays.asList(1, 4, 7);
            } else if (patternType.equals(PatternType.STRIPE_MIDDLE)) {
                bannerPosition = 1;
                dyePosition = Arrays.asList(3, 4, 5);
            } else if (patternType.equals(PatternType.STRIPE_DOWNRIGHT)) {
                bannerPosition = 1;
                dyePosition = Arrays.asList(0, 4, 8);
            } else if (patternType.equals(PatternType.STRIPE_DOWNLEFT)) {
                bannerPosition = 1;
                dyePosition = Arrays.asList(2, 4, 6);
            } else if (patternType.equals(PatternType.SMALL_STRIPES)) {
                dyePosition = Arrays.asList(0, 2, 3, 5);
            } else if (patternType.equals(PatternType.CROSS)) {
                bannerPosition = 1;
                dyePosition = Arrays.asList(0, 2, 4, 6, 8);
            } else if (patternType.equals(PatternType.STRAIGHT_CROSS)) {
                bannerPosition = 0;
                dyePosition = Arrays.asList(1, 3, 4, 5, 7);
            } else if (patternType.equals(PatternType.TRIANGLE_BOTTOM)) {
                bannerPosition = 7;
                dyePosition = Arrays.asList(4, 6, 8);
            } else if (patternType.equals(PatternType.TRIANGLE_TOP)) {
                bannerPosition = 1;
                dyePosition = Arrays.asList(0, 2, 4);
            } else if (patternType.equals(PatternType.TRIANGLES_BOTTOM)) {
                dyePosition = Arrays.asList(3, 5, 7);
            } else if (patternType.equals(PatternType.TRIANGLES_TOP)) {
                dyePosition = Arrays.asList(1, 3, 5);
            } else if (patternType.equals(PatternType.DIAGONAL_LEFT)) {
                dyePosition = Arrays.asList(0, 1, 3);
            } else if (patternType.equals(PatternType.DIAGONAL_RIGHT)) {
                dyePosition = Arrays.asList(5, 7, 8);
            } else if (patternType.equals(PatternType.DIAGONAL_UP_LEFT)) {
                dyePosition = Arrays.asList(3, 6, 7);
            } else if (patternType.equals(PatternType.DIAGONAL_UP_RIGHT)) {
                dyePosition = Arrays.asList(1, 2, 5);
            } else if (patternType.equals(PatternType.CIRCLE)) {
                bannerPosition = 1;
                dyePosition = Collections.singletonList(4);
            } else if (patternType.equals(PatternType.RHOMBUS)) {
                dyePosition = Arrays.asList(1, 3, 5, 7);
            } else if (patternType.equals(PatternType.HALF_VERTICAL)) {
                bannerPosition = 5;
                dyePosition = Arrays.asList(0, 1, 3, 4, 6, 7);
            } else if (patternType.equals(PatternType.HALF_HORIZONTAL)) {
                bannerPosition = 7;
                dyePosition = Arrays.asList(0, 1, 2, 3, 4, 5);
            } else if (patternType.equals(PatternType.HALF_VERTICAL_RIGHT)) {
                bannerPosition = 3;
                dyePosition = Arrays.asList(1, 2, 4, 5, 7, 8);
            } else if (patternType.equals(PatternType.HALF_HORIZONTAL_BOTTOM)) {
                bannerPosition = 1;
                dyePosition = Arrays.asList(3, 4, 5, 6, 7, 8);
            } else if (patternType.equals(PatternType.BORDER)) {
                dyePosition = Arrays.asList(0, 1, 2, 3, 5, 6, 7, 8);
            } else if (patternType.equals(PatternType.CURLY_BORDER)) {
                recipe.put(1, new ItemStack(Material.VINE));
                if (!pattern.getColor().equals(DyeColor.BLACK)) {
                    dyePosition = Collections.singletonList(7);
                }
            } else if (patternType.equals(PatternType.CREEPER)) {
                recipe.put(1, new ItemStack(Material.CREEPER_HEAD));
                if (!pattern.getColor().equals(DyeColor.BLACK)) {
                    dyePosition = Collections.singletonList(7);
                }
            } else if (patternType.equals(PatternType.GRADIENT)) {
                bannerPosition = 1;
                dyePosition = Arrays.asList(0, 2, 4, 7);
            } else if (patternType.equals(PatternType.GRADIENT_UP)) {
                bannerPosition = 7;
                dyePosition = Arrays.asList(1, 4, 6, 8);
            } else if (patternType.equals(PatternType.BRICKS)) {
                recipe.put(1, new ItemStack(Material.BRICK));
                if (!pattern.getColor().equals(DyeColor.BLACK)) {
                    dyePosition = Collections.singletonList(7);
                }
            } else if (patternType.equals(PatternType.SKULL)) {
                recipe.put(1, new ItemStack(Material.WITHER_SKELETON_SKULL));
                if (!pattern.getColor().equals(DyeColor.BLACK)) {
                    dyePosition = Collections.singletonList(7);
                }
            } else if (patternType.equals(PatternType.FLOWER)) {
                recipe.put(1, new ItemStack(Material.OXEYE_DAISY));
                if (!pattern.getColor().equals(DyeColor.BLACK)) {
                    dyePosition = Collections.singletonList(7);
                }
            } else if (patternType.equals(PatternType.MOJANG)) {
                recipe.put(1, new ItemStack(Material.ENCHANTED_GOLDEN_APPLE));
                if (!pattern.getColor().equals(DyeColor.BLACK)) {
                    dyePosition = Collections.singletonList(7);
                }
            } else if (patternType.equals(PatternType.PIGLIN)) {
                recipe.put(7, new ItemStack(Material.PIGLIN_BANNER_PATTERN));
                dyePosition = Collections.singletonList(5);
            } else if (patternType.equals(PatternType.GLOBE)) {
                recipe.put(7, new ItemStack(Material.GLOBE_BANNER_PATTERN));
                dyePosition = Collections.singletonList(5);
            } else if (patternType.equals(PatternType.FLOW)) {
                recipe.put(7, new ItemStack(Material.FLOW_BANNER_PATTERN));
                dyePosition = Collections.singletonList(5);
            } else if (patternType.equals(PatternType.GUSTER)) {
                recipe.put(7, new ItemStack(Material.GUSTER_BANNER_PATTERN));
                dyePosition = Collections.singletonList(5);
            }
            //放置旗幟與染料
            recipe.put(bannerPosition, prevBanner);
            for (int i : dyePosition) {
                recipe.put(i, dyeItem.clone());
            }
        }
        //合成結果
        //當前banner
        ItemStack currentBanner = new ItemStack(DyeColorUtil.toBannerMaterial(baseColor));
        BannerMeta cbm = (BannerMeta) Objects.requireNonNull(currentBanner.getItemMeta());
        //新增至目前的Pattern
        for (int i = 0; i < step - 1; i++) {
            cbm.addPattern(bm.getPattern(i));
        }
        currentBanner.setItemMeta(cbm);
        recipe.put(9, currentBanner);

        return recipe;
    }

    static public String serialize(ItemStack banner) {
        //只檢查旗幟
        if (!isBanner(banner)) {
            return null;
        }
        DyeColor color = Objects.requireNonNull(DyeColorUtil.of(banner.getType()));
        short colorCode = DyeColorUtil.toShort(color);
        StringBuilder dataStringBuilder = new StringBuilder(String.valueOf(colorCode));

        BannerMeta bm = (BannerMeta) Objects.requireNonNull(banner.getItemMeta());

        for (Pattern pattern : bm.getPatterns()) {
            dataStringBuilder
                .append(";")
                .append(pattern.getPattern().getIdentifier())
                .append(":")
                .append(DyeColorUtil.toShort(pattern.getColor()));
        }
        String dataString = dataStringBuilder.toString();

        return SerializationUtil.objectToBase64(dataString);
    }

    static public ItemStack deserialize(String bannerString) {
        try {
            String dataString = SerializationUtil.objectFromBase64(bannerString);
            String[] dataArray = dataString.split(";");

            ItemStack banner = new ItemStack(DyeColorUtil.toBannerMaterial(DyeColorUtil.of(Short.parseShort(dataArray[0]))));

            BannerMeta bm = (BannerMeta) Objects.requireNonNull(banner.getItemMeta());

            for (int i = 1; i < dataArray.length; i++) {
                String[] patternData = dataArray[i].split(":");
                PatternType patternType = PatternType.getByIdentifier(patternData[0]);
                DyeColor patternColor = DyeColorUtil.of(Short.parseShort(patternData[1]));
                Pattern pattern = new Pattern(patternColor, Objects.requireNonNull(patternType));
                bm.addPattern(pattern);
            }
            banner.setItemMeta(bm);
            return banner;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
