package tw.jyhsu.bannermaker.util;

import tw.jyhsu.bannermaker.registry.DyeColorRegistry;
import com.google.common.collect.Maps;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 旗幟圖樣版面：負責產出 GUI 顯示用的 3x3 配方圖、判斷是否為 loom 配方、列出可用 PatternType。
 * <p>
 * 注意：產出的 3x3 grid 為 pre-1.14 vanilla 合成配方版面，**現代 vanilla 已改用 loom**；
 * 本類僅供 BannerInfoGUI 視覺化展示，並非可在合成台執行的真實配方。
 * 從 {@link BannerUtil} 抽出以遵循單一職責原則。
 */
public class BannerPatternLayout {

    private BannerPatternLayout() {
        // Utility class
    }

    /**
     * 用於 {@link #getPatternRecipe(ItemStack, int)} 的 3x3 配方展示。
     * 在 contributor 執行期間累積：旗幟要放哪個格、染料要放哪些格、額外物品（如 CREEPER_HEAD）已直接寫入 recipe map。
     */
    private static final class RecipeContext {
        private final DyeColor color;
        private final HashMap<Integer, ItemStack> recipe;
        int bannerSlot = 4;
        List<Integer> dyeSlots = Collections.emptyList();

        RecipeContext(DyeColor color, HashMap<Integer, ItemStack> recipe) {
            this.color = color;
            this.recipe = recipe;
        }

        RecipeContext bannerAt(int slot) {
            this.bannerSlot = slot;
            return this;
        }

        RecipeContext dyeAt(Integer... slots) {
            this.dyeSlots = Arrays.asList(slots);
            return this;
        }

        RecipeContext putAt(int slot, Material material) {
            recipe.put(slot, new ItemStack(material));
            return this;
        }

        /** 非黑色才需要額外染料；BLACK 色已是 base，不再加色。 */
        RecipeContext dyeAtIfNotBlack(int slot) {
            if (color != DyeColor.BLACK) {
                this.dyeSlots = Collections.singletonList(slot);
            }
            return this;
        }
    }

    @FunctionalInterface
    private interface PatternRecipeContributor {
        void contribute(RecipeContext ctx);
    }

    /** Banner 在預設位置 (4)，純擺染料於指定 slots。 */
    private static PatternRecipeContributor dyes(Integer... slots) {
        return ctx -> ctx.dyeAt(slots);
    }

    /** Banner 在指定位置，染料於後續 slots。 */
    private static PatternRecipeContributor bannerAndDyes(int bannerSlot, Integer... dyeSlots) {
        return ctx -> ctx.bannerAt(bannerSlot).dyeAt(dyeSlots);
    }

    /** 特殊物品於 slot 1，非黑色時再於 slot 7 加 1 染料。 */
    private static PatternRecipeContributor specialItem(Material item) {
        return ctx -> ctx.putAt(1, item).dyeAtIfNotBlack(7);
    }

    /** Loom 旗幟圖形物品於 slot 7，染料於 slot 5。 */
    private static PatternRecipeContributor loomPatternItem(Material item) {
        return ctx -> ctx.putAt(7, item).dyeAt(5);
    }

    /** BRICKS 版本敏感：1.21.2+ 用 FIELD_MASONED_BANNER_PATTERN，否則 BRICK；其餘同 specialItem。 */
    private static PatternRecipeContributor bricksRecipeContributor() {
        return ctx -> {
            Material fieldMasoned = Material.matchMaterial("FIELD_MASONED_BANNER_PATTERN");
            ctx.putAt(1, fieldMasoned != null ? fieldMasoned : Material.BRICK).dyeAtIfNotBlack(7);
        };
    }

    /** PatternType → 3x3 配方位置的查表。 */
    private static final Map<Object, PatternRecipeContributor> RECIPE_CONTRIBUTORS = Map.ofEntries(
        // 1 染料、預設 banner 位置
        Map.entry(PatternType.SQUARE_BOTTOM_LEFT, dyes(6)),
        Map.entry(PatternType.SQUARE_BOTTOM_RIGHT, dyes(8)),
        Map.entry(PatternType.SQUARE_TOP_LEFT, dyes(0)),
        Map.entry(PatternType.SQUARE_TOP_RIGHT, dyes(2)),
        // 3 染料、預設 banner 位置
        Map.entry(PatternType.STRIPE_BOTTOM, dyes(6, 7, 8)),
        Map.entry(PatternType.STRIPE_TOP, dyes(0, 1, 2)),
        Map.entry(PatternType.STRIPE_LEFT, dyes(0, 3, 6)),
        Map.entry(PatternType.STRIPE_RIGHT, dyes(2, 5, 8)),
        Map.entry(PatternType.TRIANGLES_BOTTOM, dyes(3, 5, 7)),
        Map.entry(PatternType.TRIANGLES_TOP, dyes(1, 3, 5)),
        Map.entry(PatternType.DIAGONAL_LEFT, dyes(0, 1, 3)),
        Map.entry(PatternType.DIAGONAL_RIGHT, dyes(1, 2, 5)),
        Map.entry(PatternType.DIAGONAL_UP_LEFT, dyes(3, 6, 7)),
        Map.entry(PatternType.DIAGONAL_UP_RIGHT, dyes(5, 7, 8)),
        Map.entry(PatternType.SMALL_STRIPES, dyes(0, 2, 3, 5)),
        Map.entry(PatternType.RHOMBUS, dyes(1, 3, 5, 7)),
        Map.entry(PatternType.BORDER, dyes(0, 1, 2, 3, 5, 6, 7, 8)),
        // banner 在非預設位置
        Map.entry(PatternType.STRIPE_CENTER, bannerAndDyes(3, 1, 4, 7)),
        Map.entry(PatternType.STRIPE_MIDDLE, bannerAndDyes(1, 3, 4, 5)),
        Map.entry(PatternType.STRIPE_DOWNRIGHT, bannerAndDyes(1, 0, 4, 8)),
        Map.entry(PatternType.STRIPE_DOWNLEFT, bannerAndDyes(1, 2, 4, 6)),
        Map.entry(PatternType.CROSS, bannerAndDyes(1, 0, 2, 4, 6, 8)),
        Map.entry(PatternType.STRAIGHT_CROSS, bannerAndDyes(0, 1, 3, 4, 5, 7)),
        Map.entry(PatternType.TRIANGLE_BOTTOM, bannerAndDyes(7, 4, 6, 8)),
        Map.entry(PatternType.TRIANGLE_TOP, bannerAndDyes(1, 0, 2, 4)),
        Map.entry(PatternType.CIRCLE, bannerAndDyes(1, 4)),
        Map.entry(PatternType.HALF_VERTICAL, bannerAndDyes(5, 0, 1, 3, 4, 6, 7)),
        Map.entry(PatternType.HALF_HORIZONTAL, bannerAndDyes(7, 0, 1, 2, 3, 4, 5)),
        Map.entry(PatternType.HALF_VERTICAL_RIGHT, bannerAndDyes(3, 1, 2, 4, 5, 7, 8)),
        Map.entry(PatternType.HALF_HORIZONTAL_BOTTOM, bannerAndDyes(1, 3, 4, 5, 6, 7, 8)),
        Map.entry(PatternType.GRADIENT, bannerAndDyes(1, 0, 2, 4, 7)),
        Map.entry(PatternType.GRADIENT_UP, bannerAndDyes(7, 1, 4, 6, 8)),
        // 特殊物品 + 條件染料
        Map.entry(PatternType.CURLY_BORDER, specialItem(Material.VINE)),
        Map.entry(PatternType.CREEPER, specialItem(Material.CREEPER_HEAD)),
        Map.entry(PatternType.SKULL, specialItem(Material.WITHER_SKELETON_SKULL)),
        Map.entry(PatternType.FLOWER, specialItem(Material.OXEYE_DAISY)),
        Map.entry(PatternType.MOJANG, specialItem(Material.ENCHANTED_GOLDEN_APPLE)),
        Map.entry(PatternType.BRICKS, bricksRecipeContributor()),
        // Loom 旗幟圖形物品
        Map.entry(PatternType.PIGLIN, loomPatternItem(Material.PIGLIN_BANNER_PATTERN)),
        Map.entry(PatternType.GLOBE, loomPatternItem(Material.GLOBE_BANNER_PATTERN)),
        Map.entry(PatternType.FLOW, loomPatternItem(Material.FLOW_BANNER_PATTERN)),
        Map.entry(PatternType.GUSTER, loomPatternItem(Material.GUSTER_BANNER_PATTERN))
    );

    /**
     * 取得伺服器目前支援的所有 banner pattern 類型清單（已排除 BASE）。
     * 結果依 namespaced key 排序，供 GUI 顯示與選擇使用。
     *
     * @return 可用的 PatternType 清單
     */
    public static List<PatternType> getPatternTypeList() {
        return Registry.BANNER_PATTERN.stream()
            .sorted(Comparator.comparing(p -> p.getKey().toString()))
            .filter(pattern -> !pattern.getKey().getKey().equals("base"))
            .collect(Collectors.toList());
    }

    /**
     * 判斷是否為紡織機配方
     *
     * @param patternRecipe 欲檢查配方，由 getPatternRecipe() 產出
     * @return 是否為紡織機配方
     */
    public static boolean isLoomRecipe(HashMap<Integer, ItemStack> patternRecipe) {
        for (Map.Entry<Integer, ItemStack> entry : patternRecipe.entrySet()) {
            ItemStack itemStack = entry.getValue();
            // 包含旗幟圖形物品者，直接視為紡織機配方
            if (BannerUtil.isBannerPatternItemStack(itemStack)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 為指定 banner 的第 {@code step} 層 pattern 建構 GUI 上顯示的 3x3 配方圖示。
     * 第 0 步顯示「材料準備」、第 1 步起每步顯示上一層的 banner 與當前 pattern 所需材料的擺放位置。
     * <p>
     * 注意：3x3 grid 為 pre-1.14 vanilla 合成配方版面，**現代 vanilla 已改用 loom**；
     * 本方法僅供 BannerInfoGUI 視覺化展示，並非可在合成台執行的真實配方。
     *
     * @param banner 完整旗幟（含所有 pattern）
     * @param step   配方步驟（1-based）；通常為 1 ~ 圖案數
     * @return 9 格 grid（key 為 0–8 slot index）的物品擺放圖
     */
    public static HashMap<Integer, ItemStack> getPatternRecipe(final ItemStack banner, int step) {
        HashMap<Integer, ItemStack> recipe = Maps.newHashMap();
        //填滿空氣
        for (int i = 0; i < 10; i++) {
            recipe.put(i, new ItemStack(Material.AIR));
        }
        //只處理旗幟
        if (!BannerUtil.isBanner(banner)) {
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
            //配方版面由 RECIPE_CONTRIBUTORS 查表決定；patternType 宣告為 Object 是為了避開 PatternType class ↔ interface 二進位相容問題
            Object patternType = pattern.getPattern();
            RecipeContext ctx = new RecipeContext(dyeColor, recipe);
            PatternRecipeContributor contributor = RECIPE_CONTRIBUTORS.get(patternType);
            if (contributor != null) {
                contributor.contribute(ctx);
            }
            //放置旗幟與染料
            recipe.put(ctx.bannerSlot, prevBanner);
            for (int i : ctx.dyeSlots) {
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
}
