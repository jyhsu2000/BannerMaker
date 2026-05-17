package tw.jyhsu.bannermaker.banner;

import tw.jyhsu.bannermaker.registry.DyeColorRegistry;
import tw.jyhsu.bannermaker.util.InventoryUtil;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 旗幟成本計算：給定一面 banner，回報插件內部定義的合成材料清單與「玩家是否有足夠材料」判斷。
 * 用於 GUI 顯示、{@link tw.jyhsu.bannermaker.service.BannerService#craft} 扣材料、
 * {@link tw.jyhsu.bannermaker.service.EconomyService#getPrice} 計算 banner 價格。
 * <p>
 * 各 PatternType 對應的材料規則由 {@code MATERIAL_CONTRIBUTORS} 查表決定（從 {@link BannerUtil} 抽出）。
 */
public class BannerCost {

    private BannerCost() {
        // Utility class
    }

    /**
     * 累積各 Pattern 對材料的貢獻：以 Material 為 key 計數，並對 loom 旗幟圖形物品只計入一次。
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
     * key 宣告為 Object 以避免 invokeinterface（詳見 CLAUDE.md 跨版本相容性陷阱）。
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
     * 取得旗幟材料清單
     *
     * @param banner 欲取得材料清單之旗幟
     * @return 該 banner 在插件內部定價下所需的完整材料清單；非合法 banner 回傳空清單
     */
    public static List<ItemStack> getMaterials(ItemStack banner) {
        List<ItemStack> materialList = new ArrayList<>();
        //只檢查旗幟
        if (!BannerUtil.isBanner(banner)) {
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
    public static boolean hasEnoughMaterials(Inventory inventory, ItemStack banner) {
        //只檢查旗幟
        if (!BannerUtil.isBanner(banner)) {
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
}
