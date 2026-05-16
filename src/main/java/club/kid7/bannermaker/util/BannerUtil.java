package club.kid7.bannermaker.util;

import club.kid7.bannermaker.BannerMaker;
import club.kid7.bannermaker.registry.DyeColorRegistry;
import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XTag;
import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Registry;
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
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


public class BannerUtil {

    /**
     * 累積各 Pattern 對材料的貢獻：以 Material 為 key 計數，並對 loom 旗幟圖形物品只計入一次。
     * 取代原本以 54 格 {@link Inventory} 作為累加器的寫法，意圖更明確、無 Bukkit Inventory 副作用。
     */
    private static final class MaterialAccumulator {
        private final Map<Material, Integer> counts = new LinkedHashMap<>();
        private final Set<Material> loomItemsAdded = new HashSet<>();

        void add(ItemStack stack) {
            if (stack == null || stack.getType().isAir()) {
                return;
            }
            counts.merge(stack.getType(), stack.getAmount(), Integer::sum);
        }

        /** Loom 旗幟圖形物品：整個合成流程中只計入一次（後續貢獻者再呼叫不會重複加）。 */
        void addLoomItemOnce(Material type) {
            if (loomItemsAdded.add(type)) {
                counts.merge(type, 1, Integer::sum);
            }
        }

        /** 攤平為 ItemStack 清單，自動依各 Material 的 maxStackSize 切分。 */
        List<ItemStack> toItemStacks() {
            List<ItemStack> result = new ArrayList<>();
            counts.forEach((mat, count) -> {
                int remaining = count;
                int stackSize = Math.max(1, mat.getMaxStackSize());
                while (remaining > 0) {
                    int take = Math.min(remaining, stackSize);
                    result.add(new ItemStack(mat, take));
                    remaining -= take;
                }
            });
            return result;
        }
    }

    /**
     * Pattern 對材料貢獻策略：給定累積器與該 pattern 的染色，
     * 把這個 pattern 應消耗的所有 ItemStack 加入累積器。
     */
    @FunctionalInterface
    private interface PatternMaterialContributor {
        void contribute(MaterialAccumulator acc, DyeColor color);
    }

    /** N 個對應色染料的 contributor。 */
    private static PatternMaterialContributor dyeOnly(int count) {
        return (acc, color) -> acc.add(DyeColorRegistry.getDyeItemStack(color, count));
    }

    /** 一個特殊物品 + 1 個對應色染料（黑色為 base，不額外加色）。 */
    private static PatternMaterialContributor specialWithOptionalDye(Material special) {
        return (acc, color) -> {
            acc.add(new ItemStack(special));
            if (!color.equals(DyeColor.BLACK)) {
                acc.add(DyeColorRegistry.getDyeItemStack(color, 1));
            }
        };
    }

    /** Loom 用的旗幟圖形物品：累積過程中只需要 1 個（可重複使用），每次 pattern 仍消耗 1 染料。 */
    private static PatternMaterialContributor patternItem(Material item) {
        return (acc, color) -> {
            acc.addLoomItemOnce(item);
            acc.add(DyeColorRegistry.getDyeItemStack(color, 1));
        };
    }

    /** BRICKS 的版本敏感 contributor：1.21.2+ 用 FIELD_MASONED_BANNER_PATTERN，否則 fallback BRICK。 */
    private static PatternMaterialContributor bricksContributor() {
        return (acc, color) -> {
            Material fieldMasoned = Material.matchMaterial("FIELD_MASONED_BANNER_PATTERN");
            acc.add(new ItemStack(fieldMasoned != null ? fieldMasoned : Material.BRICK));
            if (!color.equals(DyeColor.BLACK)) {
                acc.add(DyeColorRegistry.getDyeItemStack(color, 1));
            }
        };
    }

    /**
     * 各 PatternType 對應的材料貢獻策略表。
     * key 宣告為 Object 以避免 invokeinterface（詳見 getMaterials 內註解）。
     */
    private static final Map<Object, PatternMaterialContributor> MATERIAL_CONTRIBUTORS = Map.ofEntries(
        // 1 染料 ── 方形角落與圓
        Map.entry(PatternType.SQUARE_BOTTOM_LEFT, dyeOnly(1)),
        Map.entry(PatternType.SQUARE_BOTTOM_RIGHT, dyeOnly(1)),
        Map.entry(PatternType.SQUARE_TOP_LEFT, dyeOnly(1)),
        Map.entry(PatternType.SQUARE_TOP_RIGHT, dyeOnly(1)),
        Map.entry(PatternType.CIRCLE, dyeOnly(1)),
        // 3 染料 ── 條紋、三角、對角
        Map.entry(PatternType.STRIPE_BOTTOM, dyeOnly(3)),
        Map.entry(PatternType.STRIPE_TOP, dyeOnly(3)),
        Map.entry(PatternType.STRIPE_LEFT, dyeOnly(3)),
        Map.entry(PatternType.STRIPE_RIGHT, dyeOnly(3)),
        Map.entry(PatternType.STRIPE_CENTER, dyeOnly(3)),
        Map.entry(PatternType.STRIPE_MIDDLE, dyeOnly(3)),
        Map.entry(PatternType.STRIPE_DOWNRIGHT, dyeOnly(3)),
        Map.entry(PatternType.STRIPE_DOWNLEFT, dyeOnly(3)),
        Map.entry(PatternType.TRIANGLE_BOTTOM, dyeOnly(3)),
        Map.entry(PatternType.TRIANGLE_TOP, dyeOnly(3)),
        Map.entry(PatternType.TRIANGLES_BOTTOM, dyeOnly(3)),
        Map.entry(PatternType.TRIANGLES_TOP, dyeOnly(3)),
        Map.entry(PatternType.DIAGONAL_LEFT, dyeOnly(3)),
        Map.entry(PatternType.DIAGONAL_RIGHT, dyeOnly(3)),
        Map.entry(PatternType.DIAGONAL_UP_LEFT, dyeOnly(3)),
        Map.entry(PatternType.DIAGONAL_UP_RIGHT, dyeOnly(3)),
        // 4 染料 ── 小條紋、菱形、漸層
        Map.entry(PatternType.SMALL_STRIPES, dyeOnly(4)),
        Map.entry(PatternType.RHOMBUS, dyeOnly(4)),
        Map.entry(PatternType.GRADIENT, dyeOnly(4)),
        Map.entry(PatternType.GRADIENT_UP, dyeOnly(4)),
        // 5 染料 ── 十字
        Map.entry(PatternType.CROSS, dyeOnly(5)),
        Map.entry(PatternType.STRAIGHT_CROSS, dyeOnly(5)),
        // 6 染料 ── 半邊
        Map.entry(PatternType.HALF_VERTICAL, dyeOnly(6)),
        Map.entry(PatternType.HALF_HORIZONTAL, dyeOnly(6)),
        Map.entry(PatternType.HALF_VERTICAL_RIGHT, dyeOnly(6)),
        Map.entry(PatternType.HALF_HORIZONTAL_BOTTOM, dyeOnly(6)),
        // 8 染料 ── 邊框
        Map.entry(PatternType.BORDER, dyeOnly(8)),
        // 特殊物品 + 染料
        Map.entry(PatternType.CURLY_BORDER, specialWithOptionalDye(Material.VINE)),
        Map.entry(PatternType.CREEPER, specialWithOptionalDye(Material.CREEPER_HEAD)),
        Map.entry(PatternType.SKULL, specialWithOptionalDye(Material.WITHER_SKELETON_SKULL)),
        Map.entry(PatternType.FLOWER, specialWithOptionalDye(Material.OXEYE_DAISY)),
        Map.entry(PatternType.MOJANG, specialWithOptionalDye(Material.ENCHANTED_GOLDEN_APPLE)),
        Map.entry(PatternType.BRICKS, bricksContributor()),
        // 旗幟圖形物品（loom 用，可重複使用）
        Map.entry(PatternType.PIGLIN, patternItem(Material.PIGLIN_BANNER_PATTERN)),
        Map.entry(PatternType.GLOBE, patternItem(Material.GLOBE_BANNER_PATTERN)),
        Map.entry(PatternType.FLOW, patternItem(Material.FLOW_BANNER_PATTERN)),
        Map.entry(PatternType.GUSTER, patternItem(Material.GUSTER_BANNER_PATTERN))
    );

    /**
     * 檢查 ItemStack 是否為旗幟
     *
     * @param itemStack 要檢查的物品
     * @return 是否為旗幟
     */
    static public boolean isBanner(ItemStack itemStack) {
        if (itemStack == null) {
            return false;
        }
        return isBanner(itemStack.getType());
    }

    /**
     * 檢查 Material 是否為旗幟
     *
     * @param material 要檢查的材質
     * @return 是否為旗幟
     */
    static public boolean isBanner(Material material) {
        if (material == null) {
            return false;
        }
        return XTag.BANNERS.isTagged(XMaterial.matchXMaterial(material));
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
        DyeColor baseColor = DyeColorRegistry.getDyeColor(banner.getType());
        //羊毛
        ItemStack wool = new ItemStack(DyeColorRegistry.getWoolMaterial(baseColor), 6);
        materialList.add(wool);
        //Pattern 材料：用 MaterialAccumulator 累積，避免建立 54 格 Bukkit Inventory 作為暫存容器
        MaterialAccumulator accumulator = new MaterialAccumulator();
        BannerMeta bm = (BannerMeta) Objects.requireNonNull(banner.getItemMeta());
        //逐 Pattern 累積材料：實際對應規則於 MATERIAL_CONTRIBUTORS 查表，
        //patternType 宣告為 Object 是為了避開 PatternType class ↔ interface 二進位相容問題（詳 CLAUDE.md）。
        for (Pattern pattern : bm.getPatterns()) {
            Object patternType = pattern.getPattern();
            PatternMaterialContributor contributor = MATERIAL_CONTRIBUTORS.get(patternType);
            if (contributor != null) {
                contributor.contribute(accumulator, pattern.getColor());
            }
        }
        List<ItemStack> patternMaterials = accumulator.toItemStacks();
        InventoryUtil.sort(patternMaterials);
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
        if (BannerMaker.getInstance().isEnableComplexBannerCraft()) {
            if (player.hasPermission("BannerMaker.getBanner.complex-craft")) {
                return true;
            }
        }
        return isCraftableInSurvival(banner);
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
        return Registry.BANNER_PATTERN.stream()
            .sorted(Comparator.comparing(p -> p.getKey().toString()))
            .filter(pattern -> !pattern.getKey().getKey().equals("base"))
            .collect(Collectors.toList());
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
        DyeColor baseColor = DyeColorRegistry.getDyeColor(banner.getType());
        if (step == 1) {
            //第一步，旗幟合成
            //羊毛
            ItemStack wool = new ItemStack(DyeColorRegistry.getWoolMaterial(baseColor));
            for (int i = 0; i < 6; i++) {
                recipe.put(i, wool.clone());
            }
            //木棒
            ItemStack stick = new ItemStack(Material.STICK);
            recipe.put(7, stick);
        } else if (step <= totalStep) {
            //新增Pattern
            //當前banner
            ItemStack prevBanner = new ItemStack(DyeColorRegistry.getBannerMaterial(baseColor));
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
            ItemStack dyeItem = DyeColorRegistry.getDyeItemStack(dyeColor, 1);
            //旗幟位置
            int bannerPosition = 4;
            //染料位置
            List<Integer> dyePosition = Collections.emptyList();
            //根據Pattern決定位置（宣告為 Object 以避免 invokeinterface，詳見 getMaterials 註解）
            Object patternType = pattern.getPattern();
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
                dyePosition = Arrays.asList(1, 2, 5);
            } else if (patternType.equals(PatternType.DIAGONAL_UP_LEFT)) {
                dyePosition = Arrays.asList(3, 6, 7);
            } else if (patternType.equals(PatternType.DIAGONAL_UP_RIGHT)) {
                dyePosition = Arrays.asList(5, 7, 8);
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
                // 1.21.2 起 loom 不再接受 brick；改為由 FIELD_MASONED_BANNER_PATTERN 物品產生 bricks pattern
                Material fieldMasoned = Material.matchMaterial("FIELD_MASONED_BANNER_PATTERN");
                recipe.put(1, fieldMasoned != null
                    ? new ItemStack(fieldMasoned)
                    : new ItemStack(Material.BRICK));
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
        ItemStack currentBanner = new ItemStack(DyeColorRegistry.getBannerMaterial(baseColor));
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
        DyeColor color = Objects.requireNonNull(DyeColorRegistry.getDyeColor(banner.getType()));
        int colorCode = DyeColorRegistry.getValue(color);
        StringBuilder dataStringBuilder = new StringBuilder(String.valueOf(colorCode));

        BannerMeta bm = (BannerMeta) Objects.requireNonNull(banner.getItemMeta());

        for (Pattern pattern : bm.getPatterns()) {
            dataStringBuilder
                .append(";")
                .append(pattern.getPattern().getIdentifier())
                .append(":")
                .append(DyeColorRegistry.getValue(pattern.getColor()));
        }
        String dataString = dataStringBuilder.toString();

        return SerializationUtil.objectToBase64(dataString);
    }

    static public ItemStack deserialize(String bannerString) {
        try {
            String dataString = SerializationUtil.objectFromBase64(bannerString);
            String[] dataArray = dataString.split(";");

            ItemStack banner = new ItemStack(DyeColorRegistry.getBannerMaterial(Integer.parseInt(dataArray[0])));

            BannerMeta bm = (BannerMeta) Objects.requireNonNull(banner.getItemMeta());

            for (int i = 1; i < dataArray.length; i++) {
                String[] patternData = dataArray[i].split(":");
                PatternType patternType = PatternType.getByIdentifier(patternData[0]);
                DyeColor patternColor = DyeColorRegistry.getDyeColor(Integer.parseInt(patternData[1]));
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
