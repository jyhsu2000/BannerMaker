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

    /**
     * 將 banner（底色 + 所有 pattern）編碼為 Base64 字串，供 {@code /bm view} 分享指令或網路傳輸使用。
     * 格式：{@code <colorCode>;<patternId>:<colorCode>;...} 之後再以 Base64 編碼。
     *
     * @param banner 欲序列化的旗幟
     * @return Base64 編碼後的字串；若 banner 不是合法 banner，回傳 {@code null}
     * @see #deserialize(String)
     */
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

    /**
     * 由 {@link #serialize(ItemStack)} 產生的 Base64 字串還原為 ItemStack。
     * 解析失敗時拋出 {@link RuntimeException}，呼叫端應自行 try/catch 處理（例如顯示「無效的旗幟字串」訊息）。
     *
     * @param bannerString {@code /bm view} 指令傳入的 Base64 字串
     * @return 還原後的 banner ItemStack
     * @throws RuntimeException 若字串無法解碼或結構不符
     * @see #serialize(ItemStack)
     */
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
