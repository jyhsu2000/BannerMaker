package club.kid7.bannermaker;

import club.kid7.bannermaker.registry.DyeColorRegistry;
import club.kid7.bannermaker.util.BannerUtil;
import club.kid7.bannermaker.util.PersistentDataUtil;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.bukkit.block.banner.PatternType.BORDER;
import static org.bukkit.block.banner.PatternType.CROSS;
import static org.bukkit.block.banner.PatternType.CURLY_BORDER;
import static org.bukkit.block.banner.PatternType.DIAGONAL_RIGHT;
import static org.bukkit.block.banner.PatternType.DIAGONAL_UP_RIGHT;
import static org.bukkit.block.banner.PatternType.HALF_HORIZONTAL;
import static org.bukkit.block.banner.PatternType.HALF_HORIZONTAL_BOTTOM;
import static org.bukkit.block.banner.PatternType.HALF_VERTICAL;
import static org.bukkit.block.banner.PatternType.HALF_VERTICAL_RIGHT;
import static org.bukkit.block.banner.PatternType.RHOMBUS;
import static org.bukkit.block.banner.PatternType.SQUARE_BOTTOM_LEFT;
import static org.bukkit.block.banner.PatternType.SQUARE_BOTTOM_RIGHT;
import static org.bukkit.block.banner.PatternType.SQUARE_TOP_LEFT;
import static org.bukkit.block.banner.PatternType.SQUARE_TOP_RIGHT;
import static org.bukkit.block.banner.PatternType.STRIPE_BOTTOM;
import static org.bukkit.block.banner.PatternType.STRIPE_CENTER;
import static org.bukkit.block.banner.PatternType.STRIPE_DOWNLEFT;
import static org.bukkit.block.banner.PatternType.STRIPE_DOWNRIGHT;
import static org.bukkit.block.banner.PatternType.STRIPE_LEFT;
import static org.bukkit.block.banner.PatternType.STRIPE_MIDDLE;
import static org.bukkit.block.banner.PatternType.STRIPE_RIGHT;
import static org.bukkit.block.banner.PatternType.STRIPE_TOP;
import static org.bukkit.block.banner.PatternType.TRIANGLES_BOTTOM;
import static org.bukkit.block.banner.PatternType.TRIANGLES_TOP;
import static org.bukkit.block.banner.PatternType.TRIANGLE_BOTTOM;
import static org.bukkit.block.banner.PatternType.TRIANGLE_TOP;

/*
 * 字母旗幟外觀靈感來源：
 *   無框字母  https://www.youtube.com/watch?v=PncIlxt8TtI
 *   有框字母  https://www.youtube.com/watch?v=jLYvPD_vbZg
 *   數字＆問號＆驚嘆號＆句號 https://www.youtube.com/watch?v=xODU7WWTXYE
 */
public class AlphabetBanner {

    private final String alphabet;
    private DyeColor baseColor;
    private DyeColor dyeColor;
    private boolean bordered;

    public AlphabetBanner(String alphabet, DyeColor baseColor, DyeColor dyeColor, boolean bordered) {
        this.alphabet = ChatColor.stripColor(alphabet.toUpperCase()).substring(0, 1);
        this.baseColor = baseColor;
        this.dyeColor = dyeColor;
        this.bordered = bordered;
    }

    public DyeColor getBaseColor() {
        return baseColor;
    }

    public void setBaseColor(DyeColor baseColor) {
        this.baseColor = baseColor;
    }

    public DyeColor getDyeColor() {
        return dyeColor;
    }

    public void setDyeColor(DyeColor dyeColor) {
        this.dyeColor = dyeColor;
    }

    public boolean isBordered() {
        return bordered;
    }

    public void setBordered(boolean bordered) {
        this.bordered = bordered;
    }

    public static ItemStack get(String alphabet) {
        return get(alphabet, DyeColor.WHITE, DyeColor.BLACK, true);
    }

    public static ItemStack get(String alphabet, DyeColor baseColor, DyeColor dyeColor, boolean bordered) {
        return new AlphabetBanner(alphabet, baseColor, dyeColor, bordered).toItemStack();
    }

    /**
     * 檢查 ItemStack 是否為字母旗幟（由本插件透過 {@link #toItemStack()} 生成）。
     */
    public static boolean isAlphabetBanner(ItemStack itemStack) {
        if (!BannerUtil.isBanner(itemStack)) {
            return false;
        }
        if (!itemStack.hasItemMeta()) {
            return false;
        }
        ItemMeta itemMeta = Objects.requireNonNull(itemStack.getItemMeta());
        String bannerType = PersistentDataUtil.get(itemMeta, "banner-type");
        return "alphabet-banner".equals(bannerType);
    }

    /**
     * 套用 {@link #UNBORDERED} 或 {@link #BORDERED} 中的圖案定義，產生字母旗幟。
     * 若 invert=true，最後將整支旗幟材料改為 dyeColor（造成顏色「反轉」視覺）。
     */
    public ItemStack toItemStack() {
        ItemStack banner = new ItemStack(DyeColorRegistry.getBannerMaterial(baseColor));
        BannerMeta bannerMeta = (BannerMeta) Objects.requireNonNull(banner.getItemMeta());
        bannerMeta.setDisplayName(BannerMaker.getInstance().getMessageService().formatToString("&a" + alphabet));
        PersistentDataUtil.set(bannerMeta, "banner-type", "alphabet-banner");

        AlphabetDef def = (bordered ? BORDERED : UNBORDERED).get(alphabet);
        if (def != null) {
            for (PatternEntry p : def.patterns()) {
                bannerMeta.addPattern(new Pattern(p.useDye() ? dyeColor : baseColor, p.type()));
            }
            if (def.invert()) {
                banner = new ItemStack(DyeColorRegistry.getBannerMaterial(dyeColor));
            }
        }

        banner.setItemMeta(bannerMeta);
        return banner;
    }

    // ===== 字母圖案資料表 =====
    //
    // 每個字元對應一個 AlphabetDef：一組 PatternEntry（決定使用 base 還是 dye 顏色 + PatternType），
    // 以及是否在最後將旗幟材料改為 dyeColor（invert，用於產生視覺上的「反白」字母）。
    //
    // PatternType 在 1.21.x 內由 enum 演化為 interface；本檔僅以「靜態欄位存取」與「建構式參數」
    // 使用 PatternType，不對其呼叫實例方法，因此 enum / interface 兩種 bytecode 形式都安全。

    private record PatternEntry(boolean useDye, PatternType type) {}

    private record AlphabetDef(boolean invert, List<PatternEntry> patterns) {}

    private static PatternEntry dye(PatternType t) {
        return new PatternEntry(true, t);
    }

    private static PatternEntry base(PatternType t) {
        return new PatternEntry(false, t);
    }

    private static AlphabetDef patterns(PatternEntry... entries) {
        return new AlphabetDef(false, List.of(entries));
    }

    private static AlphabetDef invertedPatterns(PatternEntry... entries) {
        return new AlphabetDef(true, List.of(entries));
    }

    private static final Map<String, AlphabetDef> UNBORDERED = new HashMap<>();
    private static final Map<String, AlphabetDef> BORDERED = new HashMap<>();

    static {
        // ---- 無框字母 ----
        UNBORDERED.put("A", patterns(dye(STRIPE_TOP), dye(STRIPE_LEFT), dye(STRIPE_RIGHT), dye(STRIPE_MIDDLE)));

        AlphabetDef uB = patterns(dye(STRIPE_TOP), dye(STRIPE_LEFT), dye(STRIPE_RIGHT), dye(STRIPE_MIDDLE), dye(STRIPE_BOTTOM));
        UNBORDERED.put("B", uB);
        UNBORDERED.put("8", uB);

        UNBORDERED.put("C", patterns(dye(STRIPE_LEFT), dye(STRIPE_TOP), dye(STRIPE_BOTTOM)));
        UNBORDERED.put("D", invertedPatterns(base(RHOMBUS), dye(STRIPE_TOP), dye(STRIPE_BOTTOM), dye(STRIPE_LEFT)));
        UNBORDERED.put("E", patterns(dye(STRIPE_MIDDLE), base(STRIPE_RIGHT), dye(STRIPE_BOTTOM), dye(STRIPE_TOP), dye(STRIPE_LEFT)));
        UNBORDERED.put("F", patterns(dye(STRIPE_MIDDLE), base(STRIPE_RIGHT), dye(STRIPE_LEFT), dye(STRIPE_TOP)));
        UNBORDERED.put("G", patterns(dye(STRIPE_RIGHT), base(HALF_HORIZONTAL), dye(STRIPE_BOTTOM), dye(STRIPE_LEFT), dye(STRIPE_TOP)));
        UNBORDERED.put("H", invertedPatterns(base(STRIPE_TOP), base(STRIPE_BOTTOM), dye(STRIPE_LEFT), dye(STRIPE_RIGHT)));
        UNBORDERED.put("I", patterns(dye(STRIPE_TOP), dye(STRIPE_BOTTOM), dye(STRIPE_CENTER)));
        UNBORDERED.put("J", patterns(dye(STRIPE_LEFT), base(HALF_HORIZONTAL), dye(STRIPE_BOTTOM), dye(STRIPE_RIGHT)));
        UNBORDERED.put("K", patterns(dye(STRIPE_MIDDLE), base(STRIPE_RIGHT), dye(STRIPE_DOWNLEFT), dye(STRIPE_DOWNRIGHT), dye(STRIPE_LEFT)));
        UNBORDERED.put("L", patterns(dye(STRIPE_LEFT), dye(STRIPE_BOTTOM)));
        UNBORDERED.put("M", patterns(dye(TRIANGLE_TOP), base(TRIANGLES_TOP), dye(STRIPE_LEFT), dye(STRIPE_RIGHT)));
        UNBORDERED.put("N", patterns(dye(STRIPE_LEFT), base(TRIANGLE_TOP), dye(STRIPE_RIGHT), dye(STRIPE_DOWNRIGHT)));
        UNBORDERED.put("O", patterns(dye(STRIPE_TOP), dye(STRIPE_RIGHT), dye(STRIPE_BOTTOM), dye(STRIPE_LEFT)));
        UNBORDERED.put("P", invertedPatterns(base(HALF_HORIZONTAL), dye(STRIPE_RIGHT), base(STRIPE_BOTTOM), dye(STRIPE_TOP), dye(STRIPE_LEFT)));
        UNBORDERED.put("Q", patterns(dye(STRIPE_DOWNRIGHT), base(HALF_HORIZONTAL), dye(STRIPE_LEFT), dye(STRIPE_BOTTOM), dye(STRIPE_RIGHT), dye(STRIPE_TOP)));
        UNBORDERED.put("R", invertedPatterns(base(HALF_HORIZONTAL_BOTTOM), dye(STRIPE_DOWNRIGHT), base(HALF_VERTICAL), dye(STRIPE_LEFT), dye(STRIPE_TOP), dye(STRIPE_MIDDLE)));
        UNBORDERED.put("S", patterns(dye(TRIANGLE_TOP), dye(TRIANGLE_BOTTOM), dye(SQUARE_TOP_RIGHT), dye(SQUARE_BOTTOM_LEFT), base(RHOMBUS), dye(STRIPE_DOWNRIGHT)));
        UNBORDERED.put("T", patterns(dye(STRIPE_CENTER), dye(STRIPE_TOP)));
        UNBORDERED.put("U", patterns(dye(STRIPE_BOTTOM), dye(STRIPE_RIGHT), dye(STRIPE_LEFT)));
        UNBORDERED.put("V", patterns(dye(STRIPE_LEFT), base(TRIANGLES_BOTTOM), dye(STRIPE_DOWNLEFT)));
        UNBORDERED.put("W", patterns(dye(TRIANGLE_BOTTOM), base(TRIANGLES_BOTTOM), dye(STRIPE_LEFT), dye(STRIPE_RIGHT)));
        UNBORDERED.put("X", patterns(dye(STRIPE_DOWNLEFT), dye(STRIPE_DOWNRIGHT)));
        UNBORDERED.put("Y", patterns(dye(STRIPE_DOWNRIGHT), base(HALF_HORIZONTAL_BOTTOM), dye(STRIPE_DOWNLEFT)));

        AlphabetDef uZ = patterns(dye(TRIANGLE_TOP), dye(TRIANGLE_BOTTOM), dye(SQUARE_TOP_LEFT), dye(SQUARE_BOTTOM_RIGHT), base(RHOMBUS), dye(STRIPE_DOWNLEFT));
        UNBORDERED.put("Z", uZ);
        UNBORDERED.put("2", uZ);

        UNBORDERED.put("1", patterns(dye(SQUARE_TOP_LEFT), base(BORDER), dye(STRIPE_CENTER)));
        UNBORDERED.put("3", patterns(dye(STRIPE_MIDDLE), base(STRIPE_LEFT), dye(STRIPE_BOTTOM), dye(STRIPE_RIGHT), dye(STRIPE_TOP)));
        UNBORDERED.put("4", invertedPatterns(base(HALF_HORIZONTAL), dye(STRIPE_LEFT), base(STRIPE_BOTTOM), dye(STRIPE_RIGHT), dye(STRIPE_MIDDLE)));
        UNBORDERED.put("5", invertedPatterns(base(HALF_VERTICAL_RIGHT), base(HALF_HORIZONTAL_BOTTOM), dye(STRIPE_BOTTOM), base(DIAGONAL_UP_RIGHT), dye(STRIPE_DOWNRIGHT), dye(STRIPE_TOP)));
        UNBORDERED.put("6", patterns(dye(STRIPE_RIGHT), base(HALF_HORIZONTAL), dye(STRIPE_BOTTOM), dye(STRIPE_MIDDLE), dye(STRIPE_LEFT), dye(STRIPE_TOP)));
        UNBORDERED.put("7", patterns(dye(STRIPE_TOP), base(DIAGONAL_RIGHT), dye(STRIPE_DOWNLEFT)));
        UNBORDERED.put("9", patterns(dye(STRIPE_LEFT), base(HALF_HORIZONTAL_BOTTOM), dye(STRIPE_MIDDLE), dye(STRIPE_TOP), dye(STRIPE_RIGHT)));
        UNBORDERED.put("0", patterns(dye(STRIPE_TOP), dye(STRIPE_RIGHT), dye(STRIPE_BOTTOM), dye(STRIPE_LEFT), dye(STRIPE_DOWNLEFT)));
        UNBORDERED.put("?", patterns(dye(STRIPE_RIGHT), base(HALF_HORIZONTAL_BOTTOM), dye(STRIPE_TOP), dye(STRIPE_MIDDLE), dye(SQUARE_BOTTOM_LEFT)));
        UNBORDERED.put("!", patterns(dye(HALF_HORIZONTAL), dye(STRIPE_MIDDLE), dye(SQUARE_BOTTOM_LEFT), base(HALF_VERTICAL_RIGHT)));
        UNBORDERED.put(".", patterns(dye(SQUARE_BOTTOM_LEFT)));

        // ---- 有框字母 ----
        BORDERED.put("A", invertedPatterns(base(HALF_HORIZONTAL), base(STRIPE_BOTTOM), dye(STRIPE_RIGHT), dye(STRIPE_TOP), dye(STRIPE_LEFT), base(BORDER)));

        AlphabetDef bB = invertedPatterns(base(STRIPE_CENTER), dye(STRIPE_BOTTOM), dye(STRIPE_TOP), dye(STRIPE_MIDDLE), base(BORDER));
        BORDERED.put("B", bB);
        BORDERED.put("8", bB);

        BORDERED.put("C", patterns(dye(STRIPE_LEFT), dye(STRIPE_TOP), dye(STRIPE_BOTTOM), base(BORDER)));
        BORDERED.put("D", patterns(dye(STRIPE_RIGHT), dye(STRIPE_BOTTOM), dye(STRIPE_TOP), base(CURLY_BORDER), dye(STRIPE_LEFT), base(BORDER)));
        BORDERED.put("E", patterns(dye(STRIPE_MIDDLE), base(STRIPE_RIGHT), dye(STRIPE_LEFT), dye(STRIPE_TOP), dye(STRIPE_BOTTOM), base(BORDER)));
        BORDERED.put("F", patterns(dye(STRIPE_MIDDLE), base(STRIPE_RIGHT), dye(STRIPE_LEFT), dye(STRIPE_TOP), base(BORDER)));
        BORDERED.put("G", patterns(dye(STRIPE_RIGHT), base(HALF_HORIZONTAL), dye(STRIPE_BOTTOM), dye(STRIPE_LEFT), dye(STRIPE_TOP), base(BORDER)));
        BORDERED.put("H", patterns(dye(STRIPE_MIDDLE), dye(STRIPE_RIGHT), dye(STRIPE_LEFT), base(BORDER)));
        BORDERED.put("I", patterns(dye(STRIPE_TOP), dye(STRIPE_BOTTOM), dye(STRIPE_CENTER), base(BORDER)));
        BORDERED.put("J", patterns(dye(STRIPE_LEFT), base(HALF_HORIZONTAL), dye(STRIPE_BOTTOM), dye(STRIPE_RIGHT), base(BORDER)));
        // K 的兩個 STRIPE_LEFT 為原始程式碼的設計，保留不變
        BORDERED.put("K", patterns(dye(STRIPE_LEFT), dye(STRIPE_LEFT), dye(STRIPE_MIDDLE), base(HALF_VERTICAL_RIGHT), dye(CROSS), base(BORDER)));
        BORDERED.put("L", patterns(dye(STRIPE_LEFT), dye(STRIPE_BOTTOM), base(BORDER)));
        BORDERED.put("M", patterns(dye(TRIANGLE_TOP), base(TRIANGLES_TOP), dye(STRIPE_LEFT), dye(STRIPE_RIGHT), base(BORDER)));
        BORDERED.put("N", patterns(dye(STRIPE_LEFT), base(DIAGONAL_UP_RIGHT), dye(STRIPE_DOWNRIGHT), dye(STRIPE_RIGHT), base(BORDER)));
        BORDERED.put("O", patterns(dye(STRIPE_LEFT), dye(STRIPE_TOP), dye(STRIPE_RIGHT), dye(STRIPE_BOTTOM), base(BORDER)));
        BORDERED.put("P", invertedPatterns(base(HALF_HORIZONTAL), dye(STRIPE_RIGHT), base(STRIPE_BOTTOM), dye(STRIPE_LEFT), dye(STRIPE_TOP), base(BORDER)));
        BORDERED.put("Q", patterns(dye(STRIPE_LEFT), dye(STRIPE_TOP), dye(STRIPE_RIGHT), dye(STRIPE_BOTTOM), base(BORDER), dye(SQUARE_BOTTOM_RIGHT)));
        BORDERED.put("R", patterns(dye(STRIPE_RIGHT), dye(STRIPE_TOP), base(HALF_HORIZONTAL_BOTTOM), dye(STRIPE_DOWNRIGHT), dye(STRIPE_LEFT), base(BORDER)));
        BORDERED.put("S", patterns(dye(STRIPE_BOTTOM), dye(STRIPE_TOP), base(RHOMBUS), dye(STRIPE_DOWNRIGHT), base(BORDER), base(CURLY_BORDER)));
        BORDERED.put("T", patterns(dye(STRIPE_TOP), dye(STRIPE_CENTER), base(BORDER)));
        BORDERED.put("U", patterns(dye(STRIPE_LEFT), dye(STRIPE_BOTTOM), dye(STRIPE_RIGHT), base(BORDER)));
        BORDERED.put("V", patterns(dye(STRIPE_LEFT), base(TRIANGLES_BOTTOM), dye(STRIPE_DOWNLEFT), base(BORDER)));
        BORDERED.put("W", patterns(dye(TRIANGLE_BOTTOM), base(TRIANGLES_BOTTOM), dye(STRIPE_LEFT), dye(STRIPE_RIGHT), base(BORDER)));
        BORDERED.put("X", patterns(dye(STRIPE_TOP), dye(STRIPE_BOTTOM), base(STRIPE_CENTER), dye(CROSS), base(BORDER)));
        BORDERED.put("Y", patterns(dye(CROSS), base(HALF_VERTICAL_RIGHT), dye(STRIPE_DOWNLEFT), base(BORDER)));
        BORDERED.put("Z", patterns(dye(STRIPE_TOP), dye(STRIPE_BOTTOM), dye(STRIPE_DOWNLEFT), base(BORDER)));
        BORDERED.put("1", patterns(dye(SQUARE_TOP_LEFT), dye(STRIPE_CENTER), dye(STRIPE_BOTTOM), base(BORDER)));
        BORDERED.put("2", patterns(dye(STRIPE_TOP), base(RHOMBUS), dye(STRIPE_DOWNLEFT), dye(STRIPE_BOTTOM), base(BORDER)));
        BORDERED.put("3", patterns(dye(STRIPE_MIDDLE), base(STRIPE_LEFT), dye(STRIPE_BOTTOM), dye(STRIPE_RIGHT), dye(STRIPE_TOP), base(BORDER)));
        BORDERED.put("4", patterns(base(HALF_HORIZONTAL), dye(STRIPE_LEFT), base(STRIPE_BOTTOM), dye(STRIPE_RIGHT), dye(STRIPE_MIDDLE), base(BORDER)));
        BORDERED.put("5", patterns(dye(STRIPE_BOTTOM), dye(STRIPE_DOWNRIGHT), base(CURLY_BORDER), dye(SQUARE_BOTTOM_LEFT), dye(STRIPE_TOP), base(BORDER)));
        BORDERED.put("6", invertedPatterns(base(HALF_HORIZONTAL_BOTTOM), dye(STRIPE_RIGHT), base(STRIPE_TOP), dye(STRIPE_BOTTOM), dye(STRIPE_LEFT), base(BORDER)));
        BORDERED.put("7", patterns(dye(STRIPE_TOP), base(DIAGONAL_RIGHT), dye(STRIPE_DOWNLEFT), dye(SQUARE_BOTTOM_LEFT), base(BORDER)));
        BORDERED.put("9", invertedPatterns(base(HALF_HORIZONTAL), dye(STRIPE_LEFT), base(STRIPE_BOTTOM), dye(STRIPE_TOP), dye(STRIPE_RIGHT), base(BORDER)));
        BORDERED.put("0", patterns(dye(STRIPE_TOP), dye(STRIPE_RIGHT), dye(STRIPE_BOTTOM), dye(STRIPE_LEFT), dye(STRIPE_DOWNLEFT), base(BORDER)));
        BORDERED.put("?", patterns(dye(STRIPE_RIGHT), base(HALF_HORIZONTAL_BOTTOM), dye(STRIPE_TOP), dye(STRIPE_MIDDLE), dye(SQUARE_BOTTOM_LEFT), base(BORDER)));
        BORDERED.put("!", patterns(dye(HALF_HORIZONTAL), dye(STRIPE_MIDDLE), dye(SQUARE_BOTTOM_LEFT), base(HALF_VERTICAL_RIGHT), base(BORDER)));
        BORDERED.put(".", patterns(dye(SQUARE_BOTTOM_LEFT), base(BORDER)));
    }
}
